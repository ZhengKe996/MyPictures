<template>
  <div class="flex items-center justify-center">
    <div
      class="w-full max-w-5xl h-[80vh] bg-white rounded-xl shadow-lg overflow-hidden transition-all duration-300 hover:shadow-2xl scroll-smooth"
    >
      <div class="flex h-full">
        <!-- 左侧图片区域 -->
        <div class="w-2/3 h-full relative group">
          <div class="w-full h-full overflow-hidden">
            <img
              v-if="picture?.url"
              :src="picture.url"
              :alt="picture?.name"
              class="w-full h-full object-cover group-hover:scale-105 transition-all duration-500 selection:bg-blue-100 selection:text-blue-900"
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
              type="secondary"
              class="px-4 py-2 rounded-lg shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all duration-300 bg-orange-500 hover:bg-orange-600 text-white border-none"
              @click="showFullscreenModal = true"
            >
              View Full Screen
            </Button>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="w-1/3 flex flex-col h-full overflow-hidden">
          <div class="flex-1 overflow-y-auto p-6 space-y-4">
            <!-- 标题和用户信息区域 -->
            <div class="pb-4 border-b border-gray-100">
              <h1
                class="text-2xl font-bold text-center text-gray-800 mb-6 gradient-primary bg-clip-text text-transparent"
              >
                {{ picture?.name || "Untitled Picture" }}
              </h1>
              <div
                class="flex my-1 items-center text-sm text-gray-600 space-x-4 whitespace-nowrap overflow-hidden"
              >
                <span class="flex items-center">
                  <i class="i-tabler-user size-4 opacity-75 mr-1.5"></i>
                  {{ picture?.user?.userName || "Unknown User" }}
                </span>
              </div>
              <div
                class="flex my-1 items-center text-sm text-gray-600 space-x-4 whitespace-nowrap overflow-hidden"
              >
                <span class="flex items-center">
                  <i class="i-tabler-clock size-4 opacity-75 mr-1.5"></i>
                  {{ picture?.editTime || "No Time Info" }}
                </span>
              </div>
            </div>

            <!-- 描述信息区域 -->
            <div class="py-4 border-b border-gray-100">
              <h2 class="text-sm font-medium text-gray-500 mb-3">
                Description
              </h2>
              <div class="relative">
                <p
                  v-if="picture?.introduction"
                  class="text-gray-600 leading-relaxed text-sm transition-all duration-300 selection:bg-blue-100 selection:text-blue-900"
                  :class="{ 'line-clamp-3 hover:line-clamp-none': !isExpanded }"
                >
                  {{ picture.introduction }}
                </p>
                <p v-else class="text-gray-400 italic text-sm">
                  No description available
                </p>
                <div
                  v-if="picture?.introduction && hasMoreContent"
                  class="absolute bottom-0 inset-x-0 h-8 bg-gradient-to-t from-white to-transparent transition-opacity duration-300"
                  :class="{ 'opacity-0': isExpanded }"
                >
                  <button
                    @click="isExpanded = !isExpanded"
                    class="absolute bottom-0 left-1/2 -translate-x-1/2 text-xs text-blue-600 hover:text-blue-700 bg-white px-2 py-0.5 rounded-full shadow-sm hover:shadow transition-all duration-200"
                  >
                    {{ isExpanded ? "Collapse" : "Expand" }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 审核信息区域 -->
            <div
              v-if="
                picture?.reviewMessage &&
                userStore.getUserRole === ACCESSENUM.ADMIN
              "
              class="py-4 border-b border-gray-100"
            >
              <h2 class="text-sm font-medium text-gray-500 mb-3">
                Review Info
              </h2>
              <div class="relative">
                <p
                  class="leading-relaxed text-sm transition-all duration-300 selection:bg-blue-100 selection:text-blue-900 p-3 rounded-lg border"
                  :class="reviewStatusStyle"
                >
                  <i :class="reviewStatusIcon" class="mr-1.5"></i>
                  {{ picture.reviewMessage }}
                </p>
              </div>
            </div>

            <!-- 分类和标签区域 -->
            <div class="py-4 border-b border-gray-100">
              <div class="flex gap-8">
                <!-- 主色调显示 -->
                <div class="space-y-2 flex-1">
                  <h2 class="text-sm font-medium text-gray-500">Main Color</h2>
                  <div class="flex items-center gap-2">
                    <div
                      v-if="picture?.picColor"
                      class="w-12 h-6 rounded-md shadow-sm border border-gray-200"
                      :style="{
                        backgroundColor: convertOxToHex(picture.picColor),
                      }"
                      :title="picture.picColor"
                    ></div>
                    <span class="text-xs text-gray-600">{{
                      picture?.picColor || "Not Available"
                    }}</span>
                  </div>
                </div>

                <!-- 分类显示 -->
                <div class="space-y-2 flex-1">
                  <h2 class="text-sm font-medium text-gray-500">Category</h2>
                  <div class="flex items-center gap-2">
                    <Badges
                      :text="picture?.category || 'Uncategorized'"
                      :color="getRandomUnoColor()"
                      variant="soft"
                      size="sm"
                    />
                  </div>
                </div>
              </div>

              <!-- 标签区域 -->
              <div class="space-y-2 py-4">
                <h2 class="text-sm font-medium text-gray-500">Tags</h2>
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
                    >No tags</span
                  >
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮区域 - 固定在底部 -->
          <div
            class="p-6 border-t border-gray-100 bg-white/80 backdrop-blur-sm"
          >
            <!-- 下载和分享按钮 -->
            <div class="grid grid-cols-2 gap-4 mb-4">
              <Button
                type="primary"
                size="md"
                icon="i-tabler-download"
                :is-active-anim="true"
                :disabled="!picture?.url"
                @click="handleDownload"
              >
                Download
              </Button>
              <Button
                type="secondary"
                size="md"
                icon="i-tabler-share"
                :is-active-anim="true"
                :disabled="!picture?.url"
                @click="handleShare"
              >
                Share
              </Button>
            </div>
            <!-- 管理员操作按钮 -->
            <div v-if="isAdmin" class="grid grid-cols-3 gap-3">
              <Button
                type="warning"
                size="md"
                icon="i-tabler-checklist"
                :is-active-anim="true"
                @click="openReviewDialog"
              >
                Review
              </Button>
              <Button
                type="primary"
                size="md"
                icon="i-tabler-cut"
                :is-active-anim="true"
                @click="openCropDialog"
              >
                Crop
              </Button>
              <Button
                type="success"
                size="md"
                icon="i-tabler-wand"
                :is-active-anim="true"
                :loading="isAiProcessing"
                @click="handleAiEnhance"
              >
                AI
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 审核对话框 -->
    <Dialog
      v-model="showReviewDialog"
      :title="'Review Picture - ' + (picture?.name || 'Unnamed Picture')"
      confirmText="Approve"
      cancelText="Reject"
      :confirmButtonColor="'green'"
      :cancelButtonColor="'red'"
      :confirmHandler="() => handleReview(1)"
      :cancelHandler="() => handleReview(2)"
    >
      <div class="space-y-4">
        <p class="text-gray-600">Please select review result:</p>
        <div class="space-y-2">
          <textarea
            v-model="reviewComment"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Enter review comments (optional)"
          ></textarea>
        </div>
      </div>
    </Dialog>

    <!-- 裁剪对话框 -->
    <Dialog
      v-model="showCropDialog"
      title="Crop Image"
      confirmText="Confirm"
      :showClose="true"
      :confirmButtonColor="'blue'"
    >
      <ImageCropper
        v-if="showCropDialog"
        :imageUrl="picture?.url"
        :fileName="picture?.name"
        @crop="handleCropComplete"
        @error="(error) => Message.error(error.message)"
      />
    </Dialog>

    <!-- AI扩图结果对话框 -->
    <Dialog
      v-model="showAiResultDialog"
      title="AI Enhancement Result"
      :showClose="true"
      :confirmButtonColor="'blue'"
      :showConfirm="false"
    >
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <h3 class="text-sm font-medium text-gray-700">Original Image</h3>
            <img
              :src="picture?.url"
              alt="Original"
              class="w-full h-auto rounded-lg border border-gray-200"
            />
          </div>
          <div class="space-y-2">
            <h3 class="text-sm font-medium text-gray-700">Enhanced Image</h3>
            <img
              :src="aiResultUrl"
              alt="Enhanced"
              class="w-full h-auto rounded-lg border border-gray-200"
            />
          </div>
        </div>
        <!-- 添加按钮区域 -->
        <div class="flex justify-end gap-4 mt-4">
          <Button
            type="secondary"
            size="md"
            icon="i-tabler-download"
            :is-active-anim="true"
            @click="handleAiResultDownload"
          >
            Download Result
          </Button>
          <Button
            type="primary"
            size="md"
            icon="i-tabler-check"
            :is-active-anim="true"
            :loading="isApplyingResult"
            @click="handleApplyAiResult"
          >
            Apply Result
          </Button>
        </div>
      </div>
    </Dialog>

    <!-- 全屏查看模态框 -->
    <ModalBox
      v-model="showFullscreenModal"
      :fullscreen="true"
      :closeOnClickOverlay="true"
      contentClass="flex items-center justify-center w-screen h-screen p-4"
    >
      <div class="w-screen h-screen p-4 flex items-center justify-center">
        <img
          v-if="picture?.url"
          :src="picture.url"
          :alt="picture?.name"
          class="max-w-[95vw] max-h-[95vh] w-auto h-auto object-contain select-none"
          @click.stop
          @contextmenu.prevent
        />
      </div>
    </ModalBox>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from "vue";
