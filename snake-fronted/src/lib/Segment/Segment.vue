<template>
  <div
    class="inline-flex rounded-lg p-1 transition-all"
    :class="[
      disabled ? 'bg-gray-100 opacity-50' : 'bg-gray-100',
      block ? 'w-full' : '',
    ]"
    role="tablist"
    :aria-disabled="disabled"
  >
    <button
      v-for="(item, index) in options"
      :key="item.value"
      :class="[
        'relative flex items-center justify-center rounded-md transition-all duration-200',
        SEGMENT_CONFIGS.sizeClasses[size],
        block ? 'flex-1' : '',
        value === item.value
          ? 'bg-white text-gray-900 shadow'
          : 'text-gray-500 hover:text-gray-900',
        disabled ? 'cursor-not-allowed' : 'cursor-pointer',
      ]"
      role="tab"
      :aria-selected="value === item.value"
      :tabindex="disabled ? -1 : 0"
      @click="!disabled && handleSelect(item.value)"
      @keydown.space.prevent="!disabled && handleSelect(item.value)"
      @keydown.enter.prevent="!disabled && handleSelect(item.value)"
    >
      <i
        v-if="item.icon"
        :class="[item.icon, SEGMENT_CONFIGS.iconSizeClasses[size]]"
        class="mr-1"
      ></i>
      {{ item.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface SegmentOption {
  label: string;
  value: string | number;
  icon?: string;
}

interface SegmentProps {
  options: SegmentOption[];
  value?: string | number;
  size?: "sm" | "md" | "lg";
  disabled?: boolean;
  block?: boolean;
}

const SEGMENT_CONFIGS = {
  sizeClasses: {
    sm: "px-2.5 py-1.5 text-sm",
    md: "px-3.5 py-2 text-base",
    lg: "px-4 py-2.5 text-lg",
  },
  iconSizeClasses: {
    sm: "text-sm",
    md: "text-base",
    lg: "text-lg",
  },
};

const props = withDefaults(defineProps<SegmentProps>(), {
  size: "md",
  disabled: false,
  block: false,
});

const emit = defineEmits<{
  (e: "update:value", value: string | number): void;
  (e: "change", value: string | number): void;
}>();

const handleSelect = (optionValue: string | number) => {
  emit("update:value", optionValue);
  emit("change", optionValue);
};
</script>

<style scoped></style>
