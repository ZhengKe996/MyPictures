import { PictureControllerService as Service } from "@/generated";
import { type PictureEditType } from "@/config";

// Add this interface near the top of the file
interface UploadImageParams {
  file: Blob;
  fileUrl?: string;
  id?: string;
  picName?: string;
  spaceId?: string;
}

// Add new interface for URL upload
interface UploadImageUrlParams {
  fileUrl: string;
  id?: string;
  picName?: string;
  spaceId?: string;
}

/**
 * 异步上传图像文件。
 *
 * @param file 要上传的图像文件
 * @param params 上传参数配置
 * @param params.fileUrl 可选，文件URL
 * @param params.id 可选，图片ID，用于更新现有图片
 * @param params.picName 可选，图片名称
 * @param params.spaceId 可选，空间ID
 * @returns Promise 包含上传响应结果
 */
export const UploadImageFile = async (
  file: File,
  params: Partial<Omit<UploadImageParams, "file">> = {}
) => {
  const { fileUrl, id, picName, spaceId } = params;
  return await Service.uploadPictureUsingPost(
    file,
    fileUrl,
    id,
    picName,
    spaceId
  );
};

/**
 * 异步获取标签分类列表
 *
 * 本函数通过发送GET请求，从服务器获取所有图片标签分类信息
 *
 * @returns {Promise<any>} 返回一个Promise对象，解析后包含标签分类信息
 */
export const GetTagCategory = async () => {
  return await Service.listPictureTagCategoryUsingGet();
};

/**
 * 根据图片ID获取图片信息
 * 此函数调用了Service中的getPictureByIdUsingGet方法，以异步方式获取图片信息
 *
 * @param id 图片的唯一标识符，用于指定需要获取信息的图片
 * @returns 返回一个Promise，解析为图片信息
 */
export const GetPictureById = async (id: string) => {
  return await Service.getPictureByIdUsingGet(id);
};

/**
 * 编辑图片信息的异步函数
 *
 * 此函数通过调用Service中的editPictureUsingPost方法来编辑图片信息
 * 它封装了对服务层的调用，提供了一个更具体的接口给其他组件使用
 *
 * @param pictureEditRequest 图片编辑的请求对象，包含需要编辑的图片信息
 * @returns 返回一个Promise，解析为服务层editPictureUsingPost方法的返回值
 */
export const EditPictureInfo = async (pictureEditRequest: PictureEditType) => {
  return await Service.editPictureUsingPost(pictureEditRequest);
};

/**
 * 获取图片列表
 *
 * 此函数通过POST请求从服务端获取图片列表支持分页查询和多种缓存策略
 * 当前实现的是不使用缓存的版本，可以根据需要启用不同的缓存策略
 *
 * @param form 包含查询参数的对象
 * @param form.current 当前页码
 * @param form.pageSize 每页记录数
 * @param form.category 图片类别（可选）
 * @param form.id 图片ID（可选）
 * @param form.name 图片名称（可选）
 * @param form.userId 用户ID（可选）
 * @param form.picFormat 图片格式（可选）
 * @param form.tags 图片标签数组（可选）
 * @param form.spaceId 空间ID（可选）
 * @returns 返回一个Promise对象，解析为图片列表
 */
export const GetPictureList = async (form: {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  userId?: string;
  picFormat?: string;
  tags?: Array<string>;
  spaceId?: string;
  startEditTime?: string;
  endEditTime?: string;
}) => {
  // 不使用缓存
  return await Service.listPictureVoByPageUsingPost(form);
  // 使用分布式缓存
  // return await Service.listPictureVoByPageWithCacheUsingPost(form);
  // 使用本地缓存
  // return await Service.listPictureVoByPageWithLocalCacheUsingPost(form);
  // 使用多级缓存
  // return await Service.listPictureVoByPageWithMultilevelCacheUsingPost(form);
};

/**
 * 根据图片ID获取图片信息
 *
 * 此函数通过调用Service的服务接口，异步获取指定ID的图片信息
 * 主要用于在需要的地方通过图片ID来获取图片的详细信息，以便进行后续的操作或处理
 *
 * @param id 图片的唯一标识符，用于指定需要获取信息的图片
 * @returns 返回一个Promise，解析后提供图片信息对象
 */
