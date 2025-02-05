<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-1 justify-between items-center px-2">
      <div
        class="grid w-full max-w-lg lg:max-w-xs flex justify-start items-center"
      >
        <div class="flex justify-start items-center">
          <label
            for="search"
            class="block text-sm/6 font-medium text-gray-900 whitespace-nowrap"
            >Space Name:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.spaceName"
                class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                @keypress="handleKeyPress"
              />
              <div class="flex py-1.5 pr-1.5">
                <kbd
                  class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                  >↵</kbd
                >
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-start items-center">
          <label
            for="search"
            class="block text-sm/6 font-medium text-gray-900 whitespace-nowrap"
            >Space Level:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.spaceLevel"
                class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                @keypress="handleKeyPress"
              />
              <div class="flex py-1.5 pr-1.5">
                <kbd
                  class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                  >↵</kbd
                >
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-start items-center">
          <label
            for="search"
            class="block text-sm/6 font-medium text-gray-900 whitespace-nowrap"
            >User ID:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.userId"
                class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                @keypress="handleKeyPress"
              />
              <div class="flex py-1.5 pr-1.5">
                <kbd
                  class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                  >↵</kbd
                >
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-start items-center">
          <Button
            @click="LoadList"
            :icon="'i-tabler:pointer-search'"
            size="sm"
            class="whitespace-nowrap w-auto inline-flex items-center justify-center px-4 py-2"
          >
            搜索
          </Button>
        </div>
      </div>

      <div class="flex items-center">
        <Button
          @click="handleAdd"
          :icon="'i-tabler:plus'"
          size="sm"
          class="whitespace-nowrap w-auto inline-flex items-center justify-center px-4 py-2 bg-blue-600 hover:bg-blue-500"
        >
          新增
        </Button>
      </div>
    </div>
    <TableList :columns="SpaceManagerColumns">
      <template #tr>
        <template v-if="ListInfo.length">
          <tr v-for="item in ListInfo" :key="item.id" class="even:bg-gray-50">
            <td
              class="whitespace-nowrap py-4 pl-4 px-3 text-sm font-medium text-gray-900 sm:pl-3 text-center"
            >
              {{ item.id }}
            </td>
            <td
              class="whitespace-nowrap px-3 py-4 text-sm text-gray-500 text-center"
            >
              {{ item.spaceName }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-12 overflow-hidden text-center"
            >
              {{ item.spaceLevel }}
            </td>
            <td
              class="whitespace-nowrap px-3 py-4 text-sm text-gray-500 text-center"
            >
              <div class="flex flex-col items-center justify-center">
                <div>
                  大小：{{ formatSize(item.totalSize) }} /
                  {{ formatSize(item.maxSize) }}
                </div>
                <div>数量：{{ item.totalCount }} / {{ item.maxCount }}</div>
              </div>
            </td>
            <td
              class="whitespace-nowrap px-3 py-4 text-sm text-gray-500 text-center"
            >
              {{ item.user?.userName }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              {{ item.createTime }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              {{ item.editTime }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              <span
                class="text-emerald-600 hover:text-emerald-700 cursor-pointer transition-colors hover:underline"
                @click="item.id && EditSpace(item.id)"
              >
                Edit
              </span>
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              <span
                class="text-red-600 hover:text-red-700 cursor-pointer transition-colors hover:underline"
                @click="item.id && handleDelete(item.id)"
              >
                Delete
              </span>
            </td>
          </tr>
        </template>
        <tr v-else>
          <td :colspan="SpaceManagerColumns.length" class="py-16">
            <div
              class="flex flex-col items-center justify-center space-y-4 animate-fade-in animate-duration-500 animate-ease-out"
            >
              <div
                class="rounded-full bg-gray-50 p-4 animate-hover-scale animate-duration-300"
              >
                <i class="i-tabler:database-off size-8 text-gray-400"></i>
              </div>
              <div class="text-center">
                <h3 class="text-base font-semibold text-gray-900 mb-1">
                  暂无数据
                </h3>
                <p class="text-sm text-gray-500 mb-4">
                  点击下方按钮添加新的空间
                </p>
                <Button
                  type="primary"
                  size="sm"
                  :icon="'i-tabler:plus'"
                  class="animate-hover-scale animate-duration-300"
                  @click="handleAdd"
                >
                  创建新空间
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
          确定要删除此空间吗？删除后该空间内的所有图片都将被删除，此操作不可恢复。
        </p>
        <div
          v-if="currentItem"
          class="bg-gray-50 dark:bg-zinc-700 p-4 rounded-lg space-y-2"
        >
          <div class="flex justify-between items-center">
            <p class="font-medium text-gray-900 dark:text-gray-100">
              {{ currentItem.spaceName }}
            </p>
            <span class="text-sm text-gray-500">ID: {{ currentItem.id }}</span>
          </div>
          <div class="text-sm text-gray-500 dark:text-gray-400">
            <p>空间等级：{{ currentItem.spaceLevel }}</p>
            <p>创建时间：{{ currentItem.createTime }}</p>
            <p>所属用户：{{ currentItem.user?.userName }}</p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import Pagination from "@/lib/Pagination";
import Button from "@/lib/Button";
import { useThrottleFn } from "@vueuse/core";
import {
  DefaultPictureTexts,
  SpaceManagerColumns,
  type SpaceType,
} from "@/config";
import { formatSize } from "@/utils";
import { onMounted, ref } from "vue";
import { GetSpaceList, DeleteSpaceById } from "@/services";
import { Message } from "@/lib/Message";
import router from "@/router";
import dayjs from "dayjs";
import Dialog from "@/lib/Dialog/Dialog.vue";

const total = ref<number>(0);
interface SpaceInfoInterface {
  current: number;
  pageSize: number;
  userId?: number;
  spaceLevel?: number;
  spaceName?: string;
}

const PageInfo = ref<SpaceInfoInterface>({
  current: 1,
  pageSize: 20,
});
const ChangeCurrentPageHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });

const ListInfo = ref<SpaceType[]>([]);

const LoadList = async () => {
  const { data, code, message } = await GetSpaceList(PageInfo.value);
  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    ListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: SpaceType) => ({
          ...item,
          id: item.id ? String(item.id) : "",
          createTime: item.createTime
            ? dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss")
            : DefaultPictureTexts.NO_CREATE_TIME,
          editTime: item.editTime
            ? dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss")
            : DefaultPictureTexts.NO_UPDATE_TIME,
        }))
      : [];
  } else Message.error(`获取题目失败, 原因: ${message}`);
};

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);
onMounted(() => LoadList());

const handleAdd = () => router.push(`/add/space`);

// 添加删除处理函数
const showDeleteDialog = ref(false);
const currentItem = ref<SpaceType | null>(null);

const handleDelete = (id: string) => {
  const item = ListInfo.value.find((item) => item.id === id);
  if (item) {
    currentItem.value = item;
    showDeleteDialog.value = true;
  }
};

// Add confirm delete method
const confirmDelete = async () => {
  if (!currentItem.value?.id) return;

  try {
    const { code, message } = await DeleteSpaceById(currentItem.value.id);
    if (code === 0) {
      Message.success("删除成功");
      await LoadList();
    } else {
      Message.error(`删除失败: ${message}`);
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

const EditSpace = (id: number | string) => router.push(`/update/space/${id}`);
</script>

<style scoped></style>
