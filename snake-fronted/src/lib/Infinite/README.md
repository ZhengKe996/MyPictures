# Infinite Scroll Component

一个基于 Vue 3 的无限滚动加载组件，支持自定义加载样式、错误处理等特性。

## 属性

| 属性名        | 类型    | 默认值               | 说明                       |
| ------------- | ------- | -------------------- | -------------------------- |
| modelValue    | boolean | false                | 加载状态                   |
| isFinished    | boolean | false                | 是否已加载全部数据         |
| loadingText   | string  | '加载中...'          | 加载提示文本               |
| finishedText  | string  | '没有更多数据了'     | 加载完成提示文本           |
| error         | boolean | false                | 是否加载出错               |
| errorText     | string  | '加载失败，点击重试' | 错误提示文本               |
| threshold     | number  | 100                  | 触发加载的距离阈值(px)     |
| immediateLoad | boolean | true                 | 是否在初始化时立即执行加载 |

## 事件

| 事件名  | 参数 | 说明                   |
| ------- | ---- | ---------------------- |
| onLoad  | -    | 加载更多时触发         |
| onRetry | -    | 错误状态点击重试时触发 |

## 插槽

| 插槽名   | 说明               |
| -------- | ------------------ |
| default  | 列表内容           |
| loading  | 自定义加载中提示   |
| finished | 自定义加载完成提示 |
| error    | 自定义错误提示     |

## 基础用法

```vue
<template>
  <Infinite v-model="loading" :is-finished="finished" @on-load="loadMore">
    <div v-for="item in list" :key="item.id">
      {{ item.name }}
    </div>
  </Infinite>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Infinite from "./Infinite.vue";

const loading = ref(false);
const finished = ref(false);
const list = ref([]);

const loadMore = async () => {
  try {
    const newData = await fetchData();
    list.value.push(...newData);
    loading.value = false;
    if (newData.length < 10) {
      finished.value = true;
    }
  } catch (error) {
    loading.value = false;
  }
};
</script>
```

## 自定义样式示例

```vue
<template>
  <Infinite
    v-model="loading"
    :is-finished="finished"
    loading-text="玩命加载中..."
    finished-text="我是有底线的"
    @on-load="loadMore"
  >
    <template #loading>
      <div class="custom-loading">
        <!-- 自定义加载动画 -->
      </div>
    </template>
    <!-- 列表内容 -->
  </Infinite>
</template>
```

## 错误处理示例

```vue
<template>
  <Infinite
    v-model="loading"
    :is-finished="finished"
    :error="error"
    @on-load="loadMore"
    @on-retry="handleRetry"
  >
    <!-- 列表内容 -->
  </Infinite>
</template>
```