export const GetPictureInfoById = async (id: string) => {
  return await Service.getPictureVoByIdUsingGet(id);
};

/**
 * 执行图片的管理员审核操作
 *
 * 此函数用于更新图片的审核状态和信息，通过发送异步请求给后端服务来完成审核流程
 * 它主要用于管理员在审核界面进行图片审核时调用
 *
 * @param form 包含审核信息的对象
 * @param form.id 图片的唯一标识符
 * @param form.reviewStatus 图片的审核状态，通常用数字表示（如0表示未审核，1表示审核通过等）
 * @param form.reviewMessage 管理员输入的审核消息或意见
 * @returns 返回后端服务的响应结果，通常包括审核是否成功的信息
 */
export const AdminReviewPicture = async (form: {
  id: string;
  reviewStatus: number;
  reviewMessage: string;
}) => {
  return await Service.doPictureReviewUsingPost(form);
};

/**
 * 通过URL上传图片文件
 *
 * @param file 图片URL地址
 * @param params 上传参数配置
 * @param params.id 可选，图片ID，用于更新现有图片
 * @param params.picName 可选，图片名称
 * @param params.spaceId 可选，空间ID
 * @returns Promise 包含上传响应结果
 */
export const UploadImageFileByUrl = async (
  fileUrl: string,
  params: Partial<Omit<UploadImageUrlParams, "fileUrl">> = {}
) => {
  const { id, picName, spaceId } = params;
  return await Service.uploadPictureByUrlUsingPost({
    fileUrl,
    id,
    picName,
    spaceId,
  });
};

/**
 * 使用必应搜索引擎进行图片上传
 *
 * 本函数通过调用Service的uploadPictureByBingUsingPost方法，实现批量上传图片的功能
 * 主要用于后台管理操作，可以根据指定的搜索文本、数量和名称前缀从必应上抓取并上传图片
 *
 * @param form 包含上传请求参数的对象
 * @param form.count 可选参数，指定希望上传的图片数量，默认为服务端配置的默认值
 * @param form.namePrefix 可选参数，上传后的图片名称前缀，默认为服务端配置的默认值
 * @param form.searchText 可选参数，用于必应搜索的文本，默认为服务端配置的默认值
 * @returns 返回上传图片的结果，具体类型依赖于Service.uploadPictureByBingUsingPost方法的返回值
 */
export const AdminBatchByBing = async (form: {
  count?: number;
  namePrefix?: string;
  searchText?: string;
}) => {
  return await Service.uploadPictureByBingUsingPost(form);
};

/**
 * 使用Pexels进行图片上传
 *
 * 本函数通过调用uploadPictureByPexelsUsingPost方法，实现批量上传图片的功能
 * 主要用于后台管理操作，可以根据指定的搜索文本、数量和名称前缀从必应上抓取并上传图片
 *
 * @param form 包含上传请求参数的对象
 * @param form.count 可选参数，指定希望上传的图片数量，默认为服务端配置的默认值
 * @param form.namePrefix 可选参数，上传后的图片名称前缀，默认为服务端配置的默认值
 * @param form.searchText 可选参数，用于必应搜索的文本，默认为服务端配置的默认值
 * @returns 返回上传图片的结果，具体类型依赖于Service.uploadPictureByPexelsUsingPost方法的返回值
 */
export const AdminBatchByPexels = async (form: {
  count?: number;
  namePrefix?: string;
  searchText?: string;
}) => {
  return await Service.uploadPictureByPexelsUsingPost(form);
};

/**
 * 根据图片ID删除图片
 *
 * 此函数通过调用Service的deletePictureUsingPost方法来删除指定ID的图片
 * 选择使用POST方法进行删除操作可能是由于考虑到URL长度限制或安全性问题
 *
 * @param id 图片的唯一标识符，用于指定哪张图片需要被删除
 * @returns 返回Service.deletePictureUsingPost的执行结果，通常是异步操作的结果
 */
export const DeletePictureById = async (id: string) => {
  return await Service.deletePictureUsingPost({ id });
};
