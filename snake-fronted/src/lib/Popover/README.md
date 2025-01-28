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

### 基础用法

最简单的悬停提示：

```vue
<template>
  <Popover>
    <template #reference>
      <button class="px-4 py-2 bg-blue-500 text-white rounded">
        悬停查看更多
      </button>
    </template>
    <div class="p-2">这是一段提示文本</div>
  </Popover>
</template>
```

### 四个方向展示

```vue
<template>
  <div class="space-x-4">
    <Popover
      v-for="direction in ['top', 'right', 'bottom', 'left']"
      :key="direction"
      :placement="direction"
      :offset="12"
    >
      <template #reference>
        <button class="btn-blue">{{ direction }}</button>
      </template>
      <div class="p-2">{{ direction }} 方向的提示</div>
    </Popover>
  </div>
</template>
```

## 🎯 高级用法

### 下拉菜单

实现一个功能完整的下拉菜单：

```vue
<template>
  <Popover trigger="click" placement="bottom">
    <template #reference>
      <button
        class="flex items-center px-4 py-2 bg-indigo-500 text-white rounded"
      >
        <span>操作菜单</span>
        <svg class="w-4 h-4 ml-2" viewBox="0 0 24 24">
          <path
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </button>
    </template>

    <div class="w-48 py-2">
      <a
        v-for="item in menuItems"
        :key="item.label"
        class="block px-4 py-2 hover:bg-gray-100 dark:hover:bg-zinc-700 cursor-pointer"
      >
        {{ item.label }}
      </a>
    </div>
  </Popover>
</template>

<script setup>
const menuItems = [
  { label: "查看详情", action: () => {} },
  { label: "编辑信息", action: () => {} },
  { label: "删除", action: () => {} },
];
</script>
```

### 用户信息卡片

展示丰富的用户信息卡片：

```vue
<template>
  <Popover trigger="hover" placement="right" :delay="200">
    <template #reference>
      <span class="text-blue-500 hover:underline cursor-pointer"> 张三 </span>
    </template>

    <div class="w-72 p-4">
      <div class="flex items-start space-x-4">
        <img src="avatar.jpg" class="w-16 h-16 rounded-full" />
        <div>
          <h3 class="font-bold text-lg">张三</h3>
          <p class="text-gray-500 text-sm">前端开发工程师</p>
          <div class="mt-2 text-sm">
            <p>✉️ zhangsan@example.com</p>
            <p>📱 138****1234</p>
          </div>
        </div>
      </div>

      <div class="mt-4 pt-4 border-t dark:border-zinc-700">
        <button class="w-full py-2 bg-blue-500 text-white rounded">
          发送消息
        </button>
      </div>
    </div>
  </Popover>
</template>
```

### 快速编辑表单

在气泡中实现表单编辑功能：

```vue
<template>
  <Popover trigger="click" placement="right" customClass="shadow-xl">
    <template #reference>
      <button class="btn-yellow">编辑信息</button>
    </template>

    <form @submit.prevent="onSubmit" class="w-80 p-4">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-1">标题</label>
          <input
            v-model="form.title"
            type="text"
            class="w-full px-3 py-2 border rounded"
          />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            class="w-full px-3 py-2 border rounded"
          >
          </textarea>
        </div>

        <div class="flex justify-end space-x-2">
          <button type="button" class="px-4 py-2 border rounded">取消</button>
          <button
            type="submit"
            class="px-4 py-2 bg-blue-500 text-white rounded"
          >
            保存
          </button>
        </div>
      </div>
    </form>
  </Popover>
</template>
```

## 📖 API

### Props

| 参数        | 说明             | 类型                                   | 默认值   |
| ----------- | ---------------- | -------------------------------------- | -------- |
| placement   | 弹出位置         | 'top' \| 'right' \| 'bottom' \| 'left' | 'bottom' |
| offset      | 偏移距离         | number                                 | 8        |
| delay       | 延迟关闭时间(ms) | number                                 | 300      |
| trigger     | 触发方式         | 'hover' \| 'click'                     | 'hover'  |
| customClass | 自定义类名       | string                                 | ''       |

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