import { type PictureType, DefaultPictureTexts } from "@/config";
import {
  GetPictureInfoById,
  AdminCreateAIPictureTask,
  AdminGetAITaskResult,
  UploadImageFileByUrl,
} from "@/services";
import { Message } from "@/lib/Message";
import Button from "@/lib/Button/Button.vue"; // 更新导入路径
import Badges from "@/lib/Badges/Badges.vue";
import { getRandomUnoColor } from "@/utils/color";
import dayjs from "dayjs";
import Dialog from "@/lib/Dialog/Dialog.vue";
import { convertOxToHex } from "@/utils/colorConverter";
import ImageCropper from "@/components/ImageCropper/ImageCropper.vue";
import ModalBox from "@/lib/ModalBox/ModalBox.vue";

import { AdminReviewPicture } from "@/services";

import { useUserStore } from "@/store/user";

interface PictureDisplay extends PictureType {
  createTime: string;
  editTime: string;
}

const { id } = defineProps<{
  id: string;
}>();

const picture = ref<PictureDisplay>();

/**
 * 加载图片信息
 *
 * 该函数通过图片ID从后端获取图片详细信息，并将其存储在picture响应式变量中
 * 如果获取成功，将对数据进行格式化处理，包括时间格式化、默认值设置等
 * 如果获取失败，将显示错误消息
 */
