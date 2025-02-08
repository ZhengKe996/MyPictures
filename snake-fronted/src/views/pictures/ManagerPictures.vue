<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <!-- 修改这部分布局 -->
    <div class="flex flex-col gap-4 mb-4">
      <!-- 搜索区域居中 -->
      <div class="flex w-full justify-center items-center">
        <div class="w-full max-w-xl">
          <SearchInput
            v-model="PageInfo.name"
            label="Name:"
            @search="handleSearch"
            @keypress="handleKeyPress"
            @clear="handleClear"
          />
        </div>
      </div>

      <!-- 添加按钮右对齐 -->
      <div class="flex justify-end px-2">
        <Button
          @click="handleAdd"
          :icon="'i-tabler:plus'"
          size="sm"
          class="whitespace-nowrap w-auto inline-flex items-center justify-center px-4 py-2 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
        >
          新增
        </Button>
      </div>
    </div>
    <TableList :columns="PictureManagerColumns">
      <template #tr>
        <template v-if="ListInfo.length">
          <tr
            v-for="item in processedListInfo"
            :key="item.id"
            v-memo="[
              item.id,
              item.name,
              item.thumbnailUrl,
              item.category,
              item.tags?.length,
            ]"
            class="even:bg-gray-50 border-b border-gray-100 hover:bg-gray-50/60 transition-colors duration-200 group"
          >
            <!-- ID列 -->
            <td
              class="whitespace-nowrap py-2 pl-6 pr-4 text-sm font-medium text-gray-900 border-r border-gray-100/50 text-center max-w-[80px] truncate"
            >
              <GenericTooltip :content="String(item.id) || '-'">
                <template #trigger>
                  <span
                    :class="{ 'text-gray-400 italic': !item.user?.userName }"
                  >
                    {{ item.id || "-" }}
                  </span>
                </template>
              </GenericTooltip>
            </td>

            <!-- 图片列 -->
            <td
              class="whitespace-nowrap p-4 text-sm text-gray-500 border-r border-gray-100/50 text-center"
            >
              <img
                v-if="item.url || item.thumbnailUrl"
                ref="imageRefs"
                class="inline-block size-14 rounded-md object-cover cursor-pointer hover:opacity-90 transition-opacity shadow-sm hover:shadow-md"
                :src="item.thumbnailUrl ?? item.url"
                :alt="item.name || DefaultPictureTexts.UNNAMED_PICTURE"
                @click="toggleFullscreen(item.id)"
                @error="handleImageError"
              />
              <div
                v-else
                class="inline-flex size-14 rounded-md bg-gray-100 items-center justify-center text-gray-400"
              >
                <i class="i-tabler:photo-off size-6"></i>
              </div>
            </td>

            <!-- 名称列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[80px]"
            >
              <GenericTooltip
                :content="item.name || DefaultPictureTexts.UNNAMED_PICTURE"
              >
                <template #trigger>
                  <div class="w-full truncate">
                    {{ item.name || DefaultPictureTexts.UNNAMED_PICTURE }}
                  </div>
                </template>
              </GenericTooltip>
            </td>

            <!-- 描述列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[80px]"
            >
              <GenericTooltip
                :content="
                  item.introduction || DefaultPictureTexts.NO_DESCRIPTION
                "
              >
                <template #trigger>
                  <div
                    class="w-full truncate"
                    :class="{ 'text-gray-400 italic': !item.introduction }"
                  >
                    {{
                      item.introduction || DefaultPictureTexts.NO_DESCRIPTION
                    }}
                  </div>
                </template>
              </GenericTooltip>
            </td>

            <!-- 分类列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[80px] truncate"
            >
              <span :class="{ 'text-gray-400 italic': !item.category }">
                {{ item.category || DefaultPictureTexts.UNCLASSIFIED }}
              </span>
            </td>

            <!-- 标签列 -->
            <td
              class="whitespace-nowrap truncate px-2 py-2 text-sm text-gray-500 max-w-[80px] overflow-hidden border-r border-gray-100/50 text-center"
            >
              <GenericTooltip
                :content="
                  item.tags?.length
                    ? item.tags.join(', ')
                    : DefaultPictureTexts.NO_TAGS
                "
              >
                <template #trigger>
                  <span :class="{ 'text-gray-400 italic': !item.tags?.length }">
                    {{
                      item.tags?.length
                        ? item.tags.join(", ")
                        : DefaultPictureTexts.NO_TAGS
                    }}
                  </span>
                </template>
              </GenericTooltip>
            </td>

            <!-- 用户列 -->
            <td
              class="whitespace-nowrap truncate px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[80px]"
            >
              <GenericTooltip
                :content="
                  item.user?.userName || DefaultPictureTexts.UNKNOWN_USER
                "
              >
                <template #trigger>
                  <span
                    :class="{ 'text-gray-400 italic': !item.user?.userName }"
                  >
                    {{
                      item.user?.userName || DefaultPictureTexts.UNKNOWN_USER
                    }}
                  </span>
                </template>
              </GenericTooltip>
            </td>

            <!-- 时间列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[80px] truncate"
            >
              <GenericTooltip
                :content="item.editTime || DefaultPictureTexts.NO_TIME"
              >
                <template #trigger>
                  <span
                    :class="{ 'text-gray-400 italic': !item.user?.userName }"
                  >
                    {{ item.editTime || DefaultPictureTexts.NO_TIME }}
                  </span>
                </template>
              </GenericTooltip>
            </td>

            <!--  审核信息列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm text-gray-500 border-r border-gray-100/50 text-center max-w-[100px] truncate"
            >
              <span :class="{ 'text-gray-400 italic': !item.editTime }">
                {{ item.reviewMessage || DefaultPictureTexts.NO_REVIEW }}
              </span>
            </td>

            <!-- 操作按钮列 -->
            <td
              class="whitespace-nowrap px-2 py-2 text-sm font-medium text-center max-w-[50px]"
            >
              <span
                class="text-blue-600 hover:text-blue-700 cursor-pointer transition-colors hover:underline group-hover:scale-105 inline-block"
                @click="item.id && DetailPicture(item.id)"
              >
                Detail
              </span>
            </td>
            <td
              class="whitespace-nowrap px-2 py-2 text-sm font-medium text-center max-w-[50px]"
            >
              <span
                class="text-emerald-600 hover:text-emerald-700 cursor-pointer transition-colors hover:underline group-hover:scale-105 inline-block"
                @click="item.id && EditPicture(item.id)"
              >
                Edit
              </span>
            </td>
            <td
              class="whitespace-nowrap px-2 py-2 text-sm font-medium text-center max-w-[50px]"
            >
              <span
                class="text-red-600 hover:text-red-700 cursor-pointer transition-colors hover:underline group-hover:scale-105 inline-block"
                @click="item.id && handleDelete(item.id)"
              >
                Delete
              </span>
            </td>
          </tr>
        </template>
        <tr v-else>
          <td :colspan="PictureManagerColumns.length" class="py-16">
            <div
              class="flex flex-col items-center justify-center space-y-4 animate-fade-in animate-duration-500 animate-ease-out"
            >
              <div
                class="rounded-full bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end p-4 animate-hover-scale animate-duration-300"
              >
                <i class="i-tabler:photo-off size-8 text-white"></i>
              </div>
              <div class="text-center">
                <h3 class="text-base font-semibold text-gray-900 mb-1">
                  暂无图片数据
                </h3>
                <p class="text-sm text-gray-500 mb-4">
                  点击下方按钮上传新的图片
                </p>
                <Button
                  type="primary"
                  size="sm"
                  :icon="'i-tabler:plus'"
                  class="animate-hover-scale animate-duration-300 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
                  @click="handleAdd"
                >
                  上传新图片
                </Button>
              </div>
            </div>
          </td>
        </tr>
      </template>
    </TableList>

    <Pagination
      :total="total"
      :current="PageInfo.current"
      :page-size="PageInfo.pageSize"
      @change="ChangeCurrentPageHandle"
      class="mt-6"
    />

    <!-- 删除确认对话框 -->
    <Dialog
      v-model="showDeleteDialog"
      title="删除确认"
      :confirmText="'删除'"
      :cancelText="'取消删除'"
      :confirmButtonColor="'red'"
      :cancelButtonColor="'gray'"
      :confirmHandler="confirmDelete"
      :cancelHandler="handleCancelDelete"
    >
      <div class="space-y-4">
        <p class="text-gray-600 dark:text-gray-300">
          确定要删除这张图片吗？此操作不可恢复。
        </p>
        <div
          v-if="currentItem"
          class="flex items-center space-x-4 bg-gray-50 dark:bg-zinc-700 p-3 rounded-lg"
        >
          <img
            :src="currentItem.thumbnailUrl ?? currentItem.url"
            class="w-16 h-16 object-cover rounded"
            alt="预览图"
          />
          <div>
            <p class="font-medium text-gray-900 dark:text-gray-100">
              {{ currentItem.name }}
            </p>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              创建于 {{ currentItem.createTime }}
            </p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import SearchInput from "@/lib/SearchInput";
