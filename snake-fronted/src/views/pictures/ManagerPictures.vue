<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
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
            <!-- TODO 添加 图片预览 -->
            <img
              class="inline-block size-14 rounded-md max-w-8 max-h-8"
              :src="item.url ? item.url : ''"
              alt=""
            />
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-8 overflow-hidden"
          >
            {{ item.name }}
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
      @previous="PreviousHandle"
      @next="NextHandle"
      @select-page="SelectPageHandle"
    />
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import Pagination from "@/components/Pagination";
import { PictureManagerColumns, type PictureType } from "@/config";
import { ref, watchEffect } from "vue";
import { useThrottleFn } from "@vueuse/core";
import { AdminGetPictureList } from "@/services";
import { Message } from "@/components/Message";
import dayjs from "dayjs";
const total = ref<number>(0); // 题目总数

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

const PreviousHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });

const NextHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });

const SelectPageHandle = (current: number) =>
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

    console.log(PictureListInfo.value);
  } else Message("error", `获取失败, 原因: ${message}`);
}, 1000);
watchEffect(() => LoadList());
</script>