const LoadInfo = async () => {
  try {
    const { data, code, message } = await GetPictureInfoById(id);
    if (code === 0 && data) {
      picture.value = {
        ...data,
        name: data.name || DefaultPictureTexts.UNNAMED_PICTURE,
        introduction: data.introduction || "",
        category: data.category || DefaultPictureTexts.UNCLASSIFIED,
        tags: data.tags || [],
        user: {
          ...data.user,
          userName: data.user?.userName || DefaultPictureTexts.UNKNOWN_USER,
        },
        createTime: data.createTime
          ? dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss")
          : DefaultPictureTexts.NO_CREATE_TIME,
        editTime: data.editTime
          ? dayjs(data.editTime).format("YYYY-MM-DD HH:mm:ss")
          : DefaultPictureTexts.NO_UPDATE_TIME,
        reviewStatus: data.reviewStatus ?? 0, // 添加这一行
        reviewMessage:
          data.reviewMessage ||
          (data.reviewStatus === 0
            ? DefaultPictureTexts.NO_REVIEW
            : data.reviewStatus === 1
            ? DefaultPictureTexts.REVIEW_PASS
            : data.reviewStatus === 2
            ? DefaultPictureTexts.REVIEW_REJECT
            : "Error"),
      };
    } else {
      Message.error(`获取图片信息失败: ${message}`);
    }
  } catch (error) {
    Message.error("获取图片信息失败");
  }
};

/**
 * 处理标签点击事件
 *
 * 当用户点击图片标签时，触发此函数，目前仅在控制台打印标签信息
 * 可以在此处添加更多标签点击后的逻辑
 *
 * @param tag 被点击的标签文本
 */
const handleTagClick = (tag: string) => {
  console.log("Tag clicked:", tag);
  // 处理标签点击事件
};

import { saveAs } from "file-saver";
import { ACCESSENUM } from "@/access";

/**
 * 处理下载图片的功能
 *
 * 此函数检查图片是否存在和用户信息是否完整，然后尝试下载图片
 * 如果图片或用户信息不完整，或者下载过程中出现错误，将显示相应的警告或错误消息
 */
