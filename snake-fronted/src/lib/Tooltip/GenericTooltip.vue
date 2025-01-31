<template>
  <div class="relative inline-block w-full" ref="tooltipContainer">
    <div @mouseenter="showTooltip" @mouseleave="hideTooltip" class="w-full">
      <slot name="trigger">
        <button
          class="text-gray-600 transition-colors duration-200 focus:outline-none dark:text-gray-200 dark:hover:text-blue-400 hover:text-blue-500"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            class="w-6 h-6"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z"
            />
          </svg>
        </button>
      </slot>
    </div>

    <Teleport to="body">
      <Transition name="tooltip-fade">
        <div
          v-if="isVisible"
          ref="tooltip"
          :class="tooltipClasses"
          :style="positionStyle"
        >
          <slot name="content">
            <p class="truncate">{{ content }}</p>
          </slot>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from "vue";

interface TooltipProps {
  content?: string;
  position?: "top" | "bottom" | "left" | "right";
  offset?: number;
}

const props = withDefaults(defineProps<TooltipProps>(), {
  content: "",
  position: "top",
  offset: 10,
});

const tooltipContainer = ref<HTMLElement | null>(null);
const tooltip = ref<HTMLElement | null>(null);
const isVisible = ref(false);
const positionStyle = ref({});

const tooltipClasses = computed(() => [
  "absolute px-5 py-3 text-center text-gray-600 bg-white rounded-lg shadow-lg dark:shadow-none shadow-gray-200 dark:bg-gray-800 dark:text-white",
  "transition-opacity duration-200 z-50",
  "min-w-[12rem] max-w-xs",
]);

const calculatePosition = () => {
  if (!tooltipContainer.value || !tooltip.value) return;

  const triggerRect = tooltipContainer.value.getBoundingClientRect();
  const tooltipRect = tooltip.value.getBoundingClientRect();
  const viewportWidth = window.innerWidth;
  const viewportHeight = window.innerHeight;

  let top = 0;
  let left = 0;

  // 基础位置计算
  switch (props.position) {
    case "top":
      top = triggerRect.top - tooltipRect.height - props.offset;
      left = triggerRect.left + (triggerRect.width - tooltipRect.width) / 2;
      break;
    case "bottom":
      top = triggerRect.bottom + props.offset;
      left = triggerRect.left + (triggerRect.width - tooltipRect.width) / 2;
      break;
    case "left":
      top = triggerRect.top + (triggerRect.height - tooltipRect.height) / 2;
      left = triggerRect.left - tooltipRect.width - props.offset;
      break;
    case "right":
      top = triggerRect.top + (triggerRect.height - tooltipRect.height) / 2;
      left = triggerRect.right + props.offset;
      break;
  }

  // 边界检查和调整
  if (left < 0) left = 0;
  if (left + tooltipRect.width > viewportWidth) {
    left = viewportWidth - tooltipRect.width;
  }
  if (top < 0) top = 0;
  if (top + tooltipRect.height > viewportHeight) {
    top = viewportHeight - tooltipRect.height;
  }

  positionStyle.value = {
    position: "fixed",
    top: `${top}px`,
    left: `${left}px`,
    zIndex: "9999",
  };
};

const showTooltip = () => {
  isVisible.value = true;
  nextTick(() => {
    calculatePosition();
  });
};

const hideTooltip = () => {
  isVisible.value = false;
};

// 监听窗口大小变化重新计算位置
onMounted(() => {
  window.addEventListener("resize", calculatePosition);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", calculatePosition);
});
</script>

<style scoped>
.tooltip-fade-enter-active,
.tooltip-fade-leave-active {
  transition: opacity 0.2s ease;
}

.tooltip-fade-enter-from,
.tooltip-fade-leave-to {
  opacity: 0;
}
</style>
