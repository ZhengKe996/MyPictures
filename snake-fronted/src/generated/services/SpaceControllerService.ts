/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_boolean_ } from '../models/BaseResponse_boolean_';
import type { BaseResponse_List_SpaceLevel_ } from '../models/BaseResponse_List_SpaceLevel_';
import type { BaseResponse_long_ } from '../models/BaseResponse_long_';
import type { BaseResponse_Page_Space_ } from '../models/BaseResponse_Page_Space_';
import type { BaseResponse_Page_SpaceVO_ } from '../models/BaseResponse_Page_SpaceVO_';
import type { BaseResponse_Space_ } from '../models/BaseResponse_Space_';
import type { BaseResponse_SpaceVO_ } from '../models/BaseResponse_SpaceVO_';
import type { DeleteRequest } from '../models/DeleteRequest';
import type { SpaceAddRequest } from '../models/SpaceAddRequest';
import type { SpaceEditRequest } from '../models/SpaceEditRequest';
import type { SpaceQueryRequest } from '../models/SpaceQueryRequest';
import type { SpaceUpdateRequest } from '../models/SpaceUpdateRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SpaceControllerService {
    /**
     * addSpace
     * @param spaceAddRequest spaceAddRequest
     * @returns BaseResponse_long_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static addSpaceUsingPost(
        spaceAddRequest: SpaceAddRequest,
    ): CancelablePromise<BaseResponse_long_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/add',
            body: spaceAddRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * deleteSpace
     * @param deleteRequest deleteRequest
     * @returns BaseResponse_boolean_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static deleteSpaceUsingPost(
        deleteRequest: DeleteRequest,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/delete',
            body: deleteRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * editSpace
     * @param spaceEditRequest spaceEditRequest
     * @returns BaseResponse_boolean_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static editSpaceUsingPost(
        spaceEditRequest: SpaceEditRequest,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/edit',
            body: spaceEditRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceById
     * @param id id
     * @returns BaseResponse_Space_ OK
     * @throws ApiError
     */
    public static getSpaceByIdUsingGet(
        id?: number,
    ): CancelablePromise<BaseResponse_Space_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/space/get',
            query: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceVOById
     * @param id id
     * @returns BaseResponse_SpaceVO_ OK
     * @throws ApiError
     */
    public static getSpaceVoByIdUsingGet(
        id?: number,
    ): CancelablePromise<BaseResponse_SpaceVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/space/get/vo',
            query: {
                'id': id,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listSpaceLevel
     * @returns BaseResponse_List_SpaceLevel_ OK
     * @throws ApiError
     */
    public static listSpaceLevelUsingGet(): CancelablePromise<BaseResponse_List_SpaceLevel_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/space/list/level',
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listSpaceByPage
     * @param spaceQueryRequest spaceQueryRequest
     * @returns BaseResponse_Page_Space_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static listSpaceByPageUsingPost(
        spaceQueryRequest: SpaceQueryRequest,
    ): CancelablePromise<BaseResponse_Page_Space_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/list/page',
            body: spaceQueryRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * listSpaceVOByPage
     * @param spaceQueryRequest spaceQueryRequest
     * @returns BaseResponse_Page_SpaceVO_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static listSpaceVoByPageUsingPost(
        spaceQueryRequest: SpaceQueryRequest,
    ): CancelablePromise<BaseResponse_Page_SpaceVO_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/list/page/vo',
            body: spaceQueryRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * updateSpace
     * @param spaceUpdateRequest spaceUpdateRequest
     * @returns BaseResponse_boolean_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static updateSpaceUsingPost(
        spaceUpdateRequest: SpaceUpdateRequest,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/space/update',
            body: spaceUpdateRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