import TableList from "@/components/TableList";
import Pagination from "@/lib/Pagination";
import { PictureManagerColumns, type PictureType } from "@/config";
import { ref, watchEffect, onMounted, computed, watch } from "vue";
import { useThrottleFn } from "@vueuse/core";
import { GetPictureList, DeletePictureById } from "@/services";
import { Message } from "@/lib/Message";
import { useFullscreen } from "@vueuse/core";
import dayjs from "dayjs";
import Button from "@/lib/Button";
import GenericTooltip from "@/lib/Tooltip";
import router from "@/router";
import { DefaultPictureTexts } from "@/config";
import Dialog from "@/lib/Dialog/Dialog.vue";

const total = ref<number>(0); // 题目总数

const imageRefs = ref<HTMLImageElement[]>([]);

/**
 * 切换指定图片的全屏模式。
 *
 * @param id 图片的唯一标识符，用于定位要切换全屏的特定图片。如果为 undefined，则函数不执行任何操作。
 */
const toggleFullscreen = (id: string | undefined) => {
  // 如果未提供 ID，则不执行任何操作
  if (id === undefined) return;

  // 根据 ID 查找实际的图片元素
  const item = ListInfo.value.find((picture) => picture.id === id);
  // 如果找不到图片元素，则不执行任何操作
  if (!item) return;

  // 查找特定图片的 DOM 元素
  const imageElement = imageRefs.value.find(
    (ref) => ref?.getAttribute("src") === (item.thumbnailUrl ?? item.url)
  );
  // 如果找不到图片的 DOM 元素，则不执行任何操作
  if (!imageElement) return;

  // 在进入全屏前将源设置为原始图片 URL
  if (item.url) {
    imageElement.src = item.url;
    // 为该特定图片创建一个新的全屏实例
    const { toggle } = useFullscreen(imageElement);
    toggle();
    // 在退出全屏后重置为缩略图
    imageElement.addEventListener("fullscreenchange", () => {
      if (!document.fullscreenElement && item.thumbnailUrl) {
        imageElement.src = item.thumbnailUrl;
      }
    });
  }
};

