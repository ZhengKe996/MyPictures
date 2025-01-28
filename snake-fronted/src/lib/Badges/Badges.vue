<template>
  <span
    :class="[variantClasses, roundedClasses, cursorClasses]"
    class="inline-flex items-center py-0.5 font-medium transition-colors"
    :title="title"
    @click="handleClick"
    role="status"
  >
    <slot name="prefix"></slot>
    {{ text }}
    <slot name="suffix"></slot>
  </span>
</template>

<script setup lang="ts">
import { computed } from "vue";

type ColorScheme =
  | "red"
  | "orange"
  | "yellow"
  | "green"
  | "blue"
  | "indigo"
  | "purple"
  | "gray";
type Size = "sm" | "md" | "lg";
type Variant = "solid" | "soft" | "outline";
type Rounded = "sm" | "md" | "lg" | "full";

interface Props {
  text: string;
  color?: ColorScheme;
  size?: Size;
  variant?: Variant;
  rounded?: Rounded;
  index?: number;
  title?: string;
  clickable?: boolean;
}

const emit = defineEmits<{
  (e: "click", value: string): void;
}>();

const props = withDefaults(defineProps<Props>(), {
  color: "blue",
  size: "md",
  variant: "soft",
  rounded: "md",
  index: 0,
  clickable: false,
});

const variantMap = {
  solid: {
    red: "bg-red-500 text-white",
    orange: "bg-orange-500 text-white",
    yellow: "bg-yellow-500 text-white",
    green: "bg-green-500 text-white",
    blue: "bg-blue-500 text-white",
    indigo: "bg-indigo-500 text-white",
    purple: "bg-purple-500 text-white",
    gray: "bg-gray-500 text-white",
  },
  soft: {
    red: "bg-red-100 text-red-700",
    orange: "bg-orange-100 text-orange-700",
    yellow: "bg-yellow-100 text-yellow-700",
    green: "bg-green-100 text-green-700",
    blue: "bg-blue-100 text-blue-700",
    indigo: "bg-indigo-100 text-indigo-700",
    purple: "bg-purple-100 text-purple-700",
    gray: "bg-gray-100 text-gray-700",
  },
  outline: {
    red: "border border-red-500 text-red-700",
    orange: "border border-orange-500 text-orange-700",
    yellow: "border border-yellow-500 text-yellow-700",
    green: "border border-green-500 text-green-700",
    blue: "border border-blue-500 text-blue-700",
    indigo: "border border-indigo-500 text-indigo-700",
    purple: "border border-purple-500 text-purple-700",
    gray: "border border-gray-500 text-gray-700",
  },
};

const sizeMap = {
  sm: "text-xs px-1.5",
  md: "text-sm px-2",
  lg: "text-base px-2.5",
};

const roundedMap = {
  sm: "rounded",
  md: "rounded-md",
  lg: "rounded-lg",
  full: "rounded-full",
};

const variantClasses = computed(() => {
  const color = props.color;
  const variant = props.variant;
  const size = props.size;

  return `${variantMap[variant][color]} ${sizeMap[size]}`;
});

const roundedClasses = computed(() => roundedMap[props.rounded]);

const cursorClasses = computed(() =>
  props.clickable ? "cursor-pointer hover:opacity-80" : ""
);

const handleClick = () => {
  if (props.clickable) {
    emit("click", props.text);
  }
};
</script>

<style scoped></style>
