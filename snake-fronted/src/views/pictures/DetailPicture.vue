<template>
  <div class="flex items-center justify-center">
    <div
      class="w-full max-w-5xl h-[75vh] bg-white rounded-xl shadow-lg overflow-hidden transition-all duration-300 hover:shadow-2xl"
    >
      <div class="flex h-full">
        <!-- 左侧图片区域 -->
        <div class="w-2/3 h-full relative group">
          <div class="w-full h-full overflow-hidden">
            <img
              v-if="picture?.url"
              :src="picture.url"
              :alt="picture?.name"
              class="w-full h-full object-cover transition-all duration-500 group-hover:scale-105"
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
            class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition-all duration-300 flex items-center justify-center opacity-0 group-hover:opacity-100"
          >
            <Button
              class="px-4 py-2 rounded-lg shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all duration-300"
            >
              查看原图
            </Button>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="w-1/3 p-6 flex flex-col">
          <div class="flex-1 space-y-4">
            <div>
              <h1 class="text-2xl font-bold text-gray-800 tracking-tight mb-2">
                {{ picture?.name }}
              </h1>
              <div class="flex items-center text-sm text-gray-500 space-x-3">
                <span class="flex items-center">
                  <i class="ri-user-line mr-1"></i>
                  {{ picture?.user }}
                </span>
                <span class="flex items-center">
                  <i class="ri-time-line mr-1"></i>
                  {{ picture?.editTime }}
                </span>
              </div>
            </div>

            <p class="text-gray-600 leading-relaxed line-clamp-3">
              {{ picture?.introduction }}
            </p>

            <div class="space-y-3">
              <div class="flex items-center gap-2">
                <span class="text-sm font-medium text-gray-700">分类：</span>
                <Badges
                  :text="picture?.category ?? '未分类'"
                  :color="getRandomUnoColor()"
                  variant="soft"
                  size="sm"
                />
              </div>

              <div v-if="picture?.tags?.length" class="space-y-1">
                <span class="text-sm font-medium text-gray-700">标签：</span>
                <div class="flex flex-wrap gap-2">
                  <Badges
                    v-for="tag in picture?.tags"
                    :key="tag"
                    :text="tag"
                    :color="getRandomUnoColor()"
                    variant="soft"
                    size="sm"
                    clickable
                    @click="handleTagClick(tag)"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮区 -->
          <div class="flex gap-3 pt-4">
            <Button
              type="primary"
              size="md"
              icon="i-tabler:download"
              block
              :is-active-anim="true"
              @click="handleDownload"
            >
              下载
            </Button>
            <Button
              type="secondary"
              size="md"
              icon="i-tabler:share"
              block
              :is-active-anim="true"
              @click="handleShare"
            >
              分享
            </Button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { type PictureType } from "@/config";
import { GetPictureInfoById } from "@/services";
import { Message } from "@/lib/Message";
import Button from "@/lib/Button/Button.vue"; // 更新导入路径
import Badges from "@/lib/Badges/Badges.vue";
import { getRandomUnoColor } from "@/utils/color";

const { id } = defineProps<{
  id: string;
}>();

const picture = ref<PictureType>();

const LoadInfo = async () => {
  try {
    const { data, code, message } = await GetPictureInfoById(id);
    if (code === 0 && data) {
      picture.value = data;
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
  // 处理下载逻辑
  console.log("Downloading...");
};

const handleShare = () => {
  // 处理分享逻辑
  console.log("Sharing...");
};

onMounted(() => {
  if (id) LoadInfo();
});
</script>

<style scoped>
/* Remove scrollbar styles as they're no longer needed */
</style>
