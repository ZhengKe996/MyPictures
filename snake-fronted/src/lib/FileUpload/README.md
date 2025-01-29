# FileUpload 文件上传组件

一个功能丰富的 Vue 3 文件上传组件，支持拖拽上传功能。

## 功能特点

- 支持拖拽上传
- 文件类型验证
- 文件大小验证
- 可自定义外观
- 图片预览功能
- 响应式设计
- 流畅动画效果

## 安装

```bash
npm install @your-org/file-upload
```

## 基础用法

```vue
<template>
  <FileUpload
    v-model="file"
    accept="image/*"
    :maxSize="5242880"
    @success="handleSuccess"
    @error="handleError"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import FileUpload from "@your-org/file-upload";

const file = ref(null);

const handleSuccess = (file: File) => {
  console.log("文件上传成功:", file);
};

const handleError = (error: string) => {
  console.error("上传错误:", error);
};
</script>
```

## 属性说明

| 属性       | 类型           | 默认值      | 说明               |
| ---------- | -------------- | ----------- | ------------------ |
| modelValue | `File \| null` | `null`      | 文件双向绑定值     |
| accept     | `string`       | `'image/*'` | 接受的文件类型     |
| maxSize    | `number`       | `10485760`  | 最大文件大小(10MB) |
| preview    | `boolean`      | `true`      | 是否显示预览       |
| disabled   | `boolean`      | `false`     | 是否禁用上传功能   |

## 事件说明

| 事件名            | 参数类型       | 说明           |
| ----------------- | -------------- | -------------- |
| update:modelValue | `File \| null` | 文件变更时触发 |
| success           | `File`         | 上传成功时触发 |
| error             | `string`       | 上传错误时触发 |
| remove            | `void`         | 移除文件时触发 |

## 自定义样式示例

```vue
<template>
  <FileUpload v-model="file" class="custom-uploader" :preview="false">
    <template #default="{ isDragging }">
      <div :class="{ dragging: isDragging }">自定义上传界面</div>
    </template>
  </FileUpload>
</template>
```

## 自定义样式说明

组件提供了默认插槽，可以完全自定义上传区域的外观。插槽提供了 `isDragging` 参数，用于实现拖拽状态的样式变化。您可以使用 Tailwind CSS 或自定义 CSS 来设置样式。
