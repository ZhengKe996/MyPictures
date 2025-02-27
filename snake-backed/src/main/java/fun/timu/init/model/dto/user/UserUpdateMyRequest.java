package fun.timu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class UserUpdateMyRequest implements Serializable {
    private static final long serialVersionUID = -2051713482777579151L;
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
    
}
