<template>
  <div ref="containerRef" class="relative inline-block text-left">
    <button
      ref="triggerRef"
      type="button"
      :class="[
        'inline-flex items-center justify-center gap-x-2 rounded-md font-medium',
        'transition-all duration-200 ease-out',
        'focus:outline-none focus:ring-2 focus:ring-primary-500/50 focus:ring-offset-2',
        DROPDOWN_CONFIGS.sizeClasses[size],
        buttonClass ||
          'bg-white text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50',
      ]"
      @click="toggle"
      aria-haspopup="true"
      :aria-expanded="isOpen"
    >
      <i v-if="triggerIcon" :class="[triggerIcon, triggerIconSizeClass]"></i>
      {{ label }}
      <i
        :class="[
          iconClass || 'i-tabler-chevron-down',
          triggerIconSizeClass,
          isOpen ? 'rotate-180' : '',
          'transform transition-transform duration-300 ease-out',
        ]"
      ></i>
    </button>

    <teleport to="body">
      <transition
        enter-active-class="transition duration-200 ease-out"
        enter-from-class="opacity-0 translate-y-1 scale-95"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-active-class="transition duration-150 ease-in"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        leave-to-class="opacity-0 translate-y-1 scale-95"
      >
        <div
          v-show="isOpen"
          ref="dropdownRef"
          :style="dropdownStyle"
          class="fixed z-50 origin-top-right"
          :class="[DROPDOWN_CONFIGS.dropdownSizeClasses[size]]"
        >
          <div
            class="overflow-hidden rounded-lg bg-white shadow-lg ring-1 ring-black/5 backdrop-blur-sm"
            :class="[
              'transition-all duration-200',
              isOpen ? 'opacity-100' : 'opacity-0',
            ]"
          >
            <div class="py-1">
              <template v-for="(item, index) in items" :key="item.id">
                <div
                  v-if="item.type === 'divider'"
                  class="my-1 h-px bg-gray-200"
                  role="separator"
                ></div>
                <component
                  v-else
                  :is="item.type === 'button' ? 'button' : 'a'"
                  :href="item.type === 'link' ? item.href : undefined"
                  :class="[
                    'group relative flex w-full items-center px-4 py-2',
                    itemSizeClasses[size],
                    'transition-all duration-200',
                    'focus:outline-none focus:bg-gray-50',
                    currentFocus === index
                      ? 'bg-gray-50 text-gray-900'
                      : 'text-gray-700',
                    item.disabled
                      ? 'cursor-not-allowed opacity-50'
                      : 'hover:bg-gray-50 active:bg-gray-100',
                    item.danger && !item.disabled
                      ? 'text-red-600 hover:text-red-700 hover:bg-red-50'
                      : '',
                  ]"
                  @click="!item.disabled && handleItemClick(item)"
                  @mouseover="!item.disabled && (currentFocus = index)"
                  :disabled="item.disabled"
                  :tabindex="isOpen && !item.disabled ? 0 : -1"
                >
                  <span class="flex items-center gap-2 truncate">
                    <i
                      v-if="item.icon"
                      :class="[
                        item.icon,
                        itemIconSizeClass,
                        item.iconClass ||
                          'text-gray-400 group-hover:text-gray-500',
                      ]"
                    ></i>
                    <span>{{ item.label }}</span>
                  </span>
                  <i
                    v-if="item.endIcon"
                    :class="[
                      item.endIcon,
                      itemIconSizeClass,
                      'ml-auto',
                      item.iconClass ||
                        'text-gray-400 group-hover:text-gray-500',
                    ]"
                  ></i>
                </component>
              </template>
            </div>
          </div>
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from "vue";
import {
  useEventListener,
  onClickOutside,
  useWindowSize,
  useElementBounding,
  useWindowScroll,
  useFocus,
  useDocumentVisibility,
} from "@vueuse/core";
import type { DropdownItem, DropdownProps } from "./index";
import { DROPDOWN_CONFIGS } from "./index";

const props = withDefaults(defineProps<DropdownProps>(), {
  buttonClass: "",
  iconClass: "",
  triggerIcon: "",
  size: "md",
  placement: "bottom",
});

const emit = defineEmits<{
  (e: "select", item: DropdownItem): void;
  (e: "open"): void;
  (e: "close"): void;
}>();

