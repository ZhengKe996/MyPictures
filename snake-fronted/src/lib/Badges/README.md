# Badges 标签组件

一个高度可定制的 Vue 3 标签组件，支持 TypeScript 和 Tailwind CSS。

## 特性

- 多种样式变体（实心、柔和、描边）
- 八种颜色主题
- 三种尺寸选项
- 可自定义圆角
- 支持点击事件
- 支持前缀和后缀插槽
- TypeScript 支持
- 无障碍访问支持

## 基础用法

```vue
<template>
  <Badges text="默认标签" />
  <Badges text="成功" color="green" />
  <Badges text="警告" color="yellow" />
  <Badges text="错误" color="red" />
</template>
```

## 样式变体

```vue
<template>
  <Badges text="实心样式" variant="solid" />
  <Badges text="柔和样式" variant="soft" />
  <Badges text="描边样式" variant="outline" />
</template>
```

## 尺寸选项

```vue
<template>
  <Badges text="小尺寸" size="sm" />
  <Badges text="中等尺寸" size="md" />
  <Badges text="大尺寸" size="lg" />
</template>
```

## 圆角设置

```vue
<template>
  <Badges text="小圆角" rounded="sm" />
  <Badges text="中等圆角" rounded="md" />
  <Badges text="大圆角" rounded="lg" />
  <Badges text="完全圆角" rounded="full" />
</template>
```

## 可点击标签

```vue
<template>
  <Badges text="点击我" clickable @click="handleClick" />
</template>

<script setup>
const handleClick = (text) => {
  console.log(`点击了标签：${text}`);
};
</script>
```

## 使用插槽

```vue
<template>
  <Badges text="带图标">
    <template #prefix>
      <IconStar class="w-4 h-4 mr-1" />
    </template>
  </Badges>
</template>
```

## Props

| 属性名    | 类型    | 默认值 | 说明                                                      |
| --------- | ------- | ------ | --------------------------------------------------------- |
| text      | string  | -      | 标签文本内容                                              |
| color     | string  | 'blue' | 颜色主题：red/orange/yellow/green/blue/indigo/purple/gray |
| size      | string  | 'md'   | 尺寸：sm/md/lg                                            |
| variant   | string  | 'soft' | 样式变体：solid/soft/outline                              |
| rounded   | string  | 'md'   | 圆角大小：sm/md/lg/full                                   |
| clickable | boolean | false  | 是否可点击                                                |
| title     | string  | -      | 鼠标悬停提示文本                                          |

## 事件

| 事件名 | 参数           | 说明                         |
| ------ | -------------- | ---------------------------- |
| click  | (text: string) | 点击标签时触发，返回标签文本 |
