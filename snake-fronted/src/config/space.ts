// 空间级别枚举
export const SPACE_LEVEL_ENUM = {
  COMMON: 0,
  PROFESSIONAL: 1,
  FLAGSHIP: 2,
} as const;

// 空间级别文本映射
export const SPACE_LEVEL_MAP: Record<number, string> = {
  0: "普通版",
  1: "专业版",
  2: "旗舰版",
};

// 空间级别选项映射
export const SPACE_LEVEL_OPTIONS = Object.keys(SPACE_LEVEL_MAP).map((key) => {
  const value = Number(key); // Convert string key to number
  return {
    label: SPACE_LEVEL_MAP[value],
    value,
  };
});

import { type SpaceVO } from "@/generated";

export type { SpaceVO as SpaceType };

import type { CustomColumn } from "./index";

export const SpaceManagerColumns: CustomColumn[] = [
  { key: "id", label: "ID" },
  { key: "spaceName", label: "Space Name" },
  { key: "spaceLevel", label: "Space Level" },
  { key: "condition", label: "Condition" },
  { key: "user", label: "User" },
  { key: "createTime", label: "CreateTime" },
  // { key: "editTime", label: "EditTime" },
  { key: "edit", label: "Edit" },
  { key: "remove", label: "Remove" },
];
