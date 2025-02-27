package fun.timu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = 6179308909127732737L;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 用户简介
     */
    private String userProfile;
}
