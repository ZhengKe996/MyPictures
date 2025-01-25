<template>
  <transition name="down" @after-leave="destroy">
    <div
      v-show="isVisable"
      class="min-w-[420px] fixed top-[20px] left-[50%] translate-x-[-50%] z-50 flex items-center px-3 py-1.5 rounded-sm border cursor-pointer"
      :class="styles[type].containerClass"
    >
      <i
        v-if="type === 'success'"
        class="i-tabler:circle-check h-1.5 w-1.5 mr-1.5"
        :class="styles[type].fillClass"
      ></i>
      <i
        v-else-if="type === 'warn'"
        class="i-tabler:alert-octagon h-1.5 w-1.5 mr-1.5"
        :class="styles[type].fillClass"
      ></i>
      <i
        v-else
        class="i-tabler:playstation-x h-1.5 w-1.5 mr-1.5"
        :class="styles[type].fillClass"
      ></i>

      <span class="text-sm" :class="styles[type].textClass">
        {{ content }}
      </span>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

interface Props {
  type: "success" | "warn" | "error";
  content: string;
  duration?: number;
  destroy: () => void;
}
const { duration = 2000, type } = defineProps<Props>();

// 样式表数据
const styles = {
  // 警告
  warn: {
    icon: "warn",
    fillClass: "fill-warn-300",
    textClass: "text-warn-300",
    containerClass:
      "bg-warn-100 border-warn-200  hover:shadow-lg hover:shadow-warn-100",
  },
  // 错误
  error: {
    icon: "error",
    fillClass: "fill-error-300",
    textClass: "text-error-300",
    containerClass:
      "bg-error-100 border-error-200  hover:shadow-lg hover:shadow-error-100",
  },
  // 成功
  success: {
    icon: "success",
    fillClass: "fill-success-300",
    textClass: "text-success-300",
    containerClass:
      "bg-success-100 border-success-200  hover:shadow-lg hover:shadow-success-100",
  },
};

// 控制显示处理
const isVisable = ref(false);
/**
 * 保证动画展示，需要在 mounted 之后进行展示
 */
onMounted(() => {
  isVisable.value = true;
  /**
   * 延迟时间关闭
   */
  setTimeout(() => {
    isVisable.value = false;
  }, duration);
});
</script>

<style lang="scss" scoped>
.down-enter-active,
.down-leave-active {
  transition: all 0.5s;
}

.down-enter-from,
.down-leave-to {
  opacity: 0;
  transform: translate3d(-50%, -100px, 0);
}
</style>
