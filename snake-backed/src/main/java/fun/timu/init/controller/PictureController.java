package fun.timu.init.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import fun.timu.init.annotation.AuthCheck;
import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.DeleteRequest;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.constant.UserConstant;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.manager.CacheManager;
import fun.timu.init.model.dto.picture.*;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.PictureReviewStatusEnum;
import fun.timu.init.model.vo.PictureTagCategory;
import fun.timu.init.model.vo.PictureVO;
import fun.timu.init.service.PictureService;
import fun.timu.init.service.SpaceService;
import fun.timu.init.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/picture")
public class PictureController {
    private static final Logger logger = LoggerFactory.getLogger(PictureController.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder().initialCapacity(1024).maximumSize(10000L).expireAfterWrite(5L, TimeUnit.MINUTES).build(); // 缓存 5 分钟移除


    private final UserService userService;
    private final PictureService pictureService;
    private final SpaceService spaceService;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheManager cacheManager;

    public PictureController(UserService userService, PictureService pictureService, SpaceService spaceService, StringRedisTemplate stringRedisTemplate, CacheManager cacheManager) {
        this.userService = userService;
        this.pictureService = pictureService;
        this.spaceService = spaceService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheManager = cacheManager;
    }

    /**
     * 上传图片（可重新上传）
     * <p>
     * 该接口允许用户上传图片，并且支持相同名称图片的重新上传
     * 主要用于处理图片文件的上传请求，验证用户权限，并调用服务层方法完成图片上传逻辑
     *
     * @param multipartFile        前端上传的图片文件，通过RequestPart注解获取
     * @param pictureUploadRequest 包含图片上传相关信息的请求对象，如图片描述等
     * @param request              HTTP请求对象，用于获取当前登录用户信息
     * @return 返回包含上传成功后的图片信息的响应对象
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        try {
            // 获取当前登录的用户信息
            User loginUser = userService.getLoginUser(request);

            // 验证文件大小和类型
            validateFile(multipartFile);

            // 调用服务层方法，执行图片上传逻辑
            PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);

            // 记录日志
            logger.info("User {} uploaded picture: {}", loginUser.getUserName(), pictureVO.getName());

            // 返回上传成功后的图片信息
            return ResultUtils.success(pictureVO);
        } catch (Exception e) {
            // 记录异常日志
            logger.error("Failed to upload picture", e);
            // 返回错误信息给前端
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    /**
     * 通过 URL 上传图片（可重新上传）
     */
    @PostMapping("/upload/url")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<PictureVO> uploadPictureByUrl(@RequestBody PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            // 获取图片的URL
            String fileUrl = pictureUploadRequest.getFileUrl();
            // 调用服务层方法上传图片
            PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
            // 日志记录图片上传信息
            logger.info("User {} uploaded picture: {}", loginUser.getUserName(), pictureVO.getName());
            // 返回上传成功的图片信息
            return ResultUtils.success(pictureVO);
        } catch (Exception e) {
            // 记录异常日志
            logger.error("Failed to upload picture", e);
            // 返回错误信息给前端
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }


    /**
     * 验证上传的文件是否符合规范
     * 此方法主要进行以下三项检查：
     * 1. 文件是否为空或未选择
     * 2. 文件大小是否超过允许的最大值（5MB）
     * 3. 文件类型是否为允许的图片类型
     *
     * @param file 要验证的MultipartFile对象
     * @throws Exception 如果文件验证失败，则抛出IllegalArgumentException
     */
    private void validateFile(MultipartFile file) throws Exception {
        // 检查文件是否为空或未选择
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        // 检查文件大小是否超过最大允许值
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超出限制，最大允许5MB");
        }
        // 检查文件是否为有效的图片类型
        if (!isValidImageType(file)) {
            throw new IllegalArgumentException("仅允许上传JPEG、PNG、GIF格式的图片");
        }
    }

    /**
     * 检查上传的文件是否为允许的图像类型
     *
     * @param file 待检查的文件，包含文件的类型信息
     * @return 如果文件类型属于允许的图像类型，则返回true；否则返回false
     */
    private boolean isValidImageType(MultipartFile file) {
        // 遍历允许的图像类型列表
        for (String type : ALLOWED_IMAGE_TYPES) {
            // 如果文件的类型与当前遍历的图像类型相匹配，则返回true
            if (type.equals(file.getContentType())) return true;
        }
        // 如果遍历完所有类型都没有匹配，则返回false
        return false;
    }


