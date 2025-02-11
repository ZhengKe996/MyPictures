import Service from "@/utils/request";

interface GithubUser {
  login: string;
  id: number;
  avatar_url: string;
  name: string;
  location: string;
  public_repos: number;
  followers: number;
  following: number;
}

export interface GithubRepo {
  id: number;
  name: string;
  full_name: string;
  html_url: string;
  description: string;
  stargazers_count: number;
  language: string;
  fork: boolean;
  created_at: string;
  updated_at: string;
}

export const GetGithubUser = async (username: string): Promise<GithubUser> => {
  return await Service.get(`https://api.github.com/users/${username}`);
};

export const GetUserRepos = async (username: string): Promise<GithubRepo[]> => {
  return await Service.get(`https://api.github.com/users/${username}/repos`);
};
