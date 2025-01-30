<template>
  <span
    :class="[
      baseClasses,
      sizeClasses,
      variantClasses,
      clickable ? 'cursor-pointer hover:opacity-80' : '',
    ]"
    @click="handleClick"
  >
    <slot>{{ text }}</slot>
  </span>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { TagProps } from "./index";

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
  "rounded",
  "transition-all",
  "duration-200",
]);

const sizeClasses = computed(() => ({
  "text-xs py-1 px-2": props.size === "sm",
  "text-sm py-1.5 px-3": props.size === "md",
  "text-base py-2 px-4": props.size === "lg",
}));

const variantClasses = computed(() => {
  if (props.customColor) {
    return `bg-${props.customColor} text-white`;
  }

  const variants = {
    primary: "bg-blue-600 text-white",
    secondary: "bg-gray-500 text-white",
    success: "bg-green-500 text-white",
    danger: "bg-red-500 text-white",
    warning: "bg-yellow-500 text-white",
    info: "bg-purple-400 text-white",
    light: "bg-gray-100 text-gray-800",
    dark: "bg-gray-800 text-white",
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
  transition: all 0.3s ease;
}

.tag-enter-from,
.tag-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
