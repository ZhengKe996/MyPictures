<template>
  <div class="w-full animated animated-duration-500 animated-fade-in">
    <div class="flex flex-col gap-4 mb-4">
      <div class="flex w-full justify-center items-center">
        <div class="w-full max-w-xl grid grid-cols-2 gap-4">
          <div class="flex justify-start items-center">
            <label
              for="account"
              class="block text-sm/6 font-medium text-gray-900 mr-2"
            >
              Account:
            </label>
            <div class="flex-grow">
              <div
                class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-custom-gradient-end"
              >
                <input
                  type="text"
                  id="account"
                  v-model="PageInfo.userAccount"
                  class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                  @keypress="handleKeyPress"
                />
                <div class="flex py-1.5 pr-1.5">
                  <kbd
                    class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                  >
                    ↵
                  </kbd>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-start items-center">
            <label
              for="username"
              class="block text-sm/6 font-medium text-gray-900 mr-2"
            >
              UserName:
            </label>
            <div class="flex-grow">
              <div
                class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-custom-gradient-end"
              >
                <input
                  type="text"
                  id="username"
                  v-model="PageInfo.userName"
                  class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6"
                  @keypress="handleKeyPress"
                />
                <div class="flex py-1.5 pr-1.5">
                  <kbd
                    class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                  >
                    ↵
                  </kbd>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-end px-2">
        <Button
          @click="handleAdd"
          :icon="'i-tabler-plus'"
          size="sm"
          class="whitespace-nowrap w-auto inline-flex items-center justify-center px-4 py-2 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
        >
          Add New
        </Button>
      </div>
    </div>

    <TableList :columns="UserManagerColumns">
      <template #tr>
        <template v-if="ListInfo.length">
          <tr
            v-for="item in ListInfo"
            :key="item.id"
            class="even:bg-gray-50 border-b border-gray-100 hover:bg-gray-50/60 transition-colors duration-200 group"
          >
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
                  item.userRole === ACCESSENUM.ADMIN ? 'ADMIN' : 'Normal User'
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
                class="inline-flex items-center gap-x-1.5 rounded-md bg-green-600 px-2.5 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-green-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-green-600 transition-all duration-300 ease-out hover:scale-105 group-hover:scale-105 inline-block"
                @click=""
              >
                Edit
                <i class="i-tabler-edit size-5" />
              </button>
            </td>
            <td
              class="whitespace-nowrap truncate px-3 py-4 text-sm text-gray-500 text-center"
            >
              <button
                type="button"
                class="inline-flex items-center gap-x-1.5 rounded-md bg-red-600 px-2.5 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-red-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600 transition-all duration-300 ease-out hover:scale-105 group-hover:scale-105 inline-block"
                @click="item.id && handleDelete(item.id)"
              >
                Delete
                <i class="i-tabler-layout-grid-remove" size-5 />
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
                class="rounded-full bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end p-4 animate-hover-scale animate-duration-300"
              >
                <i class="i-tabler-users-off size-8 text-white"></i>
              </div>
              <div class="text-center">
                <h3 class="text-base font-semibold text-gray-900 mb-1">
                  No User Data
                </h3>
                <p class="text-sm text-gray-500 mb-4">
                  Click the button below to add a new user
                </p>
                <Button
                  type="primary"
                  size="sm"
                  :icon="'i-tabler-plus'"
                  class="animate-hover-scale animate-duration-300 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
                  @click="handleAdd"
                >
                  Add New User
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
      class="mt-6"
    />

    <!-- 删除确认对话框 -->
    <Dialog
      v-model="showDeleteDialog"
      title="Delete Confirmation"
      :confirmText="'Delete'"
      :cancelText="'Cancel'"
      :confirmButtonColor="'red'"
      :cancelButtonColor="'gray'"
      :confirmHandler="confirmDelete"
      :cancelHandler="handleCancelDelete"
    >
      <div class="space-y-4">
        <p class="text-gray-600 dark:text-gray-300">
          Are you sure you want to delete this user? All data associated with
          this user will be permanently deleted and cannot be recovered.
        </p>
        <div
          v-if="currentItem"
          class="bg-gray-50 dark:bg-zinc-700 p-4 rounded-lg space-y-2"
        >
          <div class="flex items-center space-x-4">
            <img
              :src="currentItem.userAvatar"
              class="w-12 h-12 rounded-full"
              alt="User Avatar"
            />
            <div>
              <p class="font-medium text-gray-900 dark:text-gray-100">
                {{ currentItem.userName }}
              </p>
              <p class="text-sm text-gray-500">
                Account: {{ currentItem.userAccount }}
              </p>
            </div>
          </div>
          <div class="text-sm text-gray-500">
            <p>
              User Role:
              {{
                currentItem.userRole === ACCESSENUM.ADMIN
                  ? "ADMIN"
                  : "Normal User"
              }}
            </p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import TableList from "@/components/TableList";
import { UserManagerColumns, DefaultUserAvatar } from "@/config";
import Badges from "@/lib/Badges";
import Pagination from "@/lib/Pagination";
import { ACCESSENUM } from "@/access";
import Button from "@/lib/Button";
import Dialog from "@/lib/Dialog/Dialog.vue";
import { useUserManagement } from "./hooks";

const {
  total,
  PageInfo,
  ListInfo,
  showDeleteDialog,
  currentItem,
  ChangeCurrentPageHandle,
  handleKeyPress,
  handleAdd,
  handleDelete,
  confirmDelete,
  handleCancelDelete,
} = useUserManagement();
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

.animate-hover-scale {
  transition: transform 0.3s;
}

.animate-hover-scale:hover {
  transform: scale(1.05);
}

@keyframes subtleGradient {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.active-button {
  background-size: 200% auto;
  animation: subtleGradient 3s ease infinite;
}
</style>
