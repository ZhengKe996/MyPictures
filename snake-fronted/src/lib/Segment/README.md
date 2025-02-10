# Segment 分段控制器

分段控制器组件，用于在不同视图之间进行切换。

## 基础用法

最简单的分段控制器使用方式。

```vue
<template>
  <Segment
    v-model:value="selected"
    :options="[
      { label: '每日', value: 'daily' },
      { label: '每周', value: 'weekly' },
      { label: '每月', value: 'monthly' },
    ]"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import Segment from "@/lib/Segment/Segment.vue";

const selected = ref("daily");
const handleChange = (value: string | number) => {
  console.log("选中的值：", value);
};
</script>
```

## 带图标的分段控制器

可以在选项中添加图标。

```vue
<template>
  <Segment
    v-model:value="selected"
    :options="[
      { label: '列表', value: 'list', icon: 'i-tabler-list' },
      { label: '卡片', value: 'card', icon: 'i-tabler-cards' },
      { label: '网格', value: 'grid', icon: 'i-tabler-grid-dots' },
    ]"
  />
</template>
```

## 不同尺寸

支持三种尺寸：small、medium、large。

```vue
<template>
  <Segment v-model:value="selected" :options="options" size="sm" />
  <Segment v-model:value="selected" :options="options" size="md" />
  <Segment v-model:value="selected" :options="options" size="lg" />
</template>
```

## 块级模式

使用 `block` 属性可以将分段控制器设置为块级元素，宽度为 100%。

```vue
<template>
  <Segment v-model:value="selected" :options="options" block />
</template>
```

## 禁用状态

使用 `disabled` 属性可以禁用整个分段控制器。

```vue
<template>
  <Segment v-model:value="selected" :options="options" disabled />
</template>
```

## API

### Props

| 参数           | 说明           | 类型                   | 默认值  |
| -------------- | -------------- | ---------------------- | ------- |
| options        | 选项数组       | `SegmentOption[]`      | `[]`    |
| value(v-model) | 当前选中的值   | `string \| number`     | -       |
| size           | 控件大小       | `'sm' \| 'md' \| 'lg'` | `'md'`  |
| disabled       | 是否禁用       | `boolean`              | `false` |
| block          | 是否为块级元素 | `boolean`              | `false` |

### SegmentOption 类型

```typescript
interface SegmentOption {
  label: string; // 选项显示文本
  value: string | number; // 选项值
  icon?: string; // 选项图标（可选）
}
```

### Events

| 事件名称     | 说明             | 回调参数                    |
| ------------ | ---------------- | --------------------------- |
| update:value | 选中值变化时触发 | `(value: string \| number)` |
| change       | 选中值变化时触发 | `(value: string \| number)` |
