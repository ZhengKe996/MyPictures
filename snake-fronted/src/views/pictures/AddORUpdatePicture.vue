<template>
  <div class="w-full min-h-screen bg-gray-50/30 p-6">
    <!-- Space ID Banner with improved design -->
    <div
      v-if="spaceID"
      class="mb-6 p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg border-l-4 border-blue-500 shadow-sm hover:shadow-md transition-all duration-300"
    >
      <div class="flex items-center">
        <span class="text-blue-700 font-medium">Save to Space:</span>
        <span class="text-blue-600 ml-2">{{ spaceID }}</span>
      </div>
    </div>

    <!-- Main Content Container -->
    <div class="max-w-4xl mx-auto space-y-8 bg-white rounded-xl shadow-sm p-6">
      <!-- Header -->
      <div
        class="flex items-center justify-between border-b border-gray-100 pb-4"
      >
        <h2
          class="text-2xl font-bold bg-gradient-to-r from-gray-900 to-gray-600 bg-clip-text text-transparent"
        >
          {{ isUpdateMode ? "Update Image" : "Create Image" }}
        </h2>
      </div>

      <!-- Upload Tabs -->
      <Tabs v-model="activeTab" :tabs="['File', 'URL']" class="mb-8">
        <template #File>
          <FileUpload
            :file="picture"
            @upload="uploadFileHandle"
            @remove="removeFileHandle"
          />
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

      <!-- Form Section with validation -->
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <TextInput
          v-model="pictureForm.name"
          id="picture-name"
          label="Name"
          placeholder="Enter image name"
          required
          :error="formErrors.name"
        />

        <TextInput
          v-model="pictureForm.introduction"
          id="picture-introduction"
          label="Description"
          placeholder="Enter image description"
          :multiline="true"
          :rows="3"
        />

        <SelectMenus
          v-model="selectedCategory"
          :options="transformedCategoryOptions"
          label="Category"
          placeholder="Select a category"
          :error="formErrors.category"
        />

        <!-- Tags Selection with validation -->
        <div class="space-y-4">
          <div
            class="w-full p-4 bg-white border border-gray-200 rounded-lg shadow-sm"
          >
            <div class="mb-4 flex items-center justify-between">
              <div>
                <span class="text-sm font-medium text-gray-700">Tags</span>
                <span class="ml-2 text-sm text-gray-500">
                  (Selected {{ selectedTagsCount }} / {{ tagOptions.length }})
                </span>
              </div>
              <CheckBox
                v-if="tagOptions.length > 0"
                v-model="selectAllTags"
                id="select-all-tags"
                label="Select All"
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
                color="orange"
                size="sm"
                inline
                @change="handleTagChange"
              />
            </div>
          </div>
          <p v-if="formErrors.tags" class="text-sm text-red-500 mt-1">
            {{ formErrors.tags }}
          </p>
        </div>

        <!-- Submit Button with loading state -->
        <button
          type="submit"
          class="w-full py-3 px-4 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-medium rounded-lg shadow-sm hover:shadow-md transition-all duration-300 hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="isSubmitting || !isFormValid"
        >
          <span v-if="isSubmitting" class="inline-flex items-center">
            <i class="i-tabler-loader-2 animate-spin mr-2"></i>
            {{ isUpdateMode ? "Updating..." : "Creating..." }}
          </span>
          <span v-else>{{ isUpdateMode ? "Update" : "Create" }}</span>
        </button>
      </form>
    </div>
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
import TextInput from "@/lib/TextInput";
import SelectMenus, { type SelectOption } from "@/lib/SelectMenus";
import Tabs from "@/lib/Tabs";

const imageUrl = ref("");
const activeTab = ref("File");

const currentImage = ref<string>("");
const loadding = ref<boolean>(false);

const picture = ref<PictureType>(DefaultPictureInfo);
const pictureForm = ref<
  PictureEditType & { name: string; introduction: string }
>({
  ...DefaultPictureEditInfo,
  name: "",
  introduction: "", // 确保这里是空字符串而不是 null
});
const spaceID = ref<string>("");

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
      picture.value = pictureForm.value = {
        ...temp,
        introduction: temp.introduction ?? "", // 确保 introduction 为字符串
      };
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
    if (!file) {
      throw new Error("Please select an image to upload");
    }

    // Add file type validation
    const allowedTypes = ["image/jpeg", "image/png", "image/gif"];
    if (!allowedTypes.includes(file.type)) {
      throw new Error("Only JPG, PNG, and GIF formats are supported");
    }

    // Add file size validation (e.g., 10MB limit)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      throw new Error("Image size cannot exceed 10MB");
    }

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
      picture.value = pictureForm.value = {
        ...temp,
        introduction: temp.introduction ?? "", // 确保 introduction 为字符串
      };
      Message.success("Upload successful");
    } else {
      Message.error(`Upload failed: ${message}`);
    }
    // TODO 使用AI识别图片，自动填充标签和分类
  } catch (error) {
    Message.error(`Upload failed: ${error}`);
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
    Message.error(`Failed to get tags and categories: ${message}`);
  }
};

