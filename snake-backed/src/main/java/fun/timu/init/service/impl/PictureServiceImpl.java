package fun.timu.init.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.exception.ThrowUtils;
import fun.timu.init.manager.FileManager;
import fun.timu.init.mapper.PictureMapper;
import fun.timu.init.model.dto.file.UploadPictureResult;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.PictureVO;
import fun.timu.init.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private FileManager fileManager;

    /**
     * 上传图片方法
     *
     * @param multipartFile        图片文件，用于上传
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
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 检查用户是否已登录，未登录则抛出无权限错误
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);

        // 用于判断是新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }

        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            // 如果图片不存在，则抛出未找到错误
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }

        // 上传图片，得到信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);

        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());

        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        // 保存或更新图片信息
        boolean result = this.saveOrUpdate(picture);
        // 如果保存失败，则抛出操作错误
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");

        // 将图片对象转换为视图对象并返回
        return PictureVO.objToVo(picture);
    }

}