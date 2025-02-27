<template>
  <div
    class="bg-white dark:bg-zinc-900 xl:dark:bg-zinc-800 rounded-lg pb-1 transition-all duration-300 hover:shadow-xl group/card preserve-3d"
  >
    <div
      class="relative w-full rounded-lg group overflow-hidden"
      :style="{
        backgroundColor: randomRGB(),
      }"
      @click="onToPinsClick"
    >
      <!-- 图片 -->
      <img
        ref="imgTarget"
        class="w-full rounded-lg bg-transparent transition-all duration-500 ease-out will-change-transform group-hover/card:scale-[1.03] group-hover/card:brightness-90"
        :src="picture.url"
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
            :icon="'i-tabler-edit'"
            size="lg"
            @click.stop="onEdit"
          ></Button>

          <!-- 右上角 - 下载按钮 -->
          <Button
            :icon="'i-tabler-download'"
            class="absolute top-2 right-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-75 bg-zinc-100/70 hover:bg-zinc-200/70 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="onDownload"
            size="lg"
          ></Button>

          <!-- 左下角 - 删除按钮 -->
          <Button
            :icon="'i-tabler-trash'"
            class="absolute bottom-2 left-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-150 bg-red-500/70 hover:bg-red-600 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="onDelete"
            size="lg"
          ></Button>

          <!-- 右下角 - 全屏按钮 -->
          <Button
            :icon="'i-tabler-aspect-ratio'"
            class="absolute bottom-2 right-2 transform translate-y-2 opacity-0 group-hover/card:translate-y-0 group-hover/card:opacity-100 transition-all duration-300 delay-200 bg-zinc-100/70 hover:bg-zinc-200/70 hover:scale-110 active:scale-95 animate-hover"
            @click.stop="showFullScreenModal = true"
            size="lg"
          ></Button>
        </div>
      </div>
    </div>

    <!-- 内容信息 -->
    <div class="p-3 cursor-pointer group/info">
      <p
        class="text-sm font-bold text-zinc-900 dark:text-zinc-300 transition-colors duration-300 line-clamp-1 group-hover/info:text-blue-500"
      >
        {{ picture.name }}
      </p>
      <div class="flex items-center justify-between mt-2">
        <div class="flex items-center">
          <img
            class="h-5 w-5 rounded-full transition-transform duration-300 hover:scale-110 animate-hover"
            :src="picture.user?.userAvatar"
            alt=""
          />
          <span
            class="text-sm text-zinc-500 ml-2 transition-colors duration-300 hover:text-zinc-700 dark:hover:text-zinc-300"
          >
            {{ picture.user?.userName }}
          </span>
        </div>
        <!-- 主色调显示 -->
        <div
          v-if="picture.picColor"
          class="flex items-center gap-1.5"
          :title="`主色调: ${picture.picColor}`"
        >
          <div
            class="w-4 h-4 rounded-full border border-gray-200"
            :style="{ backgroundColor: convertOxToHex(picture.picColor) }"
          ></div>
        </div>
      </div>
    </div>
  </div>

  <!-- 添加全屏查看模态框 -->
  <ModalBox
    v-model="showFullScreenModal"
    :fullscreen="true"
    :closeOnClickOverlay="true"
    contentClass="flex items-center justify-center w-screen h-screen p-4"
  >
    <div class="w-screen h-screen p-4 flex items-center justify-center">
      <img
        v-if="picture?.url"
        :src="picture.url"
        :alt="picture.name"
        class="max-w-[95vw] max-h-[95vh] w-auto h-auto object-contain select-none"
        @click.stop
        @contextmenu.prevent
      />
    </div>
  </ModalBox>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { type PictureType } from "@/config";
import Button from "@/lib/Button";
import { randomRGB } from "@/utils/color";
import { convertOxToHex } from "@/utils/colorConverter";
import ModalBox from "@/lib/ModalBox/ModalBox.vue";

const { picture, width } = defineProps<{
  picture: PictureType;
  width?: number;
}>();
const emits = defineEmits(["edit", "delete", "download", "preview"]);

// 添加新的全屏状态管理
const showFullScreenModal = ref(false);

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
