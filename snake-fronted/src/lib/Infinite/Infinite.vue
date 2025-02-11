<template>
  <div class="infinite-loading" ref="container">
    <div class="infinite-content">
      <slot></slot>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="infinite-loading-status">
      <slot name="loading">
        <p class="text-gray-500 text-center py-4">{{ loadingText }}</p>
      </slot>
    </div>

    <!-- Finished State -->
    <div v-if="isFinished" class="infinite-finished-status">
      <slot name="finished">
        <p class="text-gray-400 text-center py-4">{{ finishedText }}</p>
      </slot>
    </div>

    <!-- Error State -->
    <div v-if="error" class="infinite-error-status">
      <slot name="error">
        <p class="text-red-500 text-center py-4">{{ errorText }}</p>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, nextTick } from "vue";
import { useThrottleFn } from "@vueuse/core";

interface Props {
  modelValue?: boolean; // loading state
  isFinished?: boolean;
  threshold?: number;
  distance?: number;
  loadingText?: string;
  finishedText?: string;
  errorText?: string;
  immediateCheck?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  isFinished: false,
  threshold: 100,
  distance: 0,
  loadingText: "加载中...",
  finishedText: "没有更多了",
  errorText: "加载失败",
  immediateCheck: true,
});

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "on-load"): void;
}>();

const container = ref<HTMLElement | null>(null);
const loading = ref(props.modelValue);
const error = ref(false);

// 检查是否需要加载更多
const checkPosition = () => {
  if (!container.value || loading.value || props.isFinished) return;

  const { clientHeight } = document.documentElement;
  const containerBottom = container.value.getBoundingClientRect().bottom;

  // 检查是否接近底部
  if (containerBottom <= clientHeight + props.threshold) {
    loading.value = true;
    emit("update:modelValue", true);
    emit("on-load");
  }
};

// 使用节流的滚动检查
const throttledCheck = useThrottleFn(checkPosition, 200);

// 监听 loading 状态变化
watch(
  () => props.modelValue,
  (newVal) => {
    loading.value = newVal;
  }
);

// 监听完成状态变化
watch(
  () => props.isFinished,
  (newVal) => {
    if (newVal) {
      loading.value = false;
      emit("update:modelValue", false);
    }
  }
);

// 初始化和清理
onMounted(() => {
  document.addEventListener("scroll", throttledCheck);
  if (props.immediateCheck) {
    // 使用 nextTick 确保内容已渲染
    nextTick(() => checkPosition());
  }
});

onUnmounted(() => {
  document.removeEventListener("scroll", throttledCheck);
});

// 提供重置方法
const reset = () => {
  error.value = false;
  loading.value = false;
  emit("update:modelValue", false);
};

// 暴露方法
defineExpose({ reset });
</script>

<style scoped>
.infinite-loading {
  width: 100%;
  height: 100%;
}

.infinite-content {
  min-height: 100px;
}
</style>