onMounted(() => LoadList());

interface PictureInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: string;
  name?: string;
  userId?: string;
  picFormat?: string;
  tags?: Array<string>;
  spaceId?: string;
}

// 分页请求数据
const PageInfo = ref<PictureInfoInterface>({
  current: 1,
  pageSize: 20,
});

const ListInfo = ref<PictureType[]>([]);

// 使用 computed 创建响应式查询参数
const queryParams = computed(() => ({
  current: PageInfo.value.current,
  pageSize: PageInfo.value.pageSize,
  name: PageInfo.value.name || undefined,
  category: PageInfo.value.category || undefined,
  userId: PageInfo.value.userId || undefined,
  tags: PageInfo.value.tags || undefined,
}));

// 使用 computed 创建缓存键
const cacheKey = computed(() => JSON.stringify(queryParams.value));

// 使用 computed 处理列表数据
const processedListInfo = computed(() =>
  ListInfo.value.map((item: PictureType) => ({
    ...item,
    displayName: item.name || DefaultPictureTexts.UNNAMED_PICTURE,
    displayTags: item.tags?.length
      ? item.tags.join(", ")
      : DefaultPictureTexts.NO_TAGS,
    displayCategory: item.category || DefaultPictureTexts.UNCLASSIFIED,
    displayUser: item.user?.userName || DefaultPictureTexts.UNKNOWN_USER,
  }))
);

// 使用 computed 判断是否需要重新加载
const shouldReloadList = computed(() => {
  const currentKey = cacheKey.value;
  return !requestCache.has(currentKey);
});

// 使用 watch 精确监听查询参数变化
watch(
  () => ({
    name: PageInfo.value.name,
    current: PageInfo.value.current,
    pageSize: PageInfo.value.pageSize,
    category: PageInfo.value.category,
    userId: PageInfo.value.userId,
    tags: PageInfo.value.tags,
  }),
  (newParams, oldParams) => {
    // 深度比较参数是否发生变化
    const hasParamChanged = Object.keys(newParams).some(
      (key) =>
        newParams[key as keyof typeof newParams] !==
        oldParams[key as keyof typeof oldParams]
    );

    if (hasParamChanged) {
      // 如果参数发生变化，触发加载
      LoadList();
    }
  },
  {
    deep: true, // 深度监听
    immediate: false, // 不立即触发
  }
);

// 优化搜索和清除逻辑
const handleSearch = () => {
  // 重置到第一页并触发加载
  PageInfo.value = {
    ...PageInfo.value,
    current: 1,
  };
  LoadList();
};

const handleClear = () => {
  // 重置所有查询参数
  PageInfo.value = {
    current: 1,
    pageSize: 20,
    name: "",
    category: undefined,
    userId: undefined,
    tags: undefined,
  };
  LoadList();
};

