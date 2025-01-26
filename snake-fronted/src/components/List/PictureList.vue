<template>
  <div>
    <infinite v-model="loading" :isFinished="isFinished" @on-load="getListData">
      <!-- <div v-for="item in data">{{ item.id }}</div> -->
      <waterfall
        class="px-1 w-full"
        :data="data"
        nodeKey="id"
        :column="5"
        :picturePreReading="false"
      >
        <template v-slot="{ item, width }">
          <div class="overflow-hidden rounded-lg bg-white shadow-sm">
            <div class="px-4 py-5 sm:p-6">
              <!-- Content goes here -->
              {{ item.id }}
            </div>
          </div>
        </template>
      </waterfall>
    </infinite>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import Infinite from "../Infinite";
import Waterfall from "../Waterfall";
import type { PictureType } from "@/config";

const loading = ref<boolean>(false);
const isFinished = ref<boolean>(false);
const emits = defineEmits(["on-load"]);
let query = {
  page: 1,
  size: 20,
};
const { data = [], load = false } = defineProps<{
  data: PictureType[];
  load: boolean;
}>();
const resetQuery = (newQuery: any) => {
  query = { ...query, ...newQuery };
  // 重置状态
  isFinished.value = false;
};

const getListData = async () => {
  console.log("getListData 触底");
  if (isFinished.value) return;
  if (data.length) query.page += 1;
  emits("on-load", query);
};

watch(
  () => load,
  (newLoad) => (loading.value = newLoad)
);
</script>

<style scoped></style>
