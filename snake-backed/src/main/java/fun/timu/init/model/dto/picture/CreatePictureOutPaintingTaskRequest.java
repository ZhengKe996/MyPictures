package fun.timu.init.model.dto.picture;

import fun.timu.init.api.aliYunAi.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;


/**
 * 创建扩图任务请求
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