// 优化分页处理
const ChangeCurrentPageHandle = (current: number) => {
  PageInfo.value = {
    ...PageInfo.value,
    current,
  };
  LoadList();
};

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);

// 添加请求缓存
const requestCache = new Map();

// 重构 LoadList 方法
const LoadList = useThrottleFn(async () => {
  if (!shouldReloadList.value) {
    ListInfo.value = requestCache.get(cacheKey.value);
    return;
  }

  try {
    const { data, code, message } = await GetPictureList(queryParams.value);

    if (code === 0 && data) {
      const processedRecords = processRecordsData(data.records);

      // 更新缓存和列表
      requestCache.set(cacheKey.value, processedRecords);
      ListInfo.value = processedRecords;
      total.value = Number(data.total) ?? 0;
    } else {
      handleRequestError(message);
    }
  } catch (error) {
    handleNetworkError();
  }
}, 500);

// 抽取数据处理逻辑
const processRecordsData = (records: any[]) =>
  Array.isArray(records)
    ? records.map((item: PictureType) => ({
        ...item,
        name: item.name || DefaultPictureTexts.UNNAMED_PICTURE,
        introduction: item.introduction || "",
        category: item.category || DefaultPictureTexts.UNCLASSIFIED,
        tags: item.tags || [],
        user: {
          ...item.user,
          userName: item.user?.userName || DefaultPictureTexts.UNKNOWN_USER,
        },
        createTime: formatTime(item.createTime),
        editTime: formatTime(item.editTime),
        reviewMessage: getReviewMessage(item.reviewMessage, item.reviewStatus),
      }))
    : [];

// 时间格式化函数
const formatTime = (time: string | undefined) =>
  time
    ? dayjs(time).format("YYYY-MM-DD HH:mm:ss")
    : DefaultPictureTexts.NO_CREATE_TIME;

// 审核状态处理函数
const getReviewMessage = (
  reviewMessage: string | undefined,
  reviewStatus: number | undefined
) =>
  reviewMessage ||
  (reviewStatus === 1
    ? DefaultPictureTexts.REVIEW_PASS
    : reviewStatus === 2
    ? DefaultPictureTexts.REVIEW_REJECT
    : DefaultPictureTexts.NO_REVIEW);

// 错误处理函数
const handleRequestError = (message: string) => {
  const cachedData = requestCache.get(cacheKey.value);

  if (cachedData) {
    ListInfo.value = cachedData;
    Message.warning("使用缓存数据，请检查网络");
  } else {
    Message.error(`获取失败, 原因: ${message}`);
  }
};

const handleNetworkError = () => {
  const cachedData = requestCache.get(cacheKey.value);

  if (cachedData) {
    ListInfo.value = cachedData;
    Message.warning("网络错误，使用缓存数据");
  } else {
    Message.error("网络请求发生错误");
  }
};

const EditPicture = (id: number | string) =>
  router.push(`/update/picture/${id}`);
const DetailPicture = (id: number | string) =>
  router.push(`/detail/picture/${id}`);

// 添加删除处理函数
const handleDelete = (id: number | string) => {
  const item = ListInfo.value.find((item) => item.id === id);
  if (item) {
    currentItem.value = item;
    showDeleteDialog.value = true;
  }
};
const handleAdd = () => router.push(`/add/picture`);

// Add dialog control states
const showDeleteDialog = ref(false);
const currentItem = ref<PictureType | null>(null);

// Add confirm delete method
const confirmDelete = async () => {
  if (!currentItem.value?.id) return;

  try {
    const { code } = await DeletePictureById(currentItem.value.id.toString());
    if (code === 0) {
      Message.success("删除成功");
      await LoadList();
    } else {
      Message.error("删除失败");
    }
  } catch (error) {
    Message.error("删除操作发生错误");
  } finally {
    currentItem.value = null;
    showDeleteDialog.value = false;
  }
};

// Add cancel delete method
const handleCancelDelete = () => {
  showDeleteDialog.value = false;
  currentItem.value = null;
  Message.warning("已取消删除操作");
};

// 在 script setup 中添加图片错误处理函数
const defaultPlaceholderImage = "/path/to/placeholder.png";

const handleImageError = (event: Event) => {
  const imgElement = event.target as HTMLImageElement;
  imgElement.src = defaultPlaceholderImage;
  imgElement.classList.add("opacity-50");
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-hover-scale {
  transition: transform 0.3s;
}

.animate-hover-scale:hover {
  transform: scale(1.05);
}

@keyframes subtleGradient {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.active-button {
  background-size: 200% auto;
  animation: subtleGradient 3s ease infinite;
}
</style>
