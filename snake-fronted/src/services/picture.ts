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
  return await PictureControllerService.listPictureVoByPageUsingPost(form);
};
