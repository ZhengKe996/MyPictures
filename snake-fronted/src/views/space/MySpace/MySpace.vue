<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 transform -translate-y-4"
      enter-to-class="opacity-100 transform translate-y-0"
      leave-active-class="transition-all duration-300 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isExit"
        class="space-content animate-fade-in animate-duration-500"
      >
        <div class="max-w-6xl mx-auto">
          <!-- 头部信息 -->
          <div
            class="flex flex-col gap-6 mb-8 animate-fade-in-up animate-duration-700"
          >
            <!-- 标题行 -->
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <h1 class="text-2xl font-bold text-gray-800">
                  {{ username }}
                  <span class="text-sm font-normal text-gray-500 ml-2">
                    (Private Space)
                  </span>
                </h1>
              </div>
            </div>

            <!-- 搜索和操作行 -->
            <div class="flex items-center justify-between gap-4">
              <!-- 搜索框 -->
              <div class="flex-1 max-w-md">
                <SearchInput
                  v-model="PageInfo.name"
                  :showLabel="false"
                  placeholder="Search image name..."
                  @search="handleSearch"
                  @clear="handleClear"
                />
              </div>

              <!-- 筛选和操作按钮组 -->
              <div class="flex items-center gap-3">
                <SelectMenus
                  v-model="selectedCategory"
                  :options="categoryOptions"
                  placeholder="Select Category"
                  @update:modelValue="handleCategorySelect"
                  class="w-[140px]"
                />
                <SelectMenus
                  v-model="selectedDateRange"
                  :options="dateRangeOptions"
                  placeholder="Select Time Range"
                  @update:modelValue="handleDateRangeOptionSelect"
                  class="w-[140px]"
                />
                <Popover trigger="click" placement="bottom-end">
                  <template #reference>
                    <Button
                      type="primary"
                      icon="i-tabler:calendar"
                      :isActiveAnim="true"
                      class="min-w-[180px]"
                    >
                      {{ dateLabel }}
                    </Button>
                  </template>
                  <div class="w-[320px]">
                    <Calendars
                      mode="range"
                      :start-date="startDate"
                      :end-date="endDate"
                      @range-select="handleDateRangeSelect"
                      class="w-full"
                    />
                  </div>
                </Popover>

                <ColorInput
                  v-model="oxColor"
                  :colors="customColors"
                  :color-options="{ outputFormat: '0x' }"
                  @change="handleColorChange"
                />

                <!-- 重置按钮 - 更新样式 -->
                <button
                  type="button"
                  class="flex items-center gap-2 px-4 py-2.5 rounded-xl font-medium text-gray-700 bg-white border border-gray-200 hover:bg-indigo-50 hover:text-indigo-600 hover:border-indigo-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 active:bg-indigo-100 transition-all duration-200 ease-out group shadow-sm"
                  @click="handleReset"
                  title="Reset all filters"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-5 w-5 transition-all duration-300 ease-out group-hover:rotate-180 group-hover:text-indigo-500"
                    viewBox="0 0 24 24"
                    stroke-width="2"
                    stroke="currentColor"
                    fill="none"
                  >
                    <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                    <path d="M20 11a8.1 8.1 0 0 0 -15.5 -2m-.5 -4v4h4" />
                    <path d="M4 13a8.1 8.1 0 0 0 15.5 2m.5 4v-4h-4" />
                  </svg>
                  <span class="text-sm">Reset Filters</span>
                  <span
                    class="absolute -top-1 -right-1 size-2 rounded-full bg-indigo-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                    aria-hidden="true"
                  ></span>
                </button>

                <div class="w-px h-8 bg-gray-200/80"></div>

                <Button
                  type="primary"
                  icon="i-tabler:photo-plus"
                  @click="handleCreatePhoto"
                  :isActiveAnim="true"
                  class="w-[140px]"
                >
                  Create Image
                </Button>
              </div>
            </div>
          </div>

          <infinite
            v-model="loading"
            :isFinished="isFinished"
            @on-load="getLoadData"
            :threshold="100"
            :immediate-check="true"
            loading-text="Loading..."
            finished-text="No more data"
          >
            <waterfall
              class="px-1 w-full animate-fade-in animate-duration-300 animate-ease-out"
              :data="PictureListInfo"
              nodeKey="id"
              :column="5"
              :picturePreReading="false"
            >
              <template v-slot="{ item, width }">
                <div
                  class="overflow-hidden rounded-lg bg-white shadow-sm animate-fade-in-up animate-duration-500"
                >
                  <SpaceItem
                    :picture="(item as PictureType)"
                    :width="width"
                    @edit="handleEdit"
                    @delete="handleDelete"
                    @download="handleDownload"
                    @preview="handlePreview"
                  ></SpaceItem>
                </div>
              </template>
            </waterfall>

            <!-- 添加空状态展示 -->
            <template v-if="!loading && PictureListInfo.length === 0">
              <div
                class="flex flex-col items-center justify-center py-16 space-y-4 animate-fade-in"
              >
                <div
                  class="rounded-full bg-gray-50 p-4 animate-hover-scale animate-duration-300"
                >
                  <i class="i-tabler:photo-off size-8 text-gray-400"></i>
                </div>
                <div class="text-center">
                  <h3 class="text-base font-semibold text-gray-900 mb-1">
                    No Images Found
                  </h3>
                  <p class="text-sm text-gray-500 mb-4">
                    Please try different search criteria
                  </p>
                </div>
              </div>
            </template>
          </infinite>
        </div>
        <Pagination
          :total="total"
          :current="PageInfo.current"
          :page-size="PageInfo.pageSize"
          @change="ChangeCurrentPageHandle"
        />
      </div>
      <div
        v-else
        class="flex flex-col items-center justify-center min-h-[400px]"
      >
        <div class="text-center space-y-8">
          <div
            class="animate-bounce-alt animate-duration-3000 animate-infinite animate-ease-in-out"
          >
            <div
              class="i-tabler:database-off text-8xl text-gray-400/80 mb-4 transform transition duration-300 hover:text-blue-500/80"
            ></div>
          </div>
          <div class="space-y-3">
            <h3 class="text-xl font-medium text-gray-700">
              You haven't created your space yet
            </h3>
            <p class="text-gray-500 text-sm">
              Create a space to store and manage your photos!
            </p>
            <button
              @click="handleCreateSpace"
              class="mt-4 px-6 py-2.5 bg-blue-500 text-white rounded-lg transform transition-all duration-200 hover:bg-blue-600 hover:scale-105 hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 active:scale-95"
            >
              Create Space
            </button>
          </div>
        </div>
      </div>
    </Transition>
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
          Are you sure you want to delete this image? This action cannot be
          undone.
        </p>
        <div
          v-if="currentPicture"
          class="flex items-center space-x-4 bg-gray-50 dark:bg-zinc-700 p-3 rounded-lg"
        >
          <img
            :src="currentPicture.thumbnailUrl"
            class="w-16 h-16 object-cover rounded"
            alt="Preview Image"
          />
          <div>
            <p class="font-medium text-gray-900 dark:text-gray-100">
              {{ currentPicture.name }}
            </p>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Created at {{ currentPicture.createTime }}
            </p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from "vue";
