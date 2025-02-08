# CheckBox 复选框组件

一个功能完整的 Vue 3 复选框组件，支持多种样式、尺寸和状态。

## 特性

- 支持 v-model 双向绑定
- 自定义颜色主题
- 三种尺寸变体
- 支持禁用状态
- 支持半选状态
- 支持行内和垂直布局
- 支持带描述文本的模式
- 平滑的动画效果

## 基础用法

```vue
<template>
  <CheckBox v-model="checked" id="basic" label="基础复选框" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import CheckBox from "./CheckBox.vue";

const checked = ref(false);
</script>
```

## 属性

| 属性名        | 类型                       | 默认值       | 说明                       |
| ------------- | -------------------------- | ------------ | -------------------------- |
| modelValue    | boolean                    | false        | 复选框的值（支持 v-model） |
| id            | string                     | -            | 唯一标识符（必填）         |
| label         | string                     | -            | 显示文本（必填）           |
| name          | string                     | ''           | 表单名称                   |
| description   | string                     | ''           | 详细描述文本               |
| disabled      | boolean                    | false        | 是否禁用                   |
| color         | string                     | 'indigo'     | 主题颜色                   |
| layout        | 'horizontal' \| 'vertical' | 'horizontal' | 布局方式                   |
| size          | 'sm' \| 'md' \| 'lg'       | 'md'         | 尺寸大小                   |
| inline        | boolean                    | false        | 是否行内显示               |
| indeterminate | boolean                    | false        | 是否半选状态               |
| wrapperClass  | string                     | ''           | 容器额外类名               |
| labelClass    | string                     | ''           | 标签额外类名               |

## 事件

| 事件名            | 参数                | 说明           |
| ----------------- | ------------------- | -------------- |
| update:modelValue | (value: boolean)    | 值更新时触发   |
| change            | (event: Event)      | 状态变化时触发 |
| focus             | (event: FocusEvent) | 获得焦点时触发 |
| blur              | (event: FocusEvent) | 失去焦点时触发 |
| click             | (event: MouseEvent) | 点击时触发     |

## 使用示例

### 不同尺寸

```vue
<template>
  <CheckBox v-model="checked" id="small" label="小尺寸" size="sm" />
  <CheckBox v-model="checked" id="medium" label="中等尺寸" size="md" />
  <CheckBox v-model="checked" id="large" label="大尺寸" size="lg" />
</template>
```

### 不同颜色主题

```vue
<template>
  <CheckBox v-model="checked" id="indigo" label="靛蓝主题" color="indigo" />
  <CheckBox v-model="checked" id="red" label="红色主题" color="red" />
  <CheckBox v-model="checked" id="green" label="绿色主题" color="green" />
  <CheckBox v-model="checked" id="blue" label="蓝色主题" color="blue" />
  <CheckBox v-model="checked" id="yellow" label="黄色主题" color="yellow" />
  <CheckBox v-model="checked" id="purple" label="紫色主题" color="purple" />
</template>
```

### 带描述的复选框

```vue
<template>
  <CheckBox
    v-model="checked"
    id="with-desc"
    label="订阅通知"
    description="接收我们的最新更新和重要通知。"
    layout="vertical"
  />
</template>
```

### 横向列表

```vue
<template>
  <div class="flex flex-wrap gap-4">
    <CheckBox
      v-for="item in items"
      :key="item.id"
      v-model="item.checked"
      :id="item.id"
      :label="item.label"
      inline
      size="sm"
    />
  </div>
</template>
```

### 全选功能

```vue
<template>
  <div class="space-y-4">
    <CheckBox
      v-model="selectAll"
      id="select-all"
      label="全选"
      :indeterminate="hasSelection && !allSelected"
    />
    <div class="space-y-2">
      <CheckBox
        v-for="item in items"
        :key="item.id"
        v-model="item.selected"
        :id="item.id"
        :label="item.name"
        @change="updateSelectAll"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";

const items = ref([
  { id: 1, name: "选项1", selected: false },
  { id: 2, name: "选项2", selected: false },
  { id: 3, name: "选项3", selected: false },
]);

const hasSelection = computed(() => items.value.some((item) => item.selected));
const allSelected = computed(() => items.value.every((item) => item.selected));

const selectAll = computed({
  get: () => allSelected.value,
  set: (value) => {
    items.value.forEach((item) => (item.selected = value));
  },
});

const updateSelectAll = () => {
  // 自动更新全选状态
};
</script>
```

## 最佳实践

1. 始终提供唯一的 id 属性
2. 使用 v-model 进行双向绑定
3. 为复杂列表使用合适的键值 (key)
4. 根据实际需求选择合适的尺寸和颜色
5. 使用 description 属性提供更多上下文信息
6. 使用 inline 属性实现横向布局
