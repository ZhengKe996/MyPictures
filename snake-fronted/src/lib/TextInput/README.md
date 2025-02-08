# TextInput 组件

一个现代化的、可定制的输入框组件，支持多种主题颜色和状态。

## 特性

- 支持多种尺寸（sm/md/lg）
- 支持多种主题颜色
- 支持禁用状态
- 支持错误提示
- 支持描述文本
- 支持前缀/后缀图标
- 支持清除按钮
- 自适应移动端

## 基础用法

```vue
<template>
  <TextInput
    v-model="inputValue"
    id="basic"
    label="用户名"
    placeholder="请输入用户名"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import TextInput from "./TextInput.vue";

const inputValue = ref("");
</script>
```

## 不同尺寸

```vue
<TextInput
  v-model="value"
  size="sm"
  label="小尺寸"
  placeholder="小尺寸输入框"
/>

<TextInput
  v-model="value"
  size="md"
  label="中尺寸"
  placeholder="中尺寸输入框"
/>

<TextInput
  v-model="value"
  size="lg"
  label="大尺寸"
  placeholder="大尺寸输入框"
/>
```

## 主题颜色

```vue
<TextInput
  v-model="value"
  color="blue"
  label="蓝色主题"
  placeholder="蓝色输入框"
/>

<TextInput
  v-model="value"
  color="emerald"
  label="绿色主题"
  placeholder="绿色输入框"
/>

<TextInput
  v-model="value"
  color="rose"
  label="玫瑰红主题"
  placeholder="玫瑰红输入框"
/>
```

## 状态展示

```vue
<!-- 禁用状态 -->
<TextInput
  v-model="value"
  disabled
  label="禁用状态"
  placeholder="禁用的输入框"
/>

<!-- 错误状态 -->
<TextInput
  v-model="value"
  error="这是一个错误提示"
  label="错误状态"
  placeholder="错误状态的输入框"
/>

<!-- 带描述信息 -->
<TextInput
  v-model="value"
  description="这是一段帮助文本"
  label="带描述信息"
  placeholder="带描述的输入框"
/>
```

## 带图标

```vue
<!-- 前缀图标 -->
<TextInput v-model="value" label="搜索">
  <template #prefix>
    <svg class="size-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor">
      <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
    </svg>
  </template>
</TextInput>

<!-- 后缀图标 -->
<TextInput v-model="value" label="链接">
  <template #suffix>
    <svg class="size-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor">
      <path d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101" />
    </svg>
  </template>
</TextInput>
```

## Props

| 属性名       | 类型                 | 默认值   | 说明                |
| ------------ | -------------------- | -------- | ------------------- |
| modelValue   | string               | ''       | 输入框的值          |
| id           | string               | -        | 输入框的 id（必填） |
| label        | string               | -        | 标签文本            |
| type         | string               | 'text'   | 输入框类型          |
| placeholder  | string               | ''       | 占位符文本          |
| disabled     | boolean              | false    | 是否禁用            |
| required     | boolean              | false    | 是否必填            |
| error        | string               | -        | 错误提示文本        |
| description  | string               | -        | 描述文本            |
| color        | ColorType            | 'indigo' | 主题颜色            |
| size         | 'sm' \| 'md' \| 'lg' | 'md'     | 输入框尺寸          |
| wrapperClass | string               | ''       | 外层容器类名        |
| labelClass   | string               | ''       | 标签类名            |

## Events

| 事件名            | 参数       | 说明             |
| ----------------- | ---------- | ---------------- |
| update:modelValue | string     | 输入值更新时触发 |
| focus             | FocusEvent | 获得焦点时触发   |
| blur              | FocusEvent | 失去焦点时触发   |

## Slots

| 插槽名 | 说明           |
| ------ | -------------- |
| prefix | 输入框前缀内容 |
| suffix | 输入框后缀内容 |

## 支持的颜色主题

- slate/gray/zinc/neutral/stone
- red/orange/amber/yellow
- lime/green/emerald/teal
- cyan/sky/blue/indigo
- violet/purple/fuchsia
- pink/rose
