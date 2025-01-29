# Avatar 组件文档

一个灵活的头像组件，可以显示用户信息，并具有可选的链接功能。

## 特性

- 可配置的头像尺寸
- 可选的链接功能
- 用户名显示
- 可选的角色显示
- 悬停效果
- 响应式设计

## 属性

| 属性名      | 类型    | 默认值 | 描述                             |
| ----------- | ------- | ------ | -------------------------------- |
| linkable    | boolean | false  | 使头像可点击并包裹在一个链接中   |
| href        | string  | ''     | 当 linkable 为 true 时的链接 URL |
| size        | string  | 'md'   | 头像尺寸（'sm', 'md', 'lg' 等）  |
| userAvatar  | string  | ''     | 用户头像图片的 URL               |
| userName    | string  | ''     | 用户的显示名称                   |
| userRole    | string  | ''     | 用户的角色或头衔                 |
| showRole    | boolean | false  | 是否显示用户角色                 |
| customClass | string  | ''     | 要应用的额外 CSS 类              |

## 使用示例

```vue
<template>
  <Avatar
    :linkable="true"
    href="https://example.com"
    size="lg"
    userAvatar="https://example.com/avatar.jpg"
    userName="John Doe"
    userRole="Administrator"
    :showRole="true"
    customClass="custom-avatar-class"
  />
</template>

<script setup lang="ts">
import Avatar from "./Avatar.vue";
</script>
```
