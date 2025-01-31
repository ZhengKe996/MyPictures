<template>
  <div class="flex items-center justify-center">
    <div
      class="w-full max-w-5xl h-[75vh] bg-white rounded-xl shadow-lg overflow-hidden transition-all duration-300 hover:shadow-2xl scroll-smooth"
    >
      <div class="flex h-full">
        <!-- 左侧图片区域 -->
        <div class="w-2/3 h-full relative group">
          <div class="w-full h-full overflow-hidden">
            <img
              v-if="picture?.url"
              :src="picture.url"
              :alt="picture?.name"
              ref="imgTarget"
              :class="[
                'w-full h-full transition-all duration-500 selection:bg-blue-100 selection:text-blue-900',
                isFullscreen
                  ? 'object-contain bg-black/80 p-4'
                  : 'object-cover group-hover:scale-105',
              ]"
            />
            <div
              v-else
              class="w-full h-full bg-gray-200 animate-pulse flex items-center justify-center"
            >
              <span class="text-gray-400">Loading...</span>
            </div>
          </div>
          <!-- 图片悬浮操作区 -->
          <div
            v-show="!isFullscreen"
            class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition-all duration-300 flex items-center justify-center opacity-0 group-hover:opacity-100"
          >
            <Button
              class="px-4 py-2 rounded-lg shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all duration-300"
              @click="onImgFullScreen"
            >
              查看原图
            </Button>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="w-1/3 p-6 flex flex-col divide-y divide-gray-100">
          <!-- 标题和用户信息区域 -->
          <div class="pb-5">
            <h1
              class="text-2xl font-bold text-gray-800 tracking-tight mb-3 transition-colors duration-300"
            >
              {{ picture?.name || "未命名图片" }}
            </h1>
            <div class="flex items-center text-sm text-gray-600 space-x-4">
              <span class="flex items-center">
                <i class="i-tabler:user size-4 opacity-75 mr-1.5"></i>
                {{ picture?.user?.userName || "未知用户" }}
              </span>
              <span class="flex items-center">
                <i class="i-tabler:clock size-4 opacity-75 mr-1.5"></i>
                {{ picture?.editTime || "暂无时间信息" }}
              </span>
            </div>
          </div>

          <!-- 描述信息区域 -->
          <div class="py-5">
            <h2 class="text-sm font-medium text-gray-500 mb-3">简介</h2>
            <div class="relative">
              <p
                v-if="picture?.introduction"
                class="text-gray-600 leading-relaxed text-sm transition-all duration-300 selection:bg-blue-100 selection:text-blue-900"
                :class="{ 'line-clamp-3 hover:line-clamp-none': !isExpanded }"
              >
                {{ picture.introduction }}
              </p>
              <p v-else class="text-gray-400 italic text-sm">暂无描述信息</p>
              <div
                v-if="picture?.introduction && hasMoreContent"
                class="absolute bottom-0 inset-x-0 h-8 bg-gradient-to-t from-white to-transparent transition-opacity duration-300"
                :class="{ 'opacity-0': isExpanded }"
              >
                <button
                  @click="isExpanded = !isExpanded"
                  class="absolute bottom-0 left-1/2 -translate-x-1/2 text-xs text-blue-600 hover:text-blue-700 bg-white px-2 py-0.5 rounded-full shadow-sm hover:shadow transition-all duration-200"
                >
                  {{ isExpanded ? "收起" : "展开" }}
                </button>
              </div>
            </div>
          </div>

          <!-- 分类和标签区域 -->
          <div class="py-5 space-y-4">
            <div class="space-y-2">
              <h2 class="text-sm font-medium text-gray-500">分类</h2>
              <div class="flex items-center gap-2">
                <Badges
                  :text="picture?.category || '未分类'"
                  :color="getRandomUnoColor()"
                  variant="soft"
                  size="sm"
                />
              </div>
            </div>

            <div class="space-y-2">
              <h2 class="text-sm font-medium text-gray-500">标签</h2>
              <div class="flex flex-wrap gap-2">
                <template v-if="picture?.tags?.length">
                  <Badges
                    v-for="tag in picture.tags"
                    :key="tag"
                    :text="tag"
                    :color="getRandomUnoColor()"
                    variant="soft"
                    size="sm"
                    clickable
                    class="transform hover:scale-102 transition-transform duration-200"
                    @click="handleTagClick(tag)"
                  />
                </template>
                <span v-else class="text-sm text-gray-400 italic"
                  >暂无标签</span
                >
              </div>
            </div>
          </div>

          <!-- 操作按钮区域 -->
          <div class="pt-5 mt-auto">
            <div class="flex gap-4">
              <Button
                type="primary"
                size="md"
                icon="i-tabler:download"
                class="flex-1"
                :is-active-anim="true"
                :disabled="!picture?.url"
                @click="handleDownload"
              >
                下载
              </Button>
              <Button
                type="secondary"
                size="md"
                icon="i-tabler:share"
                class="flex-1"
                :is-active-anim="true"
                :disabled="!picture?.url"
                @click="handleShare"
              >
                分享
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { type PictureType } from "@/config";
import { GetPictureInfoById } from "@/services";
import { Message } from "@/lib/Message";
import Button from "@/lib/Button/Button.vue"; // 更新导入路径
import Badges from "@/lib/Badges/Badges.vue";
import { getRandomUnoColor } from "@/utils/color";
import dayjs from "dayjs";
import { useFullscreen } from "@vueuse/core";

