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
  "blue-600",
  "green-500",
  "red-500",
  "yellow-500",
  "purple-400",
  "pink-500",
  "indigo-500",
  "teal-500",
];

export const getRandomColor = () => {
  return COLOR_PALETTE[Math.floor(Math.random() * COLOR_PALETTE.length)];
};

export { default as Tag } from "./Tag.vue";
export { default as TagsList } from "./TagsList.vue";
