# Generic Tooltip Component

一个基于 Vue 3 + TypeScript + Tailwind CSS 的通用提示框组件。

## 特性

- 🎯 支持四个方向的定位（上、下、左、右）
- 🎨 支持自定义触发器和内容
- 📱 响应式设计，自动调整位置
- 🌓 支持暗色模式
- ⚡️ 平滑的过渡动画

## 安装

```bash
# 确保已安装依赖
npm install vue@3.x typescript tailwindcss
```

## 属性说明

| 属性名   | 类型                                   | 默认值 | 描述           |
| -------- | -------------------------------------- | ------ | -------------- |
| content  | string                                 | ''     | 提示框内容     |
| position | 'top' \| 'bottom' \| 'left' \| 'right' | 'top'  | 提示框显示位置 |
| offset   | number                                 | 10     | 提示框偏移距离 |

## 插槽

| 插槽名  | 描述             |
| ------- | ---------------- |
| trigger | 自定义触发器     |
| content | 自定义提示框内容 |

## 使用示例

### 基础用法

```vue
<template>
  <GenericTooltip content="这是一个简单的提示">
    <template #trigger>
      <button>悬停查看提示</button>
    </template>
  </GenericTooltip>
</template>
```

### 自定义位置

```vue
<template>
  <GenericTooltip content="右侧提示" position="right">
    <template #trigger>
      <span>右侧提示</span>
    </template>
  </GenericTooltip>
</template>
```

### 自定义内容

```vue
<template>
  <GenericTooltip position="top">
    <template #trigger>
      <button class="px-4 py-2 bg-blue-500 text-white rounded">高级提示</button>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <h3 class="font-bold">自定义标题</h3>
        <p>这里可以放置更复杂的内容</p>
        <div class="flex items-center gap-2">
          <span>⭐️</span>
          <span>包含图标</span>
        </div>
      </div>
    </template>
  </GenericTooltip>
</template>
```

### 不同方向的示例

```vue
<template>
  <div class="flex gap-4">
    <GenericTooltip content="上方提示" position="top">
      <template #trigger>
        <button>上</button>
      </template>
    </GenericTooltip>

    <GenericTooltip content="右侧提示" position="right">
      <template #trigger>
        <button>右</button>
      </template>
    </GenericTooltip>

    <GenericTooltip content="下方提示" position="bottom">
      <template #trigger>
        <button>下</button>
      </template>
    </GenericTooltip>

    <GenericTooltip content="左侧提示" position="left">
      <template #trigger>
        <button>左</button>
      </template>
    </GenericTooltip>
  </div>
</template>
```

### 自定义偏移距离

```vue
<template>
  <GenericTooltip content="更远的提示" position="top" :offset="20">
    <template #trigger>
      <button>更大偏移</button>
    </template>
  </GenericTooltip>
</template>
```

## 注意事项

1. 确保父容器为 `relative` 定位
2. 如果 tooltip 内容过长，将自动截断
3. 窗口大小改变时会自动重新计算位置
4. 支持深色模式，自动跟随系统主题

## 样式定制

组件使用 Tailwind CSS 类名，可以通过覆盖以下类来自定义样式：

```css
/* 自定义提示框样式 */
.tooltip {
  @apply px-5 py-3 text-center text-gray-600 bg-white rounded-lg shadow-lg;
}

/* 深色模式 */
.dark .tooltip {
  @apply text-white bg-gray-800;
}
```
