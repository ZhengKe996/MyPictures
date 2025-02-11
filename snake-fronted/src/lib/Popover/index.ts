import { ref, computed, nextTick } from "vue";
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

// 类型定义
export type PlacementType =
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

export interface PopoverProps {
  placement?: PlacementType;
  offset?: number;
  delay?: number;
  trigger?: "hover" | "click";
  arrow?: boolean;
  customClass?: string;
  contentClass?: string;
  closeOnContentClick?: boolean;
  showDelay?: number;
  hideDelay?: number;
  persistent?: boolean;
}

export interface PopoverEmits {
  (evt: "show"): void;
  (evt: "hide"): void;
}

// Hook: usePopover
export function usePopover(
  props: PopoverProps,
  emit: (event: "show" | "hide") => void
) {
  const containerRef = ref<HTMLElement | null>(null);
  const triggerRef = ref<HTMLElement | null>(null);
  const popoverRef = ref<HTMLElement | null>(null);
  const [isVisible, _] = useToggle(false);

  // VueUse composables
  const triggerBounds = useElementBounding(triggerRef);
  const popoverBounds = useElementBounding(popoverRef);
  const isHovered = useElementHover(containerRef);

  // Timeout handlers
  const { start: startShowTimer, stop: stopShowTimer } = useTimeoutFn(() => {
    isVisible.value = true;
    emit("show");
  }, props.showDelay || 0);

  const { start: startHideTimer, stop: stopHideTimer } = useTimeoutFn(() => {
    isVisible.value = false;
    emit("hide");
  }, props.hideDelay || 300);

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

  // Event handlers setup
  onClickOutside(
    containerRef,
    () => {
      if (props.trigger === "click" && !props.persistent) {
        hide();
      }
    },
    { ignore: [triggerRef] }
  );

  whenever(isHovered, (value) => {
    if (props.trigger !== "hover") return;
    value ? show() : hide();
  });

  useEventListener(triggerRef, "click", (event: Event) => {
    if (props.trigger !== "click") return;
    event.stopPropagation();
    isVisible.value ? hide() : show();
  });

  useEventListener(popoverRef, "click", (event: Event) => {
    if (!props.closeOnContentClick) {
      event.stopPropagation();
      return;
    }
    hide();
  });

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

  // Computed styles
  const getArrowPositionClass = computed(() => {
    const baseClasses = "border-gray-200 dark:border-zinc-700";
    const positions = {
      top: `bottom-[-7px] border-b border-r ${baseClasses}`,
      bottom: `top-[-7px] border-t border-l ${baseClasses}`,
      left: `right-[-7px] border-r border-t ${baseClasses}`,
      right: `left-[-7px] border-l border-b ${baseClasses}`,
    };

    const direction = props.placement?.split("-")[0] as keyof typeof positions;
    return positions[direction];
  });

  const getAnimationClass = computed(() => {
    const direction = props.placement?.split("-")[0];
    return (
      {
        top: "slide-down-fade",
        bottom: "slide-up-fade",
        left: "slide-right-fade",
        right: "slide-left-fade",
      }[direction as string] || "slide-up-fade"
    );
  });

  const popoverStyle = computed(() => {
    const style: Record<string, string> = {};
    if (!triggerBounds.width.value || !popoverBounds.width.value) return style;

    const { width: triggerWidth, height: triggerHeight } = triggerBounds;
    const { width: popoverWidth } = popoverBounds;
    const offset = props.offset ?? 8;

    // 计算位置样式
    switch (props.placement) {
      case "top":
        style.bottom = `${triggerHeight.value + offset}px`;
        style.left = `${(triggerWidth.value - popoverWidth.value) / 2}px`;
        break;
      // ... 其他位置计算保持不变
    }

    return style;
  });

  return {
    containerRef,
    triggerRef,
    popoverRef,
    isVisible,
    getArrowPositionClass,
    getAnimationClass,
    popoverStyle,
    show,
    hide,
  } as const;
}

export { default } from "./Popover.vue";
