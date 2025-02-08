<template>
  <div
    class="w-full h-full flex flex-col animated animated-duration-500 animated-fade-in"
  >
    <!-- Add Space ID Banner -->
    <div
      v-if="spaceID"
      class="mb-4 p-3 bg-blue-50 border-l-4 border-blue-500 rounded-r flex items-center justify-between"
    >
      <div class="flex items-center">
        <span class="text-blue-700 font-medium">保存到空间：</span>
        <span class="text-blue-600 ml-2">{{ spaceID }}</span>
      </div>
      <a-tooltip title="将在保存后添加到指定空间">
        <div
          class="w-4 h-4 rounded-full border-2 border-blue-500 flex items-center justify-center cursor-help group hover:bg-blue-500 transition-colors duration-200"
        >
          <span
            class="text-blue-500 text-xs font-serif italic font-bold group-hover:text-white"
            >i</span
          >
        </div>
      </a-tooltip>
    </div>

    <h2 class="mb-4 text-xl">{{ isUpdateMode ? "更新图片" : "创建图片" }}</h2>
    <div
      class="w-full mb-2 border-b border-gray-200 transition-all duration-300 ease-in-out"
    >
      <Tabs
        v-model="activeTab"
        :tabs="['File', 'URL']"
        @change="handleTabChange"
        class="transition-all duration-300 ease-in-out"
      >
        <template #File>
          <a-spin :spinning="loadding" :delay="delayTime">
            <FileUpload
              :file="picture"
              @upload="uploadFileHandle"
              @remove="removeFileHandle"
            />
          </a-spin>
        </template>
        <template #URL>
          <div class="container mx-auto px-4 py-8">
            <div class="max-w-4xl mx-auto space-y-8">
              <div class="bg-white p-6 rounded-lg shadow-lg">
                <h1 class="text-2xl font-bold mb-6 text-gray-800">
                  Image Upload
                </h1>

                <URLUpload
                  v-bind:image-url="imageUrl"
                  :is-loading="loadding"
                  @upload="handleUpload"
                  placeholder="Enter image URL"
                />

                <!-- Display additional image information if needed -->
                <div v-if="currentImage" class="mt-4 p-4 bg-gray-50 rounded-lg">
                  <p class="text-sm text-gray-600">
                    Current image URL:
                    <a
                      :href="currentImage"
                      target="_blank"
                      class="text-blue-500 hover:underline break-all"
                    >
                      {{ currentImage }}
                    </a>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </template>
      </Tabs>
    </div>
    <a-form class="my-2" layout="vertical" :model="pictureForm">
      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入简介"
          :rows="2"
          autoSize
          allowClear
        />
      </a-form-item>
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          :options="categoryOptions"
          placeholder="请输入分类"
        />
      </a-form-item>
      <div>
        <a-form-item label="标签" name="tags">
          <div class="w-full p-3 border border-gray-200 rounded-md">
            <div class="mb-4 flex items-center justify-between">
              <span class="text-sm text-gray-500">
                已选择 {{ selectedTagsCount }} / {{ tagOptions.length }}
              </span>
              <CheckBox
                v-if="tagOptions.length > 0"
                v-model="selectAllTags"
                id="select-all-tags"
                label="全选"
                color="orange"
                :indeterminate="hasTagSelection && !allTagsSelected"
                @change="handleSelectAllChange"
              />
            </div>
            <div class="flex flex-wrap gap-2">
              <CheckBox
                v-for="tag in tagOptions"
                :key="tag.value"
                v-model="selectedTags[tag.value]"
                :id="`tag-${tag.value}`"
                :label="tag.label"
                color="blue"
                size="sm"
                inline
                @change="handleTagChange"
              />
            </div>
          </div>
        </a-form-item>
      </div>

      <a-form-item>
        <a-button
          @click="handleSubmit"
          type="primary"
          html-type="submit"
          class="w-full"
          >{{ isUpdateMode ? "更新" : "创建" }}</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from "vue";
import { useRoute, useRouter, onBeforeRouteLeave } from "vue-router";
const route = useRoute();
const router = useRouter();
import FileUpload from "@/components/FileUpload";

