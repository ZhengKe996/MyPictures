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

interface Props {
  placement?: "top" | "bottom" | "left" | "right";
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

// 计算弹出层样式
const popoverStyle = computed(() => {
  const style: Record<string, string> = {};

  if (!triggerBounds.width.value || !popoverBounds.width.value) return style;

  switch (props.placement) {
    case "top":
      style.bottom = `${triggerBounds.height.value + props.offset}px`;
      style.left = `${
        (triggerBounds.width.value - popoverBounds.width.value) / 2
      }px`;
      break;
    case "bottom":
      style.top = `${triggerBounds.height.value + props.offset}px`;
      style.left = `${
        (triggerBounds.width.value - popoverBounds.width.value) / 2
      }px`;
      break;
    case "left":
      style.right = `${triggerBounds.width.value + props.offset}px`;
      style.top = `${
        (triggerBounds.height.value - popoverBounds.height.value) / 2
      }px`;
      break;
    case "right":
      style.left = `${triggerBounds.width.value + props.offset}px`;
      style.top = `${
        (triggerBounds.height.value - popoverBounds.height.value) / 2
      }px`;
      break;
  }

  return style;
});

// 计算过渡动画类
const transitClass = computed(() => {
  const base = "transition-all duration-200 ease-in-out";
  const transitions = {
    top: {
      enter: `${base}`,
      leave: `${base}`,
      enterFrom: "opacity-0 transform -translate-y-2",
      leaveTo: "opacity-0 transform -translate-y-2",
    },
    bottom: {
      enter: `${base}`,
      leave: `${base}`,
      enterFrom: "opacity-0 transform translate-y-2",
      leaveTo: "opacity-0 transform translate-y-2",
    },
    left: {
      enter: `${base}`,
      leave: `${base}`,
      enterFrom: "opacity-0 transform -translate-x-2",
      leaveTo: "opacity-0 transform -translate-x-2",
    },
    right: {
      enter: `${base}`,
      leave: `${base}`,
      enterFrom: "opacity-0 transform translate-x-2",
      leaveTo: "opacity-0 transform translate-x-2",
    },
  };

  return transitions[props.placement];
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
