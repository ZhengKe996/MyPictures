<template>
  <div
    class="w-full h-full animate-fade-in animate-duration-500 animate-ease-out"
  >
    <!-- Search and Filter Area - Centered with Max Width -->
    <div class="max-w-6xl mx-auto w-full px-6 space-y-6 mb-8">
      <!-- Search and Color Selection Row - Optimized Layout -->
      <div
        class="flex flex-col sm:flex-row items-center gap-4 bg-white p-4 rounded-xl shadow-sm border border-gray-200"
      >
        <div class="w-full sm:w-2/3">
          <SearchInput
            v-model="PageInfo.name"
            label="Name:"
            @search="handleSearch"
            @keypress="handleKeyPress"
            class="w-full"
          />
        </div>
        <div class="flex items-center gap-4 w-full sm:w-1/3 justify-end">
          <ColorInput
            v-model="oxColor"
            :colors="customColors"
            :color-options="{ outputFormat: '0x' }"
            @change="handleColorChange"
            class="min-w-[120px]"
          />
          <button
            type="button"
            class="flex items-center gap-2 px-4 py-2.5 rounded-xl font-medium text-white gradient-primary hover:shadow-lg transition-all duration-300 ease-out hover:scale-105 group"
            @click="handleReset"
            title="Reset all filters"
          >
            <i
              class="i-tabler-refresh h-5 w-5 transition-all duration-300 ease-out group-hover:rotate-180"
            ></i>
            <span class="text-sm">Reset</span>
          </button>
        </div>
      </div>

      <!-- Category and Tag Sections -->
      <div class="space-y-3">
        <div
          class="w-full border-b border-gray-200 transition-all duration-300 ease-in-out"
        >
          <Tabs
            v-model="activeTab"
            :tabs="categoryOptions"
            @change="handleTabChange"
            class="transition-all duration-300 ease-in-out"
          />
        </div>
        <div
          class="w-full border-b border-gray-200 transition-all duration-300 ease-in-out"
        >
          <TagsList
            :tags="tagOptions"
            v-model="selectedTags"
            @change="handleChange"
            :multiple="true"
          />
        </div>
      </div>
    </div>

    <!-- Waterfall Content Area -->
    <div class="max-w-6xl mx-auto">
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
          :column="4"
          :picturePreReading="false"
        >
          <template v-slot="{ item, width }">
            <div
              class="overflow-hidden rounded-lg bg-white shadow-sm animate-fade-in-up animate-duration-500 hover:shadow-lg transition-all duration-300"
            >
              <Item :picture="(item as PictureType)" :width="width"></Item>
            </div>
          </template>
        </waterfall>

        <!-- Empty State Display -->
        <template v-if="!loading && PictureListInfo.length === 0">
          <div
            class="flex flex-col items-center justify-center py-16 space-y-4 animate-fade-in"
          >
            <div
              class="rounded-full gradient-primary p-4 animate-hover-scale animate-duration-300"
            >
              <i class="i-tabler-photo-off size-8 text-white"></i>
            </div>
            <div class="text-center">
              <h3 class="text-base font-semibold text-gray-900 mb-1">
                No Images Found
              </h3>
              <p class="text-sm text-gray-500 mb-4">
                Try adjusting your search criteria
              </p>
              <Button
                type="primary"
                size="sm"
                :icon="'i-tabler-refresh'"
                class="gradient-primary text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
                @click="handleReset"
              >
                Reset Filters
              </Button>
            </div>
          </div>
        </template>
      </infinite>
    </div>
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
import { Item } from "@/components/ListItem";
import SearchInput from "@/lib/SearchInput";
import ColorInput from "@/lib/ColorInput";
import Button from "@/lib/Button";
// Tabs
import Tabs, { type TabItem } from "@/lib/Tabs";
const activeTab = ref("");
const handleTabChange = (tab: TabItem) => {
  if (tab.name === ALLCategory) PageInfo.value.category = undefined;
  else PageInfo.value.category = tab.name;
  PageInfo.value.current = 1;
  isFinished.value = false;
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
  PageInfo.value.current = 1;
  isFinished.value = false;
  LoadList();
};
// Loading
const loading = ref<boolean>(false);
const isFinished = ref<boolean>(false);

interface PictureInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: string;
  name?: string;
  userId?: string;
  picFormat?: string;
  tags?: Array<string>;
  picColor?: string; // 添加颜色字段
}
const PageInfo = ref<PictureInfoInterface>({
  current: 1,
  pageSize: 10,
});

const PictureListInfo = ref<PictureType[]>([]);

const getLoadData = async () => {
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

const handleSearch = () => {
  PageInfo.value.current = 1;
  isFinished.value = false;
  LoadList();
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
  if (event.key === "Enter") handleSearch();
}, 300);

// Get Tag and Category Options
const categoryOptions = ref<string[]>([]);
const tagOptions = ref<{ id: number; name: string }[]>([]);
const customColors = ref<string[]>();
const getTagCategoryOptions = useThrottleFn(async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    tagOptions.value = (data.tagList ?? []).map((tag, index) => ({
      id: index + 1, // 假设 id 是从 1 开始的唯一标识符
      name: tag,
    }));
    tagOptions.value.unshift({ id: 0, name: ALLCategory });
    categoryOptions.value = [ALLCategory, ...(data.categoryList ?? [])];

    // 添加颜色列表处理
    customColors.value = (data.colorList ?? []).map((data: string) => {
      return data;
    });
  } else {
    Message.error(`获取标签和分类选项失败${message}`);
  }
}, 1000);
watchEffect(() => getTagCategoryOptions());

// 添加颜色选择相关的响应式变量
const oxColor = ref();

// 添加颜色变化处理函数
const handleColorChange = (color: string) => {
  PageInfo.value = {
    ...PageInfo.value,
    current: 1,
    picColor: color || undefined,
  };
  isFinished.value = false;
  LoadList();
};

// 添加重置功能
const handleReset = () => {
  // 重置所有筛选条件
  PageInfo.value = {
    current: 1,
    pageSize: 10,
  };

  // 重置选中的标签和分类
  selectedTags.value = [];
  activeTab.value = "";
  oxColor.value = undefined;

  // 重新加载列表
  isFinished.value = false;
  LoadList();

  Message.success("已重置所有筛选条件");
};
</script>