// Refs
const containerRef = ref<HTMLElement | null>(null);
const triggerRef = ref<HTMLElement | null>(null);
const dropdownRef = ref<HTMLElement | null>(null);

// State
const isOpen = ref(false);
const currentFocus = ref(-1);

// VueUse hooks
const { width: windowWidth, height: windowHeight } = useWindowSize();
const triggerBounds = useElementBounding(triggerRef);
const dropdownBounds = useElementBounding(dropdownRef);
const { x: scrollX, y: scrollY } = useWindowScroll();
const { focused } = useFocus(triggerRef, { initialValue: false });
const visibility = useDocumentVisibility();

// 监听点击外部
onClickOutside(containerRef, () => {
  if (isOpen.value) {
    close();
  }
});

// 计算下拉菜单位置
const dropdownStyle = computed(() => {
  if (!triggerBounds.bottom.value) return {};

  const { bottom, left, right, top, width } = {
    bottom: triggerBounds.bottom.value,
    left: triggerBounds.left.value,
    right: triggerBounds.right.value,
    top: triggerBounds.top.value,
    width: triggerBounds.width.value,
  };
  const dropdownHeight = dropdownBounds.height.value || 0;

  let positionY = bottom + 8;
  let positionX = left;

  // 检查是否需要向上显示
  if (positionY + dropdownHeight > windowHeight.value) {
    positionY = top - dropdownHeight - 8;
  }

  // 检查是否需要向左对齐
  if (positionX + width > windowWidth.value) {
    positionX = right - width;
  }

  return {
    top: `${positionY}px`,
    left: `${positionX}px`,
  };
});

// Size classes
const triggerIconSizeClass = computed(
  () => DROPDOWN_CONFIGS.iconSizeClasses.trigger[props.size]
);

const itemIconSizeClass = computed(
  () => DROPDOWN_CONFIGS.iconSizeClasses.item[props.size]
);

const itemSizeClasses = DROPDOWN_CONFIGS.itemSizeClasses;

// Methods
const open = async () => {
  isOpen.value = true;
  currentFocus.value = props.items.findIndex(
    (item) => item.type !== "divider" && !item.disabled
  );
  emit("open");
};

const close = () => {
  isOpen.value = false;
  currentFocus.value = -1;
  emit("close");
};

const toggle = () => {
  isOpen.value ? close() : open();
};

// 使用 useKeyboard 处理键盘导航
useEventListener(containerRef, "keydown", (e: KeyboardEvent) => {
  if (!isOpen.value) return;

  switch (e.key) {
    case "ArrowDown":
      e.preventDefault();
      navigateItems(1);
      break;
    case "ArrowUp":
      e.preventDefault();
      navigateItems(-1);
      break;
    case "Enter":
      e.preventDefault();
      if (currentFocus.value >= 0) {
        const item = props.items[currentFocus.value];
        if (item && !item.disabled) {
          handleItemClick(item);
        }
      }
      break;
    case "Escape":
      e.preventDefault();
      close();
      break;
  }
});

// 当文档可见性改变时处理
watch(visibility, (isVisible) => {
  if (!isVisible && isOpen.value) {
    close();
  }
});

// 监听滚动和调整大小
watch([scrollX, scrollY, windowWidth, windowHeight], () => {
  if (isOpen.value) {
    nextTick(() => {
      const style = dropdownStyle.value;
      if (dropdownRef.value) {
        Object.assign(dropdownRef.value.style, style);
      }
    });
  }
});

// navigateItems 方法
const navigateItems = (direction: number) => {
  const enabledItems = props.items.reduce<number[]>((acc, item, index) => {
    if (item.type !== "divider" && !item.disabled) {
      acc.push(index);
    }
    return acc;
  }, []);

  if (enabledItems.length === 0) return;

  const currentIndex = enabledItems.indexOf(currentFocus.value);
  const nextIndex =
    currentIndex === -1
      ? 0
      : (currentIndex + direction + enabledItems.length) % enabledItems.length;

  currentFocus.value = enabledItems[nextIndex];
};

// handleItemClick 方法
const handleItemClick = (item: DropdownItem) => {
  if (item.disabled) return;

  if (item.onClick) {
    item.onClick();
  }
  emit("select", item);
  close();
};

// Focus management
watch(focused, (isFocused) => {
  if (!isFocused && !isOpen.value) {
    currentFocus.value = -1;
  }
});
</script>
