<template>
  <div>
    <slot />
    <div ref="loadingTarget" class="h-6 py-4">
      <!-- 加载更多 -->

      <i
        v-show="loading"
        class="i-tabler:loader w-8 h-8 mx-auto animate-spin"
      ></i>
      <!-- 没有更多数据 -->
      <p v-if="isFinished" class="text-center text-base text-zinc-400">
        已经没有更多数据了!
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useVModel, useIntersectionObserver } from "@vueuse/core";

interface Props {
  modelValue: boolean;
  isFinished?: boolean;
}
const props = withDefaults(defineProps<Props>(), {
  isFinished: false,
});

const emits = defineEmits(["onLoad", "update:modelValue"]);

// 处理 loading 状态
const loading = useVModel(props);

// 滚动元素
const loadingTarget = ref(null);

// 记录当前是否在底部
const targetIsIntersecting = ref(false);
useIntersectionObserver(loadingTarget, ([{ isIntersecting }]) => {
  targetIsIntersecting.value = isIntersecting;
  emits("onLoad");
});

/**
 * 触发 load 事件
 */
const emitLoad = () => {
  setTimeout(() => {
    // 当加载更多视图可见时，同时 loading为 false 数据尚未全部加载完 处理更多逻辑
    if (targetIsIntersecting.value && !loading.value && !props.isFinished) {
      // 修改加载数据的标记
      loading.value = true;
      emits("onLoad");
    }
  }, 100);
};

/**
 * 监听 loading 变化
 */
watch(loading, emitLoad);
</script>