interface PictureDisplay extends PictureType {
  createTime: string;
  editTime: string;
}

const { id } = defineProps<{
  id: string;
}>();

const picture = ref<PictureDisplay>();

const LoadInfo = async () => {
  try {
    const { data, code, message } = await GetPictureInfoById(id);
    if (code === 0 && data) {
      picture.value = {
        ...data,
        name: data.name || "未命名图片",
        introduction: data.introduction || "",
        category: data.category || "未分类",
        tags: data.tags || [],
        user: {
          ...data.user,
          userName: data.user?.userName || "未知用户",
        },
        createTime: data.createTime
          ? dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss")
          : "暂无创建时间",
        editTime: data.editTime
          ? dayjs(data.editTime).format("YYYY-MM-DD HH:mm:ss")
          : "暂无更新时间",
      };
    } else {
      Message.error(`获取图片信息失败: ${message}`);
    }
  } catch (error) {
    Message.error("获取图片信息失败");
  }
};

const handleTagClick = (tag: string) => {
  console.log("Tag clicked:", tag);
  // 处理标签点击事件
};

const handleDownload = () => {
  if (!picture.value?.url) {
    Message.warning("暂无可下载的图片");
    return;
  }
  // 处理下载逻辑
  console.log("Downloading...");
};

const handleShare = () => {
  if (!picture.value?.url) {
    Message.warning("暂无可分享的图片");
    return;
  }
  // 处理分享逻辑
  console.log("Sharing...");
};

const isExpanded = ref(false);
const hasMoreContent = computed(() => {
  if (!picture.value?.introduction) return false;
  const tempEl = document.createElement("div");
  tempEl.style.cssText =
    "position: absolute; visibility: hidden; height: auto; width: 300px; font-size: 14px; line-height: 1.6;";
  tempEl.textContent = picture.value.introduction;
  document.body.appendChild(tempEl);
  const hasMore = tempEl.offsetHeight > 72; // 约3行文本的高度
  document.body.removeChild(tempEl);
  return hasMore;
});

onMounted(() => {
  if (id) LoadInfo();
});
const imgTarget = ref<HTMLImageElement>();
const { isFullscreen, enter: onImgFullScreen, exit } = useFullscreen(imgTarget);

// 监听 ESC 键退出全屏
onMounted(() => {
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && isFullscreen.value) {
      exit();
    }
  });
});
</script>
