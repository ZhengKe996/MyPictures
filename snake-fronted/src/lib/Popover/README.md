# Popover 气泡提示组件

一个灵活的气泡提示组件，支持多种交互方式和丰富的自定义选项。

## ✨ 特性

- 📍 支持四个方向的弹出位置
- 🖱️ 支持 hover 和 click 两种触发方式
- 📱 自适应视口位置
- ⚡ 流畅的过渡动画
- 🌓 支持亮色/暗色主题
- 🎨 高度可定制的样式

## 🚀 快速开始

## 🎯 组件示例

### 1. 基础用法

```vue
<template>
  <div class="flex space-x-4">
    <!-- 悬停触发 -->
    <Popover placement="top" :delay="200">
      <template #reference>
        <button
          class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          悬停提示
        </button>
      </template>
      <div class="p-2">简单的提示文本</div>
    </Popover>

    <!-- 点击触发 -->
    <Popover trigger="click" placement="bottom">
      <template #reference>
        <button
          class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
        >
          点击提示
        </button>
      </template>
      <div class="p-2 w-48">点击显示的提示内容</div>
    </Popover>
  </div>
</template>
```

### 2. 十二个方位展示

```vue
<template>
  <div class="grid grid-cols-4 gap-4 max-w-2xl">
    <Popover
      v-for="position in positions"
      :key="position.value"
      :placement="position.value"
      :offset="12"
    >
      <template #reference>
        <button :class="position.btnClass">
          {{ position.label }} {{ position.icon }}
        </button>
      </template>
      <div class="bg-gray-800 text-white px-3 py-1.5 rounded text-sm">
        {{ position.tip }}
      </div>
    </Popover>
  </div>
</template>

<script setup lang="ts">
const positions = [
  { value: "top-start", label: "上左", icon: "↖️", btnClass: "btn-blue" },
  { value: "top", label: "上中", icon: "⬆️", btnClass: "btn-blue" },
  { value: "top-end", label: "上右", icon: "↗️", btnClass: "btn-blue" },
  { value: "right-start", label: "右上", icon: "↗️", btnClass: "btn-green" },
  { value: "right", label: "右中", icon: "➡️", btnClass: "btn-green" },
  { value: "right-end", label: "右下", icon: "↘️", btnClass: "btn-green" },
  { value: "bottom-start", label: "下左", icon: "↙️", btnClass: "btn-yellow" },
  { value: "bottom", label: "下中", icon: "⬇️", btnClass: "btn-yellow" },
  { value: "bottom-end", label: "下右", icon: "↘️", btnClass: "btn-yellow" },
  { value: "left-start", label: "左上", icon: "↖️", btnClass: "btn-purple" },
  { value: "left", label: "左中", icon: "⬅️", btnClass: "btn-purple" },
  { value: "left-end", label: "左下", icon: "↙️", btnClass: "btn-purple" },
];
</script>
```

### 3. 高级用法示例

#### 3.1 确认对话框

```vue
<template>
  <Popover trigger="click" placement="top">
    <template #reference>
      <button class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
        删除项目
      </button>
    </template>
    <div class="p-4 w-64">
      <p class="font-medium text-red-500">确认删除该项目？</p>
      <p class="text-gray-500 text-sm mt-1">此操作不可恢复</p>
      <div class="mt-4 flex justify-end space-x-2">
        <button class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">
          取消
        </button>
        <button
          class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
        >
          确认删除
        </button>
      </div>
    </div>
  </Popover>
</template>
```

#### 3.2 颜色选择器

