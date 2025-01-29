# TagsInput 标签输入组件

TagsInput 是一个功能丰富的标签输入组件，支持多种自定义选项和验证功能。

## 基础用法

```vue
<template>
  <TagsInput v-model="tags" />
</template>

<script setup>
import { ref } from "vue";
const tags = ref(["Vue", "React"]);
</script>
```

## 自定义样式

### 尺寸

支持三种尺寸：`sm`、`md`(默认)、`lg`

```vue
<template>
  <TagsInput v-model="tags" size="sm" />
  <TagsInput v-model="tags" size="md" />
  <TagsInput v-model="tags" size="lg" />
</template>
```

### 颜色

可以自定义标签颜色：

```vue
<template>
  <TagsInput v-model="tags" color="blue" />
  <TagsInput v-model="tags" color="red" />
  <TagsInput v-model="tags" color="green" />
</template>
```

## 高级功能

### 最大标签数限制

```vue
<template>
  <TagsInput v-model="tags" :maxTags="3" />
</template>
```

### 自定义验证

```vue
<template>
  <TagsInput v-model="tags" :validation="validateTag" @error="handleError" />
</template>

<script setup>
const validateTag = (tag) => {
  if (tag.length < 2) return "标签长度至少为2个字符";
  if (tag.length > 10) return "标签长度不能超过10个字符";
  return true;
};

const handleError = (message) => {
  console.log("验证错误:", message);
};
</script>
```

### 允许重复标签

默认情况下不允许重复标签，可以通过 `allowDuplicates` 属性启用：

```vue
<template>
  <TagsInput v-model="tags" :allowDuplicates="true" />
</template>
```

## Props

| 参数            | 说明             | 类型                               | 默认值       |
| --------------- | ---------------- | ---------------------------------- | ------------ |
| modelValue      | 标签数组         | string[]                           | []           |
| color           | 标签颜色         | string                             | 'blue'       |
| size            | 组件尺寸         | 'sm' \| 'md' \| 'lg'               | 'md'         |
| placeholder     | 输入框占位文本   | string                             | '请输入标签' |
| maxTags         | 最大标签数量     | number                             | Infinity     |
| validation      | 标签验证函数     | (tag: string) => boolean \| string | -            |
| allowDuplicates | 是否允许重复标签 | boolean                            | false        |

## Events

| 事件名            | 说明               | 回调参数          |
| ----------------- | ------------------ | ----------------- |
| update:modelValue | 标签数组更新时触发 | (tags: string[])  |
| error             | 验证错误时触发     | (message: string) |

## 特性

- 支持键盘操作（Enter 添加标签，Backspace 删除最后一个标签）
- 标签添加/删除动画效果
- 完善的错误提示
- 支持自定义验证
- 响应式设计
