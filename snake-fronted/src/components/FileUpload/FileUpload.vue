<template>
  <div
    class="flex items-center justify-center w-full"
    @drop="onDrop"
    @dragover="onDragOver"
    @dragleave="onDragLeave"
    :class="{
      'bg-gray-200': isDragging,
      'border-2 border-dashed border-gray-300 rounded-lg': !uploadedFile,
    }"
  >
    <label
      v-if="!uploadedFile"
      for="dropzone-file"
      class="flex flex-col items-center justify-center w-full h-64 cursor-pointer bg-gray-50 dark:hover:bg-gray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600"
    >
      <div class="flex flex-col items-center justify-center pt-5 pb-6">
        <svg
          class="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400"
          aria-hidden="true"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 20 16"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"
          />
        </svg>
        <p class="mb-2 text-sm text-gray-500 dark:text-gray-400">
          <span class="font-semibold">Click to upload</span> or drag and drop
        </p>
        <p class="text-xs text-gray-500 dark:text-gray-400">
          SVG, PNG, JPG or GIF (MAX. 800x400px)
        </p>
      </div>
      <input
        id="dropzone-file"
        @change="handleFileSelect"
        type="file"
        accept="image/*"
        class="hidden"
      />
    </label>
    <div v-else class="relative">
      <img
        :src="imageUrl"
        alt="Uploaded Image"
        class="w-full h-64 object-cover rounded-lg shadow-lg transition-transform duration-500 hover:scale-105"
      />
      <button
        @click="removeImage"
        class="absolute top-2 right-2 bg-red-500 text-white p-2 rounded-full shadow-md hover:bg-red-600 transition-colors duration-500 focus:outline-none focus:ring-2 focus:ring-red-500"
        aria-label="Remove Image"
      >
        ×
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const emits = defineEmits(["Upload"]);
const isDragging = ref(false);
const uploadedFile = ref<File | null>(null);
const imageUrl = ref<string>("");

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    const file = target.files[0];
    if (isImageFile(file)) {
      uploadedFile.value = file;
      imageUrl.value = URL.createObjectURL(file);
      emits("Upload", file);
    }
  }
};

const onDrop = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
  if (event.dataTransfer && event.dataTransfer.files.length > 0) {
    const file = event.dataTransfer.files[0];
    if (isImageFile(file)) {
      uploadedFile.value = file;
      imageUrl.value = URL.createObjectURL(file);
      emits("Upload", file);
    }
  }
};

const onDragOver = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = true;
};

const onDragLeave = () => {
  isDragging.value = false;
};

const isImageFile = (file: File): boolean => {
  return file.type.startsWith("image/");
};

const removeImage = () => {
  uploadedFile.value = null;
  imageUrl.value = "";
  emits("Upload", null);
};
</script>
