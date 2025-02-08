package fun.timu.init.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.manager.CosManager;
import fun.timu.init.manager.PexelsManager;
import fun.timu.init.manager.upload.FilePictureUpload;
import fun.timu.init.manager.upload.PictureUploadTemplate;
import fun.timu.init.manager.upload.UrlPictureUpload;
import fun.timu.init.mapper.PictureMapper;
import fun.timu.init.model.dto.file.UploadPictureResult;
import fun.timu.init.model.dto.picture.*;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.Space;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.PictureReviewStatusEnum;
import fun.timu.init.model.vo.PictureVO;
import fun.timu.init.model.vo.UserVO;
import fun.timu.init.service.PictureService;
import fun.timu.init.service.SpaceService;
import fun.timu.init.service.UserService;
import fun.timu.init.utils.ColorSimilarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    private final FilePictureUpload filePictureUpload;
    private final UrlPictureUpload urlPictureUpload;
    private final UserService userService;
    private final SpaceService spaceService;
    private final PictureMapper pictureMapper;
    private final CosManager cosManager;
    private final PexelsManager pexelsManager;
    private final TransactionTemplate transactionTemplate;
    private String defaulCategory = "素材";

    public PictureServiceImpl(FilePictureUpload filePictureUpload, UrlPictureUpload urlPictureUpload, UserService userService, SpaceService spaceService, PictureMapper pictureMapper, CosManager cosManager, PexelsManager pexelsManager, TransactionTemplate transactionTemplate) {
        this.filePictureUpload = filePictureUpload;
        this.urlPictureUpload = urlPictureUpload;
        this.userService = userService;
        this.spaceService = spaceService;
        this.pictureMapper = pictureMapper;
        this.cosManager = cosManager;
        this.pexelsManager = pexelsManager;
        this.transactionTemplate = transactionTemplate;
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

        // 空间权限校验
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            // 必须空间创建人（管理员）才能上传
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
            // 校验额度
            if (space.getTotalCount() >= space.getMaxCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
            }
            if (space.getTotalSize() >= space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
            }
        }


        // 输入验证
        if (inputSource == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的输入参数");
        }

        Long pictureId = pictureUploadRequest != null ? pictureUploadRequest.getId() : null;

        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 校验空间是否一致, 没传 spaceId，则复用原有图片的 spaceId
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    spaceId = oldPicture.getSpaceId();
                }
            } else {
                // 传了 spaceId，必须和原有图片一致
                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间 id 不一致");
                }
            }
        }

        // 构造上传路径前缀: 按照用户 id 划分目录 => 按照空间划分目录
        String uploadPathPrefix;
        if (spaceId == null) {
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }

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
            // 补充设置 spaceId
            picture.setSpaceId(spaceId);
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

            picture.setPicColor(uploadPictureResult.getPicColor());

            // 补充审核参数
            this.fillReviewParams(picture, loginUser);

            // 如果 pictureId 不为空，表示更新，否则是新增
            if (pictureId != null) {
                // 如果是更新，需要补充 id 和编辑时间
                picture.setId(pictureId);
                picture.setEditTime(new Date());
            }

            // 开启事务
            Long finalSpaceId = spaceId;
            transactionTemplate.execute(status -> {
                boolean result = this.saveOrUpdate(picture);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
                if (finalSpaceId != null) {
                    boolean update = spaceService.lambdaUpdate().eq(Space::getId, finalSpaceId).setSql("totalSize = totalSize + " + picture.getPicSize()).setSql("totalCount = totalCount + 1").update();
                    ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
                }
                return picture;
            });


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
     * 此方法根据传入的查询请求参数，构建一个查询包装器，用于数据库查询。
     * 它处理各种查询条件和排序需求，以生成高效的查询语句。
     *
     * @param pictureQueryRequest 包含查询条件和排序信息的请求对象
     * @return 返回一个配置好的QueryWrapper对象，用于执行数据库查询
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        // 创建一个新的QueryWrapper对象，用于构建查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();

        // 如果传入的查询请求对象为空，直接返回空的查询包装器
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }

        // 从pictureQueryRequest中提取各个字段的值
        Long id = pictureQueryRequest.getId(); // 获取图片ID
        String name = pictureQueryRequest.getName(); // 获取图片名称
        String introduction = pictureQueryRequest.getIntroduction(); // 获取图片简介
        String category = pictureQueryRequest.getCategory(); // 获取图片分类
        List<String> tags = pictureQueryRequest.getTags(); // 获取图片标签列表
        Long picSize = pictureQueryRequest.getPicSize(); // 获取图片大小
        Integer picWidth = pictureQueryRequest.getPicWidth(); // 获取图片宽度
        Integer picHeight = pictureQueryRequest.getPicHeight(); // 获取图片高度
        Double picScale = pictureQueryRequest.getPicScale(); // 获取图片比例
        String picFormat = pictureQueryRequest.getPicFormat(); // 获取图片格式
        String searchText = pictureQueryRequest.getSearchText(); // 获取搜索文本
        Long userId = pictureQueryRequest.getUserId(); // 获取用户ID
        String sortField = pictureQueryRequest.getSortField(); // 获取排序字段
        String sortOrder = pictureQueryRequest.getSortOrder(); // 获取排序顺序（升序或降序）
        Long spaceId = pictureQueryRequest.getSpaceId(); // 获取空间ID
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId(); // 是否为空间ID为null的记录
        String picColor = pictureQueryRequest.getPicColor();

        Date startEditTime = pictureQueryRequest.getStartEditTime(); // 获取编辑开始时间
        Date endEditTime = pictureQueryRequest.getEndEditTime(); // 获取编辑结束时间

        // 处理多字段搜索：如果searchText不为空，则在name和introduction字段中进行模糊匹配
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
        }

        // 构建其他查询条件：
        // - 根据id是否非空来决定是否添加id等于条件
        // - 根据userId是否非空来决定是否添加userId等于条件
        // - 根据name是否非空来决定是否添加name模糊匹配条件
        // - 根据introduction是否非空来决定是否添加introduction模糊匹配条件
        // - 根据picFormat是否非空来决定是否添加picFormat模糊匹配条件
        // - 根据category是否非空来决定是否添加category等于条件
        // - 根据picWidth是否非空来决定是否添加picWidth等于条件
        // - 根据picHeight是否非空来决定是否添加picHeight等于条件
        // - 根据picSize是否非空来决定是否添加picSize等于条件
        // - 根据picScale是否非空来决定是否添加picScale等于条件
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

        // 支持审核状态查询：
        // - 根据reviewStatus是否非空来决定是否添加reviewStatus等于条件
        // - 根据reviewMessage是否非空来决定是否添加reviewMessage模糊匹配条件
        // - 根据reviewerId是否非空来决定是否添加reviewerId等于条件
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // 根据spaceId是否非空来决定是否添加spaceId等于条件
        // 如果nullSpaceId为true，则添加spaceId为null的条件
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "spaceId");

        // 根据startEditTime是否非空来决定是否添加editTime大于等于条件
        // 根据endEditTime是否非空来决定是否添加editTime小于条件
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);

        queryWrapper.eq(ObjUtil.isNotEmpty(picColor), "picColor", picColor);

        // 处理标签查询：如果tags列表不为空，则对每个标签进行模糊匹配
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        // 排序处理：如果sortField不为空且sortOrder为"ascend"，则按升序排序；否则按降序排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        // 返回配置好的查询包装器
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

    /**
     * 填充图片审核参数
     * 根据登录用户的角色（管理员或非管理员）设置图片的审核状态及相关信息
     *
     * @param picture   待审核的图片对象
     * @param loginUser 当前登录的用户对象
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        // 判断当前用户是否为管理员
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

    /**
     * 通过Pexels API批量上传图片
     *
     * @param pictureUploadByBatchRequest 图片批量上传请求对象，包含搜索关键词、图片数量和名称前缀等信息
     * @param loginUser                   登录用户信息，用于记录操作者
     * @return 成功上传的图片数量
     * <p>
     * 本方法根据用户提供的搜索关键词和数量，从Pexels获取图片并上传到系统中
     * 它还允许用户指定图片名称的前缀，并且一次最多可以上传30张图片
     */
    @Override
    public int uploadPictureByPexels(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 获取搜索关键词
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        // 校验一次最多上传的图片数量
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");

        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        // 如果名称前缀为空，则使用搜索文本作为前缀
        if (StrUtil.isBlank(namePrefix)) namePrefix = searchText;

        // 根据搜索关键词、数量和名称前缀获取图片列表
        List<PictureUploadRequest> pictures = pexelsManager.getPictureByPexels(searchText, count, namePrefix);

        int uploadCount = 0;
        defaulCategory = "素材";
        // 遍历图片列表，尝试上传每一张图片
        for (PictureUploadRequest pictureUploadRequest : pictures) {

            try {
                // 调用上传图片的方法，并获取上传后的图片信息
                PictureVO pictureVO = this.uploadPicture(pictureUploadRequest.getFileUrl(), pictureUploadRequest, loginUser);
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

    /**
     * 检查用户是否有权限操作指定的图片
     * 此方法用于确定当前登录用户是否具有对特定图片执行操作的权限，根据图片所属的空间类型和用户角色来判断
     *
     * @param loginUser 登录用户信息，用于检查权限
     * @param picture   图片信息，包括图片所属空间ID和用户ID
     * @throws BusinessException 如果用户没有权限操作该图片，则抛出业务异常
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        if (loginUser == null || picture == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户或图片信息为空");
        }

        Long spaceId = picture.getSpaceId();
        Long userId = picture.getUserId();
        Long loginUserId = loginUser.getId();

        if (userId == null || loginUserId == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户ID或图片用户ID为空");
        }

        if (spaceId == null) {
            // 公共图库，仅本人或管理员可操作
            if (!userId.equals(loginUserId) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, String.format("用户 %s 无权操作公共图库中的图片 %s", loginUserId, picture.getId()));
            }
        } else {
            // 私有空间，仅空间管理员或图片所有者可操作
            if (!userId.equals(loginUserId)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, String.format("用户 %s 无权操作私有空间 %s 中的图片 %s", loginUserId, spaceId, picture.getId()));
            }
        }
    }

    /**
     * 删除图片
     *
     * @param pictureId 图片ID，用于定位要删除的图片
     * @param loginUser 登录用户，用于权限验证
     *                  <p>
     *                  此方法首先验证图片ID和登录用户的有效性，然后检查图片是否存在，
     *                  接着验证用户是否有权限删除该图片，最后从数据库中删除图片记录，
     *                  并异步删除对应的图片文件
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        // 检查图片ID是否有效
        ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
        // 检查登录用户是否为空
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 判断图片是否存在
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验用户是否有删除该图片的权限
        checkPictureAuth(loginUser, oldPicture);

        // 开启事务处理图片删除和空间更新操作
        transactionTemplate.execute(status -> {
            // 从数据库中删除图片记录
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 更新空间的使用额度，释放额度
            boolean update = spaceService.lambdaUpdate().eq(Space::getId, oldPicture.getSpaceId()).setSql("totalSize = totalSize - " + oldPicture.getPicSize()).setSql("totalCount = totalCount - 1").update();
            ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            return true;
        });
        // 异步清理文件系统中的图片文件
        this.clearPictureFile(oldPicture);
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

    /**
     * 编辑图片信息
     *
     * @param pictureEditRequest 包含要编辑的图片信息的请求对象
     * @param loginUser          当前登录的用户，用于权限验证
     *                           <p>
     *                           此方法首先将 PictureEditRequest 对象的属性复制到 Picture 对象中，然后将 tags 列表转换为 JSON 字符串，
     *                           并记录编辑时间接下来进行数据校验，确保图片信息的合法性然后根据 ID 检查图片是否存在，
     *                           如果不存在则抛出异常之后校验当前用户是否有权限编辑该图片，如果权限不足则抛出异常，
     *                           否则补充审核参数最后更新数据库中的图片信息，如果更新失败则抛出异常
     */
    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(spaceId == null || StrUtil.isBlank(picColor), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 2. 校验空间权限
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
        }
        // 3. 查询该空间下所有图片（必须有主色调）
        List<Picture> pictureList = this.lambdaQuery().eq(Picture::getSpaceId, spaceId).isNotNull(Picture::getPicColor).list();
        // 如果没有图片，直接返回空列表
        if (CollUtil.isEmpty(pictureList)) {
            return Collections.emptyList();
        }
        // 将目标颜色转为 Color 对象
        Color targetColor = Color.decode(picColor);
        // 4. 计算相似度并排序
        List<Picture> sortedPictures = pictureList.stream().sorted(Comparator.comparingDouble(picture -> {
                    // 提取图片主色调
                    String hexColor = picture.getPicColor();
                    // 没有主色调的图片放到最后
                    if (StrUtil.isBlank(hexColor)) {
                        return Double.MAX_VALUE;
                    }
                    Color pictureColor = Color.decode(hexColor);
                    // 越大越相似
                    return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
                }))
                // 取前 12 个
                .limit(12).collect(Collectors.toList());

        // 转换为 PictureVO
        return sortedPictures.stream().map(PictureVO::objToVo).collect(Collectors.toList());
    }

    /**
     * nameRule 格式：图片{序号}
     *
     * @param pictureList
     * @param nameRule
     */
    private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
        if (CollUtil.isEmpty(pictureList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }


    /**
     * 获取图片颜色列表
     * <p>
     * 本方法通过查询数据库中所有独特的图片颜色，返回一个不含重复颜色的列表
     * 使用 MyBatis-Plus 的 Wrapper 来构建查询条件，以实现高效的数据库操作
     *
     * @return 包含所有独特图片颜色的列表
     */
    @Override
    public List<String> getColorList() {
        // 使用 MyBatis-Plus 的 Wrapper 来构建查询条件
        QueryWrapper<Picture> wrapper = new QueryWrapper<>();
        // 选择 picColor 字段并去重
        wrapper.select("DISTINCT picColor");
        // 执行查询并返回结果
        return pictureMapper.selectObjs(wrapper);
    }

    /**
     * 批量编辑图片信息
     *
     * @param pictureEditByBatchRequest 包含要编辑的图片信息请求对象
     * @param loginUser                 当前登录用户
     *                                  <p>
     *                                  该方法主要用于批量更新图片的分类和标签信息确保操作的原子性，任何异常情况下进行回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        // 获取请求中的图片ID列表、空间ID、分类和标签
        List<Long> pictureIdList = pictureEditByBatchRequest.getPictureIdList();
        Long spaceId = pictureEditByBatchRequest.getSpaceId();
        String category = pictureEditByBatchRequest.getCategory();
        List<String> tags = pictureEditByBatchRequest.getTags();

        // 1. 校验参数
        ThrowUtils.throwIf(spaceId == null || CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);

        // 2. 校验空间权限
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
        }

        // 3. 查询指定图片，仅选择需要的字段
        List<Picture> pictureList = this.lambdaQuery().select(Picture::getId, Picture::getSpaceId).eq(Picture::getSpaceId, spaceId).in(Picture::getId, pictureIdList).list();


        if (pictureList.isEmpty()) {
            return;
        }

        // 4. 更新分类和标签
        pictureList.forEach(picture -> {
            if (StrUtil.isNotBlank(category)) {
                picture.setCategory(category);
            }
            if (CollUtil.isNotEmpty(tags)) {
                picture.setTags(JSONUtil.toJsonStr(tags));
            }
        });

        // 批量重命名
        String nameRule = pictureEditByBatchRequest.getNameRule();
        fillPictureWithNameRule(pictureList, nameRule);

        // 5. 批量更新
        boolean result = this.updateBatchById(pictureList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }


    /**
     * 批量编辑图片分类和标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchEditPictureMetadata(PictureBatchEditRequest request, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(request.getSpaceId() == null || CollUtil.isEmpty(request.getPictureIdList()), ErrorCode.PARAMS_ERROR, "空间ID或图片表不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR, "用户未登录");

        Long spaceId = request.getSpaceId();
        List<Long> pictureIdList = request.getPictureIdList();
        String category = request.getCategory();
        List<String> tags = request.getTags();

        // 2. 空间权限校验
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        ThrowUtils.throwIf(!loginUser.getId().equals(space.getUserId()), ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");

        // 3. 分批查询图片
        int batchSize = 100;
        List<List<Long>> partitionedPictureIds = Lists.partition(pictureIdList, batchSize);

        // 4. 使用并行流处理批次
        partitionedPictureIds.parallelStream().forEach(batchPictureIds -> {
            try {
                List<Picture> batchPictures = this.lambdaQuery().eq(Picture::getSpaceId, spaceId).in(Picture::getId, batchPictureIds).list();

                if (CollUtil.isEmpty(batchPictures)) {
                    return;
                }

                batchPictures.forEach(picture -> {
                    if (StrUtil.isNotBlank(category)) {
                        picture.setCategory(category);
                    }

                    if (tags != null) {
                        if (CollUtil.isNotEmpty(tags)) {
                            picture.setTags(JSONUtil.toJsonStr(tags));
                        } else {
                            picture.setTags(null);
                        }
                    }
                });

                String nameRule = request.getNameRule();
                if (nameRule != null) {
                    fillPictureWithNameRule(batchPictures, nameRule);
                }

                boolean updateResult = this.updateBatchById(batchPictures);
                ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "批量更新图片失败");
            } catch (Exception e) {
                log.error("批量更新图片失败", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量更新图片失败");
            }
        });
    }
}