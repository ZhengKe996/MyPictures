package fun.timu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 5885536297872332641L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
