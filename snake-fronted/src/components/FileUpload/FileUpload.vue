<template>
  <div
    class="w-full animated animated-duration-500 animated-fade-in"
    @drop="onDrop"
    @dragover="onDragOver"
    @dragleave="onDragLeave"
  >
    <div
      :class="[
        'transition-all duration-300 ease-out',
        'rounded-lg',
        isDragging
          ? 'bg-gray-50/80 border-custom-gradient-end'
          : 'bg-white hover:bg-gray-50/60',
        !file.thumbnailUrl || !file.url
          ? 'border-2 border-dashed border-gray-300 hover:border-custom-gradient-end'
          : '',
      ]"
    >
      <label
        v-if="!file.thumbnailUrl || !file.url"
        for="dropzone-file"
        class="flex flex-col items-center justify-center w-full h-64 cursor-pointer transition-all duration-300"
      >
        <div
          class="flex flex-col items-center justify-center pt-5 pb-6 space-y-4"
        >
          <div
            class="rounded-full gradient-primary p-4 animate-hover-scale animate-duration-300"
          >
            <i class="i-tabler-upload size-8 text-white"></i>
          </div>
          <div class="text-center space-y-2">
            <p class="text-base font-semibold text-gray-900">
              <span class="text-custom-gradient-end">Click to upload</span> or
              drag and drop
            </p>
            <p class="text-sm text-gray-500">
              SVG, PNG, JPG or GIF (MAX. {{ formatSize(MAX_FILE_SIZE) }})
            </p>
          </div>
        </div>
        <input
          id="dropzone-file"
          @change="handleFileSelect"
          type="file"
          accept="image/*"
          class="hidden"
        />
      </label>

      <div v-else class="relative p-4">
        <img
          :src="(file.thumbnailUrl ?? file.url) || DefaultImage"
          alt="Uploaded Image"
          class="w-full h-64 object-cover rounded-lg shadow-lg transition-all duration-300 hover:scale-105"
        />
        <button
          @click="removeImage"
          class="absolute top-6 right-6 gradient-primary text-white p-2 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 focus:outline-none focus:ring-2 focus:ring-custom-gradient-end"
          aria-label="Remove Image"
        >
          <i class="i-tabler-x size-5"></i>
        </button>
      </div>
    </div>

    <!-- Error Message -->
    <div
      v-if="errorMessage"
      class="mt-2 text-sm text-error-200 animated animated-duration-300 animated-fade-in"
    >
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { type PictureType, DefaultImage } from "@/config";
import { formatSize } from "@/utils";
import { MAX_FILE_SIZE } from "./config";

const emits = defineEmits(["Upload", "Remove"]);
const isDragging = ref(false);
const uploadedFile = ref<File | null>(null);
const errorMessage = ref<string>("");

const { file } = defineProps<{ file: PictureType }>();

const validateFile = (file: File): boolean => {
  if (!file.type.startsWith("image/")) {
    errorMessage.value = "Please upload an image file";
    return false;
  }

  if (file.size > MAX_FILE_SIZE) {
    errorMessage.value = `File size exceeds ${formatSize(MAX_FILE_SIZE)}`;
    return false;
  }

  errorMessage.value = "";
  return true;
};

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    const file = target.files[0];
    if (validateFile(file)) {
      uploadedFile.value = file;
      emits("Upload", file);
    }
  }
};

const onDrop = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
  if (event.dataTransfer?.files.length) {
    const file = event.dataTransfer.files[0];
    if (validateFile(file)) {
      uploadedFile.value = file;
      emits("Upload", file);
    }
  }
};

const onDragOver = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = true;
};

const onDragLeave = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
};

const removeImage = () => {
  uploadedFile.value = null;
  errorMessage.value = "";
  emits("Remove");
};
</script>

<style scoped>
.animate-hover-scale {
  transition: transform 0.3s ease;
}

.animate-hover-scale:hover {
  transform: scale(1.05);
}

.animated-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
