<template>
  <button
    class="text-sm text-center rounded duration-150 flex justify-center items-center"
    :class="[
      typeEnum[type as keyof typeof typeEnum],
      sizeEnum[size as keyof typeof sizeEnum].button,
      { 'active:scale-105': isActiveAnim },
      { 'flex-row-reverse': icon }, // 如果有 icon，文字在右边
    ]"
    @click.stop="onBtnClick"
  >
    <!-- 展示 loading -->
    <i v-if="loading" class="i-tabler:loader w-2 h-2 animate-spin mr-1"></i>

    <!-- icon 按钮 -->
    <i
      v-if="icon"
      class="m-1"
      :class="[sizeEnum[size as keyof typeof sizeEnum].icon, icon]"
      :style="{ color: iconColor }"
    ></i>

    <!-- 文字按钮 -->
    <span v-else class="m-1">
      <slot />
    </span>
  </button>
</template>
<script lang="ts">
// 定义按钮类型样式
const typeEnum = {
  primary:
    "text-white bg-indigo-500 hover:bg-indigo-600 active:bg-indigo-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 dark:active:bg-indigo-800",
  warning:
    "text-white bg-yellow-500 hover:bg-yellow-600 active:bg-yellow-700 dark:bg-yellow-600 dark:hover:bg-yellow-700 dark:active:bg-yellow-800",
  error:
    "text-white bg-red-500 hover:bg-red-600 active:bg-red-700 dark:bg-red-600 dark:hover:bg-red-700 dark:active:bg-red-800",
  info: "text-white bg-gray-500 hover:bg-gray-600 active:bg-gray-700 dark:bg-gray-600 dark:hover:bg-gray-700 dark:active:bg-gray-800",
};

// 定义按钮大小样式
const sizeEnum = {
  default: {
    button: "text-base px-4 py-2",
    icon: "w-6 h-6",
  },
  small: {
    button: "text-sm px-3 py-1",
    icon: "w-4 h-4",
  },
  large: {
    button: "text-lg px-6 py-3",
    icon: "w-8 h-8",
  },
};
</script>
<script setup lang="ts">
// 定义 props
const props = defineProps({
  icon: {
    type: String,
    default: "",
  },
  iconColor: {
    type: String,
    default: "",
  },
  type: {
    type: String,
    default: "primary",
    validator(val: string) {
      const keys = Object.keys(typeEnum);
      const result = keys.includes(val);
      if (!result) {
        throw new Error(`你的 type 必须是 ${keys.join("、")} 中的一个`);
      }
      return result;
    },
  },
  size: {
    type: String,
    default: "default",
    validator(val: string) {
      const keys = Object.keys(sizeEnum);
      const result = keys.includes(val);
      if (!result) {
        throw new Error(`你的 size 必须是 ${keys.join("、")} 中的一个`);
      }
      return result;
    },
  },
  isActiveAnim: {
    type: Boolean,
    default: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

// 定义 emits
const emits = defineEmits(["click"]);

// 按钮点击事件处理
const onBtnClick = () => {
  if (props.loading) {
    return;
  }
  emits("click");
};
</script>
