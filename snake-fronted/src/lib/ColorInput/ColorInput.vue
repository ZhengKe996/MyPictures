<template>
  <div class="relative" ref="colorPickerRef">
    <div
      class="color-input group relative w-12 h-12 rounded-xl cursor-pointer transition-all duration-300 hover:scale-105 ring-2 ring-gray-200 hover:ring-offset-2 hover:ring-blue-400 shadow-lg"
      :style="{ backgroundColor: displayColor }"
      @click="togglePalette"
    >
      <div
        class="absolute inset-0 rounded-xl bg-black/5 group-hover:bg-black/0 transition-colors"
      ></div>
    </div>

    <div
      v-show="showPalette"
      class="absolute mt-4 p-4 bg-white rounded-2xl shadow-[0_10px_40px_-5px_rgba(0,0,0,0.15)] backdrop-blur-sm bg-white/95 border border-gray-100 z-20 min-w-[320px] transition-all duration-200 transform"
      :class="[
        position === 'top' ? 'bottom-full mb-2' : 'top-full',
        showPalette ? 'translate-y-0 opacity-100' : 'translate-y-2 opacity-0',
      ]"
      @click.stop
    >
      <div class="mb-4 flex items-center justify-between">
        <span class="text-sm font-medium text-gray-700">选择颜色</span>
        <div class="flex items-center gap-2">
          <span class="text-xs text-gray-500 font-mono">{{ modelValue }}</span>
          <div
            class="w-8 h-8 rounded-lg shadow-inner"
            :style="{ backgroundColor: displayColor }"
          ></div>
        </div>
      </div>

      <div
        class="grid grid-cols-8 gap-2 p-2 bg-gray-50 rounded-xl mb-4"
        role="grid"
        aria-label="色彩选择面板"
      >
        <div
          v-for="(color, index) in colorList"
          :key="index"
          class="group relative"
        >
          <div
            :ref="(el) => (colorBlockRefs[index] = el as HTMLElement)"
            class="w-8 h-8 rounded-lg cursor-pointer transition-all duration-200"
            :class="[
              'ring-2 relative',
              normalizeColorForComparison(color) ===
                normalizeColorForComparison(modelValue) &&
              focusedIndex !== index
                ? 'ring-blue-400 ring-offset-2 hover:ring-blue-500'
                : 'ring-transparent',
              focusedIndex === index
                ? [
                    'ring-offset-2 scale-125 shadow-lg z-10',
                    'ring-indigo-500',
                    'after:absolute after:inset-0 after:rounded-lg after:ring-2 after:ring-white/50',
                    'before:absolute before:-inset-1 before:rounded-xl before:bg-indigo-500/20',
                  ]
                : '',
              'hover:ring-blue-400 hover:ring-offset-2 hover:scale-110',
              'focus:outline-none',
            ]"
            :style="{
              backgroundColor: formatColorForDisplay(color),
            }"
            @click="selectColor(color)"
            @keydown="handleKeyDown($event, index)"
            @blur="() => handleBlur(index)"
            tabindex="0"
            role="gridcell"
            :aria-label="`选择颜色: ${color}`"
            :aria-selected="color === modelValue"
          ></div>
        </div>
      </div>

      <div v-if="props.allowCustom" class="flex items-center gap-3">
        <div class="relative flex-1">
          <input
            type="text"
            v-model="currentCustomColor"
            class="w-full px-3 py-2 text-sm font-mono border rounded-lg bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-400 transition-all"
            placeholder="#ffffff"
            @keydown.enter.prevent="handleInputEnter"
            @click.stop
          />
          <div
            class="absolute right-2 top-1/2 -translate-y-1/2 w-4 h-4 rounded transition-all duration-200"
            :class="[isValidColor ? 'opacity-100' : 'opacity-0']"
            :style="{
              backgroundColor: isValidColor
                ? currentCustomColor
                : 'transparent',
            }"
          ></div>
        </div>
        <button
          @click="applyCustomColor"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg transition-all hover:bg-blue-600 focus:ring-2 focus:ring-blue-400 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap"
          :disabled="!isValidColor"
        >
          应用颜色
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from "vue";
import { onClickOutside, useEventListener } from "@vueuse/core";
import {
  DEFAULT_COLORS,
  isValidHexColor,
  type ColorInputProps,
  type ColorInputEmits,
} from "./index";
import { formatColor, isValidColor as validateColor } from "./colorUtils";

