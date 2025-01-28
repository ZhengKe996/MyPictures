# Pagination 分页组件

一个优雅的 Vue 3 分页组件，基于 TypeScript 和 Tailwind CSS 构建。

## 功能特点

- 🚀 基于 Vue 3 + TypeScript + Setup 语法
- 💅 使用 Tailwind CSS 构建，样式美观
- 📱 响应式设计，适配移动端
- ✨ 平滑过渡动画效果
- 🔄 支持双向绑定
- 🎯 支持快速跳转
- 🔒 支持禁用状态

## 基础用法

### 1. 简单分页

最基础的分页用法，展示页码和翻页按钮。

```vue
<template>
  <Pagination v-model:current="page" :total="100" />
</template>

<script setup lang="ts">
import { ref } from "vue";
import Pagination from "./Pagination.vue";

const page = ref(1);
</script>
```

### 2. 快速跳转

添加快速跳转输入框，方便直接输入页码。

```vue
<template>
  <Pagination
    v-model:current="page"
    :total="500"
    :show-quick-jumper="true"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";

const page = ref(1);
const handleChange = (newPage: number) => {
  console.log("页码变更为:", newPage);
};
</script>
```

### 3. 自定义分页大小

结合下拉选择框，实现动态改变每页显示条数。

```vue
<template>
  <div class="space-y-4">
    <Pagination
      v-model:current="page"
      :total="total"
      :page-size="pageSize"
      @change="handlePageChange"
    />

    <div class="flex items-center gap-2">
      <span>每页显示：</span>
      <select v-model="pageSize" class="border rounded px-2 py-1">
        <option :value="10">10 条</option>
        <option :value="20">20 条</option>
        <option :value="50">50 条</option>
      </select>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const page = ref(1);
const total = ref(500);
const pageSize = ref(20);

const handlePageChange = (newPage: number) => {
  // 在这里处理页码变化
  loadData(newPage, pageSize.value);
};
</script>
```

### 4. 在表格中使用

与表格组件结合使用的完整示例。

```vue
<template>
  <div class="space-y-4">
    <!-- 表格内容 -->
    <table class="min-w-full">
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in tableData" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>
            <button @click="handleEdit(item)">编辑</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 分页组件 -->
    <Pagination
      v-model:current="tableParams.page"
      :total="total"
      :page-size="tableParams.pageSize"
      :disabled="loading"
      @change="fetchTableData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";

const loading = ref(false);
const total = ref(0);
const tableData = ref([]);

const tableParams = reactive({
  page: 1,
  pageSize: 20,
});

const fetchTableData = async (page: number) => {
  loading.value = true;
  try {
    const { data, total: totalCount } = await api.getList({
      page,
      pageSize: tableParams.pageSize,
    });
    tableData.value = data;
    total.value = totalCount;
  } finally {
    loading.value = false;
  }
};
</script>
```

### 5. 路由集成

与 Vue Router 集成，实现 URL 同步。

```vue
<template>
  <Pagination
    v-model:current="currentPage"
    :total="total"
    @change="handlePageChange"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const currentPage = ref(1);
const total = ref(100);

// 从路由同步页码
onMounted(() => {
  const page = Number(route.query.page) || 1;
  currentPage.value = page;
});

// 更新路由
const handlePageChange = (page: number) => {
  router.push({
    query: { ...route.query, page: String(page) },
  });
};
</script>
```

## API 说明

### Props

| 参数            | 说明         | 类型      | 默认值  |
| --------------- | ------------ | --------- | ------- |
| total           | 数据总数     | `number`  | `0`     |
| current         | 当前页码     | `number`  | `1`     |
| pageSize        | 每页条数     | `number`  | `20`    |
| disabled        | 禁用状态     | `boolean` | `false` |
| showQuickJumper | 显示快速跳转 | `boolean` | `false` |

### Events

| 事件名         | 说明           | 回调参数                 |
| -------------- | -------------- | ------------------------ |
| update:current | 页码改变时触发 | `(page: number) => void` |
| change         | 页码改变时触发 | `(page: number) => void` |

## 注意事项和最佳实践

1. 处理加载状态

```ts
const loading = ref(false);

const handlePageChange = async (page: number) => {
  loading.value = true;
  try {
    await loadData(page);
  } finally {
    loading.value = false;
  }
};
```

2. 错误处理

```ts
const handlePageChange = async (page: number) => {
  try {
    await loadData(page);
  } catch (error) {
    // 显示错误提示
    showError("数据加载失败");
    console.error(error);
  }
};
```

3. 重置页码

```ts
const resetPagination = () => {
  currentPage.value = 1;
  // 可能需要重新加载数据
  loadData(1);
};
```
