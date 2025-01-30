# TagsList 组件文档

TagsList 是一个功能丰富的标签列表组件，支持单选、多选、随机颜色等特性。

## 特性

- 支持单选和多选模式
- 支持随机颜色生成
- 平滑的动画效果
- 完整的 TypeScript 支持
- 集成单个 Tag 组件的所有特性

## 基础用法

### 简单标签列表

```vue
<template>
  <TagsList :tags="tags" v-model="selectedTags" @change="handleChange" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import { TagsList } from "./components";

const tags = ref([
  { id: 1, name: "技术" },
  { id: 2, name: "设计" },
  { id: 3, name: "产品" },
]);

const selectedTags = ref([]);

const handleChange = (tags) => {
  console.log("Selected tags:", tags);
};
</script>
```

### 多选模式

```vue
<template>
  <TagsList :tags="tags" v-model="selectedTags" :multiple="true" />
</template>
```

### 自定义颜色标签

```vue
<template>
  <TagsList :tags="customTags" v-model="selectedTags" :random-colors="false" />
</template>

<script setup>
const customTags = [
  { id: 1, name: "重要", variant: "danger" },
  { id: 2, name: "进行中", variant: "warning" },
  { id: 3, name: "完成", variant: "success" },
];
</script>
```

### 随机颜色模式

```vue
<template>
  <TagsList :tags="tags" :random-colors="true" />
</template>
```

### 监听标签点击

```vue
<template>
  <TagsList :tags="tags" @tag-click="handleTagClick" />
</template>

<script setup>
const handleTagClick = (tag) => {
  console.log("Clicked tag:", tag);
};
</script>
```

## Props

| 属性名       | 类型          | 默认值 | 描述                  |
| ------------ | ------------- | ------ | --------------------- |
| tags         | EnhancedTag[] | []     | 标签数据列表          |
| modelValue   | EnhancedTag[] | []     | 选中的标签（v-model） |
| multiple     | boolean       | false  | 是否支持多选          |
| randomColors | boolean       | true   | 是否使用随机颜色      |

## 事件

| 事件名            | 参数                  | 描述               |
| ----------------- | --------------------- | ------------------ |
| change            | (tags: EnhancedTag[]) | 选中标签变化时触发 |
| update:modelValue | (tags: EnhancedTag[]) | v-model 更新事件   |
| tag-click         | (tag: EnhancedTag)    | 标签被点击时触发   |

## 类型定义

```typescript
interface EnhancedTag {
  id: string | number;
  name: string;
  variant?: TagVariant;
  color?: string;
}
```
