<template>
  <div
    class="checkbox-wrapper"
    :class="[
      layout === 'vertical' ? 'flex gap-3' : '',
      inline ? 'inline-flex' : 'flex',
      wrapperClass,
    ]"
  >
    <!-- 详细模式的文本部分 -->
    <div v-if="description" class="min-w-0 flex-1 text-sm/6">
      <label :for="id" class="font-medium text-gray-900" :class="labelClass">{{
        label
      }}</label>
      <p :id="`${id}-description`" class="text-gray-500">
        {{ description }}
      </p>
    </div>

    <!-- CheckBox 包装器 -->
    <div class="flex shrink-0 items-center" :class="[sizeClasses.container]">
      <div
        class="group relative grid grid-cols-1"
        :class="[sizeClasses.checkbox]"
      >
        <input
          :id="id"
          :name="name"
          type="checkbox"
          :checked="modelValue"
          :disabled="disabled"
          :indeterminate="indeterminate"
          :aria-describedby="description ? `${id}-description` : undefined"
          @change="handleChange"
          @focus="handleFocus"
          @blur="handleBlur"
          @click="handleClick"
          :style="colorStyle"
          class="peer relative cursor-pointer appearance-none rounded border transition-all duration-200 ease-in-out focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 disabled:cursor-not-allowed bg-white disabled:border-gray-300 disabled:bg-gray-100"
          :class="['checkbox-input', sizeClasses.input]"
        />
        <!-- 选中状态图标 -->
        <svg
          class="pointer-events-none absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 transition-transform duration-200"
          :class="[
            `stroke-white group-has-[:disabled]:stroke-gray-950/25`,
            modelValue || indeterminate ? 'scale-100' : 'scale-0',
            sizeClasses.icon,
          ]"
          viewBox="0 0 16 16"
          fill="none"
        >
          <path
            v-if="!indeterminate"
            d="M4 8.5L6.5 11L12 4"
            :stroke-width="sizeClasses.strokeWidth"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            v-else
            d="M3 8H13"
            :stroke-width="sizeClasses.strokeWidth"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </div>

      <!-- 简单模式的标签 -->
      <label
        v-if="!description"
        :for="id"
        class="select-none font-medium text-gray-900 cursor-pointer"
        :class="[sizeClasses.label, labelClass]"
      >
        {{ label }}
      </label>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";

// 定义支持的颜色类型
type ColorType =
  | "slate"
  | "gray"
  | "zinc"
  | "neutral"
  | "stone"
  | "red"
  | "orange"
  | "amber"
  | "yellow"
  | "lime"
  | "green"
  | "emerald"
  | "teal"
  | "cyan"
  | "sky"
  | "blue"
  | "indigo"
  | "violet"
  | "purple"
  | "fuchsia"
  | "pink"
  | "rose";

interface Props {
  modelValue: boolean;
  id: string;
  name?: string;
  label: string;
  description?: string;
  disabled?: boolean;
  color?: ColorType; // 更新为 ColorType
  layout?: "horizontal" | "vertical";
  size?: "sm" | "md" | "lg";
  inline?: boolean;
  indeterminate?: boolean;
  wrapperClass?: string;
  labelClass?: string;
}

const props = withDefaults(defineProps<Props>(), {
  name: "",
  disabled: false,
  color: "indigo",
  layout: "horizontal",
  description: "",
  size: "md",
  inline: false,
  indeterminate: false,
  wrapperClass: "",
  labelClass: "",
});

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "change", event: Event): void;
  (e: "focus", event: FocusEvent): void;
  (e: "blur", event: FocusEvent): void;
  (e: "click", event: MouseEvent): void;
}>();

// 尺寸相关样式
const sizeClasses = computed(
  () =>
    ({
      sm: {
        container: "h-5",
        checkbox: "size-3.5",
        input: "size-3.5",
        icon: "size-2.5",
        label: "ml-2 text-sm",
        strokeWidth: 2.5,
      },
      md: {
        container: "h-6",
        checkbox: "size-4",
        input: "size-4",
        icon: "size-3",
        label: "ml-2 text-base",
        strokeWidth: 2.5,
      },
      lg: {
        container: "h-7",
        checkbox: "size-5",
        input: "size-5",
        icon: "size-4",
        label: "ml-3 text-lg",
        strokeWidth: 3,
      },
    }[props.size])
);

