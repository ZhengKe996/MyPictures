<template>
  <div ref="containerRef" class="relative inline-block">
    <div ref="triggerRef" class="inline-block">
      <slot name="reference" />
    </div>
    <Transition
      :enter-active-class="`transform-gpu transition-all duration-300 ease-out ${getAnimationClass}`"
      :leave-active-class="'transform-gpu transition-all duration-200 ease-in-back'"
      :enter-from-class="'opacity-0 scale-95 blur-sm'"
      :leave-to-class="'opacity-0 scale-95 blur-sm'"
    >
      <div
        v-show="isVisible"
        ref="popoverRef"
        :class="[
          'absolute z-50',
          'bg-white/90 dark:bg-zinc-800/90',
          'backdrop-blur-lg',
          'border border-gray-200/50 dark:border-zinc-700/50',
          'shadow-lg shadow-gray-200/20 dark:shadow-zinc-900/30',
          'rounded-xl',
          'transform-gpu popover-animate',
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
import { usePopover, type PopoverProps, type PopoverEmits } from ".";

const props = withDefaults(defineProps<PopoverProps>(), {
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

const emit = defineEmits<PopoverEmits>();

const {
  containerRef,
  triggerRef,
  popoverRef,
  isVisible,
  getArrowPositionClass,
  getAnimationClass,
  popoverStyle,
} = usePopover(props, (event) =>
  event === "show" ? emit("show") : emit("hide")
);
</script>

<style scoped>
.popover-animate {
  will-change: transform, opacity;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(0.5rem) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideDownFade {
  from {
    opacity: 0;
    transform: translateY(-0.5rem) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideLeftFade {
  from {
    opacity: 0;
    transform: translateX(0.5rem) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes slideRightFade {
  from {
    opacity: 0;
    transform: translateX(-0.5rem) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.slide-up-fade {
  animation: slideUpFade 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-down-fade {
  animation: slideDownFade 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-left-fade {
  animation: slideLeftFade 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-right-fade {
  animation: slideRightFade 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>
