# 按钮组件

这是一个可定制的 Vue 3 按钮组件，支持各种样式、尺寸和状态。

## 属性

- `type`（可选）：定义按钮样式。可能的值有 `"primary"`、`"secondary"`、`"success"`、`"warning"`、`"error"`、`"info"`、`"ghost"`。默认值为 `"primary"`。
- `size`（可选）：定义按钮尺寸。可能的值有 `"xs"`、`"sm"`、`"md"`、`"lg"`、`"xl"`。默认值为 `"md"`。
- `icon`（可选）：按钮内显示的图标类。
- `iconColor`（可选）：图标的颜色。
- `loading`（可选）：显示加载状态的布尔值。默认值为 `false`。
- `disabled`（可选）：禁用按钮的布尔值。默认值为 `false`。
- `isActiveAnim`（可选）：启用活动动画的布尔值。默认值为 `true`。
- `block`（可选）：使按钮占据全宽的布尔值。默认值为 `false`。

## 事件

- `click`：按钮点击时触发。

## 用法

### 基本示例

```vue
<template>
  <Button @click="handleClick">点击我</Button>
</template>

<script setup lang="ts">
import Button from "./Button.vue";

const handleClick = (event: MouseEvent) => {
  console.log("按钮被点击了！", event);
};
</script>
```

### 带图标的按钮

```vue
<template>
  <Button icon="i-tabler-check" iconColor="white" @click="handleClick">
    提交
  </Button>
</template>

<script setup lang="ts">
import Button from "./Button.vue";

const handleClick = (event: MouseEvent) => {
  console.log("按钮被点击了！", event);
};
</script>
```

### 加载状态

```vue
<template>
  <Button :loading="true">加载中...</Button>
</template>

<script setup lang="ts">
import Button from "./Button.vue";
</script>
```

### 禁用状态

```vue
<template>
  <Button :disabled="true">已禁用</Button>
</template>

<script setup lang="ts">
import Button from "./Button.vue";
</script>
```

### 不同尺寸

```vue
<template>
  <div class="space-y-2">
    <Button size="xs">超小</Button>
    <Button size="sm">小</Button>
    <Button size="md">中</Button>
    <Button size="lg">大</Button>
    <Button size="xl">超大</Button>
  </div>
</template>

<script setup lang="ts">
import Button from "./Button.vue";
</script>
```

### 不同类型

```vue
<template>
  <div class="space-y-2">
    <Button type="primary">主要</Button>
    <Button type="secondary">次要</Button>
    <Button type="success">成功</Button>
    <Button type="warning">警告</Button>
    <Button type="error">错误</Button>
    <Button type="info">信息</Button>
    <Button type="ghost">幽灵</Button>
  </div>
</template>

<script setup lang="ts">
import Button from "./Button.vue";
</script>
```

### 块级按钮

```vue
<template>
  <Button :block="true">块级按钮</Button>
</template>

<script setup lang="ts">
import Button from "./Button.vue";
</script>
```

## 结论

这个按钮组件高度可定制，可以在各种场景中使用。调整属性以满足您的需求，并为您的应用程序创建一致的用户界面。
