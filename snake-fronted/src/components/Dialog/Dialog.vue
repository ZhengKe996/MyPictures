<template>
  <div>
    <!-- 蒙版 -->
    <transition name="fade">
      <div
        v-if="isVisable"
        @click="close"
        class="fixed inset-0 bg-zinc-900/80 z-40"
      ></div>
    </transition>
    <!-- 内容 -->
    <transition name="up">
      <div
        v-if="isVisable"
        class="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 max-w-[80%] max-h-[80%] overflow-auto z-50 p-6 rounded-lg border dark:border-zinc-600 bg-white dark:bg-zinc-800 xl:min-w-[35%]"
      >
        <!-- 标题 -->
        <div
          class="text-lg font-bold text-zinc-900 dark:text-zinc-200 mb-4 border-b border-zinc-200 dark:border-zinc-700 pb-2"
          v-if="title"
        >
          {{ title }}
        </div>
        <!-- 内容 -->
        <div class="text-base text-zinc-900 dark:text-zinc-200 mb-6">
          <slot />
        </div>
        <!-- 按钮 -->
        <div class="flex justify-end" v-if="cancelHandler || confirmHandler">
          <button
            type="button"
            class="rounded-md mr-2 bg-white px-4 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            @click="onCancelClick"
          >
            {{ cancelText }}
          </button>
          <button
            type="button"
            class="rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
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
 */
interface Props {
  modelValue: boolean;
  title?: string;
  cancelText?: string;
  confirmText?: string;
  cancelHandler?: () => void;
  confirmHandler?: () => void;
  close?: () => void;
}

const props = withDefaults(defineProps<Props>(), {
  cancelText: "取消",
  confirmText: "确定",
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
