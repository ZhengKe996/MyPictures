<template>
  <div
    class="bg-white dark:bg-zinc-900 xl:dark:bg-zinc-800 rounded-lg pb-1 transition-all duration-300 hover:shadow-xl group/card preserve-3d"
  >
    <div
      class="relative w-full rounded-lg group overflow-hidden"
      :style="{
        backgroundColor: randomRGB(),
      }"
    >
      <!-- 图片 -->
      <img
        ref="imgTarget"
        class="w-full rounded-lg bg-transparent transition-all duration-500 ease-out will-change-transform group-hover/card:scale-[1.03] group-hover/card:brightness-90"
        :src="currentImageUrl"
        :style="{
          height: calculateImageHeight() + 'px',
        }"
        @click="onToPinsClick"
      />
      <!-- 遮罩层 -->
      <div
        class="hidden opacity-0 w-full h-full bg-gradient-to-b from-black/30 to-black/60 absolute top-0 left-0 rounded-lg transition-all duration-300 ease-in-out group-hover/card:opacity-100 xl:block"
      >
        <!-- 四角按钮布局 -->
        <div class="absolute inset-0 p-3">
          <!-- 左上角 - 编辑按钮 -->
          <Button
            class="absolute top-2 left-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 hover:scale-110 active:scale-95 animate-hover"
            :icon="'i-tabler:edit'"
            size="lg"
            @click.stop="onEdit"
          ></Button>

          <!-- 右上角 - 下载按钮 -->
          <Button
            :icon="'i-tabler:download'"
            class="absolute top-2 right-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-75 bg-zinc-100/70 hover:bg-zinc-200/70 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="onDownload"
            size="lg"
          ></Button>

          <!-- 左下角 - 删除按钮 -->
          <Button
            :icon="'i-tabler:trash'"
            class="absolute bottom-2 left-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-150 bg-red-500/70 hover:bg-red-600 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="onDelete"
            size="lg"
          ></Button>

          <!-- 右下角 - 全屏按钮 -->
          <Button
            :icon="'i-tabler:aspect-ratio'"
            class="absolute bottom-2 right-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-200 bg-zinc-100/70 hover:bg-zinc-200/70 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="onImgFullScreen"
            size="lg"
          ></Button>
        </div>
      </div>
    </div>

    <!-- 内容信息 -->
    <div class="p-3 cursor-pointer group/info" @click="onToPinsClick">
      <p
        class="text-sm font-bold text-zinc-900 dark:text-zinc-300 transition-colors duration-300 line-clamp-1 group-hover/info:text-blue-500"
      >
        {{ picture.name }}
      </p>
      <div class="flex items-center mt-2">
        <img
          class="h-5 w-5 rounded-full transition-transform duration-300 hover:scale-110 animate-hover"
          :src="picture.user?.userProfile"
          alt=""
        />
        <span
          class="text-sm text-zinc-500 ml-2 transition-colors duration-300 hover:text-zinc-700 dark:hover:text-zinc-300"
        >
          {{ picture.user?.userName }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useFullscreen } from "@vueuse/core";
import { type PictureType } from "@/config";
import Button from "@/lib/Button";
import { randomRGB } from "@/utils/color";
import { useRouter } from "vue-router";
const router = useRouter();
const { picture, width } = defineProps<{
  picture: PictureType;
  width?: number;
}>();
const emits = defineEmits(["edit", "delete", "download", "preview"]);
const imgTarget = ref<HTMLImageElement>();

// 新增方法：计算图片高度
const calculateImageHeight = (): number => {
  const picWidth = picture.picWidth || 1;
  const picHeight = picture.picHeight || 1;
  return (width! / picWidth) * picHeight;
};

/**
 * 进入详情点击事件
 */
const onToPinsClick = (event: Event) => {
  event.stopPropagation();
  emits("preview", picture);
};

/**
 * 下载按钮点击事件
 */
const onDownload = (event: Event) => {
  event.stopPropagation();
  emits("download", picture);
};

// 追踪全屏状态
const isFullscreen = ref(false);

// 计算当前应该显示的图片URL
const currentImageUrl = computed(() => {
  return isFullscreen.value ? picture.url : picture.thumbnailUrl ?? picture.url;
});

// 全屏控制
const { toggle: toggleFullscreen, isFullscreen: fullscreenState } =
  useFullscreen(imgTarget);

// 使用 watch 监听全屏状态变化
watch(fullscreenState, (newVal) => {
  isFullscreen.value = newVal;
});

/**
 * 生成全屏方法
 */
const onImgFullScreen = async (event: Event) => {
  event.stopPropagation();
  await toggleFullscreen();
};

/**
 * 编辑按钮点击事件
 */
const onEdit = (event: Event) => {
  event.stopPropagation();
  emits("edit", picture);
};

/**
 * 删除按钮点击事件
 */
const onDelete = async (event: Event) => {
  event.stopPropagation();
  emits("delete", picture);
};
</script>
