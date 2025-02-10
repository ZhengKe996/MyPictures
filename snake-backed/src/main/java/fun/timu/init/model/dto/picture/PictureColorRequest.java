package fun.timu.init.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data

public class PictureColorRequest implements Serializable {
    /**
     * 空间 id
     */
    private Long spaceId;
}
