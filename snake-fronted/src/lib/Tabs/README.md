# Tabs 组件文档

## 概述

Tabs 组件是一个多功能的 Vue 3 组件，提供桌面和移动端友好的标签导航界面。它支持受控和非受控使用模式，内置响应式设计和无障碍功能。

## 特性

- 🖥️ 响应式设计，支持桌面和移动端不同视图
- ♿️ 完整的无障碍支持，包含 ARIA 属性
- 🎨 通过插槽自定义标签外观
- 🔄 支持 v-model 双向绑定
- 📱 移动端友好的下拉选择

## 安装

```bash
# 将 Tabs 组件添加到您的项目中
cp Tabs.vue /your-project-path/components/
```

## 基础用法

```vue
<template>
  <Tabs
    v-model="activeTab"
    :tabs="['标签1', '标签2', '标签3']"
    @change="handleTabChange"
  >
    <template #Tab1> 标签1的内容 </template>
    <template #Tab2> 标签2的内容 </template>
    <template #Tab3> 标签3的内容 </template>
  </Tabs>
</template>

<script setup>
import { ref } from "vue";
import Tabs from "./components/Tabs.vue";

const activeTab = ref("标签1");
const handleTabChange = (tab) => {
  console.log("标签已更改:", tab);
};
</script>
```

## 高级用法

```vue
<template>
  <Tabs v-model="activeTab" :tabs="advancedTabs" @change="handleTabChange">
    <template #tab="{ tab }"> {{ tab.name }} {{ tab.badge }} </template>
    <template #first> 第一个标签的自定义内容 </template>
  </Tabs>
</template>

<script setup>
import { ref } from "vue";

const activeTab = ref("first");
const advancedTabs = [
  { name: "first", badge: "(新)", disabled: false },
  { name: "second", badge: "(2)", disabled: true },
  { name: "third", content: "默认内容" },
];
</script>
```

## 属性

| 属性        | 类型                     | 默认值    | 描述                           |
| ----------- | ------------------------ | --------- | ------------------------------ |
| modelValue  | string                   | undefined | 当前激活的标签（v-model 绑定） |
| tabs        | Array<TabItem \| string> | required  | 标签项数组或字符串数组         |
| showContent | boolean                  | true      | 是否显示标签内容面板           |
| defaultTab  | string                   | ''        | 非受控模式下的默认活动标签     |

## 事件

| 事件名            | 载荷    | 描述                          |
| ----------------- | ------- | ----------------------------- |
| update:modelValue | string  | 当活动标签改变时触发(v-model) |
| change            | TabItem | 当标签选择改变时触发          |
| click             | TabItem | 当标签被点击时触发            |

## 类型

```typescript
interface TabItem {
  name: string;
  href?: string;
  disabled?: boolean;
  content?: string;
  [key: string]: any;
}
```

## 插槽

| 名称      | 属性             | 描述           |
| --------- | ---------------- | -------------- |
| tab       | { tab: TabItem } | 自定义标签渲染 |
| [tabName] | { tab: TabItem } | 特定标签的内容 |

## CSS 类

组件使用 Tailwind CSS 类进行样式设计，包括：

- 响应式设计（sm: 断点）
- 颜色过渡
- 悬停和焦点状态
- 禁用状态

## 无障碍

- 适当的 ARIA 角色和属性
- 键盘导航支持
- 屏幕阅读器友好结构

## 浏览器支持

- 所有现代浏览器
- 移动端响应式设计
- 旧浏览器渐进增强
