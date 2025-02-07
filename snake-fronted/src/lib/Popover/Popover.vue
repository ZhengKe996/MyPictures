<template>
  <div ref="containerRef" class="relative inline-block">
    <div ref="triggerRef" class="inline-block">
      <slot name="reference" />
    </div>
    <Transition
      :enter-active-class="transitClass.enter"
      :leave-active-class="transitClass.leave"
      :enter-from-class="transitClass.enterFrom"
      :leave-to-class="transitClass.leaveTo"
    >
      <div
        v-show="isVisible"
        ref="popoverRef"
        :class="[
          'absolute z-50',
          'bg-white dark:bg-zinc-800',
          'border border-gray-200 dark:border-zinc-700',
          'shadow-lg shadow-gray-200/50 dark:shadow-zinc-900/50',
          'rounded-lg backdrop-blur-sm',
          'transform-gpu motion-safe:transition-transform',
          customClass,
        ]"
        :style="popoverStyle"
      >
        <div :class="['p-2', contentClass]">
          <slot />
        </div>
        <div
          v-if="arrow"
          :class="[
            'absolute w-4 h-4 rotate-45 bg-inherit border',
            getArrowPositionClass,
          ]"
        />
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from "vue";
import {
  useElementBounding,
  useEventListener,
  useTimeoutFn,
  onClickOutside,
  useElementHover,
  useToggle,
  tryOnUnmounted,
  whenever,
} from "@vueuse/core";

// 扩展位置类型
type PlacementType =
  | "top"
  | "top-start"
  | "top-end"
  | "bottom"
  | "bottom-start"
  | "bottom-end"
  | "left"
  | "left-start"
  | "left-end"
  | "right"
  | "right-start"
  | "right-end";

interface Props {
  placement?: PlacementType;
  offset?: number;
  delay?: number;
  trigger?: "hover" | "click";
  arrow?: boolean;
  customClass?: string;
  contentClass?: string;
  closeOnContentClick?: boolean; // 新增：点击内容是否关闭
  showDelay?: number; // 新增：显示延迟
  hideDelay?: number; // 新增：隐藏延迟
  persistent?: boolean; // 新增：是否持久化显示
}

const props = withDefaults(defineProps<Props>(), {
  placement: "bottom",
  offset: 8,
  delay: 300,
  trigger: "hover",
  arrow: false,
  customClass: "",
  contentClass: "",
  closeOnContentClick: false,
  showDelay: 0,
  hideDelay: 300,
  persistent: false,
});

const emit = defineEmits<{
  show: [];
  hide: [];
}>();

// Refs
const containerRef = ref<HTMLElement | null>(null);
const triggerRef = ref<HTMLElement | null>(null);
const popoverRef = ref<HTMLElement | null>(null);
const [isVisible, toggleVisible] = useToggle(false);

// VueUse composables
const triggerBounds = useElementBounding(triggerRef);
const popoverBounds = useElementBounding(popoverRef);
const isHovered = useElementHover(containerRef);

// Timeout handlers using VueUse
const { start: startShowTimer, stop: stopShowTimer } = useTimeoutFn(() => {
  isVisible.value = true;
  emit("show");
}, props.showDelay);

const { start: startHideTimer, stop: stopHideTimer } = useTimeoutFn(() => {
  isVisible.value = false;
  emit("hide");
}, props.hideDelay);

// Click outside handling
onClickOutside(
  containerRef,
  (event) => {
    if (props.trigger === "click" && !props.persistent) {
      hide();
    }
  },
  { ignore: [triggerRef] }
);

// Watch hover state for hover trigger
whenever(isHovered, (value) => {
  if (props.trigger !== "hover") return;
  value ? show() : hide();
});

// Methods
const show = () => {
  stopHideTimer();
  startShowTimer();
};

const hide = () => {
  if (props.persistent) return;
  stopShowTimer();
  startHideTimer();
};

// Handle trigger click
const handleTriggerClick = useEventListener(
  triggerRef,
  "click",
  (event: Event) => {
    if (props.trigger !== "click") return;
    event.stopPropagation();
    isVisible.value ? hide() : show();
  }
);