const handleDownload = async () => {
  if (!picture.value || !picture.value.url) {
    Message.warning("No image available for download");
    return;
  }

  const { url, name, user, picFormat } = picture.value;

  if (!url || !name || !user || !user.userName || !picFormat) {
    Message.warning("Incomplete image information");
    return;
  }

  try {
    console.log("Downloading...");
    const fileName = `${name}-by:${user.userName}.${picFormat}`;
    await saveAs(url, fileName);
    Message.success("Download successful");
  } catch (error) {
    console.error("Download failed:", error);
    Message.error("Download failed, please try again later");
  }
};

/**
 * 处理分享图片的功能
 *
 * 当用户点击分享按钮时，触发此函数，目前仅在控制台打印分享开始信息
 * 可以在此处添加更多分享逻辑，如复制图片链接等
 */
const handleShare = () => {
  if (!picture.value?.url) {
    Message.warning("No image available to share");
    return;
  }
  console.log("Sharing...");
};

const isExpanded = ref(false);

/**
 * 计算属性，判断图片描述是否有多余内容
 *
 * 该属性用于判断图片描述是否超过三行，如果超过则显示“展开”按钮
 * 通过创建临时DOM元素计算文本高度来实现判断
 *
 * @returns 如果描述内容超过三行则返回true，否则返回false
 */
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

/**
 * 计算属性，根据审核状态返回对应的样式类名
 *
 * 该属性根据图片的审核状态返回不同的样式类名，用于在页面上显示不同的颜色和背景
 *
 * @returns 包含颜色和背景样式的字符串
 */
const reviewStatusStyle = computed(() => {
  switch (picture.value?.reviewStatus) {
    case 0:
      return "text-orange-600 bg-orange-50 border-orange-100";
    case 1:
      return "text-green-600 bg-green-50 border-green-100";
    case 2:
      return "text-red-600 bg-red-50 border-red-100";
    default:
      return "text-gray-600 bg-gray-50 border-gray-100";
  }
});

/**
 * 计算属性，根据审核状态返回对应的图标类名
 *
 * 该属性根据图片的审核状态返回不同的图标类名，用于在页面上显示不同的图标
 *
 * @returns 包含图标的字符串
 */
const reviewStatusIcon = computed(() => {
  switch (picture.value?.reviewStatus) {
    case 0:
      return "i-tabler-clock";
    case 1:
      return "i-tabler-check";
    case 2:
      return "i-tabler-x";
    default:
      return "i-tabler-question-mark";
  }
});

// 获取用户状态
const userStore = useUserStore();

/**
 * 计算属性，判断当前用户是否为管理员
 *
 * 该属性通过用户状态存储中的角色信息判断当前用户是否具有管理员权限
 *
 * @returns 如果当前用户是管理员则返回true，否则返回false
 */
const isAdmin = computed(() => userStore.getUserRole === ACCESSENUM.ADMIN);

// 审核相关的状态
const showReviewDialog = ref(false);
const reviewComment = ref("");

/**
 * 打开审核对话框
 *
 * 该函数用于显示审核对话框，并清空之前的审核意见
 */
const openReviewDialog = () => {
  showReviewDialog.value = true;
  reviewComment.value = "";
};

/**
 * 处理审核操作
 *
 * 该函数根据传入的审核状态和审核意见，调用后端接口进行图片审核操作
 * 如果审核成功，将更新本地图片数据并显示成功消息
 * 如果审核失败，将显示错误消息
 *
 * @param status 审核状态，0表示待审核，1表示审核通过，2表示审核未通过
 */
const handleReview = async (status: number) => {
  if (!picture.value?.id) return;
  try {
    const { code, message } = await AdminReviewPicture({
      id: id,
      reviewStatus: status,
      reviewMessage: reviewComment.value,
    });

    if (code === 0) {
      Message.success("审核操作成功");
      // 更新本地数据
      if (picture.value) {
        picture.value.reviewStatus = status;
        picture.value.reviewMessage =
          reviewComment.value || (status === 1 ? "审核通过" : "审核未通过");
      }
    } else {
      Message.error(`审核失败: ${message}`);
    }
  } catch (error) {
    Message.error("审核操作失败");
  } finally {
    showReviewDialog.value = false;
  }
};

// 裁剪相关的状态
const showCropDialog = ref(false);

/**
 * 打开裁剪对话框
 */
const openCropDialog = () => {
  if (!picture.value?.url) {
    Message.warning("无法获取图片");
    return;
  }
  showCropDialog.value = true;
};

import { UploadImageFile } from "@/services";
/**
 * 处理裁剪完成事件
 * @param file 裁剪后的文件
 */
