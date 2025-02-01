/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_boolean_ } from "../models/BaseResponse_boolean_";
import type { BaseResponse_Page_Picture_ } from "../models/BaseResponse_Page_Picture_";
import type { BaseResponse_Page_PictureVO_ } from "../models/BaseResponse_Page_PictureVO_";
import type { BaseResponse_Picture_ } from "../models/BaseResponse_Picture_";
import type { BaseResponse_PictureTagCategory_ } from "../models/BaseResponse_PictureTagCategory_";
import type { BaseResponse_PictureVO_ } from "../models/BaseResponse_PictureVO_";
import type { DeleteRequest } from "../models/DeleteRequest";
import type { PictureEditRequest } from "../models/PictureEditRequest";
import type { PictureQueryRequest } from "../models/PictureQueryRequest";
import type { PictureReviewRequest } from "../models/PictureReviewRequest";
import type { PictureUpdateRequest } from "../models/PictureUpdateRequest";
import type { PictureUploadRequest } from "../models/PictureUploadRequest";
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
   * listPictureTagCategory
   * @returns BaseResponse_PictureTagCategory_ OK
   * @throws ApiError
   */
  public static listPictureTagCategoryUsingGet(): CancelablePromise<BaseResponse_PictureTagCategory_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/picture/tag_category",
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
   * @returns BaseResponse_PictureVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadPictureUsingPost(
    file: Blob,
    fileUrl?: string,
    id?: string
  ): CancelablePromise<BaseResponse_PictureVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/picture/upload",
      query: {
        fileUrl: fileUrl,
        id: id,
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
