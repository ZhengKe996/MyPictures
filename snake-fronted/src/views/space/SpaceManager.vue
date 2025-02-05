<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-1 justify-start px-2">
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
    </div>
    <TableList :columns="SpaceManagerColumns">
      <template #tr>
        <tr v-for="item in ListInfo" :key="item.id" class="even:bg-gray-50">
          <td
            class="whitespace-nowrap py-4 pl-4 px-3 text-sm font-medium text-gray-900 sm:pl-3"
          >
            {{ item.id }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.spaceName }}
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-12 overflow-hidden"
          >
            {{ item.spaceLevel }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            <!-- <Badges
              :text="item.userRole === ACCESSENUM.ADMIN ? 'ADMIN' : '普通用户'"
              :color="item.userRole === ACCESSENUM.ADMIN ? 'red' : 'blue'"
            ></Badges> -->
            Condition
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.user?.userName }}
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500"
          >
            {{ item.createTime }}
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500"
          >
            {{ item.editTime }}
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500"
          >
            <button
              type="button"
              class="inline-flex items-center gap-x-1.5 rounded-md bg-green-600 px-2.5 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-green-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-green-600"
              @click=""
            >
              编辑
              <i class="i-tabler:edit size-5" />
            </button>
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500"
          >
            <button
              type="button"
              class="inline-flex items-center gap-x-1.5 rounded-md bg-red-600 px-2.5 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-red-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600"
              @click=""
            >
              删除
              <i class="i-tabler:layout-grid-remove" size-5 />
            </button>
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
import Button from "@/lib/Button";
import { useThrottleFn } from "@vueuse/core";
import { SpaceManagerColumns, type SpaceType } from "@/config";
import { onMounted, ref } from "vue";
import { GetSpaceList } from "@/services";
import { Message } from "@/lib/Message";

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
          id: item.id ? String(item.id) : "",
        }))
      : [];
  } else Message.error(`获取题目失败, 原因: ${message}`);
};

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);

onMounted(() => LoadList());
</script>

<style scoped></style>
