# Dialog 对话框组件

一个基于 Vue 3 + TypeScript + Tailwind CSS 的现代化对话框组件，支持自定义主题和暗黑模式。

## 特性 ✨

- 🎨 支持自定义按钮颜色
- 🌗 支持亮色/暗黑模式
- 💫 平滑过渡动画效果
- 🔒 完整的 TypeScript 类型支持
- 📱 响应式设计

## 安装

```bash
npm install @your-org/dialog-component
```

## 基础用法

### 简单确认框

```vue
<template>
  <button @click="showDialog = true">打开对话框</button>

  <Dialog
    v-model="showDialog"
    title="确认操作"
    :confirmHandler="handleConfirm"
    :cancelHandler="handleCancel"
  >
    确认要执行此操作吗？
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Dialog from "@your-org/dialog-component";

const showDialog = ref(false);

const handleConfirm = () => {
  console.log("用户点击了确认");
};

const handleCancel = () => {
  console.log("用户点击了取消");
};
</script>
```

### 自定义按钮颜色

```vue
<template>
  <Dialog
    v-model="showDialog"
    title="删除确认"
    confirmText="删除"
    cancelText="返回"
    confirmButtonColor="red"
    cancelButtonColor="zinc"
    :confirmHandler="handleDelete"
  >
    删除后数据将无法恢复，是否继续？
  </Dialog>
</template>
```

### 不显示关闭按钮

```vue
<template>
  <Dialog
    v-model="showDialog"
    title="系统提示"
    :showClose="false"
    confirmButtonColor="blue"
  >
    系统正在处理中，请稍候...
  </Dialog>
</template>
```

## API 文档

### Props

| 参数名             | 类型    | 默认值 | 说明                   |
| ------------------ | ------- | ------ | ---------------------- |
| modelValue         | boolean | -      | 控制对话框的显示/隐藏  |
| title              | string  | -      | 对话框标题             |
| cancelText         | string  | '取消' | 取消按钮文本           |
| confirmText        | string  | '确定' | 确认按钮文本           |
| showClose          | boolean | true   | 是否显示右上角关闭按钮 |
| cancelButtonColor  | string  | 'gray' | 取消按钮颜色           |
| confirmButtonColor | string  | 'red'  | 确认按钮颜色           |

### Events

| 事件名         | 参数       | 说明                     |
| -------------- | ---------- | ------------------------ |
| cancelHandler  | () => void | 点击取消按钮时的回调函数 |
| confirmHandler | () => void | 点击确认按钮时的回调函数 |
| close          | () => void | 对话框关闭时的回调函数   |

### 支持的颜色

组件使用 Tailwind CSS 的颜色系统，支持以下颜色：

- red
- blue
- green
- yellow
- zinc
- gray
- indigo
- purple
- pink

## 最佳实践

### 处理异步操作

```vue
<template>
  <Dialog
    v-model="showDialog"
    title="保存更改"
    :confirmHandler="handleSave"
    confirmButtonColor="blue"
  >
    <template v-if="loading">
      <div class="flex items-center">
        <span class="mr-2">保存中...</span>
        <!-- 可以添加loading图标 -->
      </div>
    </template>
    <template v-else> 是否保存当前更改？ </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";

const showDialog = ref(false);
const loading = ref(false);

const handleSave = async () => {
  loading.value = true;
  try {
    await saveData();
    showDialog.value = false;
  } finally {
    loading.value = false;
  }
};
</script>
```

## 注意事项

1. 确保在使用自定义颜色时，对应的 Tailwind 颜色类已被正确配置
2. 组件默认支持暗黑模式，无需额外配置
3. 建议通过 `v-model` 来控制对话框的显示状态