// Handle content click
useEventListener(popoverRef, "click", (event: Event) => {
  if (!props.closeOnContentClick) {
    event.stopPropagation();
    return;
  }
  hide();
});

// Update position on visibility change
whenever(isVisible, async () => {
  await nextTick();
  triggerBounds.update();
  popoverBounds.update();
});

// Cleanup
tryOnUnmounted(() => {
  stopShowTimer();
  stopHideTimer();
});

// Computed styles remain unchanged
const getArrowPositionClass = computed(() => {
  const baseClasses = "border-gray-200 dark:border-zinc-700";
  const positions = {
    top: `bottom-[-7px] border-b border-r ${baseClasses}`,
    bottom: `top-[-7px] border-t border-l ${baseClasses}`,
    left: `right-[-7px] border-r border-t ${baseClasses}`,
    right: `left-[-7px] border-l border-b ${baseClasses}`,
  };

  const direction = props.placement.split("-")[0] as keyof typeof positions;
  return positions[direction];
});

const transitClass = computed(() => {
  const base = "transition-all duration-200 ease-out";
  const directions = {
    top: "translate-y-2",
    bottom: "-translate-y-2",
    left: "translate-x-2",
    right: "-translate-x-2",
  };

  const direction = props.placement.split("-")[0] as keyof typeof directions;
  const transform = directions[direction];

  return {
    enter: `${base}`,
    leave: `${base}`,
    enterFrom: `opacity-0 scale-95 ${transform}`,
    leaveTo: `opacity-0 scale-95 ${transform}`,
  };
});

const popoverStyle = computed(() => {
  const style: Record<string, string> = {};

  if (!triggerBounds.width.value || !popoverBounds.width.value) return style;

  const { width: triggerWidth, height: triggerHeight } = triggerBounds;
  const { width: popoverWidth, height: popoverHeight } = popoverBounds;

  switch (props.placement) {
    // 顶部系列
    case "top":
      style.bottom = `${triggerHeight.value + props.offset}px`;
      style.left = `${(triggerWidth.value - popoverWidth.value) / 2}px`;
      break;
    case "top-start":
      style.bottom = `${triggerHeight.value + props.offset}px`;
      style.left = "0px";
      break;
    case "top-end":
      style.bottom = `${triggerHeight.value + props.offset}px`;
      style.right = "0px";
      break;

    // 底部系列
    case "bottom":
      style.top = `${triggerHeight.value + props.offset}px`;
      style.left = `${(triggerWidth.value - popoverWidth.value) / 2}px`;
      break;
    case "bottom-start":
      style.top = `${triggerHeight.value + props.offset}px`;
      style.left = "0px";
      break;
    case "bottom-end":
      style.top = `${triggerHeight.value + props.offset}px`;
      style.right = "0px";
      break;

    // 左侧系列
    case "left":
      style.right = `${triggerWidth.value + props.offset}px`;
      style.top = `${(triggerHeight.value - popoverHeight.value) / 2}px`;
      break;
    case "left-start":
      style.right = `${triggerWidth.value + props.offset}px`;
      style.top = "0px";
      break;
    case "left-end":
      style.right = `${triggerWidth.value + props.offset}px`;
      style.bottom = "0px";
      break;

    // 右侧系列
    case "right":
      style.left = `${triggerWidth.value + props.offset}px`;
      style.top = `${(triggerHeight.value - popoverHeight.value) / 2}px`;
      break;
    case "right-start":
      style.left = `${triggerWidth.value + props.offset}px`;
      style.top = "0px";
      break;
    case "right-end":
      style.left = `${triggerWidth.value + props.offset}px`;
      style.bottom = "0px";
      break;
  }

  return style;
});
</script>

<style scoped>
.popover-enter-active,
.popover-leave-active {
  @apply transition-all duration-200 ease-out transform-gpu;
}

.popover-enter-from,
.popover-leave-to {
  @apply opacity-0 scale-95;
}
</style>
