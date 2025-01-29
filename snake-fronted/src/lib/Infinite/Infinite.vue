<template>
  <div class="infinite-scroll-component">
    <slot />
    <div
      ref="loadingTarget"
      class="py-4 flex justify-center items-center min-h-[60px]"
    >
      <template v-if="error">
        <slot name="error">
          <div
            class="text-red-500 cursor-pointer hover:text-red-600 transition-colors"
            @click="handleRetry"
          >
            {{ errorText }}
          </div>
        </slot>
      </template>

      <template v-else-if="loading">
        <slot name="loading">
          <div class="flex items-center gap-2">
            <i class="i-tabler:loader w-5 h-5 animate-spin" />
            <span class="text-zinc-500">{{ loadingText }}</span>
          </div>
        </slot>
      </template>

      <template v-else-if="isFinished">
        <slot name="finished">
          <div class="text-zinc-400">{{ finishedText }}</div>
        </slot>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useVModel, useIntersectionObserver } from "@vueuse/core";

interface Props {
  modelValue: boolean;
  isFinished?: boolean;
  error?: boolean;
  loadingText?: string;
  finishedText?: string;
  errorText?: string;
  threshold?: number;
  immediateLoad?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  isFinished: false,
  error: false,
  loadingText: "加载中...",
  finishedText: "没有更多数据了",
  errorText: "加载失败，点击重试",
  threshold: 100,
  immediateLoad: true,
});

const emits = defineEmits(["onLoad", "onRetry", "update:modelValue"]);

const loading = useVModel(props);
const loadingTarget = ref<HTMLElement | null>(null);
const targetIsIntersecting = ref(false);

// 使用 Intersection Observer API 来观察目标元素与视口的交集情况
// 该函数在目标元素进入视口时触发回调，用于实现无限滚动、懒加载等功能
useIntersectionObserver(
  loadingTarget, // 需要观察的目标元素
  ([{ isIntersecting }]) => {
    // 当目标元素与视口的交集情况发生变化时，更新目标元素是否 intersecting 的状态
    targetIsIntersecting.value = isIntersecting;
    // 根据目标元素是否在视口中，决定是否触发加载更多数据的操作
    emitLoadIfNeeded();
  },
  {
    threshold: 0, // 设置阈值为0，表示目标元素任何部分进入视口时即触发回调
    rootMargin: `${props.threshold}px`, // 设置根元素的边距，可以调整触发回调的时机
  }
);

/**
 * 根据条件触发加载事件
 * 此函数用于检查是否需要加载内容，如果需要，就触发加载事件
 * 它通过检查多个条件来决定是否开始加载过程：
 * - 目标元素是否在视口中（targetIsIntersecting）
 * - 当前是否没有在加载中（loading）
 * - 加载是否没有完成（isFinished）
 * - 没有发生错误（error）
 * 如果以上条件都满足，则设置加载状态为true，并触发onLoad事件
 */
const emitLoadIfNeeded = () => {
  // 当目标元素在视口中、没有在加载中、加载没有完成且没有错误时，执行加载
  if (
    targetIsIntersecting.value &&
    !loading.value &&
    !props.isFinished &&
    !props.error
  ) {
    // 设置加载状态为true
    loading.value = true;
    // 触发onLoad事件
    emits("onLoad");
  }
};

/**
 * 重试操作处理函数
 *
 * 该函数在组件的错误状态存在时调用，目的是触发重新加载数据的操作
 * 它通过触发事件来通知父组件或其他监听者执行相应的操作
 */
const handleRetry = () => {
  // 当存在错误时，触发重试和加载事件
  if (props.error) {
    emits("onRetry");
    emits("onLoad");
  }
};

watch(loading, emitLoadIfNeeded);

onMounted(() => {
  if (props.immediateLoad) emitLoadIfNeeded();
});
</script>

<style scoped>
.infinite-scroll-component {
  position: relative;
}
</style>
