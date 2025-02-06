import ColorInput from "./ColorInput.vue";

export interface ColorInputProps {
  modelValue: string;
  position?: "top" | "bottom";
  /** 自定义颜色列表 */
  colors?: readonly string[];
  /** 是否允许自定义颜色 */
  allowCustom?: boolean;
  /** 默认颜色 */
  defaultColor?: string;
}

export interface ColorInputEmits {
  (e: "update:modelValue", value: string): void;
  /** 颜色改变事件 */
  (e: "change", value: string): void;
}

// 默认颜色列表 - 改为普通数组以允许修改
export const DEFAULT_COLORS = [
  "#f44336",
  "#e91e63",
  "#9c27b0",
  "#673ab7",
  "#3f51b5",
  "#2196f3",
  "#03a9f4",
  "#00bcd4",
  "#009688",
  "#4caf50",
  "#8bc34a",
  "#cddc39",
  "#ffeb3b",
  "#ffc107",
  "#ff9800",
  "#ff5722",
  "#795548",
  "#9e9e9e",
  "#607d8b",
  "#000000",
  "#ffffff",
  "#f1f1f1",
  "#333333",
  "#666666",
] as string[];

// 工具函数
export const isValidHexColor = (color: string | null): boolean => {
  if (!color) return false;
  return /^#[0-9A-Fa-f]{6}$/.test(color);
};

// 颜色验证和格式化
export const validateAndFormatColors = (
  colors: readonly string[]
): string[] => {
  return [...colors]
    .filter((color) => isValidHexColor(color))
    .map((color) => color.toLowerCase());
};

export type ColorInputInstance = InstanceType<typeof ColorInput>;

// 导出组件
export { default } from "./ColorInput.vue";
