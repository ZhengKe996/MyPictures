# RoundProgress 环形进度条组件

一个基于 Vue 3 + TypeScript + Tailwind CSS 的现代化环形进度条组件，支持自定义样式、动画效果和内容插槽。

## 特性

- 🎨 支持自定义颜色和渐变
- 📏 可调整大小和粗细
- ✨ 平滑动画效果
- 🎯 精确的进度显示 ß
- 💫 可选的装饰效果
- 📱 响应式设计
- 🔧 丰富的自定义选项

## 安装

确保你的项目中已安装 Vue 3 和 Tailwind CSS。

```bash
# 复制组件到你的项目中
cp RoundProgress.vue /your-project-path/components/
```

## 基础用法

```vue
<template>
  <RoundProgress :progress="80" />
</template>

<script setup lang="ts">
import RoundProgress from "@/components/RoundProgress.vue";
</script>
```

## 属性说明

| 属性名              | 类型    | 默认值               | 说明                 |
| ------------------- | ------- | -------------------- | -------------------- |
| progress            | number  | -                    | 进度值（0-100）      |
| size                | number  | 220                  | 组件大小（像素）     |
| strokeWidth         | number  | 12                   | 进度条宽度           |
| startColor          | string  | '#4f46e5'            | 渐变起始颜色         |
| endColor            | string  | '#7c3aed'            | 渐变结束颜色         |
| label               | string  | '完成度'             | 标签文本             |
| showDecoration      | boolean | true                 | 是否显示装饰效果     |
| duration            | number  | 2000                 | 动画持续时间（毫秒） |
| textClass           | string  | 'bg-gradient...'     | 文字样式类           |
| bgClass             | string  | 'stroke-gray-200/80' | 背景圆环样式类       |
| labelClass          | string  | 'text-gray-600...'   | 标签样式类           |
| decorationClass     | string  | 'bg-gradient...'     | 装饰效果样式类       |
| decorationDotsClass | string  | 'bg-indigo-500'      | 装饰点样式类         |

## 使用示例

### 基础用法

```vue
<RoundProgress :progress="80" />
```

### 自定义大小和颜色

```vue
<RoundProgress
  :progress="65"
  :size="150"
  start-color="#10B981"
  end-color="#059669"
  label="完成情况"
/>
```

### 自定义样式类

```vue
<RoundProgress
  :progress="75"
  text-class="text-emerald-600"
  decoration-class="bg-gradient-to-r from-emerald-500/10 to-teal-500/10"
  decoration-dots-class="bg-emerald-500"
/>
```

### 使用插槽自定义内容

```vue
<RoundProgress :progress="90">
  <template #center="{ progress }">
    <div class="text-center">
      <div class="text-3xl font-bold text-amber-600">{{ progress }}分</div>
      <div class="text-sm text-amber-500">优秀</div>
    </div>
  </template>
</RoundProgress>
```

### 禁用装饰效果

```vue
<RoundProgress :progress="70" :show-decoration="false" :size="180" />
```

## 注意事项

1. 确保项目中正确配置了 Tailwind CSS
2. 组件使用了 Vue 3 的组合式 API，需要 Vue 3.x 版本
3. TypeScript 类型支持需要 TypeScript 4.x 或更高版本
4. 自定义样式类时，建议遵循 Tailwind CSS 的设计理念

## License

MIT
