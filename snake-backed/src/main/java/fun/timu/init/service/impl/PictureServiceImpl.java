package fun.timu.init.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.manager.CosManager;
import fun.timu.init.manager.upload.FilePictureUpload;
import fun.timu.init.manager.upload.PictureUploadTemplate;
import fun.timu.init.manager.upload.UrlPictureUpload;
import fun.timu.init.mapper.PictureMapper;
import fun.timu.init.model.dto.file.UploadPictureResult;
import fun.timu.init.model.dto.picture.PictureQueryRequest;
import fun.timu.init.model.dto.picture.PictureReviewRequest;
import fun.timu.init.model.dto.picture.PictureUploadByBatchRequest;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.PictureReviewStatusEnum;
import fun.timu.init.model.vo.PictureVO;
import fun.timu.init.model.vo.UserVO;
import fun.timu.init.service.PictureService;
import fun.timu.init.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {


    private final FilePictureUpload filePictureUpload;
    private final UrlPictureUpload urlPictureUpload;
    private final UserService userService;
    private final CosManager cosManager;
    private String defaulCategory = "素材";

    PictureServiceImpl(FilePictureUpload filePictureUpload, UrlPictureUpload urlPictureUpload, UserService userService, CosManager cosManager) {
        this.filePictureUpload = filePictureUpload;
        this.urlPictureUpload = urlPictureUpload;
        this.userService = userService;
        this.cosManager = cosManager;
    }

    /**
     * 上传图片方法
     *
     * @param inputSource          图片文件，用于上传
     * @param pictureUploadRequest 图片上传请求对象，可能包含图片ID，用于判断是新增还是更新图片
     * @param loginUser            登录用户信息，用于验证权限和确定用户ID
     * @return 返回上传后的图片信息对象
     * <p>
     * 该方法首先验证用户是否有权限进行操作，然后根据请求对象中的ID判断是新增还是更新图片
     * 如果是更新操作，会检查图片ID对应的照片是否存在如果不存在，则抛出错误
     * 接着，方法构造上传路径，并调用文件管理服务上传图片上传成功后，将图片信息保存或更新到数据库中
     * 最后，将保存的图片对象转换为视图对象返回
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 检查用户是否已登录，未登录则抛出无权限错误
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR, "用户未登录");

        // 输入验证
        if (inputSource == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的输入参数");
        }

        Long pictureId = pictureUploadRequest != null ? pictureUploadRequest.getId() : null;

        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            clearPictureFile(oldPicture); // 先清空老图片的云存储文件

            // 仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限编辑此图片");
            }
        }

        // 构造上传路径前缀
        String uploadPathPrefix = "public/" + loginUser.getId();

        // 根据 inputSource 的类型区分上传方式
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }

        try {
            UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);

            // 构造要入库的图片信息
            Picture picture = new Picture();
            picture.setCategory(defaulCategory);
            picture.setUrl(uploadPictureResult.getUrl());
            picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
            String picName = uploadPictureResult.getPicName();
            if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
                picName = pictureUploadRequest.getPicName();
            }
            picture.setName(picName);

            picture.setPicSize(uploadPictureResult.getPicSize());
            picture.setPicWidth(uploadPictureResult.getPicWidth());
            picture.setPicHeight(uploadPictureResult.getPicHeight());
            picture.setPicScale(uploadPictureResult.getPicScale());
            picture.setPicFormat(uploadPictureResult.getPicFormat());
            picture.setUserId(loginUser.getId());

            // 补充审核参数
            this.fillReviewParams(picture, loginUser);

            // 如果 pictureId 不为空，表示更新，否则是新增
            if (pictureId != null) {
                // 如果是更新，需要补充 id 和编辑时间
                picture.setId(pictureId);
                picture.setEditTime(new Date());
            }

            // 保存或更新图片信息
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");

            // 将图片对象转换为视图对象并返回
            return PictureVO.objToVo(picture);
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片上传失败");
        }
    }


    /**
     * 根据Picture对象获取封装的PictureVO对象
     * 此方法首先将Picture对象转换为PictureVO对象，然后关联查询用户信息并进行封装
     *
     * @param picture Picture对象，包含图片相关信息
     * @param request HttpServletRequest对象，用于获取请求相关数据
     * @return PictureVO对象，包含封装的图片及用户信息
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 根据Picture实体页获取PictureVO对象页
     *
     * @param picturePage Picture实体的分页对象，包含当前页、页面大小和总记录数等信息
     * @param request     HTTP请求对象，用于获取请求相关的信息
     * @return 返回一个封装了PictureVO对象的分页，用于展示层
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        // 获取Picture实体列表
        List<Picture> pictureList = picturePage.getRecords();
        // 初始化PictureVO分页对象，复制当前页、页面大小和总记录数
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        // 如果实体列表为空，则直接返回空的VO分页对象
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        // 1,2,3,4
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        // 1 => user1, 2 => user2
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 生成Picture实体的查询包装器
     * 此方法根据传入的查询请求参数，构建一个查询包装器，用于数据库查询
     * 它处理各种查询条件和排序需求，以生成高效的查询语句
     *
     * @param pictureQueryRequest 包含查询条件和排序信息的请求对象
     * @return 返回一个配置好的QueryWrapper对象，用于执行数据库查询
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
        }
        // 构建其他查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);

        // 支持审核状态查询
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // 处理标签查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序处理
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 校验图片信息的合法性
     * 此方法主要用于在保存或更新图片信息前，校验传入的图片对象是否符合规范
     * 它检查图片对象是否非空，图片的URL和简介是否符合长度限制
     *
     * @param picture 图片对象，包含图片的详细信息，如ID、URL和简介
     * @throws IllegalArgumentException 如果图片对象为null或图片信息不符合规范，则抛出此异常
     */
    @Override
    public void validPicture(Picture picture) {
        // 校验图片对象是否为null
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();

        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");

        // 如果传递了 url，才校验
        if (StrUtil.isNotBlank(url)) {
            // 校验 url 长度是否超过最大限制
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }

        // 如果传递了 简介，才校验
        if (StrUtil.isNotBlank(introduction)) {
            // 校验 简介 长度是否超过最大限制
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 执行图片审核操作
     * 此方法用于更新图片的审核状态，确保图片内容符合规定
     * 它接受一个包含图片审核请求的参数和当前登录用户的信息
     * 如果请求参数不完整或图片不存在，将抛出异常
     *
     * @param pictureReviewRequest 包含要审核的图片ID和审核状态的请求对象
     * @param loginUser            当前登录的用户，用于记录审核者信息
     * @throws BusinessException 当参数错误、图片未找到或操作失败时抛出的异常
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 获取图片ID和审核状态
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        // 检查ID和审核状态是否有效，避免重复审核
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 已是该状态
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 更新审核状态
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());

        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动过审");
            picture.setReviewTime(new Date());
        } else {
            // 非管理员，创建或编辑都要改为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    /**
     * 通过批量方式上传图片（Bing）
     *
     * @param pictureUploadByBatchRequest 批量上传图片的请求对象，包含搜索文本和数量
     * @param loginUser                   登录用户信息，用于记录操作者
     * @return 成功上传的图片数量
     * <p>
     * 本函数从必应图像搜索中抓取图片，并批量上传至系统中
     */
    @Override
    public int uploadPictureByBing(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 获取搜索关键词
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        // 校验一次最多上传的图片数量
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");

        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        // 如果名称前缀为空，则使用搜索文本作为前缀
        if (StrUtil.isBlank(namePrefix)) namePrefix = searchText;

        // 构造必应图像搜索的URL
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            // 发起HTTP请求获取搜索结果页面
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            // 如果发生IO异常，则记录错误日志并抛出业务异常
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        // 获取包含图片的div元素
        Element div = document.getElementsByClass("dgControl").first();
        // 如果未找到指定的div元素，则抛出业务异常
        if (ObjUtil.isNull(div)) throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");

        // 获取所有图片元素
        Elements imgElementList = div.select("img.mimg");
        // 初始化上传成功的图片计数器
        int uploadCount = 0;
        // 遍历所有图片元素
        defaulCategory = "素材";
        for (Element imgElement : imgElementList) {
            // 获取图片的URL
            String fileUrl = imgElement.attr("src");
            // 如果图片URL为空，则记录日志并跳过当前图片
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过: {}", fileUrl);
                continue;
            }
            // 处理图片上传地址，防止出现转义问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) fileUrl = fileUrl.substring(0, questionMarkIndex);

            // 创建上传图片的请求对象
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            try {
                // 调用上传图片的方法，并获取上传后的图片信息
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                // 记录成功上传图片的日志
                log.info("图片上传成功, id = {}", pictureVO.getId());
                // 更新上传成功图片的数量
                uploadCount++;
            } catch (Exception e) {
                // 如果上传图片失败，则记录错误日志并继续上传下一张图片
                log.error("图片上传失败", e);
                continue;
            }
            // 如果上传成功的图片数量达到预期数量，则停止上传
            if (uploadCount >= count) break;
        }
        // 返回成功上传的图片数量
        return uploadCount;
    }

    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        // 判断该图片是否被多条记录使用
        String pictureUrl = oldPicture.getUrl();
        long count = this.lambdaQuery().eq(Picture::getUrl, pictureUrl).count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 1) {
            return;
        }
        // FIXME 注意，这里的 url 包含了域名，实际上只要传 key 值（存储路径）就够了
        cosManager.deleteObject(oldPicture.getUrl());
        // 清理缩略图
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            cosManager.deleteObject(thumbnailUrl);
        }
    }
}