import { UserControllerService as Service } from "@/generated";

/**
 * 上传用户头像
 *
 * 本函数通过POST请求上传用户头像文件到服务器
 * 选择仅使用一个参数设计，即文件对象，是因为RESTful API设计中简化客户端的调用和提高接口的通用性
 * 异步函数设计确保了在上传过程中可以执行其他任务，提高了程序的响应性
 *
 * @param file 用户头像文件，类型为File，代表要上传的文件对象
 * @returns 返回上传头像的Promise对象，包含服务器响应结果
 */
export const UploadAvatar = async (file: File) => {
  return Service.uploadUserAvatarUsingPost(file);
};

/**
 * 更新用户资料的函数
 *
 * 此函数用于让用户能够编辑和更新自己的个人信息它接受用户头像、用户名和用户简介的可选参数，
 * 并使用这些信息来更新用户资料通过调用Service对象的updateMyUserUsingPost方法来执行更新操作
 *
 * @param form 包含用户信息的对象
 * @param form.userAvatar 可选 用户的新头像URL
 * @param form.userName 可选 用户的新用户名
 * @param form.userProfile 可选 用户的新简介
 * @returns 返回Service.updateMyUserUsingPost的执行结果
 */
export const EditProfile = async (form: {
  userAvatar?: string;
  userName?: string;
  userProfile?: string;
}) => {
  console.log(form);
  return Service.updateMyUserUsingPost(form);
};
