/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_GithubUserVO_ } from '../models/BaseResponse_GithubUserVO_';
import type { BaseResponse_List_GithubRepoVO_ } from '../models/BaseResponse_List_GithubRepoVO_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class GithubControllerService {
    /**
     * getGithubUserRepos
     * @param username username
     * @returns BaseResponse_List_GithubRepoVO_ OK
     * @throws ApiError
     */
    public static getGithubUserReposUsingGet(
        username?: string,
    ): CancelablePromise<BaseResponse_List_GithubRepoVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/github/repos',
            query: {
                'username': username,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getGithubUserInfo
     * @param username username
     * @returns BaseResponse_GithubUserVO_ OK
     * @throws ApiError
     */
    public static getGithubUserInfoUsingGet(
        username?: string,
    ): CancelablePromise<BaseResponse_GithubUserVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/github/userinfo',
            query: {
                'username': username,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
