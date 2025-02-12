import { GithubControllerService as Service } from "@/generated";

import type { GithubUserVO, GithubRepoVO } from "@/generated";
export type { GithubUserVO as GithubUser, GithubRepoVO as GithubRepo };
export const GetGithubUser = async (username: string) => {
  return await Service.getGithubUserInfoUsingGet(username);
};

export const GetUserRepos = async (username: string) => {
  return await Service.getGithubUserReposUsingGet(username);
};
