import { PictureControllerService } from "@/generated";
import { type PictureEditType } from "@/config";

/**
 * 异步上传图像文件。
 *
 * 该函数使用 `PictureControllerService.uploadPictureUsingPost` 方法来上传图像。注释描述了函数的目的、参数和返回值类型，
 * 而不涉及底层实现细节。
 *
 * @param file 要上传的图像文件。必须是 File 对象的实例。
 * @param id 可选。当id不为空时，为更新图片信息，否则为上传图片。
 * @returns 返回一个 Promise，在图像成功上传后解析。具体的返回类型取决于 `PictureControllerService.uploadPictureUsingPost` 方法的实现。
 */
export const UploadImageFile = async (file: File, id?: string) => {
  return await PictureControllerService.uploadPictureUsingPost(file, id);
};

/**
 * 异步获取标签分类列表
 *
 * 本函数通过发送GET请求，从服务器获取所有图片标签分类信息
 *
 * @returns {Promise<any>} 返回一个Promise对象，解析后包含标签分类信息
 */
export const GetTagCategory = async () => {
  return await PictureControllerService.listPictureTagCategoryUsingGet();
};

/**
 * 根据图片ID获取图片信息
 * 此函数调用了PictureControllerService中的getPictureByIdUsingGet方法，以异步方式获取图片信息
 *
 * @param id 图片的唯一标识符，用于指定需要获取信息的图片
 * @returns 返回一个Promise，解析为图片信息
 */
export const GetPictureById = async (id: string) => {
  return await PictureControllerService.getPictureByIdUsingGet(id);
};

/**
 * 编辑图片信息的异步函数
 *
 * 此函数通过调用PictureControllerService中的editPictureUsingPost方法来编辑图片信息
 * 它封装了对服务层的调用，提供了一个更具体的接口给其他组件使用
 *
 * @param pictureEditRequest 图片编辑的请求对象，包含需要编辑的图片信息
 * @returns 返回一个Promise，解析为服务层editPictureUsingPost方法的返回值
 */
export const EditPictureInfo = async (pictureEditRequest: PictureEditType) => {
  return await PictureControllerService.editPictureUsingPost(
    pictureEditRequest
  );
};

/**
 * 获取图片列表(缓存)
 *
 * 此函数通过调用PictureControllerService的listPictureVoByPageUsingPost方法来获取图片列表
 * 它接受一个包含多种可选参数的对象，以满足不同条件下的图片查询需求
 *
 * @param form 包含分页和查询条件的表单对象
 * @param form.current 当前页码，用于分页查询
 * @param form.pageSize 每页记录数，与current一起使用进行分页
 * @param form.category 可选参数，图片类别，用于筛选特定类别的图片
 * @param form.id 可选参数，图片ID，用于查询特定的图片
 * @param form.name 可选参数，图片名称，用于模糊匹配图片名称
 * @param form.userId 可选参数，用户ID，用于查询特定用户相关的图片
 * @param form.picFormat 可选参数，图片格式，用于筛选特定格式的图片
 * @param form.tags 可选参数，标签数组，用于筛选包含特定标签的图片
 * @returns 返回一个Promise，解析为图片列表
 */
export const GetPictureList = async (form: {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  userId?: number;
  picFormat?: string;
  tags?: Array<string>;
}) => {
  // return await PictureControllerService.listPictureVoByPageUsingPost(form);
  return await PictureControllerService.listPictureVoByPageWithCacheUsingPost(
    form
  );
};

/**
 * 根据图片ID获取图片信息
 *
 * 此函数通过调用PictureControllerService的服务接口，异步获取指定ID的图片信息
 * 主要用于在需要的地方通过图片ID来获取图片的详细信息，以便进行后续的操作或处理
 *
 * @param id 图片的唯一标识符，用于指定需要获取信息的图片
 * @returns 返回一个Promise，解析后提供图片信息对象
 */
export const GetPictureInfoById = async (id: string) => {
  return await PictureControllerService.getPictureVoByIdUsingGet(id);
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
  return await PictureControllerService.doPictureReviewUsingPost(form);
};

/**
 * 通过URL上传图片文件
 *
 * 此函数用于通过URL地址上传图片到服务器它接受一个包含图片URL的对象作为参数，
 * 并可选地接受一个ID参数，用于指定与上传图片相关的实体ID
 *
 * @param form - 包含上传图片所需信息的对象
 * @param form.fileUrl - 图片的URL地址，这是上传图片的来源
 * @param form.id - （可选）与上传图片相关的实体ID，用于在服务器端关联图片和实体
 * @returns 返回一个Promise，表示上传图片的异步操作
 */
export const UploadImageFileByUrl = async (form: {
  fileUrl: string;
  id?: string;
}) => {
  return await PictureControllerService.uploadPictureByUrlUsingPost(form);
};

/**
 * 使用必应搜索引擎进行图片上传
 *
 * 本函数通过调用PictureControllerService的uploadPictureByBingUsingPost方法，实现批量上传图片的功能
 * 主要用于后台管理操作，可以根据指定的搜索文本、数量和名称前缀从必应上抓取并上传图片
 *
 * @param form 包含上传请求参数的对象
 * @param form.count 可选参数，指定希望上传的图片数量，默认为服务端配置的默认值
 * @param form.namePrefix 可选参数，上传后的图片名称前缀，默认为服务端配置的默认值
 * @param form.searchText 可选参数，用于必应搜索的文本，默认为服务端配置的默认值
 * @returns 返回上传图片的结果，具体类型依赖于PictureControllerService.uploadPictureByBingUsingPost方法的返回值
 */
export const AdminBatchByBing = async (form: {
  count?: number;
  namePrefix?: string;
  searchText?: string;
}) => {
  return await PictureControllerService.uploadPictureByBingUsingPost(form);
};
