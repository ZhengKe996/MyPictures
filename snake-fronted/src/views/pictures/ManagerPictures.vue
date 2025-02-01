<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-1 justify-start px-2">
      <div
        class="grid w-full max-w-xl lg:max-w-xs flex justify-start items-center"
      >
        <div class="flex w-full justify-start items-center">
          <label for="search" class="block text-sm/6 font-medium text-gray-900"
            >Name:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.name"
                class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                @keypress="handleKeyPress"
              />
              <Button
                @click="LoadList"
                :icon="'i-tabler:pointer-search'"
                size="md"
              ></Button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <TableList :columns="PictureManagerColumns">
      <template #tr>
        <tr
          v-for="item in PictureListInfo"
          :key="item.id"
          class="even:bg-gray-50 border-b border-gray-100 hover:bg-gray-50/60 transition-colors duration-200"
        >
          <!-- ID列 -->
          <td
            class="whitespace-nowrap py-2 pl-6 pr-4 text-sm font-medium text-gray-900 border-r border-gray-100/50 text-center max-w-[80px] truncate"
          >
            <GenericTooltip :content="String(item.id) || '-'">
              <template #trigger>
                <span :class="{ 'text-gray-400 italic': !item.user?.userName }">
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
              v-if="item.url"
              ref="imageRefs"
              class="inline-block size-14 rounded-md object-cover cursor-pointer hover:opacity-90 transition-opacity shadow-sm hover:shadow-md"
              :src="item.url"
              :alt="item.name || DefaultPictureTexts.UNNAMED_PICTURE"
              @click="toggleFullscreen(item.id)"
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
              :content="item.introduction || DefaultPictureTexts.NO_DESCRIPTION"
            >
              <template #trigger>
                <div
                  class="w-full truncate"
                  :class="{ 'text-gray-400 italic': !item.introduction }"
                >
                  {{ item.introduction || DefaultPictureTexts.NO_DESCRIPTION }}
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
              :content="item.user?.userName || DefaultPictureTexts.UNKNOWN_USER"
            >
              <template #trigger>
                <span :class="{ 'text-gray-400 italic': !item.user?.userName }">
                  {{ item.user?.userName || DefaultPictureTexts.UNKNOWN_USER }}
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
                <span :class="{ 'text-gray-400 italic': !item.user?.userName }">
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
              class="text-blue-600 hover:text-blue-700 cursor-pointer transition-colors hover:underline"
              @click="item.id && DetailPicture(item.id)"
            >
              Detail
            </span>
          </td>
          <td
            class="whitespace-nowrap px-2 py-2 text-sm font-medium text-center max-w-[50px]"
          >
            <span
              class="text-emerald-600 hover:text-emerald-700 cursor-pointer transition-colors hover:underline"
              @click="item.id && EditPicture(item.id)"
            >
              Edit
            </span>
          </td>
          <td
            class="whitespace-nowrap px-2 py-2 text-sm font-medium text-center max-w-[50px]"
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
    </TableList>

    <Pagination
      :total="total"
      :current="PageInfo.current"
      :page-size="PageInfo.pageSize"
      @change="ChangeCurrentPageHandle"
    />
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import Pagination from "@/lib/Pagination";
import { PictureManagerColumns, type PictureType } from "@/config";
import { ref, watchEffect, onMounted } from "vue";
import { useThrottleFn } from "@vueuse/core";
import { GetPictureList } from "@/services";
import { Message } from "@/lib/Message";
import { useFullscreen } from "@vueuse/core";
import dayjs from "dayjs";
import Button from "@/lib/Button";
import GenericTooltip from "@/lib/Tooltip";
import router from "@/router";
import { DefaultPictureTexts } from "@/config";

const total = ref<number>(0); // 题目总数

const imageRefs = ref<HTMLImageElement[]>([]);

const fullscreenOptions = ref<{
  enter: () => void;
  exit: () => void;
  toggle: () => void;
  isActive: boolean;
}>({ enter: () => {}, exit: () => {}, toggle: () => {}, isActive: false });

const toggleFullscreen = (id: number | undefined) => {
  if (id === undefined) return; // 如果 id 是 undefined，则直接返回
  const index = PictureListInfo.value.findIndex((item) => item.id === id);
  if (index !== -1 && imageRefs.value[index]) {
    const { enter, exit, toggle, isFullscreen } = useFullscreen(
      imageRefs.value[index]
    );
    fullscreenOptions.value = {
      enter,
      exit,
      toggle,
      isActive: isFullscreen.value,
    };
    toggle();
  }
};
onMounted(() => LoadList());

interface PictureInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  picFormat?: string;
  userId?: number;
}

// 分页请求数据
const PageInfo = ref<PictureInfoInterface>({
  current: 1,
  pageSize: 20,
});

const PictureListInfo = ref<PictureType[]>([]);

const ChangeCurrentPageHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);

const LoadList = useThrottleFn(async () => {
  const { data, code, message } = await GetPictureList(PageInfo.value);

  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    PictureListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: PictureType) => ({
          ...item,
          name: item.name || DefaultPictureTexts.UNNAMED_PICTURE,
          introduction: item.introduction || "",
          category: item.category || DefaultPictureTexts.UNCLASSIFIED,
          tags: item.tags || [],
          user: {
            ...item.user,
            userName: item.user?.userName || DefaultPictureTexts.UNKNOWN_USER,
          },
          createTime: item.createTime
            ? dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss")
            : DefaultPictureTexts.NO_CREATE_TIME,
          editTime: item.editTime
            ? dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss")
            : DefaultPictureTexts.NO_UPDATE_TIME,
          reviewMessage:
            item.reviewMessage ||
            (item.reviewStatus === 1
              ? DefaultPictureTexts.REVIEW_PASS
              : item.reviewStatus === 2
              ? DefaultPictureTexts.REVIEW_REJECT
              : DefaultPictureTexts.NO_REVIEW),
        }))
      : [];
  } else Message.error(`获取失败, 原因: ${message}`);
}, 1000);
watchEffect(() => LoadList());

const EditPicture = (id: number | string) => {
  router.push(`/update/picture/${id}`);
};
const DetailPicture = (id: number | string) =>
  router.push(`/detail/picture/${id}`);

// 添加删除处理函数
const handleDelete = (id: number | string) => {
  // TODO: 实现删除逻辑
  console.log("Delete item:", id);
};
</script>
