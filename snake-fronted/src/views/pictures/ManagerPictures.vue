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
          class="even:bg-gray-50"
        >
          <td
            class="whitespace-nowrap py-4 pl-4 px-3 text-sm font-medium text-gray-900 sm:pl-3"
          >
            {{ item.id }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            <img
              ref="imageRefs"
              class="inline-block size-14 rounded-md max-w-8 max-h-8 cursor-pointer"
              :src="item.url ? item.url : ''"
              alt=""
              @click="toggleFullscreen(item.id)"
            />
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-8 overflow-hidden"
          >
            <GenericTooltip :content="item.name">
              <template #trigger> {{ item.name }} </template>
            </GenericTooltip>
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-8 overflow-hidden"
          >
            {{ item.introduction }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.category }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.category }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.user?.userName }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.editTime }}
          </td>
          <!-- TODO: Router Link -->
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            Detail
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            Edit
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            Delete
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
import { AdminGetPictureList } from "@/services";
import { Message } from "@/lib/Message";
import { useFullscreen } from "@vueuse/core";
import dayjs from "dayjs";
import Button from "@/lib/Button";
import GenericTooltip from "@/lib/Tooltip";

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
  const { data, code, message } = await AdminGetPictureList(PageInfo.value);

  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    PictureListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: PictureType) => ({
          ...item,
          createTime:
            dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
          editTime: dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
        }))
      : [];
  } else Message.error(`获取失败, 原因: ${message}`);
}, 1000);
watchEffect(() => LoadList());
</script>
