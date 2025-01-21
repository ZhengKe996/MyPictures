package fun.timu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author zhengke
 * @date 2024年12月31日
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -2040722671668169693L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
