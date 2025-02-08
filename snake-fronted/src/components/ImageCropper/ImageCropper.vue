<template>
  <div
    class="flex flex-col w-full max-w-4xl mx-auto bg-white rounded-lg shadow-sm"
  >
    <!-- Cropper Container with loading state -->
    <div
      class="relative w-full h-[400px] bg-gray-50 rounded-t-lg overflow-hidden"
    >
      <vue-cropper
        ref="cropperRef"
        :img="imageUrl"
        :autoCrop="true"
        :fixedBox="false"
        :centerBox="true"
        :canMoveBox="true"
        :info="true"
        outputType="png"
        class="!h-full transition-all duration-300 ease-in-out"
        style="height: 100%"
        @ready="handleReady"
        @imgLoad="handleImgLoad"
        @cropstart="handleCropStart"
        @cropend="handleCropEnd"
        @error="handleError"
      />

      <!-- Loading overlay -->
      <div
        v-if="isLoading"
        class="absolute inset-0 bg-white/60 backdrop-blur-sm flex items-center justify-center transition-opacity duration-300"
      >
        <div class="flex flex-col items-center gap-2">
          <div
            class="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"
          ></div>
          <span class="text-sm text-gray-600">加载中...</span>
        </div>
      </div>
    </div>

    <!-- Controls Section with improved layout -->
    <div class="flex flex-col gap-4 p-4 border-t border-gray-100">
      <div class="flex flex-wrap justify-center gap-3">
        <button
          v-for="(action, index) in actions"
          :key="index"
          @click="action.handler"
          :disabled="isLoading"
          class="group px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-200 rounded-lg transition-all duration-200 ease-in-out hover:bg-gray-50 hover:border-gray-300 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500/20"
        >
          <span
            class="transition-transform duration-200 group-hover:scale-105 inline-block"
          >
            {{ action.label }}
          </span>
        </button>

        <button
          @click="handleConfirm"
          :disabled="isLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-blue-500 rounded-lg transition-all duration-200 ease-in-out hover:bg-blue-600 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500/20"
        >
          确认裁剪
        </button>

        <button
          @click="handleDownload"
          :disabled="isLoading"
          class="px-4 py-2 text-sm font-medium text-white bg-green-500 rounded-lg transition-all duration-200 ease-in-out hover:bg-green-600 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-green-500/20"
        >
          下载图片
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { VueCropper } from "vue-cropper";
import "vue-cropper/dist/index.css";
import { ref } from "vue";

interface Props {
  imageUrl?: string;
  fileName?: string;
}

interface Emits {
  (event: "crop", file: File): void;
  (event: "ready"): void;
  (event: "error", error: Error): void;
  (event: "load"): void;
  (event: "cropstart"): void;
  (event: "cropend"): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const cropperRef = ref();
const isLoading = ref(true);

// 组件就绪
const handleReady = () => {
  emit("ready");
};

// 图片加载完成
const handleImgLoad = () => {
  isLoading.value = false;
  emit("load");
};

// 开始裁剪
const handleCropStart = () => {
  emit("cropstart");
};

// 裁剪结束
const handleCropEnd = () => {
  emit("cropend");
};

// 错误处理
const handleError = (error: Error) => {
  console.error("Cropper error:", error);
  isLoading.value = false;
  emit("error", error);
};

// 向左旋转
const rotateLeft = () => {
  cropperRef.value.rotateLeft();
};

// 向右旋转
const rotateRight = () => {
  cropperRef.value.rotateRight();
};

// 缩放
const changeScale = (num: number) => {
  cropperRef.value.changeScale(num);
};

/**
 * 处理图片下载
 * 将裁剪后的图片转换为webp格式并下载
 */
const handleDownload = async () => {
  if (!cropperRef.value) return;

  try {
    cropperRef.value.getCropData(async (data: string) => {
      // 创建一个临时的 canvas 元素
      const canvas = document.createElement("canvas");
      const ctx = canvas.getContext("2d");
      const img = new Image();

      img.onload = async () => {
        // 设置 canvas 尺寸
        canvas.width = img.width;
        canvas.height = img.height;

        // 绘制图片
        ctx?.drawImage(img, 0, 0);

        // 转换为 webp 格式
        const webpData = canvas.toDataURL("image/webp", 0.9);

        // 创建下载链接
        const link = document.createElement("a");
        link.download = `${props.fileName || "cropped"}.webp`;
        link.href = webpData;

        // 触发下载
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      };

      img.src = data;
    });
  } catch (error) {
    console.error("Download error:", error);
  }
};

/**
 * 处理确认裁剪
 * 获取裁剪后的图片文件并发出事件
 */
const handleConfirm = () => {
  if (!cropperRef.value) return;

  try {
    cropperRef.value.getCropBlob((blob: Blob) => {
      const file = new File([blob], `${props.fileName || "cropped"}.png`, {
        type: "image/png",
      });
      emit("crop", file);
      console.log("裁剪成功！", file);
    });
  } catch (error) {
    console.error("裁剪失败:", error);
    emit("error", error as Error);
  }
};

const actions = [
  { label: "向左旋转", handler: rotateLeft },
  { label: "向右旋转", handler: rotateRight },
  { label: "放大", handler: () => changeScale(0.1) },
  { label: "缩小", handler: () => changeScale(-0.1) },
];
</script>