```vue
<template>
  <Popover trigger="click" placement="bottom-start">
    <template #reference>
      <div class="flex items-center space-x-2">
        <div
          class="w-6 h-6 rounded border"
          :style="{ backgroundColor: selectedColor }"
        ></div>
        <span>选择颜色</span>
      </div>
    </template>
    <div class="p-2 w-48">
      <div class="grid grid-cols-5 gap-2">
        <div
          v-for="color in colors"
          :key="color"
          class="w-6 h-6 rounded cursor-pointer hover:opacity-80"
          :style="{ backgroundColor: color }"
          @click="selectColor(color)"
        ></div>
      </div>
    </div>
  </Popover>
</template>

<script setup>
const colors = ["#f87171", "#60a5fa", "#34d399", "#fbbf24", "#a78bfa"];
const selectedColor = ref("#60a5fa");
const selectColor = (color) => (selectedColor.value = color);
</script>
```

#### 3.3 搜索下拉框

```vue
<template>
  <Popover trigger="click" placement="bottom-start" :offset="4">
    <template #reference>
      <input
        v-model="searchQuery"
        type="text"
        class="w-64 px-3 py-2 border rounded"
        placeholder="搜索用户..."
      />
    </template>
    <div class="w-64 py-1 max-h-64 overflow-auto">
      <template v-if="filteredUsers.length">
        <div
          v-for="user in filteredUsers"
          :key="user.id"
          class="px-3 py-2 hover:bg-gray-100 dark:hover:bg-zinc-700 cursor-pointer"
          @click="selectUser(user)"
        >
          <div class="font-medium">{{ user.name }}</div>
          <div class="text-sm text-gray-500">{{ user.email }}</div>
        </div>
      </template>
      <div v-else class="px-3 py-2 text-gray-500">无匹配结果</div>
    </div>
  </Popover>
</template>

<script setup>
const searchQuery = ref("");
const users = [
  { id: 1, name: "张三", email: "zhang@example.com" },
  { id: 2, name: "李四", email: "li@example.com" },
];
const filteredUsers = computed(() => {
  const query = searchQuery.value.toLowerCase();
  return users.filter(
    (user) =>
      user.name.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
  );
});
</script>
```

## 📖 API

### Props

| 参数        | 说明             | 类型                                                                                                                                                               | 默认值   |
| ----------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- |
| placement   | 弹出位置         | 'top' \| 'top-start' \| 'top-end' \| 'right' \| 'right-start' \| 'right-end' \| 'bottom' \| 'bottom-start' \| 'bottom-end' \| 'left' \| 'left-start' \| 'left-end' | 'bottom' |
| offset      | 偏移距离         | number                                                                                                                                                             | 8        |
| delay       | 延迟关闭时间(ms) | number                                                                                                                                                             | 300      |
| trigger     | 触发方式         | 'hover' \| 'click'                                                                                                                                                 | 'hover'  |
| customClass | 自定义类名       | string                                                                                                                                                             | ''       |

### Events

| 事件名 | 说明       | 回调参数 |
| ------ | ---------- | -------- |
| show   | 显示时触发 | -        |
| hide   | 隐藏时触发 | -        |

### Slots

| 名称      | 说明     |
| --------- | -------- |
| reference | 触发元素 |
| default   | 弹出内容 |

## 🎨 样式指南

### 常用按钮样式

```css
/* 基础按钮 */
.btn-primary {
  @apply px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600;
}

.btn-danger {
  @apply px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600;
}

/* 方位按钮 */
.btn-blue {
  @apply px-3 py-1.5 bg-blue-500 text-white rounded hover:bg-blue-600;
}

.btn-green {
  @apply px-3 py-1.5 bg-green-500 text-white rounded hover:bg-green-600;
}
```

## 💡 最佳实践

1. **移动端适配**

   - 建议使用 `click` 触发方式
   - 注意控制内容宽度，避免溢出

2. **性能优化**

   - 复杂内容建议使用延迟加载
   - 频繁触发时使用 `delay` 防抖

3. **样式定制**

   - 使用 `customClass` 添加自定义样式
   - 注意保持亮色/暗色模式的对比度

4. **无障碍访问**
   - 为触发元素添加适当的 aria 属性
   - 确保键盘可访问性