// 更新颜色映射对象
const colorMap: Record<
  ColorType,
  { light: string; medium: string; dark: string }
> = {
  slate: { light: "#cbd5e1", medium: "#64748b", dark: "#475569" },
  gray: { light: "#d1d5db", medium: "#6b7280", dark: "#4b5563" },
  zinc: { light: "#d4d4d8", medium: "#71717a", dark: "#52525b" },
  neutral: { light: "#d4d4d4", medium: "#737373", dark: "#525252" },
  stone: { light: "#d6d3d1", medium: "#78716c", dark: "#57534e" },
  red: { light: "#fca5a5", medium: "#ef4444", dark: "#dc2626" },
  orange: { light: "#fdba74", medium: "#f97316", dark: "#ea580c" },
  amber: { light: "#fcd34d", medium: "#f59e0b", dark: "#d97706" },
  yellow: { light: "#fde047", medium: "#eab308", dark: "#ca8a04" },
  lime: { light: "#bef264", medium: "#84cc16", dark: "#65a30d" },
  green: { light: "#86efac", medium: "#22c55e", dark: "#16a34a" },
  emerald: { light: "#6ee7b7", medium: "#10b981", dark: "#059669" },
  teal: { light: "#5eead4", medium: "#14b8a6", dark: "#0d9488" },
  cyan: { light: "#67e8f9", medium: "#06b6d4", dark: "#0891b2" },
  sky: { light: "#7dd3fc", medium: "#0ea5e9", dark: "#0284c7" },
  blue: { light: "#93c5fd", medium: "#3b82f6", dark: "#2563eb" },
  indigo: { light: "#a5b4fc", medium: "#6366f1", dark: "#4f46e5" },
  violet: { light: "#c4b5fd", medium: "#8b5cf6", dark: "#7c3aed" },
  purple: { light: "#d8b4fe", medium: "#a855f7", dark: "#9333ea" },
  fuchsia: { light: "#f0abfc", medium: "#d946ef", dark: "#c026d3" },
  pink: { light: "#f9a8d4", medium: "#ec4899", dark: "#db2777" },
  rose: { light: "#fda4af", medium: "#f43f5e", dark: "#e11d48" },
};

// 颜色样式计算
const colorStyle = computed(() => {
  const colors =
    colorMap[props.color as keyof typeof colorMap] || colorMap.indigo;
  return {
    "--checkbox-border-color": colors.light,
    "--checkbox-hover-color": colors.medium,
    "--checkbox-checked-color": colors.dark,
  };
});

// 事件处理
const handleChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  emit("update:modelValue", target.checked);
  emit("change", event);
};

const handleFocus = (event: FocusEvent) => {
  emit("focus", event);
};

const handleBlur = (event: FocusEvent) => {
  emit("blur", event);
};

const handleClick = (event: MouseEvent) => {
  emit("click", event);
};
</script>

<style scoped>
.checkbox-wrapper {
  @apply relative;
}

input[type="checkbox"] {
  @apply cursor-pointer;
}

input[type="checkbox"]:checked {
  animation: checkbox-pop 0.25s ease-in-out;
}

@keyframes checkbox-pop {
  0% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 悬浮效果 */
.checkbox-wrapper:hover input[type="checkbox"]:not(:disabled) {
  @apply shadow-sm;
}

/* 聚焦效果 */
.checkbox-wrapper input[type="checkbox"]:focus-visible {
  @apply ring-2 ring-offset-2;
}

.checkbox-input {
  border-color: var(--checkbox-border-color);
}

.checkbox-input:hover:not(:disabled) {
  border-color: var(--checkbox-hover-color);
}

.checkbox-input:checked {
  background-color: var(--checkbox-checked-color);
  border-color: var(--checkbox-checked-color);
}

.checkbox-input:focus-visible {
  outline-color: var(--checkbox-hover-color);
}

/* 其他已有样式保持不变 */
</style>
