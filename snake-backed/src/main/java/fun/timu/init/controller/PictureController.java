package fun.timu.init.controller;

import fun.timu.init.annotation.AuthCheck;
import fun.timu.init.common.BaseResponse;
import fun.timu.init.common.ResultUtils;
import fun.timu.init.constant.UserConstant;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.PictureVO;
import fun.timu.init.service.PictureService;
import fun.timu.init.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        // 获取当前登录的用户信息
        User loginUser = userService.getLoginUser(request);
        // 调用服务层方法，执行图片上传逻辑
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        // 返回上传成功后的图片信息
        return ResultUtils.success(pictureVO);
    }
}
