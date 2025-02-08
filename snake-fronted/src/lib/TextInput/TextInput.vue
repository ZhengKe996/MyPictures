<template>
  <div class="relative space-y-1.5" :class="wrapperClass">
    <label
      v-if="label"
      :for="id"
      class="block text-sm font-medium text-gray-700"
      :class="labelClass"
    >
      {{ label }}
    </label>
    <div class="relative">
      <div
        class="group flex w-full items-center rounded-md bg-white transition duration-200"
        :class="[
          error
            ? 'ring-1 ring-red-300 focus-within:ring-2 focus-within:ring-red-500'
            : 'ring-1 ring-gray-200 hover:ring-[var(--input-hover-color)] focus-within:ring-2 focus-within:ring-[var(--input-focus-color)]',
          disabled && 'opacity-50 cursor-not-allowed hover:ring-gray-200',
        ]"
      >
        <div v-if="$slots.prefix" class="flex items-center pl-3 text-gray-400">
          <slot name="prefix" />
        </div>

        <!-- 根据 multiline 属性选择渲染 input 或 textarea -->
        <template v-if="multiline">
          <textarea
            :id="id"
            :name="name"
            :value="modelValue"
            :disabled="disabled"
            :placeholder="placeholder"
            :required="required"
            :rows="rows"
            @input="handleInput"
            @focus="handleFocus"
            @blur="handleBlur"
            class="block min-w-0 flex-1 border-0 bg-transparent focus:ring-0 disabled:cursor-not-allowed resize-none"
            :class="[
              sizeClasses.input,
              'text-gray-900 placeholder:text-gray-400',
              'placeholder:transition-opacity placeholder:duration-200',
              'placeholder:select-none',
              'placeholder:tracking-wide',
              $slots.prefix && 'pl-2',
              $slots.suffix && 'pr-10',
              disabled && 'text-gray-500',
              'focus:placeholder:opacity-50',
            ]"
            :style="colorStyle"
          />
        </template>
        <template v-else>
          <input
            :id="id"
            :type="type"
            :name="name"
            :value="modelValue"
            :disabled="disabled"
            :placeholder="placeholder"
            :required="required"
            @input="handleInput"
            @focus="handleFocus"
            @blur="handleBlur"
            class="block min-w-0 flex-1 border-0 bg-transparent focus:ring-0 disabled:cursor-not-allowed [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
            :class="[
              sizeClasses.input,
              'text-gray-900 placeholder:text-gray-400',
              'placeholder:transition-opacity placeholder:duration-200',
              'placeholder:select-none',
              // 调整 placeholder 的间距和样式
              'placeholder:tracking-wide',
              $slots.prefix && 'pl-2',
              modelValue && $slots.suffix
                ? 'pr-16'
                : modelValue
                ? 'pr-8'
                : $slots.suffix
                ? 'pr-10'
                : 'pr-3',
              disabled && 'text-gray-500',
              // 添加焦点状态下的 placeholder 样式
              'focus:placeholder:opacity-50',
            ]"
            :style="{
              ...(modelValue
                ? { color: colorStyle['--input-focus-color'] }
                : {}),
              // 添加自定义 placeholder 样式
              '--placeholder-start-space': '0.1em',
            }"
          />
        </template>

        <!-- 清除按钮 -->
        <button
          v-if="modelValue && !disabled"
          type="button"
          class="absolute right-0 flex h-full items-center px-2 transition-opacity duration-200"
          :class="[
            $slots.suffix ? 'right-8 border-r border-gray-200' : '',
            isFocused ? 'opacity-100' : 'opacity-0 group-hover:opacity-100',
          ]"
          @click="clearInput"
        >
          <svg
            class="size-4 text-gray-400 hover:text-gray-600"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              d="M6 18L18 6M6 6l12 12"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
        <div v-if="$slots.suffix" class="flex items-center pr-3 text-gray-400">
          <slot name="suffix" />
        </div>
      </div>
    </div>
    <p
      v-if="error"
      class="mt-1 text-sm text-red-500 transition-all duration-200"
    >
      {{ error }}
    </p>
    <p v-else-if="description" class="mt-1 text-sm text-gray-500">
      {{ description }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

// 复用 CheckBox 组件的颜色类型
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
  modelValue: string;
  id: string;
  name?: string;
  label?: string;
  type?: string;
  placeholder?: string;
  disabled?: boolean;
  required?: boolean;
  description?: string;
  error?: string;
  color?: ColorType;
  size?: "sm" | "md" | "lg";
  wrapperClass?: string;
  labelClass?: string;
  multiline?: boolean;
  rows?: number;
}

const props = withDefaults(defineProps<Props>(), {
  name: "",
  type: "text",
  placeholder: "",
  disabled: false,
  required: false,
  color: "indigo",
  size: "md",
  wrapperClass: "",
  labelClass: "",
  multiline: false,
  rows: 3,
});

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "focus", event: FocusEvent): void;
  (e: "blur", event: FocusEvent): void;
}>();

const isFocused = ref(false);

// 尺寸相关样式
const sizeClasses = computed(
  () =>
    ({
      sm: {
        input: "py-1 px-2.5 text-sm tracking-tight",
      },
      md: {
        input: "py-1.5 px-3 text-base tracking-normal",
      },
      lg: {
        input: "py-2 px-3.5 text-lg tracking-normal",
      },
    }[props.size])
);

// 复用 CheckBox 的颜色映射
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
    "--input-hover-color": colors.medium,
    "--input-focus-color": colors.dark,
  };
});

// 事件处理
const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;

  // 如果是数字输入框，进行额外处理
  if (props.type === "number") {
    // 移除非数字字符（保留负号和小数点）
    value = value.replace(/[^\d.-]/g, "");
    // 确保只有一个小数点和一个负号
    const parts = value.split(".");
    if (parts.length > 2) {
      value = parts[0] + "." + parts.slice(1).join("");
    }
    if (value.split("-").length > 2) {
      value = value.replace(/-/g, "");
      if (value.startsWith(".")) {
        value = "0" + value;
      }
      value = "-" + value;
    }
    // 如果不是第一位，移除负号
    if (value.indexOf("-") > 0) {
      value = value.replace(/-/g, "");
    }
  }

  emit("update:modelValue", value);
};

const handleFocus = (event: FocusEvent) => {
  isFocused.value = true;
  emit("focus", event);
};

const handleBlur = (event: FocusEvent) => {
  isFocused.value = false;
  emit("blur", event);
};

const clearInput = () => {
  emit("update:modelValue", "");
};
</script>
