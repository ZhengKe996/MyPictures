<template>
  <div class="min-h-screen p-6 bg-gray-50">
    <Transition
      enter-active-class="transition-all duration-500 ease-out"
      enter-from-class="opacity-0 transform -translate-y-4"
      enter-to-class="opacity-100 transform translate-y-0"
      leave-active-class="transition-all duration-500 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isExit"
        class="space-content animate-fade-in animate-duration-500"
      >
        <div class="max-w-6xl mx-auto">
          <div class="container mx-auto px-4 py-6">
            <!-- 添加空间使用率显示区域 -->
            <!-- <div
              v-if="spaceStats"
              class="mb-8 flex justify-center gap-8 items-center"
            >
              <RoundProgress
                :progress="spaceStats.countUsagePercent"
                :size="150"
                start-color="#3B82F6"
                end-color="#2563EB"
                label="图片数量"
                text-class="text-blue-600"
                decoration-class="bg-gradient-to-r from-blue-500/10 to-blue-600/10"
                decoration-dots-class="bg-blue-500"
              >
                <template #center="{ progress }">
                  <div class="text-center">
                    <div class="text-2xl font-bold text-blue-600">
                      {{ progress }}%
                    </div>
                    <div class="text-sm text-blue-500">
                      {{ spaceStats.totalCount }}/{{ spaceStats.maxCount }}
                    </div>
                  </div>
                </template>
              </RoundProgress>

              <RoundProgress
                :progress="spaceStats.sizeUsagePercent"
                :size="150"
                start-color="#10B981"
                end-color="#059669"
                label="空间大小"
                text-class="text-emerald-600"
                decoration-class="bg-gradient-to-r from-emerald-500/10 to-emerald-600/10"
                decoration-dots-class="bg-emerald-500"
              >
                <template #center="{ progress }">
                  <div class="text-center">
                    <div class="text-2xl font-bold text-emerald-600">
                      {{ progress }}%
                    </div>
                    <div class="text-sm text-emerald-500">
                      {{ formatSize(spaceStats.totalSize) }}/{{
                        formatSize(spaceStats.maxSize)
                      }}
                    </div>
                  </div>
                </template>
              </RoundProgress>
            </div> -->
            <div
              class="flex flex-col gap-6 mb-8 animate-fade-in-up animate-duration-700"
            >
              <!-- Header with Gradient Title -->
              <div
                class="flex items-center justify-between border-b border-gray-200 pb-4"
              >
                <!-- 用户名称区域 -->
                <h1
                  class="text-2xl font-bold gradient-primary bg-clip-text text-transparent"
                >
                  {{ username }}
                  <span class="text-sm font-normal text-gray-500 ml-2"
                    >(Private Space)</span
                  >
                </h1>

                <!-- 重新设计的空间使用统计和创建按钮区域 -->
                <div class="flex items-center gap-3">
                  <!-- 空间使用统计 - 添加悬浮效果 -->
                  <div
                    v-if="spaceStats"
                    class="relative group cursor-pointer"
                    @click="handleNavigateToAnalyze"
                  >
                    <!-- 紧凑视图 -->
                    <div
                      class="flex items-center space-x-4 px-3 py-1.5 bg-white rounded-lg shadow-sm transition-all duration-300 hover:bg-gray-50"
                    >
                      <!-- 图片数量统计 -->
                      <div class="flex items-center space-x-2">
                        <RoundProgress
                          :progress="spaceStats.countUsagePercent"
                          :size="28"
                          :stroke-width="2.5"
                          start-color="#3B82F6"
                          end-color="#2563EB"
                          :show-decoration="false"
                          :duration="1000"
                          class="shrink-0"
                        >
                          <template #center>
                            <i
                              class="i-tabler:photo text-blue-600 text-[10px]"
                            ></i>
                          </template>
                        </RoundProgress>
                        <div class="text-xs whitespace-nowrap">
                          <span class="text-gray-500">图片：</span>
                          <span class="font-medium text-blue-600"
                            >{{ spaceStats.totalCount }}/{{
                              spaceStats.maxCount
                            }}</span
                          >
                        </div>
                      </div>

                      <div class="h-6 w-px bg-gray-200"></div>

                      <!-- 空间大小统计 -->
                      <div class="flex items-center space-x-2">
                        <RoundProgress
                          :progress="spaceStats.sizeUsagePercent"
                          :size="28"
                          :stroke-width="2.5"
                          start-color="#10B981"
                          end-color="#059669"
                          :show-decoration="false"
                          :duration="1000"
                          class="shrink-0"
                        >
                          <template #center>
                            <i
                              class="i-tabler:database text-emerald-600 text-[10px]"
                            ></i>
                          </template>
                        </RoundProgress>
                        <div class="text-xs whitespace-nowrap">
                          <span class="text-gray-500">空间：</span>
                          <span class="font-medium text-emerald-600"
                            >{{ formatSize(spaceStats.totalSize) }}/{{
                              formatSize(spaceStats.maxSize)
                            }}</span
                          >
                        </div>
                      </div>
                    </div>

                    <!-- 悬浮详细视图 -->
                    <div
                      class="hidden group-hover:block absolute top-full right-0 mt-2 p-4 bg-white rounded-xl shadow-lg z-50 min-w-[300px] transform transition-all duration-300 animate-fade-in animate-duration-200 hover:shadow-xl"
                    >
                      <div class="flex items-center justify-between mb-4">
                        <h3 class="text-sm font-medium text-gray-700">
                          空间使用详情
                        </h3>
                        <div class="text-xs text-gray-500">
                          更新于 {{ formatTime }}
                        </div>
                      </div>

                      <!-- 图片数量详情 -->
                      <div class="mb-4">
                        <div class="flex items-center justify-between mb-2">
                          <span class="text-sm text-gray-600"
                            >图片数量使用率</span
                          >
                          <span class="text-sm font-medium text-blue-600"
                            >{{ spaceStats.countUsagePercent }}%</span
                          >
                        </div>
                        <RoundProgress
                          :progress="spaceStats.countUsagePercent"
                          :size="80"
                          :stroke-width="8"
                          start-color="#3B82F6"
                          end-color="#2563EB"
                          :show-decoration="false"
                          :duration="1000"
                        >
                          <template #center>
                            <div class="text-center">
                              <div class="text-xl font-bold text-blue-600">
                                {{ spaceStats.countUsagePercent }}%
                              </div>
                              <div class="text-[10px] text-blue-500">
                                {{ spaceStats.totalCount }}/{{
                                  spaceStats.maxCount
                                }}
                              </div>
                            </div>
                          </template>
                        </RoundProgress>
                      </div>

                      <!-- 空间大小详情 -->
                      <div>
                        <div class="flex items-center justify-between mb-2">
                          <span class="text-sm text-gray-600"
                            >空间大小使用率</span
                          >
                          <span class="text-sm font-medium text-emerald-600"
                            >{{ spaceStats.sizeUsagePercent }}%</span
                          >
                        </div>
                        <RoundProgress
                          :progress="spaceStats.sizeUsagePercent"
                          :size="80"
                          :stroke-width="8"
                          start-color="#10B981"
                          end-color="#059669"
                          :show-decoration="false"
                          :duration="1000"
                        >
                          <template #center>
                            <div class="text-center">
                              <div class="text-xl font-bold text-emerald-600">
                                {{ spaceStats.sizeUsagePercent }}%
                              </div>
                              <div class="text-[10px] text-emerald-500">
                                {{ formatSize(spaceStats.totalSize) }}/{{
                                  formatSize(spaceStats.maxSize)
                                }}
                              </div>
                            </div>
                          </template>
                        </RoundProgress>
                      </div>

                      <!-- 提示信息 -->
                      <div class="mt-4 text-xs text-gray-500">
                        <p
                          v-if="spaceStats.sizeUsagePercent > 80"
                          class="text-amber-500"
                        >
                          <i class="i-tabler:alert-triangle mr-1"></i>
                          空间使用率较高，建议及时清理
                        </p>
                        <p v-else class="text-emerald-500">
                          <i class="i-tabler:check mr-1"></i>
                          空间使用正常
                        </p>
                      </div>
                    </div>

                    <!-- 添加提示文本 -->
                    <div
                      class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
                    >
                      点击查看详细分析
                    </div>
                  </div>

                  <!-- 创建图片按钮 -->
                  <Button
                    type="primary"
                    icon="i-tabler-photo-plus"
                    @click="handleCreatePhoto"
                    class="gradient-primary text-white hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
                  >
                    Create Image
                  </Button>
                </div>
              </div>

              <!-- Search and Filter Section -->
              <div class="flex flex-wrap items-center gap-4">
                <!-- Existing search and filter components with updated styles -->
                <div class="flex-1 min-w-[240px]">
                  <SearchInput
                    v-model="PageInfo.name"
                    :showLabel="false"
                    placeholder="Search image name..."
                    @search="handleSearch"
                    @clear="handleClear"
                  />
                </div>

                <div class="flex flex-wrap items-center gap-3">
                  <!-- Existing select menus with gradient hover effects -->
                  <SelectMenus
                    v-model="selectedCategory"
                    :options="categoryOptions"
                    placeholder="Select Category"
                    @update:modelValue="handleCategorySelect"
                    class="w-[140px]"
                  />
                  <ColorInput
                    v-model="oxColor"
                    :colors="customColors"
                    :color-options="{ outputFormat: '0x' }"
                    @change="handleColorChange"
                  />
                  <Popover trigger="click" placement="bottom-end">
                    <template #reference>
                      <Button
                        type="primary"
                        icon="i-tabler-calendar"
                        :isActiveAnim="true"
                        class="min-w-[180px] flex items-center gap-2 px-4 py-2.5 rounded-xl font-medium text-gray-700 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white hover:shadow-lg transition-all duration-300 ease-out hover:scale-105 group"
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
                  <SelectMenus
                    v-model="selectedDateRange"
                    :options="dateRangeOptions"
                    placeholder="Select Time Range"
                    @update:modelValue="handleDateRangeOptionSelect"
                    class="w-[140px]"
                  />

                  <!-- Gradient Reset Button -->
                  <button
                    type="button"
                    class="flex items-center gap-2 px-4 py-2.5 rounded-xl font-medium text-gray-700 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white hover:shadow-lg transition-all duration-300 ease-out hover:scale-105 group"
                    @click="handleReset"
                    title="Reset all filters"
                  >
                    <i
                      class="i-tabler-refresh h-5 w-5 transition-all duration-300 ease-out group-hover:rotate-180"
                    ></i>
                    <span class="text-sm">Reset Filters</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Waterfall and Empty State with Gradient Styles -->
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
                    class="overflow-hidden rounded-lg bg-white shadow-sm animate-fade-in-up animate-duration-500 hover:shadow-lg transition-all duration-300 hover:scale-[1.02]"
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

              <!-- Empty State with Gradient Icon -->
              <template v-if="!loading && PictureListInfo.length === 0">
                <div
                  class="flex flex-col items-center justify-center py-16 space-y-4 animate-fade-in"
                >
                  <div
                    class="rounded-full gradient-primary p-4 animate-hover-scale animate-duration-300"
                  >
                    <i class="i-tabler:photo-off size-8 text-white"></i>
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
      </div>

      <!-- No Space Created Section with Gradient Button -->
      <div
        v-else
        class="flex flex-col items-center justify-center min-h-[400px] animate-fade-in animate-duration-500"
      >
        <div class="text-center space-y-8">
          <div
            class="animate-bounce-alt animate-duration-3000 animate-infinite animate-ease-in-out"
          >
            <div
              class="i-tabler:database-off text-8xl text-gray-400/80 mb-4 transform transition duration-300 hover:text-custom-gradient-end"
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
              class="mt-4 px-6 py-2.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white rounded-lg transform transition-all duration-200 hover:scale-105 hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-custom-gradient-end focus:ring-opacity-50 active:scale-95"
            >
              Create Space
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Existing Dialog Component -->
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
import { useRouter } from "vue-router";
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
import RoundProgress from "@/lib/RoundProgress/RoundProgress.vue";

import {
  useSpaceManagement,
  useFileHandling,
  useListManagement,
  useFilterOptions,
  useDateFilter,
  useSearchAndFilter,
  type PictureType,
} from "./hooks";
import dayjs from "dayjs";

const router = useRouter();

const {
  isExit,
  spaceId,
  username,
  spaceStats, // 从 useSpaceManagement 中获取
  formatSize, // 从 useSpaceManagement 中获取
  formatTime, // 从 useSpaceManagement 中获取
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
  await CheckSpace(); // CheckSpace 现在会同时处理空间统计信息
  await loadFilterOptions();
});

// 添加跳转到分析页面的处理函数
const handleNavigateToAnalyze = () => {
  router.push({
    path: "/analyze",
    query: {
      spaceId: spaceId.value,
    },
  });
};
</script>
