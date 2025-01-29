# FileUpload 文件上传组件

## 概述

FileUpload 是一个功能丰富的 Vue 3 组件，提供拖拽文件上传功能和图片预览能力。该组件使用 TypeScript 构建，包含文件验证、自定义样式和响应式设计等特性。

## 特性

- 拖拽文件上传
- 点击上传功能
- 图片预览
- 文件类型验证
- 文件大小验证
- 自定义样式
- 无障碍设计
- 深色模式支持
- 响应式界面

## 属性配置

| 属性名           | 类型          | 默认值             | 说明                        |
| ---------------- | ------------- | ------------------ | --------------------------- |
| `file`           | `PictureType` | 必填               | 包含 url 和 name 的文件对象 |
| `defaultImage`   | `string`      | `""`               | 未选择文件时的默认图片 URL  |
| `accept`         | `string`      | `"image/*"`        | 接受的文件类型              |
| `maxSize`        | `number`      | `5242880` (5MB)    | 最大文件大小（字节）        |
| `disabled`       | `boolean`     | `false`            | 是否禁用上传功能            |
| `inputId`        | `string`      | `"dropzone-file"`  | 文件输入框的 ID             |
| `containerClass` | `string`      | `""`               | 容器附加类名                |
| `dragZoneClass`  | `string`      | `"bg-gray-50..."`  | 拖拽区域类名                |
| `previewClass`   | `string`      | `"w-full h-64..."` | 预览图片类名                |
| `allowedTypes`   | `string[]`    | `[...]`            | 允许的 MIME 类型数组        |

## 事件

| 事件名   | 参数           | 说明                     |
| -------- | -------------- | ------------------------ |
| `Upload` | `File \| null` | 当文件被选择或移除时触发 |
| `error`  | `string`       | 当验证错误发生时触发     |

## 插槽

| 插槽名        | 说明               |
| ------------- | ------------------ |
| `icon`        | 自定义上传图标     |
| `label`       | 自定义上传文本     |
| `remove-icon` | 自定义移除按钮图标 |

## 基础用法

```vue
<template>
  <FileUpload :file="file" @Upload="handleUpload" @error="handleError" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import FileUpload from "./components/FileUpload.vue";

const file = ref({ url: "", name: "" });

const handleUpload = (uploadedFile: File | null) => {
  if (uploadedFile) {
    // Handle file upload
  }
};

const handleError = (error: string) => {
  console.error(error);
};
</script>
```

## 自定义示例

```vue
<template>
  <FileUpload
    :file="file"
    accept="image/png,image/jpeg"
    :maxSize="1024 * 1024 * 2"
    containerClass="max-w-md mx-auto"
    @Upload="handleUpload"
  >
    <template #icon>
      <!-- Custom icon -->
    </template>
    <template #label>
      <!-- Custom label -->
    </template>
  </FileUpload>
</template>
```

## 注意事项

- 该组件使用 Tailwind CSS 进行样式设计
- 文件验证包括类型和大小检查
- 该组件完全响应式且无障碍
- 默认支持深色模式

## 需求

- Vue 3
- TypeScript
- Tailwind CSS
