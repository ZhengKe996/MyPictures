# SelectMenus Component

一个功能强大、支持 TypeScript 的 Vue 3 下拉选择菜单组件，具备完整的键盘导航和丰富的展示模式。

## 功能特点

- 🎨 现代化 UI 设计，基于 Tailwind CSS
- 💪 TypeScript 支持，完整的类型定义
- ⌨️ 完整的键盘导航支持
- 🎯 多种展示模式：纯文本、文本+图标、纯图标
- 🌟 流畅的动画和过渡效果
- ♿ 符合 ARIA 可访问性标准
- 📱 响应式设计

## 基础用法

```vue
<template>
  <SelectMenus
    v-model="selected"
    :options="options"
    label="选择选项"
    placeholder="请选择..."
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import SelectMenus, { type SelectOption } from "@/lib/SelectMenus";

const options: SelectOption[] = [
  { id: 1, name: "选项1", icon: "i-tabler-home" },
  { id: 2, name: "选项2", icon: "i-tabler-user" },
];

const selected = ref<SelectOption>();
</script>
```

## 展示模式示例

### 1. 文本 + 图标

```vue
<template>
  <SelectMenus
    v-model="selected"
    :options="textAndIconOptions"
    label="With Text and Icons"
  />
</template>

<script setup>
const textAndIconOptions = [
  { id: 1, name: "Dashboard", icon: "i-tabler-layout-dashboard" },
  { id: 2, name: "Settings", icon: "i-tabler-settings" },
  { id: 3, name: "Notifications", icon: "i-tabler-bell" },
];
</script>
```

### 2. 纯图标模式

```vue
<template>
  <SelectMenus
    v-model="selected"
    :options="iconOnlyOptions"
    label="Icon Only"
  />
</template>

<script setup>
const iconOnlyOptions = [
  { id: 1, icon: "i-tabler-sun" },
  { id: 2, icon: "i-tabler-moon" },
  { id: 3, icon: "i-tabler-cloud" },
];
</script>
```

### 3. 纯文本模式

```vue
<template>
  <SelectMenus
    v-model="selected"
    :options="textOnlyOptions"
    label="Text Only"
  />
</template>

<script setup>
const textOnlyOptions = [
  { id: 1, name: "Small" },
  { id: 2, name: "Medium" },
  { id: 3, name: "Large" },
];
</script>
```

## 类型定义

```typescript
interface SelectOption {
  id: number | string; // 唯一标识
  name?: string; // 显示文本（可选）
  icon?: string; // 图标类名（可选）
  avatar?: string; // 头像URL（可选）
  description?: string; // 描述文本（可选）
  [key: string]: any; // 其他自定义属性
}

interface SelectMenuProps {
  modelValue?: SelectOption;
  options: SelectOption[];
  label?: string;
  placeholder?: string;
}
```

## API 参考

### Props

| 属性名      | 类型             | 必填 | 默认值 | 描述               |
| ----------- | ---------------- | ---- | ------ | ------------------ |
| modelValue  | `SelectOption`   | 否   | -      | 当前选中的选项     |
| options     | `SelectOption[]` | 是   | -      | 选项列表           |
| label       | `string`         | 否   | -      | 下拉菜单标签       |
| placeholder | `string`         | 否   | -      | 未选择时的占位文本 |

### 组合式 API

组件提供了`useSelectMenu`组合式函数，用于自定义实现：

```typescript
import { useSelectMenu } from "@/lib/SelectMenus";

const {
  isOpen,
  highlightedIndex,
  containerRef,
  // ...其他返回值
} = useSelectMenu(props, emit);
```

### 事件

| 事件名            | 参数                    | 描述           |
| ----------------- | ----------------------- | -------------- |
| update:modelValue | `(value: SelectOption)` | 选项改变时触发 |

### 键盘快捷键

| 按键    | 功能                   |
| ------- | ---------------------- |
| `↑`     | 选择上一个选项         |
| `↓`     | 选择下一个选项         |
| `Enter` | 选择当前高亮的选项     |
| `Space` | 选择当前高亮的选项     |
| `Esc`   | 关闭下拉菜单           |
| `Tab`   | 关闭下拉菜单并移动焦点 |

## 注意事项

1. 图标使用：

   - 确保项目中已安装并配置了相应的图标库
   - icon 属性值需要与实际图标类名匹配

2. 可访问性：

   - 组件已内置 ARIA 属性支持
   - 建议总是提供有意义的 label 属性

3. 性能优化：
   - 大量选项时建议使用虚拟滚动
   - 避免频繁更新 options prop

## 样式定制

组件使用 Tailwind CSS 构建，可通过以下方式自定义样式：

1. 修改 Tailwind 配置：

```js
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: {
          50: "#f5f7ff",
          // ...自定义颜色
        },
      },
    },
  },
};
```

2. 使用 class 属性覆盖默认样式
3. 通过 CSS 变量调整主题色

## 贡献指南

1. Fork 本仓库
2. 创建特性分支
3. 提交改动
4. 发起 Pull Request

## License

MIT