const handleCropComplete = async (file: File) => {
  if (!picture.value?.id) return;

  try {
    const { code, message, data } = await UploadImageFile(file, {
      id: picture.value.id,
    });
    if (code === 0 && data) {
      Message.success("更新成功");
      // 重新加载图片信息
      await LoadInfo();
    } else {
      Message.error(`更新失败: ${message}`);
    }
  } catch (error) {
    Message.error("更新失败");
    console.error(error);
  } finally {
    showCropDialog.value = false;
  }
};

onMounted(() => {
  if (id) LoadInfo();
});

// 全屏模态框状态
const showFullscreenModal = ref(false);

// AI扩图相关状态
const isAiProcessing = ref(false);
const showAiResultDialog = ref(false);
const aiResultUrl = ref("");
let pollingTimer: number | null = null;

/**
 * 处理AI扩图
 */
const handleAiEnhance = async () => {
  if (!picture.value?.id) {
    Message.warning("Incomplete image information");
    return;
  }
  if (
    (picture.value?.picHeight ?? 0) > 4096 ||
    (picture.value?.picWidth ?? 0) > 4096 ||
    (picture.value?.picWidth ?? 0) < 512 ||
    (picture.value?.picHeight ?? 0) < 512 ||
    (picture.value?.picSize ?? 0) > 10 * 1024 * 1024
  ) {
    Message.warning("图片尺寸或大小超过限制");
    return;
  }

  try {
    isAiProcessing.value = true;
    // 创建AI扩图任务
    const { code, message, data } = await AdminCreateAIPictureTask(
      picture.value.id
    );
    if (code === 0 && data) {
      Message.success("创建AI扩图任务成功, 请等待处理完成");
      const taskId = data.output.taskId;
      // 开始轮询任务结果
      pollingTimer = window.setInterval(async () => {
        const { code: resultCode, data: taskResult } =
          await AdminGetAITaskResult(taskId);
        if (resultCode === 0 && taskResult) {
          if (
            taskResult.output?.taskStatus === "SUCCEEDED" &&
            taskResult.output?.outputImageUrl
          ) {
            // 任务完成，显示结果
            clearInterval(pollingTimer!);
            isAiProcessing.value = false;
            aiResultUrl.value = taskResult.output.outputImageUrl;
            showAiResultDialog.value = true;
          } else if (taskResult.output?.taskStatus === "FAILED") {
            // 任务失败
            clearInterval(pollingTimer!);
            isAiProcessing.value = false;
            Message.error(
              "AI扩图失败: " + taskResult.output.message || "未知错误"
            );
          }
        }
      }, 3000); // 每2秒轮询一次
    } else throw new Error(message);
  } catch (error) {
    isAiProcessing.value = false;
    Message.error(
      "Failed to create AI task: " +
        (error instanceof Error ? error.message : "Unknown error")
    );
  }
};

// 组件卸载时清理轮询定时器
onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer);
  }
});

// AI结果应用状态
const isApplyingResult = ref(false);

/**
 * 处理AI结果图片下载
 */
const handleAiResultDownload = async () => {
  if (!aiResultUrl.value || !picture.value?.name) {
    Message.warning("No AI result available for download");
    return;
  }

  try {
    const fileName = `${picture.value.name}-AI-Enhanced.webp`;
    await saveAs(aiResultUrl.value, fileName);
    Message.success("Download successful");
  } catch (error) {
    console.error("Download failed:", error);
    Message.error("Download failed, please try again later");
  }
};

/**
 * 处理应用AI结果
 */
const handleApplyAiResult = async () => {
  if (!aiResultUrl.value || !picture.value?.id) {
    Message.warning("No AI result available to apply");
    return;
  }

  try {
    isApplyingResult.value = true;
    const { message, code } = await UploadImageFileByUrl(aiResultUrl.value, {
      id: picture.value.id,
      picName: picture.value.name,
      spaceId: picture.value.spaceId,
    });
    if (code !== 0) throw new Error(message);
    // 成功后重新加载图片信息
    await LoadInfo();
    showAiResultDialog.value = false;
    Message.success("AI result applied successfully");
  } catch (error) {
    Message.error("Failed to apply AI result");
    console.error(error);
  } finally {
    isApplyingResult.value = false;
  }
};
</script>

<style scoped>
/* 添加自定义滚动条样式 */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(203, 213, 225, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(203, 213, 225, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background-color: rgba(203, 213, 225, 0.8);
}
</style>
