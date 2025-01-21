import { UserControllerService } from "@/generated";

/**
 * 异步函数：获取当前登录用户信息
 *
 * 此函数没有输入参数
 * 返回值：await UserControllerService.getLoginUserUsingGet()的返回值
 *
 * 功能描述：
 * 通过调用UserControllerService中的getLoginUserUsingGet方法来获取当前登录用户的信息
 * 该函数使用async/await语法，确保获取用户信息操作完成后再继续执行后续代码
 */
export const GetLoginInfoUser = async () => {
  return await UserControllerService.getLoginUserUsingGet();
};

/**
 * 用户登录函数
 *
 * 本函数通过调用UserControllerService中的userLoginUsingPost方法，实现用户的登录功能
 * 主要负责将用户提交的登录信息转发给后端服务，不做具体的逻辑处理
 *
 * @param form 包含用户登录所需信息的对象
 * @param form.userAccount 用户账号，应为唯一的标识符
 * @param form.userPassword 用户设置的密码，用于账户的安全验证
 * @returns 返回一个Promise对象，包含后端服务的响应结果
 */
export const UserLogin = async (form: {
  userAccount: string;
  userPassword: string;
}) => {
  return await UserControllerService.userLoginUsingPost({
    userAccount: form.userAccount,
    userPassword: form.userPassword,
  });
};

/**
 * 用户注册函数
 *
 * 本函数通过调用UserControllerService中的userRegisterUsingPost方法，实现用户的注册功能
 * 主要负责将用户提交的注册信息转发给后端服务，不做具体的逻辑处理
 *
 * @param form 包含用户注册所需信息的对象
 * @param form.userAccount 用户账号，应为唯一的标识符
 * @param form.userPassword 用户设置的密码，用于账户的安全验证
 * @param form.checkPassword 密码确认字段，确保用户正确输入密码
 * @returns 返回一个Promise对象，包含后端服务的响应结果
 */
export const UserRegister = async (form: {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}) => {
  return await UserControllerService.userRegisterUsingPost({
    userAccount: form.userAccount,
    userPassword: form.userPassword,
    checkPassword: form.checkPassword,
  });
};

/**
 * 用户登出函数
 *
 * 本函数通过调用UserControllerService中的userLogoutUsingPost方法，实现用户的登出功能
 * 主要负责将用户的登出请求转发给后端服务，不做具体的逻辑处理
 *
 * @returns 返回一个Promise对象，包含后端服务的响应结果
 */
export const UserLogout = async () => {
  return await UserControllerService.userLogoutUsingPost();
};
