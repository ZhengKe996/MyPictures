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
  return await Service.listSpaceVoByPageUsingPost(form);
};

/**
 * 根据表单数据添加或更新空间信息
 * 此函数决定是添加新空间还是更新现有空间，基于表单中是否提供了空间ID
 *
 * @param form 包含空间信息的表单对象，包括：
 * - id: 空间ID，可选，如果提供则进行更新操作，否则进行添加操作
 * - maxCount: 空间最大数量，可选
 * - maxSize: 空间最大尺寸，可选
 * - spaceLevel: 空间级别，可选
 * - spaceName: 空间名称，可选
 * @returns Promise，根据操作返回不同的结果
 */
export const AddORUpdateSpace = async (form: {
  id?: string;
  maxCount?: number;
  maxSize?: number;
  spaceLevel?: number;
  spaceName?: string;
}) => {
  // 如果表单中包含了空间ID，则执行更新空间的操作
  if (form.id) return await Service.updateSpaceUsingPost(form);
  // 如果表单中没有空间ID，则执行添加新空间的操作
  return await Service.addSpaceUsingPost(form);
};

/**
 * 根据空间ID获取空间信息
 *
 * 此函数通过发送HTTP GET请求到服务端，获取特定空间的详细信息它依赖于后端服务的实现，
 * 用于获取空间的详细信息，如名称、创建者、创建时间等
 *
 * @param spaceId 空间唯一的标识符ID
 * @returns 返回一个Promise，解析后包含空间的详细信息
 */
export const GetSpaceInfoById = async (spaceId: string) => {
  return await Service.getSpaceVoByIdUsingGet(spaceId);
};

/**
 * 根据空间ID异步删除空间。
 *
 * 该函数通过调用服务层的deleteSpaceUsingPost方法，使用POST请求方式删除指定ID的空间。
 * 封装了删除操作，使代码更具可读性和可维护性。
 *
 * @param spaceId 要删除的空间的唯一标识符。
 *        这是一个字符串，唯一标识了要删除的空间。
 * @returns 返回一个Promise对象，表示异步操作已完成。
 *          该操作没有直接的返回结果。
 */
export const DeleteSpaceById = async (spaceId: string) => {
  return await Service.deleteSpaceUsingPost({ id: spaceId });
};
