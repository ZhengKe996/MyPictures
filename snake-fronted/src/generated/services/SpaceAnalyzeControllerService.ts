/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_List_Space_ } from '../models/BaseResponse_List_Space_';
import type { BaseResponse_List_SpaceCategoryAnalyzeResponse_ } from '../models/BaseResponse_List_SpaceCategoryAnalyzeResponse_';
import type { BaseResponse_List_SpaceSizeAnalyzeResponse_ } from '../models/BaseResponse_List_SpaceSizeAnalyzeResponse_';
import type { BaseResponse_List_SpaceTagAnalyzeResponse_ } from '../models/BaseResponse_List_SpaceTagAnalyzeResponse_';
import type { BaseResponse_List_SpaceUserAnalyzeResponse_ } from '../models/BaseResponse_List_SpaceUserAnalyzeResponse_';
import type { BaseResponse_SpaceUsageAnalyzeResponse_ } from '../models/BaseResponse_SpaceUsageAnalyzeResponse_';
import type { SpaceCategoryAnalyzeRequest } from '../models/SpaceCategoryAnalyzeRequest';
import type { SpaceRankAnalyzeRequest } from '../models/SpaceRankAnalyzeRequest';
import type { SpaceSizeAnalyzeRequest } from '../models/SpaceSizeAnalyzeRequest';
import type { SpaceTagAnalyzeRequest } from '../models/SpaceTagAnalyzeRequest';
import type { SpaceUsageAnalyzeRequest } from '../models/SpaceUsageAnalyzeRequest';
import type { SpaceUserAnalyzeRequest } from '../models/SpaceUserAnalyzeRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SpaceAnalyzeControllerService {
    /**
     * getSpaceCategoryAnalyze
     * @param spaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest
     * @returns BaseResponse_List_SpaceCategoryAnalyzeResponse_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceCategoryAnalyzeUsingPost(
        spaceCategoryAnalyzeRequest: SpaceCategoryAnalyzeRequest,
    ): CancelablePromise<BaseResponse_List_SpaceCategoryAnalyzeResponse_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/category',
            body: spaceCategoryAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceRankAnalyze
     * @param spaceRankAnalyzeRequest spaceRankAnalyzeRequest
     * @returns BaseResponse_List_Space_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceRankAnalyzeUsingPost(
        spaceRankAnalyzeRequest: SpaceRankAnalyzeRequest,
    ): CancelablePromise<BaseResponse_List_Space_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/rank',
            body: spaceRankAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceSizeAnalyze
     * @param spaceSizeAnalyzeRequest spaceSizeAnalyzeRequest
     * @returns BaseResponse_List_SpaceSizeAnalyzeResponse_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceSizeAnalyzeUsingPost(
        spaceSizeAnalyzeRequest: SpaceSizeAnalyzeRequest,
    ): CancelablePromise<BaseResponse_List_SpaceSizeAnalyzeResponse_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/size',
            body: spaceSizeAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceTagAnalyze
     * @param spaceTagAnalyzeRequest spaceTagAnalyzeRequest
     * @returns BaseResponse_List_SpaceTagAnalyzeResponse_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceTagAnalyzeUsingPost(
        spaceTagAnalyzeRequest: SpaceTagAnalyzeRequest,
    ): CancelablePromise<BaseResponse_List_SpaceTagAnalyzeResponse_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/tag',
            body: spaceTagAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceUsageAnalyze
     * @param spaceUsageAnalyzeRequest spaceUsageAnalyzeRequest
     * @returns BaseResponse_SpaceUsageAnalyzeResponse_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceUsageAnalyzeUsingPost(
        spaceUsageAnalyzeRequest: SpaceUsageAnalyzeRequest,
    ): CancelablePromise<BaseResponse_SpaceUsageAnalyzeResponse_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/usage',
            body: spaceUsageAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getSpaceUserAnalyze
     * @param spaceUserAnalyzeRequest spaceUserAnalyzeRequest
     * @returns BaseResponse_List_SpaceUserAnalyzeResponse_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static getSpaceUserAnalyzeUsingPost(
        spaceUserAnalyzeRequest: SpaceUserAnalyzeRequest,
    ): CancelablePromise<BaseResponse_List_SpaceUserAnalyzeResponse_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/analyze/user',
            body: spaceUserAnalyzeRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
