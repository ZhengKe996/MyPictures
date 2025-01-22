package fun.timu.init.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.timu.init.model.dto.picture.PictureUploadRequest;
import fun.timu.init.model.entity.Picture;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

}
