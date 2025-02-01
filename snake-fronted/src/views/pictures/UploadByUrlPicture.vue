<template>
  <div
    class="w-full h-full flex flex-col animated animated-duration-500 animated-fade-in"
  >
    <h2 class="mb-4 text-xl">创建图片</h2>
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto space-y-8">
        <div class="bg-white p-6 rounded-lg shadow-lg">
          <h1 class="text-2xl font-bold mb-6 text-gray-800">Image Upload</h1>

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
          >创建</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from "vue";
import { onBeforeRouteLeave, useRouter } from "vue-router";
const router = useRouter();

import URLUpload from "@/components/URLUpload";

import {
  UploadImageFileByUrl,
  GetTagCategory,
  EditPictureInfo,
} from "@/services";
import { Message } from "@/lib/Message";
import { type PictureType, type PictureEditType } from "@/config";
import { DefaultPictureInfo, DefaultPictureEditInfo } from "./config";

const imageUrl = ref("");

const loadding = ref<boolean>(false);

const handleUpload = async (url: string) => {
  loadding.value = true;
  try {
    const { data, code, message } = await UploadImageFileByUrl({
      fileUrl: url,
    });
    if (code === 0 && data) {
      console.log("handleUpload", data);
      picture.value = pictureForm.value = data;
      imageUrl.value = data.url;
    } else {
      throw new Error(message || "Upload failed");
    }
  } catch (error) {
    Message.error(`上传失败, ${error}`);
  } finally {
    loadding.value = false;
  }
};

const currentImage = ref<string>("");

const picture = ref<PictureType>(DefaultPictureInfo);
const pictureForm = ref<PictureEditType>(DefaultPictureEditInfo);

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

const handleSubmit = async () => {
  const pictureId = picture.value.id;
  if (!pictureId) return;
  const { data, code, message } = await EditPictureInfo(pictureForm.value);
  if (code === 0 && data) {
    Message.success("成功");
    router.push(`/detail/picture/${pictureId}`);
  } else Message.error(`失败, ${message}`);
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
onMounted(() => resetForm());
</script>
