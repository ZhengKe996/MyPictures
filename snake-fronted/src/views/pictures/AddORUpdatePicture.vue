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
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          :options="tagOptions"
          mode="tags"
          placeholder="请输入标签"
          allowClear
        />
      </a-form-item>

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
import { computed, nextTick, onMounted, ref } from "vue";
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

import Tabs, { type TabItem } from "@/lib/Tabs";

const imageUrl = ref("");
const activeTab = ref("File");

const currentImage = ref<string>("");
const loadding = ref<boolean>(false);
const delayTime = 500;

const picture = ref<PictureType>(DefaultPictureInfo);
const pictureForm = ref<PictureEditType>(DefaultPictureEditInfo);

const handleTabChange = (tab: TabItem) => (activeTab.value = tab.name);

/**
 * 异步上传图片函数。
 * 该函数通过 UploadImageFileByUrl API 根据提供的 URL 上传图片，并在成功上传后更新图片信息或在失败时显示错误消息。
 *
 * @param {string} url - 要上传的图片的 URL。
 */
const handleUpload = async (url: string) => {
  // 开始上传前设置加载状态为 true
  loadding.value = true;
  try {
    // 调用 UploadImageFileByUrl 函数上传图片
    const { data, code, message } = await UploadImageFileByUrl({
      fileUrl: url,
    });
    // 如果上传成功且返回了数据
    if (code === 0 && data) {
      // 更新图片信息
      picture.value = pictureForm.value = data;
      // 更新图片 URL
      imageUrl.value = data.url;
    } else {
      // 如果上传失败，抛出错误
      throw new Error(message || "上传失败");
    }
  } catch (error) {
    // 显示错误消息
    Message.error(`上传失败, ${error}`);
  } finally {
    // 无论成功或失败，上传完成后设置加载状态为 false
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
    const { data, code, message } = await UploadImageFile(file);
    if (code === 0 && data) {
      picture.value = pictureForm.value = data;
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

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const { data, code, message } = await GetTagCategory();
  if (code === 0 && data) {
    tagOptions.value = (data.tagList ?? []).map((data: string) => {
      return { value: data, label: data };
    });
    categoryOptions.value = (data.categoryList ?? []).map((data: string) => {
      return { value: data, label: data };
    });
  } else {
    Message.error(`获取标签和分类选项失败${message}`);
  }
};

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
  } else {
    Message.error(`获取图片信息失败${message}`);
  }
};

// 重置表单方法
const resetForm = () => {
  picture.value = JSON.parse(JSON.stringify(DefaultPictureInfo));
  pictureForm.value = {
    id: -1,
    name: "",
    introduction: "",
    category: "",
    tags: [],
  };
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
const spaceID = ref<string>("");
</script>
