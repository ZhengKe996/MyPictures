# SearchInput 搜索输入框组件

一个功能完整的搜索输入框组件，支持清除、搜索按钮，以及键盘事件处理。

## 特性

- 支持双向绑定（v-model）
- 可配置标签显示
- 支持清除功能
- 支持回车搜索
- 响应式设计
- 支持不同尺寸

## 属性

| 属性名      | 类型    | 默认值      | 说明                       |
| ----------- | ------- | ----------- | -------------------------- |
| modelValue  | string  | ""          | 输入框的值（支持 v-model） |
| label       | string  | "Search"    | 标签文本                   |
| showLabel   | boolean | true        | 是否显示标签               |
| placeholder | string  | "Search..." | 占位符文本                 |
| type        | string  | "text"      | 输入框类型                 |
| id          | string  | "search"    | 输入框 ID                  |
| name        | string  | "search"    | 输入框名称                 |
| size        | string  | "md"        | 按钮大小（sm/md/lg）       |

## 事件

| 事件名            | 参数类型      | 说明                     |
| ----------------- | ------------- | ------------------------ |
| update:modelValue | string        | 输入值更新时触发         |
| search            | -             | 点击搜索按钮或回车时触发 |
| keypress          | KeyboardEvent | 键盘按下时触发           |
| clear             | -             | 点击清除按钮时触发       |

## 基础用法

```vue
<template>
  <SearchInput
    v-model="searchText"
    label="搜索："
    placeholder="请输入关键词..."
    @search="handleSearch"
    @clear="handleClear"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import SearchInput from "@/lib/SearchInput/SearchInput.vue";

const searchText = ref("");

const handleSearch = () => {
  console.log("搜索内容：", searchText.value);
};

const handleClear = () => {
  console.log("内容已清除");
};
</script>
```

## 不同尺寸

```vue
<template>
  <div class="space-y-4">
    <SearchInput v-model="searchText" label="小尺寸：" size="sm" />

    <SearchInput v-model="searchText" label="中尺寸：" size="md" />

    <SearchInput v-model="searchText" label="大尺寸：" size="lg" />
  </div>
</template>
```

## 隐藏标签

```vue
<template>
  <SearchInput
    v-model="searchText"
    :showLabel="false"
    placeholder="无标签搜索框..."
  />
</template>
```

## 自定义事件处理

```vue
<template>
  <SearchInput
    v-model="searchText"
    label="高级搜索："
    @search="handleSearch"
    @clear="handleClear"
    @keypress="handleKeyPress"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";

const searchText = ref("");

const handleSearch = () => {
  // 处理搜索逻辑
};

const handleClear = () => {
  // 处理清除逻辑
};

const handleKeyPress = (event: KeyboardEvent) => {
  // 处理键盘事件
};
</script>
```
