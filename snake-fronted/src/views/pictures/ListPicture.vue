<template>
  <div class="w-full h-full animated animated-duration-500 animated-fade-in">
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
                size="md"
              ></Button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      class="w-full mb-2 border-b border-gray-200 transition-all duration-300 ease-in-out"
    >
      <Tabs
        v-model="activeTab"
        :tabs="categoryOptions"
        @change="handleTabChange"
        class="transition-all duration-300 ease-in-out"
      >
      </Tabs>
    </div>
    <div
      class="w-full mb-4 border-b border-gray-200 transition-all duration-300 ease-in-out"
    >
      <TagsList
        :tags="tagOptions"
        v-model="selectedTags"
        @change="handleChange"
        :multiple="true"
      ></TagsList>
    </div>
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
        class="px-1 w-full"
        :data="PictureListInfo"
        nodeKey="id"
        :column="4"
        :picturePreReading="false"
      >
        <template v-slot="{ item, width }">
          <div class="overflow-hidden rounded-lg bg-white shadow-sm">
            <Item :picture="(item as PictureType)" :width="width"></Item>
          </div>
        </template>
      </waterfall>
    </infinite>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { type PictureType, ALLCategory } from "@/config";
import { useThrottleFn } from "@vueuse/core";
import { GetPictureList, GetTagCategory } from "@/services";
import dayjs from "dayjs";
import { Message } from "@/lib/Message";
import Waterfall from "@/lib/Waterfall";
import Infinite from "@/lib/Infinite";
import Button from "@/lib/Button";
import { Item } from "@/components/ListItem";

// Tabs
import Tabs, { type TabItem } from "@/lib/Tabs";
const activeTab = ref("");
const handleTabChange = (tab: TabItem) => {
  if (tab.name === ALLCategory) PageInfo.value.category = undefined;
  else PageInfo.value.category = tab.name;
  LoadList();
};

// TagsList
import { TagsList, type EnhancedTag } from "@/lib/TagsList";
const selectedTags = ref([]);
const handleChange = (tags: EnhancedTag[]) => {
  // Check if "全部" is selected
  if (tags.some((tag) => tag.name === ALLCategory)) {
    selectedTags.value = []; // Clear selected tags
    PageInfo.value.tags = []; // Clear tags in PageInfo
  } else PageInfo.value.tags = tags.map((tag) => tag.name);
  LoadList();
};
// Loading
const loading = ref<boolean>(false);
const isFinished = ref<boolean>(false);

interface PictureInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: number;
  name?: string;
  picFormat?: string;
  userId?: number;
  tags?: Array<string>;
}
const PageInfo = ref<PictureInfoInterface>({
  current: 1,
  pageSize: 10,
});

const PictureListInfo = ref<PictureType[]>([]);

const getLoadData = async () => {
  console.log("触底", PageInfo.value);
  if (isFinished.value) return;
  // 完成了第一次请求后，后续的请求让page自增
  if (PictureListInfo.value.length > 0) PageInfo.value.current++;
  await LoadList();
};
const formatRecords = (records: PictureType[]) => {
  return records.map((item: PictureType) => ({
    ...item,
    createTime: dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
    editTime: dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
  }));
};

const LoadList = useThrottleFn(async () => {
  try {
    loading.value = true;
    const { data, code, message } = await GetPictureList(PageInfo.value);

    if (code === 0 && data) {
      const formattedRecords = Array.isArray(data.records)
        ? formatRecords(data.records)
        : [];

      if (Number(PageInfo.value.current) === 1) {
        PictureListInfo.value = formattedRecords;
      } else {
        PictureListInfo.value.push(...formattedRecords);
      }

      // 判断数据是否全部加载完成
      const total = Number(data.total) || 0;
      if (PictureListInfo.value.length >= total) {
        isFinished.value = true;
      }
    } else {
      Message.error(`获取失败, 原因: ${message}`);
    }
  } catch (error) {
    Message.error(`请求失败: ${error}`);
  } finally {
    loading.value = false;
  }
}, 300);

watchEffect(() => LoadList());

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 300);

// Get Tag and Category Options
const categoryOptions = ref<string[]>([]);
const tagOptions = ref<{ id: number; name: string }[]>([]);
const getTagCategoryOptions = useThrottleFn(async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    tagOptions.value = (data.tagList ?? []).map((tag, index) => ({
      id: index + 1, // 假设 id 是从 1 开始的唯一标识符
      name: tag,
    }));
    tagOptions.value.unshift({ id: 0, name: ALLCategory });
    categoryOptions.value = [ALLCategory, ...(data.categoryList ?? [])];
  } else {
    Message.error(`获取标签和分类选项失败${message}`);
  }
}, 1000);
watchEffect(() => getTagCategoryOptions());
</script>