    /**
     * 删除图片接口
     * 该接口允许用户删除图片，但只有图片的上传者或管理员可以执行删除操作
     *
     * @param deleteRequest 包含要删除图片的ID的请求体
     * @param request       HTTP请求对象，用于获取当前登录用户的信息
     * @return 返回一个表示删除操作是否成功的BaseResponse对象
     * @throws BusinessException 当参数无效、图片不存在、用户无权限或操作失败时抛出业务异常
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 检查删除请求是否有效
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 提取需要删除的图片ID
        long id = deleteRequest.getId();

        // 判断是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回删除成功的结果
        return ResultUtils.success(true);
    }


    /**
     * 更新图片信息
     *
     * @param pictureUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        // 参数有效性检查
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);

        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

        // 补充审核参数
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(oldPicture, loginUser);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回成功结果
        return ResultUtils.success(true);
    }


    /**
     * 根据ID获取图片信息
     * 该方法使用了GetMapping注解来映射HTTP GET请求到处理方法
     * 使用AuthCheck注解来检查用户是否具有管理员角色
     *
     * @param id      图片的唯一标识符，必须大于0
     * @param request HTTP请求对象，用于获取请求信息
     * @return 返回一个封装了图片信息的BaseResponse对象
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        // 验证参数id是否有效
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        // 验证图片是否存在
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(picture);
    }


    /**
     * 根据ID获取图片的详细信息（Value Object）
     * <p>
     * 此方法通过GET请求接收一个图片ID，并返回该图片的详细信息对象（PictureVO）
     * 它首先验证ID的有效性，然后从数据库中获取图片信息，
     * 最后将图片信息封装到PictureVO中并返回给客户端
     *
     * @param id      图片的唯一标识符，用于从数据库中查询图片信息
     * @param request HTTP请求对象，用于获取请求相关的信息
     * @return 返回一个BaseResponse对象，其中包含查询到的PictureVO对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        // 验证输入的ID是否有效，如果无效则抛出参数错误异常
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 根据ID查询数据库，获取图片对象
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        // 空间权限校验
        Long spaceId = picture.getSpaceId();
        if (spaceId != null) {
            User loginUser = userService.getLoginUser(request);
            pictureService.checkPictureAuth(loginUser, picture);
        }

        // 验证查询结果是否为空，如果为空则抛出未找到错误异常
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        // 将查询到的图片对象封装到PictureVO中，并通过ResultUtils的成功方法返回
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }


    /**
     * 处理图片列表的分页请求
     * 该方法使用了@PostMapping注解，表示它处理的是POST请求
     * 使用@AuthCheck注解进行权限控制，只有具有ADMIN_ROLE角色的用户才能访问
     *
     * @param pictureQueryRequest 包含了查询参数的请求体
     * @return 返回一个包含图片分页信息的响应
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        // 获取当前页码和页面大小
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        // 返回查询结果
        return ResultUtils.success(picturePage);
    }


    /**
     * 根据分页查询图片信息
     *
     * @param pictureQueryRequest 包含查询参数和分页信息的请求对象
     * @param request             HTTP请求对象，用于获取请求上下文
     * @return 返回封装了分页图片信息的响应对象
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 获取当前页码和页面大小
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 限制爬虫：如果页面大小超过20，则抛出参数错误异常
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();

        if (spaceId != null) {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        } else {
            // 公开图库
            pictureQueryRequest.setNullSpaceId(true);
        }


        User loginUser = userService.getLoginUser(request);
        if (loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            pictureQueryRequest.setReviewStatus(null);
        } else {
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        }
        // 查询数据库：根据当前页码和页面大小查询图片记录
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));

        // 获取封装类：将查询结果封装到PictureVO对象中，并返回成功响应
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }


    /**
     * 根据页面查询图片信息，并使用缓存来优化性能
     *
     * @param pictureQueryRequest 包含查询条件的请求对象
     * @param request             HTTP请求对象，用于获取请求信息
     * @return 包含图片信息列表的响应对象
     */
    @PostMapping("/list/page/vo/cache")
    @Deprecated
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 获取当前页码和页面大小
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();

        if (spaceId != null) {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        } else {
            // 公开图库
            pictureQueryRequest.setNullSpaceId(true);
        }

        User loginUser = userService.getLoginUser(request);
        if (loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            pictureQueryRequest.setReviewStatus(null);
        } else {
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        }

        // TODO: 新增分布式缓存逻辑
        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String redisKey = "SnakePictures:listPictureVOByPage:" + hashKey;
        // 从 Redis 缓存中查询
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        String cachedValue = valueOps.get(redisKey);