// 修改重置表单方法
const resetForm = () => {
  picture.value = {
    ...JSON.parse(JSON.stringify(DefaultPictureInfo)),
    introduction: "", // 确保 introduction 为字符串
  };
  pictureForm.value = {
    id: undefined,
    name: "",
    introduction: "", // 确保 introduction 为字符串
    category: "",
    tags: [],
  };
  formErrors.value = {};
  imageUrl.value = "";

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
      introduction: data.introduction ?? "", // 确保 introduction 为字符串
    };
    pictureForm.value = {
      id: data.id,
      name: data.name ?? "",
      introduction: data.introduction ?? "", // 确保 introduction 为字符串
      category: data.category,
      tags: convertTagsToStringArray(data.tags),
    };
    // 更新 selectedTags 以反映已选中的标签
    const initialSelectedTags: { [key: string]: boolean } = {};
    tagOptions.value.forEach((tag) => {
      initialSelectedTags[tag.value] = (pictureForm.value.tags || []).includes(
        tag.value
      );
    });
    selectedTags.value = initialSelectedTags;
  } else {
    Message.error(`Failed to get image information: ${message}`);
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
        Message.error("Invalid image ID");
      }
    } else resetForm();

    // Handle spaceID from query params
    const querySpaceId = route.query.spaceId;
    spaceID.value = Array.isArray(querySpaceId)
      ? querySpaceId[0] || ""
      : querySpaceId || "";
  });
});

// Form validation state
const formErrors = ref<Record<string, string>>({});
const isSubmitting = ref(false);

// Transform category options for SelectMenus component
const transformedCategoryOptions = computed(() =>
  categoryOptions.value.map((option) => ({
    id: option.value,
    name: option.label,
    value: option.value,
  }))
);

// Handle category selection with proper type conversion
const selectedCategory = computed({
  get: () =>
    transformedCategoryOptions.value.find(
      (opt) => opt.value === pictureForm.value.category
    ) || undefined,
  set: (option: SelectOption | undefined) => {
    pictureForm.value.category = option?.value || "";
  },
});

// Validate form
const validateForm = () => {
  const errors: Record<string, string> = {};

  if (!pictureForm.value.name?.trim()) {
    errors.name = "Please enter an image name";
  }

  if (!pictureForm.value.category?.trim()) {
    errors.category = "Please select a category";
  }

  if (!pictureForm.value.tags?.length) {
    errors.tags = "Please select at least one tag";
  }

  formErrors.value = errors;
  return Object.keys(errors).length === 0;
};

// Computed property for form validity
const isFormValid = computed(() => {
  return (
    !!pictureForm.value.name?.trim() &&
    !!pictureForm.value.category?.trim() &&
    !!pictureForm.value.tags?.length
  );
});

// Modified submit handler
const handleSubmit = async () => {
  if (!validateForm()) {
    Message.error("Please fill in all required fields");
    return;
  }

  try {
    isSubmitting.value = true;
    const pictureId = picture.value.id;

    if (!pictureId && !isUpdateMode.value) {
      Message.error("Please upload an image first");
      return;
    }

    const { data, code, message } = await EditPictureInfo(pictureForm.value);

    if (code === 0 && data) {
      Message.success(
        isUpdateMode.value ? "Update successful" : "Create successful"
      );
      router.push(`/detail/picture/${pictureId}`);
    } else {
      throw new Error(message);
    }
  } catch (error) {
    Message.error(`Operation failed: ${error}`);
  } finally {
    isSubmitting.value = false;
  }
  selectedTags.value = {};
};
</script>

<style scoped>
/* Add loading animation */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* Improve form transitions */
.form-field {
  transition: all 0.3s ease-out;
}

.form-field:focus-within {
  transform: translateY(-1px);
}

/* Error state animations */
.error-message {
  animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

@keyframes shake {
  10%,
  90% {
    transform: translateX(-1px);
  }
  20%,
  80% {
    transform: translateX(2px);
  }
  30%,
  50%,
  70% {
    transform: translateX(-4px);
  }
  40%,
  60% {
    transform: translateX(4px);
  }
}
</style>
