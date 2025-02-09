/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_boolean_ } from "../models/BaseResponse_boolean_";
import type { BaseResponse_CreateOutPaintingTaskResponse_ } from "../models/BaseResponse_CreateOutPaintingTaskResponse_";
import type { BaseResponse_GetOutPaintingTaskResponse_ } from "../models/BaseResponse_GetOutPaintingTaskResponse_";
import type { BaseResponse_int_ } from "../models/BaseResponse_int_";
import type { BaseResponse_List_PictureVO_ } from "../models/BaseResponse_List_PictureVO_";
import type { BaseResponse_Page_Picture_ } from "../models/BaseResponse_Page_Picture_";
import type { BaseResponse_Page_PictureVO_ } from "../models/BaseResponse_Page_PictureVO_";
import type { BaseResponse_Picture_ } from "../models/BaseResponse_Picture_";
import type { BaseResponse_PictureTagCategory_ } from "../models/BaseResponse_PictureTagCategory_";
import type { BaseResponse_PictureVO_ } from "../models/BaseResponse_PictureVO_";
import type { CreatePictureOutPaintingTaskRequest } from "../models/CreatePictureOutPaintingTaskRequest";
import type { DeleteRequest } from "../models/DeleteRequest";
import type { PictureBatchEditRequest } from "../models/PictureBatchEditRequest";
import type { PictureEditByBatchRequest } from "../models/PictureEditByBatchRequest";
import type { PictureEditRequest } from "../models/PictureEditRequest";
import type { PictureQueryRequest } from "../models/PictureQueryRequest";
import type { PictureReviewRequest } from "../models/PictureReviewRequest";
import type { PictureUpdateRequest } from "../models/PictureUpdateRequest";
import type { PictureUploadByBatchRequest } from "../models/PictureUploadByBatchRequest";
import type { PictureUploadRequest } from "../models/PictureUploadRequest";
import type { SearchPictureByColorRequest } from "../models/SearchPictureByColorRequest";
import type { CancelablePromise } from "../core/CancelablePromise";
import { OpenAPI } from "../core/OpenAPI";
import { request as __request } from "../core/request";
export class PictureControllerService {
  /**
   * deletePicture
   * @param deleteRequest deleteRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static deletePictureUsingPost(
    deleteRequest: DeleteRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/delete",
      body: deleteRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * editPicture
   * @param pictureEditRequest pictureEditRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static editPictureUsingPost(
    pictureEditRequest: PictureEditRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/edit",
      body: pictureEditRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * editPictureByBatch
   * @param pictureEditByBatchRequest pictureEditByBatchRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static editPictureByBatchUsingPost(
    pictureEditByBatchRequest: PictureEditByBatchRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/edit/batch",
      body: pictureEditByBatchRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * editPictureByBatchParallel
   * @param pictureBatchEditRequest pictureBatchEditRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static editPictureByBatchParallelUsingPost(
    pictureBatchEditRequest: PictureBatchEditRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/edit/batch/parallel",
      body: pictureBatchEditRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * getPictureById
   * @param id id
   * @returns BaseResponse_Picture_ OK
   * @throws ApiError
   */
  public static getPictureByIdUsingGet(
    id?: string
  ): CancelablePromise<BaseResponse_Picture_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/picture/get",
      query: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * getPictureVOById
   * @param id id
   * @returns BaseResponse_PictureVO_ OK
   * @throws ApiError
   */
  public static getPictureVoByIdUsingGet(
    id?: string
  ): CancelablePromise<BaseResponse_PictureVO_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/picture/get/vo",
      query: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * listPictureByPage
   * @param pictureQueryRequest pictureQueryRequest
   * @returns BaseResponse_Page_Picture_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listPictureByPageUsingPost(
    pictureQueryRequest: PictureQueryRequest
  ): CancelablePromise<BaseResponse_Page_Picture_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/list/page",
      body: pictureQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * listPictureVOByPage
   * @param pictureQueryRequest pictureQueryRequest
   * @returns BaseResponse_Page_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listPictureVoByPageUsingPost(
    pictureQueryRequest: PictureQueryRequest
  ): CancelablePromise<BaseResponse_Page_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/list/page/vo",
      body: pictureQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * @deprecated
   * listPictureVOByPageWithCache
   * @param pictureQueryRequest pictureQueryRequest
   * @returns BaseResponse_Page_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listPictureVoByPageWithCacheUsingPost(
    pictureQueryRequest: PictureQueryRequest
  ): CancelablePromise<BaseResponse_Page_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/list/page/vo/cache",
      body: pictureQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * @deprecated
   * listPictureVOByPageWithLocalCache
   * @param pictureQueryRequest pictureQueryRequest
   * @returns BaseResponse_Page_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listPictureVoByPageWithLocalCacheUsingPost(
    pictureQueryRequest: PictureQueryRequest
  ): CancelablePromise<BaseResponse_Page_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/list/page/vo/local_cache",
      body: pictureQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * @deprecated
   * listPictureVOByPageWithMultilevelCache
   * @param pictureQueryRequest pictureQueryRequest
   * @returns BaseResponse_Page_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listPictureVoByPageWithMultilevelCacheUsingPost(
    pictureQueryRequest: PictureQueryRequest
  ): CancelablePromise<BaseResponse_Page_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/list/page/vo/multilevel_cache",
      body: pictureQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * createPictureOutPaintingTask
   * @param createPictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest
   * @returns BaseResponse_CreateOutPaintingTaskResponse_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static createPictureOutPaintingTaskUsingPost(
    createPictureOutPaintingTaskRequest: CreatePictureOutPaintingTaskRequest
  ): CancelablePromise<BaseResponse_CreateOutPaintingTaskResponse_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/out_painting/create_task",
      body: createPictureOutPaintingTaskRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * getPictureOutPaintingTask
   * @param taskId taskId
   * @returns BaseResponse_GetOutPaintingTaskResponse_ OK
   * @throws ApiError
   */
  public static getPictureOutPaintingTaskUsingGet(
    taskId?: string
  ): CancelablePromise<BaseResponse_GetOutPaintingTaskResponse_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/picture/out_painting/get_task",
      query: {
        taskId: taskId,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * doPictureReview
   * @param pictureReviewRequest pictureReviewRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static doPictureReviewUsingPost(
    pictureReviewRequest: PictureReviewRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/review",
      body: pictureReviewRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * searchPictureByColor
   * @param searchPictureByColorRequest searchPictureByColorRequest
   * @returns BaseResponse_List_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static searchPictureByColorUsingPost(
    searchPictureByColorRequest: SearchPictureByColorRequest
  ): CancelablePromise<BaseResponse_List_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/search/color",
      body: searchPictureByColorRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * listPictureTagCategory
   * @returns BaseResponse_PictureTagCategory_ OK
   * @throws ApiError
   */
  public static listPictureTagCategoryUsingGet(): CancelablePromise<BaseResponse_PictureTagCategory_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/picture/tag_category_color",
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * updatePicture
   * @param pictureUpdateRequest pictureUpdateRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static updatePictureUsingPost(
    pictureUpdateRequest: PictureUpdateRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/update",
      body: pictureUpdateRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * uploadPicture
   * @param file file
   * @param fileUrl
   * @param id
   * @param picColor
   * @param picName
   * @param spaceId
   * @returns BaseResponse_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadPictureUsingPost(
    file: Blob,
    fileUrl?: string,
    id?: string,
    picColor?: string,
    picName?: string,
    spaceId?: string
  ): CancelablePromise<BaseResponse_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/upload",
      query: {
        fileUrl: fileUrl,
        id: id,
        picColor: picColor,
        picName: picName,
        spaceId: spaceId,
      },
      formData: {
        file: file,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * uploadPictureByBing
   * @param pictureUploadByBatchRequest pictureUploadByBatchRequest
   * @returns BaseResponse_int_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadPictureByBingUsingPost(
    pictureUploadByBatchRequest: PictureUploadByBatchRequest
  ): CancelablePromise<BaseResponse_int_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/upload/bing",
      body: pictureUploadByBatchRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * uploadPictureByPexels
   * @param pictureUploadByBatchRequest pictureUploadByBatchRequest
   * @returns BaseResponse_int_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadPictureByPexelsUsingPost(
    pictureUploadByBatchRequest: PictureUploadByBatchRequest
  ): CancelablePromise<BaseResponse_int_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/upload/pexels",
      body: pictureUploadByBatchRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
  /**
   * uploadPictureByUrl
   * @param pictureUploadRequest pictureUploadRequest
   * @returns BaseResponse_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadPictureByUrlUsingPost(
    pictureUploadRequest: PictureUploadRequest
  ): CancelablePromise<BaseResponse_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/upload/url",
      body: pictureUploadRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
}
