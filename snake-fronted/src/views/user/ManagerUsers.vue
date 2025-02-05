<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-1 justify-between items-center px-2">
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
    <TableList :columns="UserManagerColumns">
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
              {{ item.userAccount }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 max-w-12 overflow-hidden text-center"
            >
              {{ item.userName }}
            </td>
            <td
              class="whitespace-nowrap px-3 py-4 text-sm text-gray-500 text-center"
            >
              <Badges
                :text="
                  item.userRole === ACCESSENUM.ADMIN ? 'ADMIN' : '普通用户'
                "
                :color="item.userRole === ACCESSENUM.ADMIN ? 'red' : 'blue'"
              ></Badges>
            </td>
            <td
              class="whitespace-nowrap px-3 py-4 text-sm text-gray-500 text-center"
            >
              <img
                class="inline-block size-14 rounded-md max-w-8 max-h-8"
                :src="item.userAvatar ? item.userAvatar : DefaultUserAvatar"
                alt=""
              />
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              {{ item.userProfile ? item.userProfile : "NULL" }}
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
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
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
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
        <tr v-else>
          <td :colspan="UserManagerColumns.length" class="py-16">
            <div
              class="flex flex-col items-center justify-center space-y-4 animate-fade-in animate-duration-500 animate-ease-out"
            >
              <div
                class="rounded-full bg-gray-50 p-4 animate-hover-scale animate-duration-300"
              >
                <i class="i-tabler:users-off size-8 text-gray-400"></i>
              </div>
              <div class="text-center">
                <h3 class="text-base font-semibold text-gray-900 mb-1">
                  暂无用户数据
                </h3>
                <p class="text-sm text-gray-500 mb-4">
                  点击下方按钮添加新的用户
                </p>
                <Button
                  type="primary"
                  size="sm"
                  :icon="'i-tabler:plus'"
                  class="animate-hover-scale animate-duration-300"
                  @click="handleAdd"
                >
                  添加新用户
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
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import { UserManagerColumns, type UserType, DefaultUserAvatar } from "@/config";
import { AdminGetUserList } from "@/services";
import { ref, watchEffect } from "vue";

import { Message } from "@/lib/Message";
import { useThrottleFn } from "@vueuse/core";
import Badges from "@/lib/Badges";
import Pagination from "@/lib/Pagination";
import { ACCESSENUM } from "@/access";
import Button from "@/lib/Button";

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

const ListInfo = ref<UserType[]>([]);
const ChangeCurrentPageHandle = (current: number) =>
  (PageInfo.value = { ...PageInfo.value, current: current });

const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
  if (event.key === "Enter") LoadList();
}, 1000);

const LoadList = useThrottleFn(async () => {
  const { data, code, message } = await AdminGetUserList(PageInfo.value);
  if (code === 0 && data) {
    total.value = Number(data.total) ?? 0;

    ListInfo.value = Array.isArray(data.records)
      ? data.records.map((item: UserType) => ({
          id: item.id ? String(item.id) : "",
          userAccount: item.userAccount ?? "",
          userName: item.userName ?? "",
          userRole: item.userRole ?? "",
          userAvatar: item.userAvatar ?? DefaultUserAvatar,
          userProfile: item.userProfile ?? "",
        }))
      : [];
  } else Message.error(`获取失败, 原因: ${message}`);
}, 1000);

watchEffect(() => LoadList());

const handleAdd = () => {
  console.log("添加新空间");
};
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
