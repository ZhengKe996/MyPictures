# ModalBox 模态框组件

一个灵活的模态框组件，支持组件式调用和函数式调用两种使用方式。

## 特性

- 支持点击遮罩层关闭
- 支持全屏显示模式
- 自定义过渡动画效果
- 支持组件式和函数式两种调用方式
- 支持自定义内容样式

## 组件式使用

### 基础用法

```vue
<template>
  <ModalBox v-model="showModal">
    <div>模态框内容</div>
  </ModalBox>
</template>

<script setup>
import { ref } from "vue";
import ModalBox from "@/lib/ModalBox";

const showModal = ref(false);
</script>
```

### 属性说明

| 属性名              | 类型    | 默认值 | 说明                             |
| ------------------- | ------- | ------ | -------------------------------- |
| modelValue          | boolean | false  | 控制模态框显示状态，支持 v-model |
| closeOnClickOverlay | boolean | true   | 是否允许点击遮罩层关闭模态框     |
| fullscreen          | boolean | false  | 是否启用全屏模式                 |
| contentClass        | string  | ''     | 自定义内容容器的样式类           |

### 事件

| 事件名            | 说明                 |
| ----------------- | -------------------- |
| update:modelValue | 模态框状态改变时触发 |
| close             | 模态框关闭时触发     |

## 最佳实践

### 图片查看器示例

```vue
<template>
  <ModalBox
    v-model="showModal"
    :fullscreen="true"
    :closeOnClickOverlay="true"
    contentClass="flex items-center justify-center w-screen h-screen p-4"
  >
    <div class="w-screen h-screen p-4 flex items-center justify-center">
      <img
        :src="imageUrl"
        :alt="imageAlt"
        class="max-w-[95vw] max-h-[95vh] w-auto h-auto object-contain select-none"
        @click.stop
        @contextmenu.prevent
      />
    </div>
  </ModalBox>
</template>
```

### 图片网格示例

```vue
<template>
  <div class="grid grid-cols-3 gap-4">
    <div
      v-for="image in images"
      :key="image.id"
      @click="showImageModal({ imageUrl: image.url })"
    >
      <img :src="image.thumbnailUrl" class="w-full h-full object-cover" />
    </div>
  </div>
</template>
```

## 函数式调用

组件提供了 `showImageModal` 函数用于快速显示图片。

### 基础用法

```typescript
import { showImageModal } from "@/lib/ModalBox";

// 显示图片
showImageModal({
  imageUrl: "https://example.com/image.jpg",
  altText: "图片说明",
  onClose: () => {
    console.log("模态框已关闭");
  },
});
```

### 函数参数说明

```typescript
interface ShowImageModalOptions {
  imageUrl: string; // 图片URL
  altText?: string; // 图片替代文本
  onClose?: () => void; // 关闭回调函数
}
```

## 注意事项

1. 模态框组件使用 `Teleport` 将内容传送到 `body` 层级，确保模态框始终显示在最上层
2. 全屏模式下会使用不同的过渡动画效果
3. 函数式调用主要用于图片查看场景，如需更复杂的自定义内容，建议使用组件式调用
4. 在显示图片时，建议添加以下处理：
   - 使用 `@click.stop` 防止点击图片时关闭模态框
   - 使用 `@contextmenu.prevent` 禁用右键菜单
   - 使用 `select-none` 类防止图片被选中
   - 使用 `object-contain` 确保图片完整显示且不变形
5. 对于长图或宽图，建议使用以下样式类组合：
   ```html
   <div class="w-screen h-screen p-4 flex items-center justify-center">
     <img class="max-w-[95vw] max-h-[95vh] w-auto h-auto object-contain" />
   </div>
   ```

## 常见问题

1. **模态框无法关闭**

   - 检查是否在内容上添加了 `@click.stop` 但没有提供关闭按钮
   - 确保 `closeOnClickOverlay` 属性设置正确

2. **图片显示不完整**

   - 使用推荐的样式类组合
   - 确保设置了正确的最大宽高限制
   - 使用 `object-contain` 而不是 `object-cover`

3. **全屏模式下的性能**
   - 对大图片使用缩略图预加载
   - 考虑使用渐进式加载
