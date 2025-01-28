<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-1 justify-start px-2">
      <div
        class="grid w-full max-w-lg lg:max-w-xs flex justify-start items-center"
      >
        <div class="flex justify-start items-center">
          <label for="search" class="block text-sm/6 font-medium text-gray-900"
            >Account:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.userAccount"
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

        <div class="flex justify-start items-center mx-4">
          <label for="search" class="block text-sm/6 font-medium text-gray-900"
            >UserName:
          </label>
          <div class="mx-2">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-indigo-600"
            >
              <input
                type="text"
                name="search"
                v-model="PageInfo.userName"
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
      </div>
    </div>
    <TableList :columns="UserManagerColumns">
      <template #tr>
        <tr v-for="item in UserListInfo" :key="item.id" class="even:bg-gray-50">
          <td
            class="whitespace-nowrap py-4 pl-4 px-3 text-sm font-medium text-gray-900 sm:pl-3"
          >
            {{ item.id }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            {{ item.userAccount }}
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-12 overflow-hidden"
          >
            {{ item.userName }}
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            <Badges
              :text="item.userRole === ACCESSENUM.ADMIN ? 'ADMIN' : '普通用户'"
              :color="item.userRole === ACCESSENUM.ADMIN ? 'red' : 'blue'"
            ></Badges>
          </td>
          <td class="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
            <img
              class="inline-block size-14 rounded-md max-w-8 max-h-8"
              :src="item.userAvatar ? item.userAvatar : DefaultUserAvatar"
              alt=""
            />
          </td>
          <td
            class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500"
          >
            {{ item.userProfile ? item.userProfile : "NULL" }}
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
      @previous="PreviousHandle"
      @next="NextHandle"
      @select-page="SelectPageHandle"
    />
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import { UserManagerColumns, type UserType, DefaultUserAvatar } from "@/config";
import { AdminGetUserList } from "@/services";
import { ref, watchEffect } from "vue";

import { Message } from "@/components/Message";
import { useThrottleFn } from "@vueuse/core";
import Badges from "@/lib/Badges";
import Pagination from "@/components/Pagination";
import { ACCESSENUM } from "@/access";

const total = ref<number>(0); // 题目总数
interface UserInfoInterface {
  current: number;
  pageSize: number;
  id?: number;
  sortField?: string;
  sortOrder?: string;
  userAccount?: string;
  userName?: string;
  userProfile?: string;
  userRole?: string;
}
// 分页请求数据
const PageInfo = ref<UserInfoInterface>({
  current: 1,
  pageSize: 20,
});

const UserListInfo = ref<UserType[]>([]);

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
  const { data, code, message } = await AdminGetUserList(PageInfo.value);
  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    UserListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: UserType) => ({
          id: item.id ? String(item.id) : "",
          userAccount: item.userAccount ?? "",
          userName: item.userName ?? "",
          userRole: item.userRole ?? "",
          userAvatar: item.userAvatar ?? DefaultUserAvatar,
          userProfile: item.userProfile ?? "",
        }))
      : [];
  } else Message("error", `获取题目失败, 原因: ${message}`);
}, 1000);

watchEffect(() => LoadList());
</script>
