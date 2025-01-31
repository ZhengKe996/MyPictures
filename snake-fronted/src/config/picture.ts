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
  { key: "reviewMessage", label: "Review" },
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
  NO_REVIEW = "未审核",
}

// 定义图片审核状态的枚举，以便统一审核状态码
export enum PIC_REVIEW_STATUS_ENUM {
  REVIEWING = 0, // 待审核状态
  PASS = 1, // 审核通过状态
  REJECT = 2, // 审核拒绝状态
}

// 创建一个映射，将审核状态码映射为人类可读的标签
// 使用as const确保映射的值在编译时不会改变
export const PIC_REVIEW_STATUS_MAP = {
  [PIC_REVIEW_STATUS_ENUM.REVIEWING]: "待审核",
  [PIC_REVIEW_STATUS_ENUM.PASS]: "通过",
  [PIC_REVIEW_STATUS_ENUM.REJECT]: "拒绝",
} as const;

// 生成审核状态的选项数组，用于界面展示或其他需要选项列表的场合
// 通过Object.entries遍历映射，将每个映射项转换为包含label和value的对象
export const PIC_REVIEW_STATUS_OPTIONS = Object.entries(
  PIC_REVIEW_STATUS_MAP
).map(([key, label]) => ({
  label, // 审核状态标签
  value: Number(key), // 审核状态值，转换为数字类型
}));
