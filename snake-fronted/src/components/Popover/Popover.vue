<template>
  <div class="relative" @mouseleave="onMouseleave" @mouseenter="onMouseenter">
    <div ref="referenceTarget">
      <!-- 具名插槽 -->
      <slot name="reference" />
    </div>
    <!-- 气泡展示动画 -->
    <transition name="slide">
      <div
        v-show="isVisable"
        ref="contentTarget"
        class="absolute p-1 z-20 bg-white dark:bg-zinc-900 border rounded-md dark:border-zinc-700"
        :style="contentStyle"
      >
        <!-- 匿名插槽 -->
        <slot />
      </div>
    </transition>
  </div>
</template>

<script lang="ts">
// 延迟关闭时长
const DELAY_TIME = 300;

const PROP_TOP_LEFT = "top-left";
const PROP_TOP_RIGHT = "top-right";
const PROP_BOTTOM_LEFT = "bottom-left";
const PROP_BOTTOM_RIGHT = "bottom-right";

// 定义指定位置的 Enum
const placementEnum = [
  PROP_TOP_LEFT,
  PROP_TOP_RIGHT,
  PROP_BOTTOM_LEFT,
  PROP_BOTTOM_RIGHT,
];
</script>

<script setup lang="ts">
import { ref, watch, nextTick } from "vue";

const props = defineProps({
  // 控制气泡弹出位置，并给出开发者错误的提示
  placement: {
    type: String,
    default: "bottom-left",
    validator(val: string) {
      const result = placementEnum.includes(val);
      if (!result) {
        throw new Error(
          `你的 placement 必须是 ${placementEnum.join("、")} 中的一个`
        );
      }
      return result;
    },
  },
});

// 控制 menu 展示
const isVisable = ref(false);

// 控制延迟关闭
let timeout: NodeJS.Timeout | null | undefined = null;
/**
 * 鼠标移入的触发行为
 */
const onMouseenter = () => {
  isVisable.value = true;
  // 再次触发时，清理延时装置
  if (timeout) {
    clearTimeout(timeout);
  }
};
/**
 * 鼠标移出的触发行为
 */
const onMouseleave = () => {
  // 延时装置
  timeout = setTimeout(() => {
    isVisable.value = false;
    timeout = null;
  }, DELAY_TIME);
};

/**
 * 计算元素尺寸
 */
const referenceTarget = ref(null);
const contentTarget = ref(null);
const useElementSize = (
  target: { offsetWidth: any; offsetHeight: any } | null
) => {
  if (!target) return {};
  return {
    width: target.offsetWidth,
    height: target.offsetHeight,
  };
};

/**
 * 计算弹层位置
 */
const contentStyle = ref<{ top: string | number; left: string | number }>({
  top: 0,
  left: 0,
});

/**
 * 监听展示的变化，在展示时计算气泡位置
 */
// 气泡展示时进行计算
watch(isVisable, () => {
  if (!isVisable) return;

  nextTick(() => {
    switch (props.placement) {
      case PROP_TOP_LEFT:
        contentStyle.value.top = 0;
        contentStyle.value.left = `${-useElementSize(contentTarget.value)
          .width}px`;
        break;
      case PROP_TOP_RIGHT:
        contentStyle.value.top = 0;
        contentStyle.value.left = `${
          useElementSize(referenceTarget.value).width
        }px`;
        break;
      case PROP_BOTTOM_LEFT:
        contentStyle.value.top = `${
          useElementSize(referenceTarget.value).height
        }px`;
        contentStyle.value.left = `${-useElementSize(contentTarget.value)
          .width}px`;
        break;
      case PROP_BOTTOM_RIGHT:
        contentStyle.value.top = `${
          useElementSize(referenceTarget.value).height
        }px`;
        contentStyle.value.left = `${
          useElementSize(referenceTarget.value).width
        }px`;
        break;
    }
  });
});
</script>

<style scoped>
.slide-enter-active {
  transition: opacity 0.3s, transform 0.3s;
}

.slide-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateY(20px);
  opacity: 0;
}
</style>
