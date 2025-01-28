<template>
  <div>
    <transition name="fade">
      <div
        v-if="isVisable"
        @click="close"
        class="fixed inset-0 bg-zinc-900/80 backdrop-blur-sm z-40"
      ></div>
    </transition>
    <transition name="up">
      <div
        v-if="isVisable"
        class="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 max-w-[80%] max-h-[80%] overflow-auto z-50 p-6 rounded-lg border shadow-lg dark:border-zinc-600 bg-white dark:bg-zinc-800 xl:min-w-[35%]"
      >
        <div
          class="flex items-center justify-between text-lg font-bold text-zinc-900 dark:text-zinc-200 mb-4 border-b border-zinc-200 dark:border-zinc-700 pb-2"
          v-if="title"
        >
          {{ title }}
          <button
            v-if="showClose"
            @click="close"
            class="text-zinc-500 hover:text-zinc-700 dark:text-zinc-400 dark:hover:text-zinc-200"
          >
            <svg
              class="w-5 h-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
        <div class="text-base text-zinc-900 dark:text-zinc-200 mb-6">
          <slot />
        </div>
        <div
          class="flex justify-end gap-2"
          v-if="cancelHandler || confirmHandler"
        >
          <button
            v-if="cancelHandler"
            type="button"
            :class="[
              'rounded-md px-4 py-2 text-sm font-semibold shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2',
              `ring-1 ring-inset ring-${cancelButtonColor}-300`,
              `text-${cancelButtonColor}-700`,
              `hover:bg-${cancelButtonColor}-50`,
              `focus:ring-${cancelButtonColor}-500`,
            ]"
            @click="onCancelClick"
          >
            {{ cancelText }}
          </button>
          <button
            v-if="confirmHandler"
            type="button"
            :class="[
              'rounded-md px-4 py-2 text-sm font-semibold shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2',
              `bg-${confirmButtonColor}-600`,
              'text-white',
              `hover:bg-${confirmButtonColor}-500`,
              `focus:ring-${confirmButtonColor}-500`,
            ]"
            @click="onConfirmClick"
          >
            {{ confirmText }}
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { useVModel } from "@vueuse/core";

/**
 * @modelValue 控制开关
 * @title 标题
 * @cancelText 取消按钮文本
 * @confirmText 确定按钮文本
 * @cancelHandler 取消按钮点击事件
 * @confirmHandler 确定按钮点击事件
 * @close 关闭的回调
 * @showClose 是否显示关闭按钮
 * @cancelButtonColor 取消按钮颜色
 * @confirmButtonColor 确定按钮颜色
 */
interface Props {
  modelValue: boolean;
  title?: string;
  cancelText?: string;
  confirmText?: string;
  cancelHandler?: () => void;
  confirmHandler?: () => void;
  close?: () => void;
  showClose?: boolean;
  cancelButtonColor?: string;
  confirmButtonColor?: string;
}

const props = withDefaults(defineProps<Props>(), {
  cancelText: "取消",
  confirmText: "确定",
  showClose: true,
  cancelButtonColor: "gray",
  confirmButtonColor: "red",
});

defineEmits(["update:modelValue"]);

// 控制显示处理
const isVisable = useVModel(props);

/**
 * 取消按钮点击事件
 */
const onCancelClick = () => {
  if (props.cancelHandler) {
    props.cancelHandler();
  }
  close();
};

/**
 * 确定按钮点击事件
 */
const onConfirmClick = () => {
  if (props.confirmHandler) {
    props.confirmHandler();
  }
  close();
};

const close = () => {
  isVisable.value = false;
  if (props.close) {
    props.close();
  }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.up-enter-active,
.up-leave-active {
  transition: all 0.3s ease;
}

.up-enter-from,
.up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

button {
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}
</style>
