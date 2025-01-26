<template>
  <div class="w-full h-full flex flex-col">
    <h2 class="mb-4 text-xl">{{ isUpdateMode ? "更新图片" : "创建图片" }}</h2>
    <a-spin :spinning="loadding" :delay="delayTime">
      <FileUpload :file="picture" @upload="uploadFileHandle" />
    </a-spin>
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
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
import FileUpload from "@/components/FileUpload";
import {
  UploadImageFile,
  GetTagCategory,
  GetPictureById,
  EditPictureInfo,
} from "@/services";
import { Message } from "@/components/Message";
import { type PictureType, type PictureEditType } from "@/config";
import { DefaultPictureInfo, DefaultPictureEditInfo } from "./config";
import { convertTagsToStringArray } from "@/utils";
const loadding = ref<boolean>(false);
const delayTime = 500;

const picture = ref<PictureType>(DefaultPictureInfo);
const pictureForm = ref<PictureEditType>(DefaultPictureEditInfo);

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
      Message("success", "上传成功");
    } else {
      Message("error", `上传失败, ${message}`);
    }
  } catch (error) {
    Message("error", `上传失败, ${error}`);
  } finally {
    loadding.value = false;
  }
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
    Message("error", `获取标签和分类选项失败${message}`);
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
    Message("error", `获取图片信息失败${message}`);
  }
};

onMounted(() => {
  nextTick(() => {
    if (isUpdateMode && route.query.id) {
      const id = Array.isArray(route.query.id)
        ? route.query.id[0]
        : route.query.id;
      if (typeof id === "string") {
        getOldPicture(id);
      } else {
        Message("error", "无效的图片ID");
      }
    } else {
      picture.value = DefaultPictureInfo;
      pictureForm.value = DefaultPictureEditInfo;
    }
  });
});

const handleSubmit = async () => {
  const pictureId = picture.value.id;
  console.log("handleSubmit", pictureForm.value);
  if (!pictureId) return;
  const { data, code, message } = await EditPictureInfo(pictureForm.value);
  if (code === 0 && data) {
    Message("success", "成功");
    router.push(`/detail/picture?id=${pictureId}`);
  } else Message("error", `失败, ${message}`);
};
</script>
