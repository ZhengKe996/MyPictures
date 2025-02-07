<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 transform -translate-y-4"
      enter-to-class="opacity-100 transform translate-y-0"
      leave-active-class="transition-all duration-300 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isExit"
        class="space-content animate-fade-in animate-duration-500"
      >
        <div class="max-w-6xl mx-auto">
          <!-- 头部信息 -->
          <div
            class="flex items-center justify-between mb-8 animate-fade-in-up animate-duration-700"
          >
            <div class="flex items-center gap-3">
              <h1 class="text-2xl font-bold text-gray-800">
                {{ username }}
                <span class="text-sm font-normal text-gray-500 ml-2">
                  (私有空间)
                </span>
              </h1>
            </div>
            <div class="flex items-center gap-4">
              <!-- 添加日期选择器 -->
              <Popover trigger="click" placement="bottom-end">
                <template #reference>
                  <Button
                    type="primary"
                    icon="i-tabler:calendar"
                    :isActiveAnim="true"
                  >
                    {{ dateLabel }}
                  </Button>
                </template>
                <div class="w-[320px]">
                  <Calendars
                    mode="range"
                    :start-date="startDate"
                    :end-date="endDate"
                    @range-select="handleDateRangeSelect"
                  />
                </div>
              </Popover>
              <Button
                type="primary"
                icon="i-tabler:photo-plus"
                @click="handleCreatePhoto"
                :isActiveAnim="true"
              >
                创建图片
              </Button>
            </div>
          </div>

          <!-- 内容区域 -->
          <!-- <div
            class="bg-white rounded-xl shadow-sm p-6 animate-fade-in-up animate-delay-200"
          ></div> -->
          <infinite
            v-model="loading"
            :isFinished="isFinished"
            @on-load="getLoadData"
            :threshold="100"
            :immediate-check="true"
            loading-text="玩命加载中..."
            finished-text="我是有底线的"
          >
            <waterfall
              class="px-1 w-full animate-fade-in animate-duration-300 animate-ease-out"
              :data="PictureListInfo"
              nodeKey="id"
              :column="5"
              :picturePreReading="false"
            >
              <template v-slot="{ item, width }">
                <div
                  class="overflow-hidden rounded-lg bg-white shadow-sm animate-fade-in-up animate-duration-500"
                >
                  <SpaceItem
                    :picture="(item as PictureType)"
                    :width="width"
                    @edit="handleEdit"
                    @delete="handleDelete"
                    @download="handleDownload"
                    @preview="handlePreview"
                  ></SpaceItem>
                </div>
              </template>
            </waterfall>

            <!-- 添加空状态展示 -->
            <template v-if="!loading && PictureListInfo.length === 0">
              <div
                class="flex flex-col items-center justify-center py-16 space-y-4 animate-fade-in"
              >
                <div
                  class="rounded-full bg-gray-50 p-4 animate-hover-scale animate-duration-300"
                >
                  <i class="i-tabler:photo-off size-8 text-gray-400"></i>
                </div>
                <div class="text-center">
                  <h3 class="text-base font-semibold text-gray-900 mb-1">
                    暂无图片数据
                  </h3>
                  <p class="text-sm text-gray-500 mb-4">请尝试更换搜索条件</p>
                </div>
              </div>
            </template>
          </infinite>
        </div>
        <Pagination
          :total="total"
          :current="PageInfo.current"
          :page-size="PageInfo.pageSize"
          @change="ChangeCurrentPageHandle"
        />
      </div>
      <div
        v-else
        class="flex flex-col items-center justify-center min-h-[400px]"
      >
        <div class="text-center space-y-8">
          <div
            class="animate-bounce-alt animate-duration-3000 animate-infinite animate-ease-in-out"
          >
            <div
              class="i-tabler:database-off text-8xl text-gray-400/80 mb-4 transform transition duration-300 hover:text-blue-500/80"
            ></div>
          </div>
          <div class="space-y-3">
            <h3 class="text-xl font-medium text-gray-700">
              你还没有创建自己的空间
            </h3>
            <p class="text-gray-500 text-sm">
              创建一个空间来存储和管理你的照片吧！
            </p>
            <button
              @click="handleCreateSpace"
              class="mt-4 px-6 py-2.5 bg-blue-500 text-white rounded-lg transform transition-all duration-200 hover:bg-blue-600 hover:scale-105 hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 active:scale-95"
            >
              创建空间
            </button>
          </div>
        </div>
      </div>
    </Transition>
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
          v-if="currentPicture"
          class="flex items-center space-x-4 bg-gray-50 dark:bg-zinc-700 p-3 rounded-lg"
        >
          <img
            :src="currentPicture.thumbnailUrl"
            class="w-16 h-16 object-cover rounded"
            alt="预览图"
          />
          <div>
            <p class="font-medium text-gray-900 dark:text-gray-100">
              {{ currentPicture.name }}
            </p>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              创建于 {{ currentPicture.createTime }}
            </p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { saveAs } from "file-saver";
