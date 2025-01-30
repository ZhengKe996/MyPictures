# Infinite Scroll Component

一个基于 Vue 3 的无限滚动加载组件，支持自定义加载样式、错误处理等特性。

## 属性

| 属性名         | 类型    | 默认值       | 说明                   |
| -------------- | ------- | ------------ | ---------------------- |
| modelValue     | boolean | false        | 加载状态               |
| isFinished     | boolean | false        | 是否已加载全部数据     |
| threshold      | number  | 100          | 触发加载的距离阈值(px) |
| distance       | number  | 0            | 额外的触发距离         |
| loadingText    | string  | '加载中...'  | 加载提示文本           |
| finishedText   | string  | '没有更多了' | 加载完成提示文本       |
| errorText      | string  | '加载失败'   | 错误提示文本           |
| immediateCheck | boolean | true         | 是否立即检查加载       |

## 事件

| 事件名            | 参数    | 说明         |
| ----------------- | ------- | ------------ |
| update:modelValue | boolean | 更新加载状态 |
| on-load           | -       | 触发加载操作 |

## 插槽

| 插槽名   | 说明               |
| -------- | ------------------ |
| default  | 列表内容           |
| loading  | 自定义加载中提示   |
| finished | 自定义加载完成提示 |
| error    | 自定义错误提示     |

## 方法

| 方法名 | 说明         |
| ------ | ------------ |
| reset  | 重置组件状态 |

## 基础用法

```vue
<template>
  <Infinite v-model="loading" :is-finished="finished" @on-load="loadMore">
    <div v-for="item in list" :key="item">
      {{ item }}
    </div>
  </Infinite>
</template>

<script setup lang="ts">
import { ref } from "vue";

const loading = ref(false);
const finished = ref(false);
const list = ref<number[]>([]);

const loadMore = async () => {
  // 模拟异步加载
  const newItems = Array.from({ length: 10 }, (_, i) => list.value.length + i);
  list.value.push(...newItems);
  loading.value = false;

  if (list.value.length >= 40) {
    finished.value = true;
  }
};
</script>
```
