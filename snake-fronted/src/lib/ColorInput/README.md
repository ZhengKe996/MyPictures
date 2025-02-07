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

## 使用示例

### 基础用法

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

### 不同颜色格式输出

```vue
<template>
  <div class="space-y-4">
    <!-- 输出十六进制格式 -->
    <ColorInput v-model="hexColor" :color-options="{ outputFormat: 'hex' }" />

    <!-- 输出 0x 格式 -->
    <ColorInput v-model="oxColor" :color-options="{ outputFormat: '0x' }" />

    <!-- 输出 RGB 格式 -->
    <ColorInput v-model="rgbColor" :color-options="{ outputFormat: 'rgb' }" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import ColorInput from "./path/to/ColorInput";
import type { ColorFormat } from "./path/to/ColorInput";

const hexColor = ref("#2196f3");
const oxColor = ref("0x2196f3");
const rgbColor = ref("rgb(33, 150, 243)");
</script>
```

### 自定义颜色列表

```vue
<template>
  <ColorInput
    v-model="color"
    :colors="customColors"
    :color-options="{ outputFormat: '0x' }"
    @change="handleColorChange"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import ColorInput from "./path/to/ColorInput";

const color = ref("0xff0000");
const customColors = [
  "0xff0000",
  "0x00ff00",
  "0x0000ff",
  "#ffff00",
  "rgb(255, 0, 255)",
  "0x00ffff",
];

const handleColorChange = (color: string) => {
  console.log("Selected color:", color); // 将输出 0x 格式的颜色值
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

| 属性名       | 类型                             | 默认值                  | 说明                   |
| ------------ | -------------------------------- | ----------------------- | ---------------------- |
| modelValue   | string                           | '#000000'               | 当前选中的颜色值       |
| position     | 'top' \| 'bottom'                | 'bottom'                | 弹出位置               |
| colors       | string[]                         | [...]                   | 自定义颜色列表         |
| allowCustom  | boolean                          | true                    | 是否允许自定义颜色输入 |
| outputFormat | 'hex' \| 'rgb' \| '0x' \| 'rgba' | 'hex'                   | 输出的颜色格式         |
| colorOptions | ColorUtilsOptions                | { outputFormat: 'hex' } | 颜色格式化选项         |

### ColorUtilsOptions

```typescript
interface ColorUtilsOptions {
  /** 输出的颜色格式 */
  outputFormat: "hex" | "rgb" | "0x" | "rgba";
  /** 是否包含 alpha 通道 */
  includeAlpha?: boolean;
}
```

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

## 支持的颜色格式

组件支持以下格式的颜色输入：

- Hex 格式：`#ff0000`
- 0x 格式：`0xff0000`
- RGB 格式：`rgb(255, 0, 0)`

所有格式都会根据 `outputFormat` 配置转换为指定的输出格式。

## 注意事项

1. 颜色值会自动转换为配置的输出格式
2. 自定义颜色列表支持混合格式的颜色值
3. 使用 `0x` 格式时，确保输入完整的 6 位颜色值
4. RGB 格式支持空格，如 `rgb(255, 0, 0)` 或 `rgb(255,0,0)`

## 贡献

欢迎提交 Issue 和 Pull Request！

## License

MIT License