import {
  GetPictureList,
  GetSpaceByUserId,
  DeletePictureById,
} from "@/services";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { onMounted, ref, computed } from "vue";
import Button from "@/lib/Button/Button.vue";
import { Message } from "@/lib/Message";
import type { PictureType } from "@/config";
import { useThrottleFn } from "@vueuse/core";
import dayjs from "dayjs";
import Pagination from "@/lib/Pagination";
import Waterfall from "@/lib/Waterfall";
import Infinite from "@/lib/Infinite";
import { SpaceItem } from "@/components/ListItem";
import Dialog from "@/lib/Dialog/Dialog.vue";
import Calendars from "@/lib/Calendars";
import Popover from "@/lib/Popover";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const isExit = ref(false);
const spaceId = ref<string>("");
const total = ref<number>(0);

const handleEdit = async (picture: PictureType) =>
  router.push(`/update/picture/${picture.id}?spaceId=${spaceId.value}`);

// 删除对话框控制
const showDeleteDialog = ref(false);
const currentPicture = ref<PictureType | null>(null);

// 更新handleDelete方法
const handleDelete = async (picture: PictureType) => {
  currentPicture.value = picture;
  showDeleteDialog.value = true;
};

// 确认删除方法
const confirmDelete = async () => {
  if (!currentPicture.value?.id) return;

  try {
    const { code } = await DeletePictureById(
      currentPicture.value.id.toString()
    );
    if (code === 0) {
      Message.success("删除成功");
      await LoadList();
    } else {
      Message.error("删除失败");
    }
  } catch (error) {
    Message.error("删除操作发生错误");
  } finally {
    currentPicture.value = null;
  }
};

// 取消删除方法
const handleCancelDelete = () => {
  showDeleteDialog.value = false;
  currentPicture.value = null;
  Message.warning("已取消删除操作");
};

const handleDownload = async (picture: PictureType) => {
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
    const fileName = `${name}-作者:${user.userName}.${picFormat}`;
    await saveAs(url, fileName);
    Message.success("下载成功");
  } catch (error) {
    console.error("下载失败:", error);
    Message.error("下载失败，请稍后再试");
  }
};

const handlePreview = (picture: PictureType) =>
  router.push(`/detail/picture/${picture.id}`);
// Loading
const loading = ref<boolean>(false);
const isFinished = ref<boolean>(false);

const getLoadData = async () => {
  if (isFinished.value) return;
  // 完成了第一次请求后，后续的请求让page自增
  if (PictureListInfo.value.length > 0) PageInfo.value.current++;
  await LoadList();
};

// 获取用户名
const username = computed(() => userStore.getUserName || "未知用户");

const CheckSpace = async () => {
  const userId = userStore.getUserID;
  const { data, code } = await GetSpaceByUserId(userId);
  if (code !== 0 && data === null) {
    isExit.value = false;
  } else {
    isExit.value = true;
    if (data && data.id) {
      spaceId.value = data.id;
      // 更新 PageInfo 中的 spaceId
      PageInfo.value.spaceId = data.id;
      // 在确认空间存在后加载列表
      await LoadList();
    }
  }
};
onMounted(() => CheckSpace());

const handleCreateSpace = () => router.push("/add/space");

const handleCreatePhoto = () =>
  router.push(`/add/picture?spaceId=${spaceId.value}`);

// 添加日期选择相关的响应式变量
const startDate = ref("");
const endDate = ref("");

// 日期展示标签
const dateLabel = computed(() => {
  if (!startDate.value && !endDate.value) {
    return "选择日期";
  }
  if (startDate.value && endDate.value) {
    return `${dayjs(startDate.value).format("MM/DD")} - ${dayjs(
      endDate.value
    ).format("MM/DD")}`;
  }
  return dayjs(startDate.value).format("YYYY/MM/DD");
});

// 处理日期范围选择
const handleDateRangeSelect = (start: string, end: string) => {
  startDate.value = start;
  endDate.value = end;
  console.log("日期范围选择成功", startDate.value, endDate.value);

  // 更新 PageInfo
  PageInfo.value = {
    ...PageInfo.value,
    current: 1, // 重置页码
    startEditTime: start ? dayjs(start).startOf("day").format() : undefined,
    endEditTime: end ? dayjs(end).endOf("day").format() : undefined,
  };

  // 重新加载列表
  LoadList();
};

interface SpaceInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  userId?: string;
  picFormat?: string;
  tags?: Array<string>;
  spaceId?: string;
  startEditTime?: string;
  endEditTime?: string;
}
const PageInfo = ref<SpaceInfoInterface>({
  current: 1,
  pageSize: 20,
  spaceId: spaceId.value,
  userId: userStore.getUserID,
});
const ChangeCurrentPageHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });
const PictureListInfo = ref<PictureType[]>([]);

const LoadList = useThrottleFn(async () => {
  const { data, message, code } = await GetPictureList(PageInfo.value);
  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    const formattedRecords = Array.isArray(data.records)
      ? formatRecords(data.records)
      : [];

    if (Number(PageInfo.value.current) === 1) {
      PictureListInfo.value = formattedRecords;
    } else {
      PictureListInfo.value.push(...formattedRecords);
    }

    // 判断数据是否全部加载完成
    if (PictureListInfo.value.length >= total.value) {
      isFinished.value = true;
    }
  } else Message.error(`获取失败, 原因: ${message}`);
}, 3000);

const formatRecords = (records: PictureType[]) => {
  return records.map((item: PictureType) => ({
    ...item,
    createTime: dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
    editTime: dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
  }));
};
</script>