import {
  UploadImageFile,
  GetTagCategory,
  GetPictureById,
  EditPictureInfo,
  UploadImageFileByUrl,
} from "@/services";
import { Message } from "@/lib/Message";
import { type PictureType, type PictureEditType } from "@/config";
import { DefaultPictureInfo, DefaultPictureEditInfo } from "./config";
import { convertTagsToStringArray } from "@/utils";
import URLUpload from "@/components/URLUpload";
import CheckBox from "@/lib/CheckBox";

import Tabs, { type TabItem } from "@/lib/Tabs";

const imageUrl = ref("");
const activeTab = ref("File");

const currentImage = ref<string>("");
const loadding = ref<boolean>(false);
const delayTime = 500;

const picture = ref<PictureType>(DefaultPictureInfo);
const pictureForm = ref<PictureEditType>(DefaultPictureEditInfo);
const spaceID = ref<string>("");

const handleTabChange = (tab: TabItem) => (activeTab.value = tab.name);

/**
 * 异步上传图片函数。
 * 该函数通过 UploadImageFileByUrl API根据提供的 URL 上传图片，并在成功上传后更新图片信息或在失败时显示错误消息。
 *
 * @param {string} url - 要上传的图片的 URL。
 */
const handleUpload = async (url: string) => {
  loadding.value = true;
  try {
    const id = picture.value.id ? String(picture.value.id) : undefined;
    const { data, code, message } = await UploadImageFileByUrl(url, {
      id: id,
      spaceId: spaceID.value,
    });
    if (code === 0 && data) {
      let temp = data;
      if (id)
        temp = {
          ...data,
          name: picture.value.name,
          introduction: picture.value.introduction,
          category: picture.value.category,
          tags: picture.value.tags,
        };
      picture.value = pictureForm.value = temp;
      imageUrl.value = data.url;
    } else {
      throw new Error(message || "上传失败");
    }
  } catch (error) {
    Message.error(`上传失败, ${error}`);
  } finally {
    loadding.value = false;
  }
};

// UPDATE MODE
const { id } = defineProps<{
  id?: string;
}>();

/**
 * 异步处理上传文件的函数
 * @param {File} file - 需要上传的文件对象
 */
const uploadFileHandle = async (file: File) => {
  loadding.value = true;
  try {
    console.log("uploadFileHandle", spaceID.value);
    const id = picture.value.id ? String(picture.value.id) : undefined;
    const { data, code, message } = await UploadImageFile(file, {
      id: id,
      spaceId: spaceID.value,
    });
    if (code === 0 && data) {
      let temp = data;
      if (id)
        temp = {
          ...data,
          name: picture.value.name,
          introduction: picture.value.introduction,
          category: picture.value.category,
          tags: picture.value.tags,
        };
      picture.value = pictureForm.value = temp;
      Message.success("上传成功");
    } else {
      Message.error(`上传失败, ${message}`);
    }
    // TODO 使用AI识别图片，自动填充标签和分类
  } catch (error) {
    Message.error(`上传失败, ${error}`);
  } finally {
    loadding.value = false;
  }
};

const removeFileHandle = () => {
  picture.value.url = "";
};

// 定义为对象数组类型
const categoryOptions = ref<{ value: string; label: string }[]>([]);
const tagOptions = ref<{ value: string; label: string }[]>([]);
const selectedTags = ref<{ [key: string]: boolean }>({});

// 计算属性来获取已选中的标签
const pictureFormTags = computed({
  get() {
    return Object.keys(selectedTags.value).filter(
      (tag) => selectedTags.value[tag]
    );
  },
  set(newTags: string[]) {
    const newSelectedTags: { [key: string]: boolean } = {};
    tagOptions.value.forEach((tag) => {
      newSelectedTags[tag.value] = newTags.includes(tag.value);
    });
    selectedTags.value = newSelectedTags;
  },
});

// 更新 pictureForm.tags 的逻辑
watch(
  pictureFormTags,
  (newTags) => {
    pictureForm.value.tags = newTags;
    console.log("pictureFormTags", pictureForm.value.tags);
  },
  { immediate: true }
);

// 计算已选择的标签数量
const selectedTagsCount = computed(
  () => Object.values(selectedTags.value).filter((v) => v).length
);

// 是否有标签被选中
const hasTagSelection = computed(() => selectedTagsCount.value > 0);

// 是否所有标签都被选中
const allTagsSelected = computed(
  () =>
    tagOptions.value.length > 0 &&
    selectedTagsCount.value === tagOptions.value.length
);