import SearchInput from "@/lib/SearchInput";
import SelectMenus from "@/lib/SelectMenus";
import Popover from "@/lib/Popover";
import ColorInput from "@/lib/ColorInput";
import Calendars from "@/lib/Calendars";
import Button from "@/lib/Button";
import { SpaceItem } from "@/components/ListItem";
import Waterfall from "@/lib/Waterfall";
import Infinite from "@/lib/Infinite";
import Pagination from "@/lib/Pagination";
import Dialog from "@/lib/Dialog";
import {
  useSpaceManagement,
  useFileHandling,
  useListManagement,
  useFilterOptions,
  useDateFilter,
  useSearchAndFilter,
  type PictureType,
} from "./hooks";

const {
  isExit,
  spaceId,
  username,
  CheckSpace,
  handleCreateSpace,
  handleCreatePhoto,
} = useSpaceManagement();
const {
  showDeleteDialog,
  currentPicture,
  handleEdit,
  handleDelete,
  confirmDelete,
  handleCancelDelete,
  handleDownload,
  handlePreview,
} = useFileHandling();

const { loading, isFinished, total, PictureListInfo, PageInfo, LoadList } =
  useListManagement(spaceId);

const { categoryOptions, customColors, loadFilterOptions } = useFilterOptions();

const {
  startDate,
  endDate,
  selectedDateRange,
  dateRangeOptions,
  dateLabel,
  handleDateRangeSelect,
  handleDateRangeOptionSelect,
} = useDateFilter(PageInfo, () => LoadList()); // 传入 LoadList 回调

const {
  selectedCategory,
  oxColor,
  handleSearch,
  handleClear,
  handleColorChange,
  handleCategorySelect,
  handleReset,
} = useSearchAndFilter(PageInfo, () => LoadList()); // 传入 LoadList 回调

// 分页和加载更多处理
const getLoadData = async () => {
  if (isFinished.value) return;
  if (PictureListInfo.value.length > 0) {
    PageInfo.value.current++;
  }
  await LoadList();
};

const ChangeCurrentPageHandle = (current: number) => {
  PageInfo.value.current = current;
  LoadList();
};

// 监听 spaceId 变化，当值存在时加载列表
watch(spaceId, async (newSpaceId) => {
  if (newSpaceId) {
    await LoadList();
  }
});

onMounted(async () => {
  await CheckSpace();
  await loadFilterOptions();
});
</script>
