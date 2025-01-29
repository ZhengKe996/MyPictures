<template>
  <button
    :disabled="disabled || loading"
    class="inline-flex items-center justify-center rounded-md transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
    :class="[
      variantStyles,
      sizeStyles,
      { 'hover:scale-105 active:scale-100': isActiveAnim },
      { 'flex-row-reverse': icon && !loading },
      { 'opacity-75 cursor-wait': loading },
    ]"
    @click="handleClick"
  >
    <span v-if="loading" class="mr-2">
      <i class="i-tabler:loader animate-spin w-4 h-4"></i>
    </span>

    <i
      v-else-if="icon"
      :class="[icon, iconSizeStyles]"
      :style="{ color: iconColor }"
      class="transform transition-transform"
    ></i>

    <span :class="[{ 'ml-2': icon && !loading, 'mr-2': loading }]">
      <slot />
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from "vue";

type ButtonType =
  | "primary"
  | "secondary"
  | "success"
  | "warning"
  | "error"
  | "info"
  | "ghost";
type ButtonSize = "xs" | "sm" | "md" | "lg" | "xl";

interface Props {
  type?: ButtonType;
  size?: ButtonSize;
  icon?: string;
  iconColor?: string;
  loading?: boolean;
  disabled?: boolean;
  isActiveAnim?: boolean;
  block?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  type: "primary",
  size: "md",
  loading: false,
  disabled: false,
  isActiveAnim: true,
  block: false,
});

const emit = defineEmits<{
  (e: "click", event: MouseEvent): void;
}>();

const variantStyles = computed(
  () =>
    ({
      primary:
        "bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500",
      secondary:
        "bg-gray-200 text-gray-900 hover:bg-gray-300 focus:ring-gray-500 dark:bg-gray-700 dark:text-gray-100",
      success:
        "bg-green-600 text-white hover:bg-green-700 focus:ring-green-500",
      warning:
        "bg-yellow-500 text-white hover:bg-yellow-600 focus:ring-yellow-500",
      error: "bg-red-600 text-white hover:bg-red-700 focus:ring-red-500",
      info: "bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500",
      ghost:
        "bg-transparent hover:bg-gray-100 text-gray-700 dark:text-gray-300 dark:hover:bg-gray-800",
    }[props.type])
);

const sizeStyles = computed(
  () =>
    ({
      xs: "px-2 py-1 text-xs",
      sm: "px-3 py-1.5 text-sm",
      md: "px-4 py-2 text-base",
      lg: "px-5 py-2.5 text-lg",
      xl: "px-6 py-3 text-xl",
    }[props.size])
);

const iconSizeStyles = computed(
  () =>
    ({
      xs: "w-3 h-3",
      sm: "w-4 h-4",
      md: "w-5 h-5",
      lg: "w-6 h-6",
      xl: "w-7 h-7",
    }[props.size])
);

const handleClick = (event: MouseEvent) => {
  if (!props.loading && !props.disabled) {
    emit("click", event);
  }
};
</script>