// 全选状态计算属性
const selectAllTags = computed({
  get: () => allTagsSelected.value,
  set: (value) =>
    handleSelectAllChange({ target: { checked: value } } as unknown as Event),
});

// 处理全选/取消全选
const handleSelectAllChange = (event: Event) => {
  const checked = (event.target as HTMLInputElement).checked;
  tagOptions.value.forEach((tag) => {
    selectedTags.value[tag.value] = checked;
  });
  updatePictureFormTags();
};

// 处理单个标签变化
const handleTagChange = () => {
  updatePictureFormTags();
};

// 更新表单中的标签数组
const updatePictureFormTags = () => {
  pictureForm.value.tags = Object.entries(selectedTags.value)
    .filter(([_, selected]) => selected)
    .map(([tag]) => tag);
};

// 修改初始化选中状态方法
const initializeSelectedTags = () => {
  selectedTags.value = Object.fromEntries(
    tagOptions.value.map((tag) => [tag.value, false])
  );

  // 如果是编辑模式，设置已选中的标签
  if (pictureForm.value.tags) {
    pictureForm.value.tags.forEach((tag) => {
      if (tag in selectedTags.value) {
        selectedTags.value[tag] = true;
      }
    });
  }
};

// 修改获取标签和分类选项的方法
const getTagCategoryOptions = async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    tagOptions.value = (data.tagList ?? []).map((data: string) => ({
      value: data,
      label: data,
    }));
    categoryOptions.value = (data.categoryList ?? []).map((data: string) => ({
      value: data,
      label: data,
    }));
    // 初始化选中状态
    initializeSelectedTags();
  } else {
    Message.error(`获取标签和分类选项失败${message}`);
  }
};

// 修改重置表单方法
const resetForm = () => {
  picture.value = JSON.parse(JSON.stringify(DefaultPictureInfo));
  pictureForm.value = {
    id: undefined,
    name: "",
    introduction: "",
    category: "",
    tags: [],
  };

  // 重置 selectedTags
  const initialSelectedTags: { [key: string]: boolean } = {};
  tagOptions.value.forEach((tag) => {
    initialSelectedTags[tag.value] = false;
  });
  selectedTags.value = initialSelectedTags;
};

// 路由离开时重置表单
onBeforeRouteLeave(() => resetForm());

onMounted(() => nextTick(() => getTagCategoryOptions()));

const isUpdateMode = computed(() => route.path.includes("update"));

// 获取老数据
const getOldPicture = async (id: string) => {
  const { data, code, message } = await GetPictureById(id);
  if (code === 0 && data) {
    picture.value = {
      ...data,
      tags: convertTagsToStringArray(data.tags),
    };
    pictureForm.value.id = data.id;
    pictureForm.value.name = data.name;
    pictureForm.value.introduction = data.introduction;
    pictureForm.value.category = data.category;
    pictureForm.value.tags = convertTagsToStringArray(data.tags);
    console.log("getOldPicture pictureForm", pictureForm.value.tags);
    // 更新 selectedTags 以反映已选中的标签
    const initialSelectedTags: { [key: string]: boolean } = {};
    tagOptions.value.forEach((tag) => {
      initialSelectedTags[tag.value] = (pictureForm.value.tags || []).includes(
        tag.value
      );
    });
    selectedTags.value = initialSelectedTags;
  } else {
    Message.error(`获取图片信息失败${message}`);
  }
};

// 路由离开时重置表单
onBeforeRouteLeave(() => resetForm());

onMounted(() => {
  nextTick(() => {
    if (isUpdateMode && id) {
      if (typeof id === "string") {
        getOldPicture(id);
      } else {
        Message.error("无效的图片ID");
      }
    } else resetForm();

    // Handle spaceID from query params
    const querySpaceId = route.query.spaceId;
    spaceID.value = Array.isArray(querySpaceId)
      ? querySpaceId[0] || ""
      : querySpaceId || "";
  });
});

const handleSubmit = async () => {
  const pictureId = picture.value.id;
  if (!pictureId) return;
  const { data, code, message } = await EditPictureInfo(pictureForm.value);
  if (code === 0 && data) {
    Message.success("成功");
    router.push(`/detail/picture/${pictureId}`);
  } else Message.error(`失败, ${message}`);
};
</script>
