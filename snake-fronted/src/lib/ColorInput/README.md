# Color Input 颜色选择器

一个优雅的颜色选择器组件，支持键盘导航、自定义颜色、预设颜色等功能。

## 特性

- 🎨 预设常用颜色
- ⌨️ 完整键盘导航支持
- 🔍 自定义颜色输入
- 📱 响应式设计
- 🎯 支持自定义颜色列表
- ✨ 平滑动画效果
- 🌈 支持明暗主题

## 安装

```bash
# 确保已安装依赖
npm install @vueuse/core
```

## 基础用法

```vue
<template>
  <ColorInput v-model="color" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import ColorInput from "./path/to/ColorInput";

const color = ref("#2196f3");
</script>
```

## 进阶用法

### 自定义颜色列表

```vue
<template>
  <ColorInput
    v-model="color"
    :colors="customColors"
    :allow-custom="false"
    @change="handleColorChange"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import ColorInput from "./path/to/ColorInput";

const color = ref("#ff0000");
const customColors = [
  "#ff0000",
  "#00ff00",
  "#0000ff",
  "#ffff00",
  "#ff00ff",
  "#00ffff",
];

const handleColorChange = (color: string) => {
  console.log("Selected color:", color);
};
</script>
```

### 向上弹出

```vue
<template>
  <ColorInput v-model="color" position="top" />
</template>
```

## 键盘操作

- `↑` `↓` `←` `→`: 在颜色格子间移动
- `Enter` 或 `Space`: 选择当前颜色
- `Home`: 跳转到第一个颜色
- `End`: 跳转到最后一个颜色
- `Esc`: 关闭选色器

## API

### Props

| 属性名      | 类型              | 默认值    | 说明                   |
| ----------- | ----------------- | --------- | ---------------------- |
| modelValue  | string            | '#000000' | 当前选中的颜色值       |
| position    | 'top' \| 'bottom' | 'bottom'  | 弹出位置               |
| colors      | string[]          | [...]     | 自定义颜色列表         |
| allowCustom | boolean           | true      | 是否允许自定义颜色输入 |

### Events

| 事件名            | 参数            | 说明             |
| ----------------- | --------------- | ---------------- |
| update:modelValue | (value: string) | 颜色值更新时触发 |
| change            | (value: string) | 颜色变化时触发   |

### Slots

组件不提供插槽，保持简单易用的设计理念。

## TypeScript 支持

组件完整支持 TypeScript，可以导入类型定义：

```typescript
import type { ColorInputProps, ColorInputEmits } from "./path/to/ColorInput";
```

## 注意事项

1. 颜色值必须是 6 位十六进制格式，例如 `#ff0000`
2. 自定义颜色列表中的无效颜色值会被过滤
3. 使用 `position="top"` 时，确保组件上方有足够空间

## 贡献

欢迎提交 Issue 和 Pull Request！

## License

MIT License
