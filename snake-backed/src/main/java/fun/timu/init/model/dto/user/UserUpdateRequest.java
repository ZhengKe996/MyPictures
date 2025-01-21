package fun.timu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 2388113322838367910L;
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}
