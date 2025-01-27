<template>
  <infinite v-model="loading" :isFinished="isFinished" @on-load="getListData">
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
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { type PictureType } from "@/config";
import { useThrottleFn } from "@vueuse/core";
import { AdminGetPictureList, GetTagCategory } from "@/services";
import dayjs from "dayjs";
import { Message } from "@/components/Message";
import Waterfall from "@/components/Waterfall";
import Infinite from "@/components/Infinite";
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
  console.log("getListData 触底");
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

    console.log(PictureListInfo.value);
  } else Message("error", `获取失败, 原因: ${message}`);
  loading.value = false;
}, 1000);
watchEffect(() => LoadList());

const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);
const getTagCategoryOptions = useThrottleFn(async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    console.log("getTagCategoryOptions", data);
    tagOptions.value = (data.tagList ?? []).map((tag) => ({
      value: tag,
      label: tag,
    }));
    categoryOptions.value = (data.categoryList ?? []).map((category) => ({
      value: category,
      label: category,
    }));
  } else {
    Message("error", `获取标签和分类选项失败${message}`);
  }
}, 1000);
watchEffect(() => getTagCategoryOptions());
</script>
