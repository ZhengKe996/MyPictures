<template>
  <div class="flex flex-col" :class="containerClass">
    <div class="flex items-center mb-2 relative">
      <input
        v-model="newTag"
        @keydown.enter.prevent="addTag"
        @keydown.backspace="removeLastTag"
        @focus="isFocused = true"
        @blur="isFocused = false"
        :placeholder="placeholder"
        :class="[
          'border rounded w-full transition-all duration-200 ease-in-out',
          'focus:outline-none focus:ring-2',
          `focus:ring-${color}-500`,
          `border-${color}-${isFocused ? '500' : '200'}`,
          inputSize,
          { 'border-red-400': hasError },
        ]"
      />
      <Transition
        enter-active-class="transition duration-200 ease-out"
        enter-from-class="transform scale-95 opacity-0"
        enter-to-class="transform scale-100 opacity-100"
        leave-active-class="transition duration-150 ease-in"
        leave-from-class="transform scale-100 opacity-100"
        leave-to-class="transform scale-95 opacity-0"
      >
        <div
          v-if="hasError"
          class="absolute left-0 -bottom-7 flex items-center space-x-1 text-sm text-red-500 bg-red-50 px-2 py-1 rounded-md shadow-sm"
        >
          <svg
            class="h-4 w-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ errorMessage }}</span>
        </div>
      </Transition>
    </div>
    <div class="flex flex-wrap gap-2">
      <transition-group name="tag">
        <Tag
          v-for="(tag, index) in modelValue"
          :key="tag"
          :index="index"
          :text="tag"
          :color="color"
          :size="size"
          @remove="removeTag"
          class="transition-all duration-200 ease-in-out"
        />
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import Tag from "./components/Tag.vue";

interface Props {
  modelValue: string[];
  color?: string;
  size?: "sm" | "md" | "lg";
  placeholder?: string;
  maxTags?: number;
  validation?: (tag: string) => boolean | string;
  allowDuplicates?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  color: "blue",
  size: "md",
  placeholder: "请输入标签",
  maxTags: Infinity,
  allowDuplicates: false,
});

const emit = defineEmits<{
  (e: "update:modelValue", value: string[]): void;
  (e: "error", message: string): void;
}>();

const newTag = ref("");
const isFocused = ref(false);
const hasError = ref(false);
const errorMessage = ref("");

const inputSize = computed(
  () =>
    ({
      sm: "px-2 py-1 text-sm",
      md: "p-2",
      lg: "px-4 py-3 text-lg",
    }[props.size])
);

const containerClass = computed(
  () =>
    ({
      sm: "text-sm",
      md: "text-base",
      lg: "text-lg",
    }[props.size])
);

const validateAndAddTag = (tag: string): boolean => {
  hasError.value = false;
  errorMessage.value = "";

  if (!tag.trim()) {
    setError("标签不能为空");
    return false;
  }

  if (!props.allowDuplicates && props.modelValue.includes(tag)) {
    setError("标签已存在");
    return false;
  }

  if (props.modelValue.length >= props.maxTags) {
    setError(`最多只能添加 ${props.maxTags} 个标签`);
    return false;
  }

  if (props.validation) {
    const validationResult = props.validation(tag);
    if (validationResult !== true) {
      setError(
        typeof validationResult === "string" ? validationResult : "标签验证失败"
      );
      return false;
    }
  }

  return true;
};

// 添加自动清除错误提示的功能
const setError = (message: string) => {
  hasError.value = true;
  errorMessage.value = message;
  emit("error", message);

  // 3秒后自动清除错误提示
  setTimeout(() => {
    hasError.value = false;
    errorMessage.value = "";
  }, 3000);
};

const addTag = () => {
  const tag = newTag.value.trim();
  if (validateAndAddTag(tag)) {
    emit("update:modelValue", [...props.modelValue, tag]);
    newTag.value = "";
  }
};

const removeTag = (index: number) => {
  const newTags = [...props.modelValue];
  newTags.splice(index, 1);
  emit("update:modelValue", newTags);
};

const removeLastTag = (event: KeyboardEvent) => {
  if (
    event.key === "Backspace" &&
    newTag.value === "" &&
    props.modelValue.length > 0
  ) {
    removeTag(props.modelValue.length - 1);
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
  transform: translateX(-10px);
}

.tag-move {
  transition: transform 0.3s ease;
}

/* 添加错误提示动画相关样式 */
.error-enter-active,
.error-leave-active {
  transition: all 0.3s ease;
}

.error-enter-from,
.error-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
