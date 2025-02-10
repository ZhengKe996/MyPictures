<template>
  <div
    class="segment-container inline-flex rounded-lg p-1 transition-all"
    :class="[
      disabled ? 'bg-gray-100 opacity-50' : 'bg-gray-100',
      block ? 'w-full' : '',
    ]"
    role="tablist"
    :aria-disabled="disabled"
  >
    <button
      v-for="(item, _) in options"
      :key="item.value"
      :class="[
        'segment-button relative flex items-center justify-center rounded-md transition-all duration-300 ease-out',
        SEGMENT_CONFIGS.sizeClasses[size],
        block ? 'flex-1' : '',
        value === item.value
          ? 'is-active bg-white text-gray-900 shadow'
          : 'text-gray-500 hover:text-gray-900 hover:bg-gray-50',
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

withDefaults(defineProps<SegmentProps>(), {
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

<style scoped>
.segment-container {
  --transition-timing: cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.segment-button {
  transform: translateZ(0);
  backface-visibility: hidden;
}

.segment-button:not(.is-active):hover {
  transform: translateY(-1px);
}

.segment-button:not(.is-active):active {
  transform: translateY(0);
}

.segment-button.is-active {
  animation: selectAnimation 0.3s var(--transition-timing);
}

@keyframes selectAnimation {
  0% {
    transform: scale(0.95);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

/* 优化焦点状态 */
.segment-button:focus-visible {
  outline: 2px solid #10b981;
  outline-offset: -2px;
}

/* 添加按压效果的阴影变化 */
.segment-button.is-active {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
}
</style>
