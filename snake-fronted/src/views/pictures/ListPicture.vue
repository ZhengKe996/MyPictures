<template>
  <div class="w-full h-full">
    <div class="flex w-full mb-4 flex-1 justify-center items-center">
      <div class="mx-auto w-full max-w-xl flex justify-start items-center">
        <div class="flex w-full justify-start items-center">
          <label for="search" class="block text-sm/6 font-medium text-gray-900"
            >Name:
          </label>
          <div class="mx-2">
            <div
              class="flex min-w-xl rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
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
                size="small"
              ></Button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <infinite
      v-model="loading"
      :isFinished="isFinished"
      @on-load="getListData"
      loading-text="玩命加载中..."
      finished-text="我是有底线的"
    >
      <waterfall
        class="px-1 w-full"
        :data="PictureListInfo"
        nodeKey="id"
        :column="4"
        :picturePreReading="false"
      >
        <template v-slot="{ item, width }">
          <div class="overflow-hidden rounded-lg bg-white shadow-sm">
            <Item :picture="item" :width="width"></Item>
          </div>
        </template>
      </waterfall>
    </infinite>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { type PictureType } from "@/config";
import { useThrottleFn } from "@vueuse/core";
import { AdminGetPictureList, GetTagCategory } from "@/services";
import dayjs from "dayjs";
import { Message } from "@/lib/Message";
import Waterfall from "@/components/Waterfall";
import Infinite from "@/lib/Infinite";
import Button from "@/components/Button";
import { Item } from "@/components/ListItem";

const loading = ref<boolean>(false);
const isFinished = ref<boolean>(false);
let query = {
  page: 1,
  size: 20,
};

interface PictureInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  picFormat?: string;
  userId?: number;
}
const PageInfo = ref<PictureInfoInterface>({
  current: 1,
  pageSize: 20,
});

const PictureListInfo = ref<PictureType[]>([]);
// TODO 这一块需要重构
const resetQuery = (newQuery: any) => {
  query = { ...query, ...newQuery };
  // 重置状态
  isFinished.value = false;
};
const getListData = async () => {
  if (isFinished.value) return;
  if (PictureListInfo.value.length) query.page += 1;
  loading.value = false;
};
const LoadList = useThrottleFn(async () => {
  loading.value = true;

  const { data, code, message } = await AdminGetPictureList(PageInfo.value);

  if (code === 0 && data) {
    // total.value = Number(data.total) ?? 0;

    PictureListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: PictureType) => ({
          ...item,
          createTime:
            dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
          editTime: dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
        }))
      : [];
  } else Message.error(`获取失败, 原因: ${message}`);
  loading.value = false;
}, 1000);
watchEffect(() => LoadList());

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);

const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);
const getTagCategoryOptions = useThrottleFn(async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    tagOptions.value = (data.tagList ?? []).map((tag) => ({
      value: tag,
      label: tag,
    }));
    categoryOptions.value = (data.categoryList ?? []).map((category) => ({
      value: category,
      label: category,
    }));
  } else {
    Message.error(`获取标签和分类选项失败${message}`);
  }
}, 1000);
watchEffect(() => getTagCategoryOptions());
</script>
