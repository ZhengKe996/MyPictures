import { UserControllerService } from "@/generated";

/**
 * 用户登录
 * @param form
 * @returns
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
