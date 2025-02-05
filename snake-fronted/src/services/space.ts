import { SpaceControllerService as Service } from "@/generated";
/**
 * 获取空间列表
 *
 * 该函数用于获取用户的空间列表，可以根据用户ID、空间级别、空间名称等条件进行筛选
 * 主要用于展示用户拥有的或感兴趣的空间信息
 *
 * @param form 包含筛选条件的对象
 * @param form.current 当前页码，用于分页查询
 * @param form.pageSize 每页显示的数量，用于分页查询
 * @param form.userId 可选参数，指定用户ID以获取特定用户的空间列表
 * @param form.spaceLevel 可选参数，指定空间级别以筛选结果
 * @param form.spaceName 可选参数，指定空间名称以模糊匹配空间
 * @returns 返回一个Promise对象，解析后包含空间列表和总数量等信息
 */
export const GetSpaceList = async (form: {
  current: number;
  pageSize: number;
  userId?: number;
  spaceLevel?: number;
  spaceName?: string;
}) => {
  return await Service.listSpaceByPageUsingPost(form);
};
