package fun.timu.init.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}