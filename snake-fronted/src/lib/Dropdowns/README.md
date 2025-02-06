# Dropdown 下拉菜单组件

一个功能完整的下拉菜单组件，支持多种尺寸、图标、禁用状态等特性。

## 特性

- 🎨 支持三种尺寸（sm/md/lg）
- 🖼️ 支持图标（开始/结束图标）
- ⌨️ 完整的键盘导航
- ♿ 良好的可访问性支持
- 🎯 支持禁用选项
- 🚫 支持分割线
- ⚠️ 支持危险操作样式
- 📱 响应式设计

## 安装

```bash
# 确保项目中已安装依赖
npm install @vueuse/core
```

## 使用

```vue
<script setup lang="ts">
import Dropdown, { type DropdownItem } from "./lib/Dropdowns";

const items: DropdownItem[] = [
  {
    id: 1,
    type: "button",
    label: "Edit",
    icon: "i-tabler-edit",
  },
  // ...更多选项
];
</script>

<template>
  <Dropdown label="Options" :items="items" @select="handleSelect" />
</template>
```

## Props

| 属性        | 类型                                   | 默认值   | 说明             |
| ----------- | -------------------------------------- | -------- | ---------------- |
| label       | string                                 | -        | 按钮文本         |
| items       | DropdownItem[]                         | -        | 下拉菜单项       |
| size        | 'sm' \| 'md' \| 'lg'                   | 'md'     | 组件大小         |
| triggerIcon | string                                 | -        | 触发器图标类名   |
| buttonClass | string                                 | -        | 自定义按钮样式类 |
| iconClass   | string                                 | -        | 自定义图标样式类 |
| placement   | 'top' \| 'bottom' \| 'left' \| 'right' | 'bottom' | 下拉菜单位置     |

## 菜单项配置

```typescript
interface DropdownItem {
  id: string | number;
  type: "link" | "button" | "divider";
  label?: string;
  href?: string;
  icon?: string;
  endIcon?: string;
  iconClass?: string;
  disabled?: boolean;
  danger?: boolean;
  onClick?: () => void;
}
```

## Events

| 事件名 | 参数                 | 说明               |
| ------ | -------------------- | ------------------ |
| select | (item: DropdownItem) | 选择菜单项时触发   |
| open   | -                    | 打开下拉菜单时触发 |
| close  | -                    | 关闭下拉菜单时触发 |

## 示例

### 基础用法

```vue
<Dropdown
  label="Options"
  :items="[
    { id: 1, type: 'button', label: 'Edit' },
    { id: 2, type: 'button', label: 'Delete' },
  ]"
/>
```

### 带图标

```vue
<Dropdown
  label="Settings"
  trigger-icon="i-tabler-settings"
  :items="[
    {
      id: 1,
      type: 'button',
      label: 'Edit',
      icon: 'i-tabler-edit',
    },
    {
      id: 2,
      type: 'button',
      label: 'Share',
      icon: 'i-tabler-share',
      endIcon: 'i-tabler-external-link',
    },
  ]"
/>
```

### 不同尺寸

```vue
<Dropdown size="sm" ... />
<Dropdown size="md" ... />
<Dropdown size="lg" ... />
```

### 复杂示例

```vue
<Dropdown
  label="User Account"
  trigger-icon="i-tabler-user"
  :items="[
    {
      id: 1,
      type: 'button',
      label: 'Profile',
      icon: 'i-tabler-user-circle',
    },
    { id: 2, type: 'divider' },
    {
      id: 3,
      type: 'button',
      label: 'Delete Account',
      icon: 'i-tabler-trash',
      danger: true,
    },
  ]"
/>
```

## 注意事项

1. 确保提供唯一的 `id` 给每个菜单项
2. 使用 `divider` 类型可以添加分割线
3. `danger` 属性会使菜单项呈现警示样式
4. 可以通过 `disabled` 属性禁用特定菜单项

## 键盘支持

- `↑/↓`: 导航菜单项
- `Enter/Space`: 选择菜单项
- `Esc`: 关闭下拉菜单