const props = withDefaults(defineProps<ColorInputProps>(), {
  modelValue: "#000000",
  position: "bottom",
  colors: () => DEFAULT_COLORS,
  allowCustom: true,
  colorOptions: () => ({ outputFormat: "hex" }),
});

const emit = defineEmits<ColorInputEmits>();

const colorPickerRef = ref<HTMLElement | null>(null);
const showPalette = ref(false);
const currentCustomColor = ref("");

// 使用 onClickOutside 替代原来的 handleClickOutside
onClickOutside(colorPickerRef, () => {
  showPalette.value = false;
});

// 使用 useEventListener 处理按键事件
useEventListener("keydown", (e: KeyboardEvent) => {
  if (e.key === "Escape" && showPalette.value) {
    showPalette.value = false;
  }
});

const focusedIndex = ref(-1);
const colorBlockRefs = ref<Array<HTMLElement | null>>([]);

// 初始化 refs 数组
onMounted(() => {
  colorBlockRefs.value = new Array(colorList.value.length).fill(null);
});

// 处理键盘导航
const handleKeyDown = (event: KeyboardEvent, index: number) => {
  const cols = 8; // 网格列数
  const total = colorList.value.length;
  let newIndex = index;

  switch (event.key) {
    case "ArrowRight":
      event.preventDefault();
      newIndex = index + 1;
      break;
    case "ArrowLeft":
      event.preventDefault();
      newIndex = index - 1;
      break;
    case "ArrowUp":
      event.preventDefault();
      newIndex = index - cols;
      break;
    case "ArrowDown":
      event.preventDefault();
      newIndex = index + cols;
      break;
    case "Enter":
    case " ":
      event.preventDefault();
      const selectedColor = colorList.value[index];
      if (isValidHexColor(selectedColor)) {
        emit("update:modelValue", selectedColor);
        showPalette.value = false; // 确保选中后关闭调色板
        focusedIndex.value = -1; // 清除焦点索引
      }
      return;
    case "Home":
      event.preventDefault();
      newIndex = 0;
      break;
    case "End":
      event.preventDefault();
      newIndex = total - 1;
      break;
    default:
      return;
  }

  // 在设置新焦点之前，给上一个焦点添加过渡效果
  if (focusedIndex.value >= 0) {
    const prevElement = colorBlockRefs.value[focusedIndex.value];
    if (prevElement) {
      prevElement.style.transition = "all 0.2s cubic-bezier(0.4, 0, 0.2, 1)";
    }
  }

  // 确保新索引在有效范围内
  newIndex = ((newIndex % total) + total) % total;
  focusedIndex.value = newIndex;

  // 聚焦到新的色块并添加过渡效果
  const targetElement = colorBlockRefs.value[newIndex];
  if (targetElement) {
    targetElement.style.transition = "all 0.2s cubic-bezier(0.4, 0, 0.2, 1)";
    targetElement.focus();
  }
};

// 监听调色板显示状态，显示时重置焦点
watch(showPalette, (newValue) => {
  if (!newValue) {
    focusedIndex.value = -1; // 确保关闭时清除焦点
  } else {
    focusedIndex.value = -1;
    // 延迟聚焦到第一个色块
    nextTick(() => {
      const firstBlock = colorBlockRefs.value[0];
      if (firstBlock) {
        firstBlock.focus();
        focusedIndex.value = 0;
      }
    });
  }
});

