import defaultImageUrl from "@/assets/defaultImage.png";
import defaultUserImageUrl from "@/assets//defaultAvatar.png";
export const DefaultUserAvatar = defaultUserImageUrl;
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
