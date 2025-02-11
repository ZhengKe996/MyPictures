<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 页面顶部背景区域 -->
    <div class="relative overflow-hidden pb-32">
      <!-- 底层渐变背景 -->
      <div
        class="absolute inset-0 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end"
      ></div>
      <!-- 白色蒙版层 -->
      <div class="absolute inset-0 bg-white/30 backdrop-blur-[2px]"></div>

      <!-- 内容区域 -->
      <div class="relative py-10">
        <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h1 class="text-3xl font-bold tracking-tight text-white">
            Account Settings
          </h1>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <main class="relative -mt-32">
      <div class="mx-auto max-w-screen-xl px-4 pb-6 sm:px-6 lg:px-8 lg:pb-16">
        <div
          class="overflow-hidden rounded-lg bg-white shadow animate-fade-in animate-duration-500"
        >
          <!-- 表单区域 -->
          <form class="divide-y divide-gray-200" @submit.prevent="handleSubmit">
            <!-- 基本信息设置 -->
            <div class="px-4 py-6 sm:p-6 lg:pb-8">
              <div class="mb-8">
                <h2
                  class="text-lg font-medium text-gray-900 gradient-primary bg-clip-text text-transparent"
                >
                  Profile Settings
                </h2>
                <p class="mt-1 text-sm text-gray-500">
                  Update your account information and preferences.
                </p>
              </div>

              <!-- 主要内容区域 - 调整布局结构 -->
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <!-- 左侧表单区域 -->
                <div class="lg:col-span-2 space-y-6">
                  <!-- 用户名设置 -->
                  <div>
                    <label
                      for="userName"
                      class="block text-sm font-medium text-gray-700"
                    >
                      Username
                    </label>
                    <div class="mt-2">
                      <input
                        type="text"
                        id="userName"
                        v-model="formData.userName"
                        class="block w-full rounded-lg px-4 py-2.5 text-gray-900 border border-gray-300 focus:ring-2 focus:ring-custom-gradient-end focus:border-transparent"
                        placeholder="Enter your username"
                      />
                    </div>
                  </div>

                  <!-- 用户简介设置 (原 email 设置) -->
                  <div>
                    <label
                      for="userProfile"
                      class="block text-sm font-medium text-gray-700"
                    >
                      User Profile
                    </label>
                    <div class="mt-2">
                      <input
                        type="text"
                        id="userProfile"
                        v-model="formData.userProfile"
                        class="block w-full rounded-lg px-4 py-2.5 text-gray-900 border border-gray-300 focus:ring-2 focus:ring-custom-gradient-end focus:border-transparent"
                        placeholder="Enter your profile description"
                      />
                    </div>
                  </div>
                </div>

                <!-- 右侧头像上传区域 -->
                <div class="lg:col-span-1">
                  <div
                    class="flex flex-col items-center p-6 bg-gray-50 rounded-lg"
                  >
                    <label class="block text-sm font-medium text-gray-700 mb-4">
                      Profile Photo
                    </label>
                    <div class="flex flex-col items-center">
                      <div
                        class="relative size-40 overflow-hidden rounded-full ring-4 ring-white shadow-lg"
                      >
                        <img
                          :src="
                            previewUrl ||
                            formData.userAvatar ||
                            '/default-avatar.png'
                          "
                          class="size-full object-cover"
                          alt="Profile photo"
                        />
                        <div
                          class="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 hover:opacity-100 transition-all duration-200 cursor-pointer"
                          @click="triggerFileInput"
                        >
                          <i class="i-tabler-camera text-white text-2xl"></i>
                        </div>
                        <!-- 隐藏的文件输入框 -->
                        <input
                          ref="fileInput"
                          type="file"
                          accept="image/*"
                          class="hidden"
                          @change="handleFileChange"
                        />
                      </div>
                      <button
                        type="button"
                        class="mt-4 px-4 py-2 text-sm font-medium text-white rounded-lg gradient-primary hover:opacity-90 transition-all duration-200 hover:scale-105"
                        @click="triggerFileInput"
                      >
                        Change Photo
                      </button>
                      <p class="mt-2 text-xs text-gray-500">
                        Recommended: Square image, at least 400x400px
                      </p>
                      <!-- 错误提示 -->
                      <p v-if="fileError" class="mt-2 text-xs text-red-500">
                        {{ fileError }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 隐私设置 -->
              <div class="mt-10 border-t border-gray-200 pt-8">
                <h3
                  class="text-lg font-medium text-gray-900 gradient-primary bg-clip-text text-transparent mb-4"
                >
                  Privacy Settings
                </h3>
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <h4 class="text-sm font-medium text-gray-900">
                        Public Profile
                      </h4>
                      <p class="text-sm text-gray-500">
                        Make your profile visible to others
                      </p>
                    </div>
                    <Switch
                      v-model="formData.isPublic"
                      class="relative inline-flex size-[38px] shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-custom-gradient-end focus:ring-offset-2"
                      :class="[
                        formData.isPublic
                          ? 'bg-custom-gradient-end'
                          : 'bg-gray-200',
                      ]"
                    >
                      <span
                        class="pointer-events-none relative inline-block size-[34px] transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                        :class="[
                          formData.isPublic ? 'translate-x-4' : 'translate-x-0',
                        ]"
                      />
                    </Switch>
                  </div>
                </div>
              </div>
            </div>

            <!-- 提交按钮 -->
            <div class="px-4 py-4 sm:px-6 flex justify-end gap-3">
              <button
                type="button"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-custom-gradient-end focus:ring-offset-2"
                @click="handleCancel"
              >
                Cancel
              </button>
              <button
                type="submit"
                class="px-4 py-2 text-sm font-medium text-white rounded-lg gradient-primary hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-custom-gradient-end focus:ring-offset-2 transition-all duration-200 hover:scale-105"
              >
                Save Changes
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>

    <!-- 图片裁剪对话框 -->
    <Dialog
      v-model="showCropDialog"
      title="裁剪头像"
      confirmText="确认"
      cancelText="取消"
      :showClose="true"
      cancelButtonColor="gray"
      confirmButtonColor="blue"
      :confirmHandler="handleCropConfirm"
      :cancelHandler="handleCropCancel"
    >
      <ImageCropper
        v-if="showCropDialog"
        :imageUrl="tempImageUrl"
        :fileName="cropFileName"
        @crop="handleCropComplete"
        @error="handleCropError"
      />
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Switch } from "@headlessui/vue";
import Dialog from "@/lib/Dialog";
import ImageCropper from "@/components/ImageCropper";
import { GetLoginInfoUser, UploadAvatar, EditProfile } from "@/services";
import { DefaultUserAvatar } from "@/config";
import { Message } from "@/lib/Message";
import { useUserStore } from "@/store/user";
import { useRouter } from "vue-router";
const userStore = useUserStore();
const router = useRouter();
// 表单数据 - TODO: 需要从后端获取初始数据
const formData = ref({
  userName: "",
  userProfile: "",
  userAvatar: "",
  isPublic: false,
});

