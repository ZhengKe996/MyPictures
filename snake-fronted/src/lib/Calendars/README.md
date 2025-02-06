# Vue 3 Calendar Component

一个基于 Vue 3 + TypeScript + Tailwind CSS 的现代化日历组件，支持单日期选择和日期范围选择。

## 特性

- 🎨 现代化 UI 设计，支持亮色/暗色主题
- 📱 完全响应式设计
- 🔄 支持单日期和日期范围选择
- 🌍 支持国际化
- ⌨️ 完整的 TypeScript 支持
- 🎯 支持自定义日期格式

## 安装

```bash
npm install dayjs   # 依赖 dayjs
```

## 基础使用

### 单日期选择模式

```vue
<template>
  <Calendars
    v-model:selectedDate="selectedDate"
    mode="single"
    @dateSelect="handleDateSelect"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Calendars } from "./lib/Calendars";

const selectedDate = ref("");

const handleDateSelect = (date: string) => {
  console.log("Selected date:", date);
};
</script>
```

### 日期范围选择模式

```vue
<template>
  <Calendars
    v-model:startDate="startDate"
    v-model:endDate="endDate"
    mode="range"
    @rangeSelect="handleRangeSelect"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Calendars } from "./lib/Calendars";

const startDate = ref("");
const endDate = ref("");

const handleRangeSelect = (start: string, end: string) => {
  console.log("Date range:", { start, end });
};
</script>
```

## Props

| 属性名             | 类型                  | 默认值         | 说明                       |
| ------------------ | --------------------- | -------------- | -------------------------- |
| mode               | `'single' \| 'range'` | `'single'`     | 选择模式                   |
| initialDate        | `string`              | 当前日期       | 初始日期 (YYYY-MM-DD)      |
| dateFormat         | `string`              | `'YYYY-MM-DD'` | 输出的日期格式             |
| disableOutsideDays | `boolean`             | `true`         | 是否禁用当前月份以外的日期 |
| locale             | `string`              | `'en'`         | 语言设置                   |
| startDate          | `string`              | -              | 范围选择的开始日期         |
| endDate            | `string`              | -              | 范围选择的结束日期         |

## Events

| 事件名              | 参数                           | 说明                         |
| ------------------- | ------------------------------ | ---------------------------- |
| update:selectedDate | `(date: string)`               | 单选模式下日期更新时触发     |
| dateSelect          | `(date: string)`               | 单选模式下选择日期时触发     |
| update:startDate    | `(date: string)`               | 范围模式下开始日期更新时触发 |
| update:endDate      | `(date: string)`               | 范围模式下结束日期更新时触发 |
| rangeSelect         | `(start: string, end: string)` | 范围模式下选择完成时触发     |

## 高级用例

### 自定义日期格式

```vue
<template>
  <Calendars
    v-model:selectedDate="selectedDate"
    date-format="YYYY/MM/DD"
    mode="single"
  />
</template>
```

### 完整范围选择示例

```vue
<template>
  <Calendars
    v-model:startDate="startDate"
    v-model:endDate="endDate"
    mode="range"
    date-format="YYYY/MM/DD"
    :disable-outside-days="false"
    locale="zh"
    @rangeSelect="handleRangeSelect"
  />
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Calendars } from "./lib/Calendars";

const startDate = ref("");
const endDate = ref("");

const handleRangeSelect = (start: string, end: string) => {
  console.log("Selected range:", { start, end });
};

// 监听日期变化
watch([startDate, endDate], ([newStart, newEnd]) => {
  if (newStart && newEnd) {
    // 处理日期范围变化
    console.log("Date range changed:", { start: newStart, end: newEnd });
  }
});
</script>
```

### 与表单集成

```vue
<template>
  <form @submit.prevent="handleSubmit">
    <div class="space-y-4">
      <label class="block">
        <span class="text-gray-700">选择日期范围</span>
        <Calendars
          v-model:startDate="formData.startDate"
          v-model:endDate="formData.endDate"
          mode="range"
          class="mt-1"
        />
      </label>

      <button
        type="submit"
        class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
      >
        提交
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { Calendars } from "./lib/Calendars";

const formData = reactive({
  startDate: "",
  endDate: "",
});

const handleSubmit = () => {
  console.log("Form submitted with:", formData);
};
</script>
```

## 注意事项

1. 组件依赖 dayjs，请确保已安装
2. 使用范围选择模式时，需要同时绑定 startDate 和 endDate
3. 日期格式支持所有 dayjs 支持的格式
4. 组件样式基于 Tailwind CSS，请确保项目中已配置