        if (cachedValue != null) {
            // 如果缓存命中，返回结果
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

        // 存入 Redis 缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // 5 - 10 分钟随机过期，防止雪崩
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);
        valueOps.set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);

        // 返回结果
        return ResultUtils.success(pictureVOPage);
    }


    /**
     * 根据页面查询图片信息列表，并使用本地缓存来加速响应
     *
     * @param pictureQueryRequest 包含查询条件和分页信息的请求对象
     * @param request             HTTP请求对象，用于获取登录用户信息
     * @return 包含图片信息列表的响应对象
     */
    @PostMapping("/list/page/vo/local_cache")
    @Deprecated
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithLocalCache(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 获取当前页码和页面大小
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 限制爬虫：如果页面大小超过20，则抛出参数错误异常
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();

        if (spaceId != null) {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        } else {
            // 公开图库
            pictureQueryRequest.setNullSpaceId(true);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 根据用户角色设置审核状态查询条件
        if (loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            pictureQueryRequest.setReviewStatus(null);
        } else {
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        }

        // TODO: 新增本地缓存逻辑
        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = "listPictureVOByPage:" + hashKey;
        // 从本地缓存中查询
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            // 如果缓存命中，返回结果
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 查询数据库：根据当前页码和页面大小查询图片记录
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));

        // 将查询结果转换为VO对象
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

        // 存入本地缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        LOCAL_CACHE.put(cacheKey, cacheValue);

        // 返回查询结果
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 根据页码和查询条件获取PictureVO列表，使用多级缓存优化查询性能
     *
     * @param pictureQueryRequest 包含查询条件和分页信息的请求对象
     * @param request             HTTP请求对象，用于获取当前登录用户信息
     * @return 包含PictureVO列表的分页响应对象
     */
    @PostMapping("/list/page/vo/multilevel_cache")
    @Deprecated
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithMultilevelCache(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 获取当前页码和页面大小
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 限制爬虫：如果页面大小超过20，则抛出参数错误异常
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();

        if (spaceId != null) {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        } else {
            // 公开图库
            pictureQueryRequest.setNullSpaceId(true);
        }

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 根据用户角色设置审核状态查询条件
        if (loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            pictureQueryRequest.setReviewStatus(null);
        } else {
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        }

        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = "SnakePictures:listPictureVOByPage:" + hashKey;

        // 从缓存中获取数据
        Page<PictureVO> cachedPage = cacheManager.getFromCache(cacheKey, Page.class);
        if (cachedPage != null) {
            return ResultUtils.success(cachedPage);
        }

        // 使用分布式锁防止缓存击穿
        String lockKey = "lock:" + cacheKey;
        if (cacheManager.acquireLock(lockKey)) {
            try {
                // 查询数据库
                Page<Picture> picturePage = pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
                Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

                // 更新缓存
                cacheManager.putToCache(cacheKey, pictureVOPage);

                // 返回查询结果
                return ResultUtils.success(pictureVOPage);
            } finally {
                // 释放锁
                cacheManager.releaseLock(lockKey);
            }
        } else {
            // 其他线程等待一段时间后重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return listPictureVOByPageWithMultilevelCache(pictureQueryRequest, request);
        }
    }


    /**
     * 编辑图片信息的接口
     * 此方法接收一个 PictureEditRequest 对象和 HttpServletRequest 请求作为参数
     * 首先，它会检查 PictureEditRequest 对象是否为空，以及其中的 id 是否有效
     * 然后，将 PictureEditRequest 对象转换为 Picture 实体类，并设置必要的属性
     * 最后，调用 service 层的方法来更新数据库中的图片信息
     *
     * @param pictureEditRequest 包含要编辑的图片信息的请求对象
     * @param request            HTTP 请求对象，用于获取当前登录用户的信息
     * @return 返回一个 BaseResponse 对象，包含一个布尔值来表示编辑操作是否成功
     * @throws BusinessException 如果参数无效或当前用户没有权限进行编辑操作，抛出此异常
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        try {
            // 检查请求参数是否有效
            if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            // 日志记录请求开始
            logger.info("Start editing picture with id: {}", pictureEditRequest.getId());

            // 在此处将实体类和 DTO 进行转换
            Picture picture = new Picture();
            BeanUtils.copyProperties(pictureEditRequest, picture);
            // 注意将 list 转为 string
            List<String> tags = pictureEditRequest.getTags();
            if (tags != null && !tags.isEmpty()) {
                picture.setTags(JSONUtil.toJsonStr(tags));
            } else {
                picture.setTags("[]");
            }

            // 设置编辑时间
            picture.setEditTime(new Date());
            // 数据校验
            pictureService.validPicture(picture);
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            // 判断图片是否存在
            long id = pictureEditRequest.getId();
            Picture oldPicture = pictureService.getById(id);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
            // 仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            // 补充审核参数
            pictureService.fillReviewParams(picture, loginUser);
            // 操作数据库
            boolean result = pictureService.updateById(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 返回成功响应
            return ResultUtils.success(true);
        } catch (Exception e) {
            logger.error("Error occurred while editing picture: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 获取图片标签类别信息
     * <p>
     * 通过GET请求访问/tag_category端点，返回包含图片标签和类别列表的响应对象
     * 此方法主要用于提供页面或API消费者获取预定义的图片标签和类别信息
     *
     * @return BaseResponse<PictureTagCategory> 包含图片标签和类别的响应对象
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        // 创建一个PictureTagCategory对象，用于存储标签和类别信息
        PictureTagCategory pictureTagCategory = new PictureTagCategory();

        // 定义一组常见的图片标签
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        // 定义一组常见的图片类别
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");

        // 将标签列表设置到PictureTagCategory对象中
        pictureTagCategory.setTagList(tagList);
        // 将类别列表设置到PictureTagCategory对象中
        pictureTagCategory.setCategoryList(categoryList);

        // 使用ResultUtils工具类，构建并返回一个表示成功操作的响应对象
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 处理图片审核请求
     * 该方法用于对上传的图片进行审核操作，只有具有管理员角色的用户可以执行此操作
     *
     * @param pictureReviewRequest 包含图片审核相关信息的请求对象，包括图片ID、审核结果等
     * @param request              HTTP请求对象，用于获取当前登录用户信息
     * @return 返回一个表示操作成功的响应对象，包含一个布尔值表示审核操作是否成功
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        // 检查请求参数是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录的用户信息
        User loginUser = userService.getLoginUser(request);

        // 执行图片审核操作
        pictureService.doPictureReview(pictureReviewRequest, loginUser);

        // 返回成功响应
        return ResultUtils.success(true);
    }


    /**
     * 处理批量抓取图片的请求（bing）
     * 该接口仅限于管理员角色的用户使用，用于批量抓取图片
     *
     * @param pictureUploadByBatchRequest 批量抓取图片的请求体，包含需要上传的图片信息
     * @param request                     HTTP请求对象，用于获取当前登录的用户信息
     * @return 返回抓取成功的图片数量
     * <p>
     * 注意：如果请求体为null，则抛出参数错误的异常
     * 通过userService获取当前登录的用户信息，然后调用pictureService进行图片的批量上传
     * 最终返回上传成功的图片数量
     */
    @PostMapping("/upload/bing")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBing(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest, HttpServletRequest request) {
        // 检查请求体是否为null，如果为null则抛出参数错误的异常
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录的用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法执行图片的批量抓取，并获取抓取成功的图片数量
        int uploadCount = pictureService.uploadPictureByBing(pictureUploadByBatchRequest, loginUser);

        // 返回上传成功的图片数量
        return ResultUtils.success(uploadCount);
    }

    /**
     * 处理批量抓取图片的请求（pexels）
     * 该接口仅限于管理员角色的用户使用，用于批量抓取图片
     *
     * @param pictureUploadByBatchRequest 批量抓取图片的请求体，包含需要上传的图片信息
     * @param request                     HTTP请求对象，用于获取当前登录的用户信息
     * @return 返回抓取成功的图片数量
     * <p>
     * 注意：如果请求体为null，则抛出参数错误的异常
     * 通过userService获取当前登录的用户信息，然后调用pictureService进行图片的批量上传
     * 最终返回上传成功的图片数量
     */
    @PostMapping("/upload/pexels")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByPexels(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest, HttpServletRequest request) {
        // 检查请求体是否为null，如果为null则抛出参数错误的异常
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);

        // 获取当前登录的用户信息
        User loginUser = userService.getLoginUser(request);

        // 调用服务层方法执行图片的批量抓取，并获取抓取成功的图片数量
        int uploadCount = pictureService.uploadPictureByPexels(pictureUploadByBatchRequest, loginUser);

        // 返回上传成功的图片数量
        return ResultUtils.success(uploadCount);
    }
}
