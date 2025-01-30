<template>
  <span
    :class="[
      baseClasses,
      sizeClasses,
      variantClasses,
      clickable ? 'cursor-pointer hover:opacity-90 active:scale-95' : '',
    ]"
    @click="handleClick"
  >
    <slot>{{ text }}</slot>
  </span>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import type { TagProps } from "./index";
import { getRandomColor } from "./index";

const props = withDefaults(defineProps<TagProps>(), {
  variant: "primary",
  clickable: false,
  size: "md",
});

const emit = defineEmits<{
  (e: "click", value: string): void;
}>();

const baseClasses = computed(() => [
  "inline-block",
  "font-bold",
  "text-center",
  "rounded-full", // 改为圆角效果
  "transition-all",
  "duration-300",
  "ease-in-out",
  "shadow-sm",
  "backdrop-blur-sm",
]);

const sizeClasses = computed(() => ({
  "text-xs py-1 px-2": props.size === "sm",
  "text-sm py-1.5 px-3": props.size === "md",
  "text-base py-2 px-4": props.size === "lg",
}));

const randomBgColor = ref("");

onMounted(() => {
  randomBgColor.value = getRandomColor();
});

const variantClasses = computed(() => {
  if (props.customColor) {
    return `bg-${props.customColor} text-white`;
  }

  if (!props.variant) {
    return `bg-${randomBgColor.value} text-white`;
  }

  const variants = {
    primary: "bg-blue-5 text-white",
    secondary: "bg-gray-5 text-white",
    success: "bg-green-5 text-white",
    danger: "bg-red-5 text-white",
    warning: "bg-yellow-5 text-white",
    info: "bg-cyan-5 text-white", // 更改为 cyan
    light: "bg-gray-3 text-gray-8",
    dark: "bg-gray-7 text-white",
  };

  return variants[props.variant];
});

const handleClick = () => {
  if (props.clickable) {
    emit("click", props.text);
  }
};
</script>

<style scoped>
.tag-enter-active,
.tag-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-enter-from,
.tag-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

span {
  backface-visibility: hidden;
  transform: translateZ(0);
  -webkit-font-smoothing: subpixel-antialiased;
}
</style>
