import type { PictureVO, PictureEditRequest } from "@/generated";

export type { PictureVO as PictureType, PictureEditRequest as PictureEditType };

import type { CustomColumn } from "./index";
export const ALLCategory = "全部";

export const PictureManagerColumns: CustomColumn[] = [
  { key: "id", label: "ID" },
  { key: "picture", label: "Picture" },
  { key: "name", label: "Name" },
  { key: "introduction", label: "Introduction" },
  { key: "category", label: "Category" },
  { key: "tags", label: "Tags" },
  // { key: "picInfo", label: "Info" },
  { key: "user", label: "Author" },
  { key: "editTime", label: "EditTime" },
  { key: "detail", label: "Detail" },
  { key: "edit", label: "Edit" },
  { key: "remove", label: "Remove" },
];

export enum DefaultPictureTexts {
  UNNAMED_PICTURE = "未命名图片",
  NO_DESCRIPTION = "暂无描述",
  UNCLASSIFIED = "未分类",
  NO_TAGS = "暂无标签",
  UNKNOWN_USER = "未知用户",
  NO_CREATE_TIME = "暂无创建时间",
  NO_UPDATE_TIME = "暂无更新时间",
  NO_TIME = "暂无时间信息",
}
