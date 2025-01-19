export enum ACCESSENUM {
  NOLOGIN = "nologin",
  USER = "user",
  ADMIN = "admin",
}
export const NOLOGIN = "未登录";
/**
 * 检查用户访问权限
 *
 * 此函数用于比较用户当前的访问权限与执行某项操作所需的访问权限
 * 它根据用户是否具有所需的访问级别来决定是否允许执行操作
 *
 * @param UserAccess 当前用户的访问权限级别，可能为未登录、普通用户或管理员
 * @param NeedAccess 执行操作所需的访问权限级别，可能为不需要登录、普通用户或管理员
 * @returns 如果用户具有执行操作所需的访问权限，则返回true；否则返回false
 */
export const CheckACCESS = (UserAccess: ACCESSENUM, NeedAccess: ACCESSENUM) => {
  // 确定当前用户的访问权限，如果没有提供，则默认为未登录
  const LoginUserAccess: ACCESSENUM = UserAccess ?? ACCESSENUM.NOLOGIN;

  // 定义权限等级映射表
  const ACCESS_LEVELS: { [key in ACCESSENUM]: number } = {
    [ACCESSENUM.NOLOGIN]: 0,
    [ACCESSENUM.USER]: 1,
    [ACCESSENUM.ADMIN]: 2,
  };

  // 检查传入参数是否为有效枚举值
  if (!(NeedAccess in ACCESS_LEVELS) || !(LoginUserAccess in ACCESS_LEVELS)) {
    throw new Error("Invalid access level provided");
  }

  // 不需要登录
  if (NeedAccess === ACCESSENUM.NOLOGIN) return true;

  // 检查用户权限是否满足需求
  return ACCESS_LEVELS[LoginUserAccess] >= ACCESS_LEVELS[NeedAccess];
};
