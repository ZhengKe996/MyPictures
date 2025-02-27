<template>
  <div class="bg-white dark:bg-zinc-900 xl:dark:bg-zinc-800 rounded pb-1">
    <div
      class="relative w-full rounded cursor-zoom-in group"
      :style="{
        backgroundColor: randomRGB(),
      }"
      @click="onToPinsClick"
    >
      <!-- 图片 -->
      <img
        ref="imgTarget"
        class="w-full rounded bg-transparent"
        :src="picture.url"
        :style="{
          height: calculateImageHeight() + 'px',
        }"
      />
      <!-- 遮罩层 -->
      <div
        class="hidden opacity-0 w-full h-full bg-zinc-900/50 absolute top-0 left-0 rounded duration-300 group-hover:opacity-100 xl:block"
      >
        <!-- 分享 -->
        <Button class="absolute top-1.5 left-1.5" size="md">分享</Button>

        <!-- 点赞 -->
        <Button
          class="absolute top-1.5 right-1.5"
          :icon="'i-tabler-heart'"
          size="lg"
        ></Button>

        <!-- 下载 -->
        <Button
          class="absolute bottom-1.5 left-1.5 bg-zinc-100/70 hover:bg-zinc-200/70 transition-all duration-300"
          :icon="'i-tabler-download'"
          :loading="isDownloading"
          @click.stop="onDownload"
          size="lg"
        >
        </Button>

        <!-- 全屏 -->
        <Button
          :icon="'i-tabler-aspect-ratio'"
          class="absolute bottom-1.5 right-1.5 bg-zinc-100/70"
          @click.stop="showFullScreenModal = true"
          size="lg"
        ></Button>
      </div>
    </div>
    <!-- 标题 -->
    <p class="text-sm mt-1 font-bold text-zinc-900 dark:text-zinc-300 px-1">
      {{ picture.name }}
    </p>
    <!-- 作者 -->
    <div class="flex items-center mt-1 px-1">
      <img
        class="h-2 w-2 rounded-full"
        :src="picture.user?.userProfile"
        alt=""
      />
      <span class="text-sm text-zinc-500 ml-1">{{
        picture.user?.userName
      }}</span>
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
import { useRouter } from "vue-router";
import { saveAs } from "file-saver";
import { Message } from "@/lib/Message";
import ModalBox from "@/lib/ModalBox/ModalBox.vue";
const router = useRouter();
const { picture, width } = defineProps<{
  picture: PictureType;
  width?: number;
}>();
const emits = defineEmits(["click"]);

// 添加新的全屏状态管理
const showFullScreenModal = ref(false);

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
const onToPinsClick = () => router.push(`/detail/picture/${picture.id}`);

/**
 * 下载按钮点击事件
 */
const isDownloading = ref(false);

const onDownload = async (event: Event) => {
  event.stopPropagation();
  if (!picture?.url) {
    Message.warning("暂无可下载的图片");
    return;
  }

  const { url, name, user, picFormat } = picture;

  if (!url || !name || !user?.userName || !picFormat) {
    Message.warning("图片信息不完整，无法下载");
    return;
  }

  try {
    isDownloading.value = true;
    const fileName = `${name}-作者:${user.userName}.${picFormat}`;
    await saveAs(url, fileName);
    Message.success("下载成功");
  } catch (error) {
    console.error("下载失败:", error);
    Message.error("下载失败，请稍后再试");
  } finally {
    isDownloading.value = false;
  }
};
</script>
