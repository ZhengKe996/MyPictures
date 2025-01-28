<template>
  <div
    class="relative inline-block"
    @mouseleave="onMouseleave"
    @mouseenter="onMouseenter"
    @click.stop="onClick"
  >
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
          'absolute z-50 p-2',
          'bg-white dark:bg-zinc-800 shadow-lg',
          'border border-gray-200 dark:border-zinc-700 rounded-lg',
          customClass,
        ]"
        :style="popoverStyle"
      >
        <slot />
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import {
  ref,
  watch,
  nextTick,
  computed,
  onMounted,
  onBeforeUnmount,
} from "vue";
import { useElementBounding } from "@vueuse/core";

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
}

const props = withDefaults(defineProps<Props>(), {
  placement: "bottom",
  offset: 8,
  delay: 300,
  trigger: "hover",
  arrow: false,
  customClass: "",
});

const isVisible = ref(false);
const triggerRef = ref<HTMLElement | null>(null);
const popoverRef = ref<HTMLElement | null>(null);

// 使用 VueUse 的 useElementBounding 来获取元素位置
const triggerBounds = useElementBounding(triggerRef);
const popoverBounds = useElementBounding(popoverRef);

let timeout: NodeJS.Timeout | null = null;

// 更新弹出层样式计算逻辑
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

// 更新过渡动画类
const transitClass = computed(() => {
  const base = "transition-all duration-200 ease-in-out";
  const baseTransitions = {
    top: { y: "-2", x: "0" },
    bottom: { y: "2", x: "0" },
    left: { y: "0", x: "-2" },
    right: { y: "0", x: "2" },
  };

  const getBaseDirection = (placement: PlacementType) => {
    if (placement.startsWith("top")) return "top";
    if (placement.startsWith("bottom")) return "bottom";
    if (placement.startsWith("left")) return "left";
    return "right";
  };

  const direction = getBaseDirection(props.placement);
  const { x, y } = baseTransitions[direction];

  return {
    enter: base,
    leave: base,
    enterFrom: `opacity-0 transform translate-x-${x} translate-y-${y}`,
    leaveTo: `opacity-0 transform translate-x-${x} translate-y-${y}`,
  };
});

const emit = defineEmits(["show", "hide"]);

// 修改可见状态的处理函数
const toggleVisible = (value: boolean) => {
  isVisible.value = value;
  emit(value ? "show" : "hide");
};

// 新增点击处理函数
const onClick = (event: MouseEvent) => {
  if (props.trigger !== "click") return;
  event.stopPropagation();
  toggleVisible(!isVisible.value);
};

// 修改点击外部处理函数
const handleClickOutside = (event: MouseEvent) => {
  if (props.trigger !== "click") return;
  const target = event.target as HTMLElement;
  if (
    !triggerRef.value?.contains(target) &&
    !popoverRef.value?.contains(target)
  ) {
    toggleVisible(false);
  }
};

// 修改鼠标进入处理函数
const onMouseenter = () => {
  if (props.trigger !== "hover") return;
  if (timeout) clearTimeout(timeout);
  toggleVisible(true);
};

const onMouseleave = () => {
  if (props.trigger !== "hover") return;
  timeout = setTimeout(() => {
    toggleVisible(false);
    timeout = null;
  }, props.delay);
};

// 监听可见性变化时重新计算位置
watch(isVisible, async (val) => {
  if (val) {
    await nextTick();
    // 触发重新计算
    triggerBounds.update();
    popoverBounds.update();
  }
});

onMounted(() => {
  if (props.trigger === "click") {
    document.addEventListener("click", handleClickOutside);
  }
});

onBeforeUnmount(() => {
  if (props.trigger === "click") {
    document.removeEventListener("click", handleClickOutside);
  }
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
