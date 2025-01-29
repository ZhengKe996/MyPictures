# Vue3 图片瀑布流组件

一个高性能、可定制的 Vue3 瀑布流布局组件，适用于图片展示、商品列表等场景。

## ✨ 特性

- 🚀 高性能渲染，支持大规模数据
- 💪 完整的 TypeScript 类型支持
- 🎨 支持图片预加载，避免布局抖动
- 📱 响应式布局，自动适应容器宽度
- 🛠 高度可定制的插槽系统
- 🎯 精确的元素定位和动画效果
- 🔄 支持动态列数调整
- 📦 零依赖，轻量化设计

## 🚀 快速开始

```vue
<template>
  <Waterfall
    :data="imageList"
    :column="3"
    nodeKey="id"
    :columnSpacing="16"
    :rowSpacing="16"
  >
    <template #default="{ item, width }">
      <div class="image-card">
        <img
          :src="item.url"
          :alt="item.title"
          class="w-full h-auto object-cover rounded-lg"
        />
        <div class="p-3">
          <h3 class="text-lg font-medium">{{ item.title }}</h3>
        </div>
      </div>
    </template>
  </Waterfall>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Waterfall from "@mylib/vue3-waterfall";

interface ImageItem {
  id: number;
  url: string;
  title: string;
}

const imageList = ref<ImageItem[]>([
  {
    id: 1,
    url: "/images/1.jpg",
    title: "示例图片1",
  },
  // ... 更多图片数据
]);
</script>
```

## 📝 Props

| 参数              | 说明               | 类型    | 默认值 | 是否必填 |
| ----------------- | ------------------ | ------- | ------ | -------- |
| data              | 瀑布流数据源       | array   | []     | 是       |
| nodeKey           | 数据项唯一标识字段 | string  | -      | 是       |
| column            | 列数               | number  | 2      | 否       |
| columnSpacing     | 列间距(px)         | number  | 20     | 否       |
| rowSpacing        | 行间距(px)         | number  | 20     | 否       |
| picturePreReading | 开启图片预加载     | boolean | true   | 否       |
| animation         | 开启过渡动画       | boolean | true   | 否       |

## 🎈 插槽

### default

默认插槽用于自定义每个数据项的渲染内容。

```typescript
interface SlotProps {
  item: any; // 当前数据项
  width: number; // 当前列宽度
  index: number; // 数据项索引
}
```

## 🛠 方法

| 方法名 | 说明                 | 参数 | 返回值        |
| ------ | -------------------- | ---- | ------------- |
| reset  | 重新计算并布局瀑布流 | -    | Promise<void> |

## 💡 最佳实践

1. 图片加载优化

```vue
<template #default="{ item, width }">
  <div class="image-wrapper">
    <img
      :src="item.url"
      :alt="item.title"
      loading="lazy"
      class="w-full transition-opacity duration-300"
      @load="handleImageLoad"
    />
  </div>
</template>
```

2. 响应式列数

```vue
<template>
  <Waterfall :data="items" :column="getColumnCount()" nodeKey="id">
    <!-- 插槽内容 -->
  </Waterfall>
</template>

<script setup>
const getColumnCount = () => {
  const width = window.innerWidth;
  if (width < 640) return 1;
  if (width < 1024) return 2;
  return 3;
};
</script>
```

## ⚠️ 注意事项

1. 确保提供的 nodeKey 在数据项中是唯一的
2. ~~. 大数据量场景建议开启 picturePreReading~~
3. 如遇到性能问题，可以调整 animation 和过渡效果
4. 对于图片密集型场景，建议使用图片懒加载

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 帮助改进组件。

## 📄 许可证

MIT License
