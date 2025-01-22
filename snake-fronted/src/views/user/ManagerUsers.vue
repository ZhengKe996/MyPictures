<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
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
              :index="item.userRole === ACCESSENUM.ADMIN ? 0 : 3"
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
import { onMounted, ref, watchEffect } from "vue";
import { message as Message } from "ant-design-vue";
import Badges from "@/components/Badges";
import Pagination from "@/components/Pagination";
import { ACCESSENUM } from "@/access";

const total = ref<number>(0); // 题目总数
// 分页请求数据
const PageInfo = ref<{ current: number; pageSize: number }>({
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

const LoadList = async () => {
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
    console.log(UserListInfo.value);
  } else Message.error(`获取题目失败, 原因: ${message}`);
};

watchEffect(() => LoadList());
onMounted(() => LoadList());
</script>
