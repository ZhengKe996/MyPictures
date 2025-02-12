import defaultImageUrl from "@/assets/defaultImage.png";

export const DefaultUserAvatar = "/src/assets/defaultAvatar.png";
export const DefaultImage = defaultImageUrl;
export const GitHubUserName = "Zhengke0110";
export enum LayoutMenu {
  BasicLayout = "BasicLayout",
  UserLayout = "UserLayout",
}
export type CustomColumn = {
  key: string;
  label: string;
};

export const config = {
  BASE_URL: "",
};

export * from "./user";
export * from "./picture";
export * from "./space";
export * from "./analyze";