// 修改现有的 togglePalette 函数
const togglePalette = () => {
  showPalette.value = !showPalette.value;
};

// 修改 isValidColor 计算属性
const isValidColor = computed(() => {
  return validateColor(currentCustomColor.value);
});

// 更新 colorList computed 属性
const colorList = computed(() => {
  const sourceColors = props.colors ?? DEFAULT_COLORS;

  try {
    return sourceColors.map((color) => {
      try {
        // 如果颜色已经是 0x 格式且需要 0x 输出，直接使用
        if (
          props.colorOptions.outputFormat === "0x" &&
          color.startsWith("0x")
        ) {
          return color;
        }

        // 其他情况，进行格式转换
        return formatColor(color, props.colorOptions);
      } catch (e) {
        console.warn(`Invalid color in list: ${color}`, e);
        return props.colorOptions.outputFormat === "0x"
          ? "0x000000"
          : "#000000";
      }
    });
  } catch (e) {
    console.error("Error processing color list:", e);
    return DEFAULT_COLORS.map((color) =>
      formatColor(color, props.colorOptions)
    );
  }
});

// 更新 displayColor computed 属性
const displayColor = computed(() => {
  return formatColorForDisplay(props.modelValue || "#000000");
});

// 更新 selectColor 方法
const selectColor = (color: string) => {
  try {
    if (!color) return;

    const formattedColor = formatColor(color, props.colorOptions);
    emit("update:modelValue", formattedColor);
    emit("change", formattedColor);
    currentCustomColor.value = formattedColor;
    showPalette.value = false;
  } catch (e) {
    console.error("Error selecting color:", e);
  }
};

// 修改 applyCustomColor 函数，添加保存功能
const applyCustomColor = () => {
  if (!props.allowCustom) return;

  try {
    if (validateColor(currentCustomColor.value)) {
      const formattedColor = formatColor(
        currentCustomColor.value,
        props.colorOptions
      );
      emit("update:modelValue", formattedColor);
      emit("change", formattedColor);
      showPalette.value = false;
      currentCustomColor.value = "";
    } else {
      console.warn("请输入有效的颜色值");
    }
  } catch (e) {
    console.error("Error applying custom color:", e);
  }
};

// 处理输入框回车事件
const handleInputEnter = () => {
  if (validateColorFormat(currentCustomColor.value)) {
    applyCustomColor();
  } else {
    console.warn("请输入有效的颜色值");
  }
};

// 添加失焦处理
const handleBlur = (index: number) => {
  if (focusedIndex.value === index) {
    focusedIndex.value = -1;
  }
};

// 添加颜色转换方法
const normalizeColorInput = (color: string): string => {
  try {
    return formatColor(color, props.colorOptions);
  } catch (e) {
    console.warn("Color normalization failed:", e);
    return props.colorOptions.outputFormat === "0x" ? "0x000000" : "#000000";
  }
};

// 添加颜色格式验证方法
const validateColorFormat = (color: string): boolean => {
  try {
    const normalized = normalizeColorInput(color);
    return Boolean(
      normalized && normalized !== "#000000" && normalized !== "0x000000"
    );
  } catch {
    return false;
  }
};

// 添加用于显示的颜色格式化函数
const formatColorForDisplay = (color: string): string => {
  try {
    // 始终以 hex 格式显示颜色
    return formatColor(color, { outputFormat: "hex" });
  } catch (e) {
    console.warn("Error formatting color for display:", color, e);
    return "#000000";
  }
};

// 添加用于比较的颜色格式化函数
const normalizeColorForComparison = (color: string): string => {
  try {
    // 将颜色统一转换为目标格式进行比较
    return formatColor(color, props.colorOptions);
  } catch (e) {
    console.warn("Error normalizing color for comparison:", color, e);
    return props.colorOptions.outputFormat === "0x" ? "0x000000" : "#000000";
  }
};
</script>
