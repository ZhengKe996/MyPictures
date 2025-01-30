export type TagVariant =
  | "primary"
  | "secondary"
  | "success"
  | "danger"
  | "warning"
  | "info"
  | "light"
  | "dark";

export interface Tag {
  id: string | number;
  name: string;
}

export interface EnhancedTag extends Tag {
  variant?: TagVariant;
  color?: string;
}

export interface TagProps {
  text: string;
  variant?: TagVariant;
  customColor?: string;
  clickable?: boolean;
  size?: "sm" | "md" | "lg";
}

const COLOR_PALETTE = [
  "blue-5", // 基础蓝色
  "red-5", // 基础红色
  "green-5", // 基础绿色
  "cyan-5", // 基础青色（替换 orange-5）
  "gray-5", // 基础灰色
  "yellow-5", // 基础黄色
];

export const getRandomColor = () => {
  return COLOR_PALETTE[Math.floor(Math.random() * COLOR_PALETTE.length)];
};

export { default as Tag } from "./Tag.vue";
export { default as TagsList } from "./TagsList.vue";