// 文件上传相关
const fileInput = ref<HTMLInputElement | null>(null);
const previewUrl = ref<string>("");
const fileError = ref<string>("");

// 裁剪相关状态
const showCropDialog = ref(false);
const tempImageUrl = ref("");
const cropFileName = ref("");
const currentFile = ref<File | null>(null);

const triggerFileInput = () => {
  fileInput.value?.click();
};

const handleFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (!input.files?.length) return;

  const file = input.files[0];

  // 验证文件类型
  if (!file.type.startsWith("image/")) {
    fileError.value = "Please upload an image file";
    return;
  }

  // 验证文件大小（例如最大 5MB）
  if (file.size > 5 * 1024 * 1024) {
    fileError.value = "Image size should be less than 5MB";
    return;
  }

  // 清除之前的错误
  fileError.value = "";
  currentFile.value = file;

  // 创建临时URL用于裁剪预览
  tempImageUrl.value = URL.createObjectURL(file);
  cropFileName.value = file.name;

  // 打开裁剪对话框
  showCropDialog.value = true;
};

// 处理裁剪完成
const handleCropComplete = async (croppedFile: File) => {
  try {
    // 更新预览
    previewUrl.value = URL.createObjectURL(croppedFile);

    // 上传裁剪后的图片
    await handleAvatarUpload(croppedFile);

    // 关闭对话框
    showCropDialog.value = false;

    // 清理临时URL
    URL.revokeObjectURL(tempImageUrl.value);
  } catch (error) {
    console.error("Error handling cropped image:", error);
    fileError.value = "Failed to process cropped image";
  }
};

// 处理裁剪错误
const handleCropError = (error: Error) => {
  console.error("Crop error:", error);
  fileError.value = "Failed to crop image";
  showCropDialog.value = false;
};

// 处理裁剪确认
const handleCropConfirm = () => {
  // Dialog 的确认按钮会自动关闭对话框
  // ImageCropper 组件会通过 @crop 事件发送裁剪后的文件
};

// 处理裁剪取消
const handleCropCancel = () => {
  // 清理临时URL
  URL.revokeObjectURL(tempImageUrl.value);
  tempImageUrl.value = "";
  currentFile.value = null;
};

/**
 * 处理头像上传
 * @API POST /api/user/avatar
 * @param file 裁剪后的图片文件
 * TODO: 实现文件上传到服务器的逻辑
 */
const handleAvatarUpload = async (file: File) => {
  try {
    const { code, data, message } = await UploadAvatar(file);
    if (code === 0 && data) {
      formData.value.userAvatar = data;
    } else throw new Error(message);
  } catch (error) {
    console.error("Error uploading avatar:", error);
    fileError.value = "Failed to upload image";
    throw error;
  }
};

/**
 * 处理表单提交
 * TODO: 实现更新用户资料的逻辑
 */
const handleSubmit = async () => {
  try {
    const { data, code, message } = await EditProfile(formData.value);
    if (code === 0 && data) {
      Message.success("Profile updated successfully");
    } else throw new Error(message);
    console.log("Form submitted:", formData.value);
  } catch (error) {
    console.error("Error submitting form:", error);
    Message.error("Failed to update profile");
  }
  await userStore.setLoginInfo();
  router.push("/account/about");
};

/**
 * 初始化用户数据
 * TODO: 组件挂载时获取用户资料
 */
const initUserData = async () => {
  try {
    const { data, message, code } = await GetLoginInfoUser();
    if (code === 0 && data) {
      formData.value = {
        userName: data.userName ?? "",
        userProfile: data.userProfile ?? "", // 现在作为用户简介
        userAvatar: data.userAvatar ?? DefaultUserAvatar,
        isPublic: false,
      };
    } else throw new Error(message);
  } catch (error) {
    console.error("Error fetching user data:", error);
    Message.error("Failed to fetch user data");
  }
};

// 组件挂载时获取用户数据
onMounted(async () => {
  await initUserData();
});

// 处理取消
const handleCancel = () => {
  // 实现取消逻辑
  console.log("Form cancelled");
};
</script>
