// 基础类型定义
export type ItemType = "link" | "button" | "divider";
export type DropdownSize = "sm" | "md" | "lg";
export type DropdownPlacement = "top" | "bottom" | "left" | "right";

// 下拉菜单项接口
export interface DropdownItem {
  id: string | number;
  type: ItemType;
  label?: string;
  href?: string;
  icon?: string;
  endIcon?: string;
  iconClass?: string;
  disabled?: boolean;
  danger?: boolean;
  onClick?: () => void;
}

// 组件属性接口
export interface DropdownProps {
  label: string;
  items: DropdownItem[];
  buttonClass?: string;
  iconClass?: string;
  triggerIcon?: string;
  size?: DropdownSize;
  placement?: DropdownPlacement;
}

// 组件事件类型
export interface DropdownEvents {
  onSelect: (item: DropdownItem) => void;
  onOpen?: () => void;
  onClose?: () => void;
}

// 样式配置
export const DROPDOWN_CONFIGS = {
  sizeClasses: {
    sm: "px-2.5 py-1.5 text-xs",
    md: "px-3 py-2 text-sm",
    lg: "px-4 py-2 text-base",
  },

  dropdownSizeClasses: {
    sm: "min-w-[160px] max-w-[200px]",
    md: "min-w-[200px] max-w-[240px]",
    lg: "min-w-[240px] max-w-[320px]",
  },

  itemSizeClasses: {
    sm: "py-1.5 text-xs",
    md: "py-2 text-sm",
    lg: "py-2.5 text-base",
  },

  iconSizeClasses: {
    trigger: {
      sm: "size-3",
      md: "size-4",
      lg: "size-5",
    },
    item: {
      sm: "size-3",
      md: "size-4",
      lg: "size-4",
    },
  },
} as const;

// 导出组件
import Dropdowns from "./Dropdowns.vue";
export default Dropdowns;

// 导出组件实例类型
export type DropdownInstance = InstanceType<typeof Dropdowns>;
