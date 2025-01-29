<template>
  <div
    class="file-upload relative"
    @drop.prevent="onDrop"
    @dragover.prevent="onDragOver"
    @dragleave.prevent="onDragLeave"
    :class="[
      'transition-all duration-300 ease-in-out',
      isDragging ? 'scale-102 bg-gray-100/50 dark:bg-gray-700/50' : '',
      disabled ? 'opacity-50 cursor-not-allowed' : '',
    ]"
  >
    <div
      v-if="!modelValue"
      class="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-4"
      :class="[
        'transition-colors duration-300',
        isDragging ? 'border-primary-500' : '',
      ]"
    >
      <label
        class="flex flex-col items-center justify-center min-h-[200px] cursor-pointer"
        :class="{ 'cursor-not-allowed': disabled }"
      >
        <slot name="default" :isDragging="isDragging">
          <div class="flex flex-col items-center justify-center space-y-4">
            <div
              class="p-4 rounded-full bg-gray-50 dark:bg-gray-800 transition-transform duration-300"
              :class="{ 'scale-110': isDragging }"
            >
              <svg
                class="w-8 h-8 text-gray-500 dark:text-gray-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                />
              </svg>
            </div>
            <div class="text-center space-y-2">
              <p class="text-sm text-gray-600 dark:text-gray-400">
                <span class="font-medium">Click to upload</span> or drag and
                drop
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-500">
                {{ accept }} (Max: {{ formatFileSize(maxSize) }})
              </p>
            </div>
          </div>
        </slot>
        <input
          ref="fileInput"
          type="file"
          :accept="accept"
          class="hidden"
          :disabled="disabled"
          @change="handleFileSelect"
        />
      </label>
    </div>

    <div v-else-if="preview && previewUrl" class="relative group">
      <img
        :src="previewUrl"
        alt="Preview"
        class="w-full h-[200px] object-cover rounded-lg shadow-lg transition-all duration-300 group-hover:brightness-90"
      />
      <button
        v-if="!disabled"
        @click="removeFile"
        class="absolute top-2 right-2 p-2 bg-red-500 text-white rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-300 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
      >
        <span class="sr-only">Remove file</span>
        <svg
          class="w-4 h-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M6 18L18 6M6 6l12 12"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import type { FileUploadProps, FileUploadEmits } from "./types";

const props = withDefaults(defineProps<FileUploadProps>(), {
  accept: "image/*",
  maxSize: 10 * 1024 * 1024,
  preview: true,
  disabled: false,
  previewUrl: "",
});

const emit = defineEmits<FileUploadEmits>();

const isDragging = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files?.length) {
    validateAndEmitFile(input.files[0]);
  }
};

const onDrop = (event: DragEvent) => {
  isDragging.value = false;
  const file = event.dataTransfer?.files[0];
  if (file) {
    validateAndEmitFile(file);
  }
};

const validateAndEmitFile = (file: File) => {
  if (props.disabled) return;

  if (!file.type.match(props.accept.replace("/*", "/.*"))) {
    emit("error", "Invalid file type");
    return;
  }

  if (file.size > props.maxSize) {
    emit("error", `File size exceeds ${formatFileSize(props.maxSize)}`);
    return;
  }

  emit("update:modelValue", file);
  emit("success", file);
};

const removeFile = () => {
  if (props.disabled) return;
  emit("update:modelValue", null);
  emit("remove");
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

const onDragOver = () => {
  if (!props.disabled) {
    isDragging.value = true;
  }
};

const onDragLeave = () => {
  isDragging.value = false;
};

const formatFileSize = (bytes: number): string => {
  const units = ["B", "KB", "MB", "GB"];
  let size = bytes;
  let unitIndex = 0;
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }
  return `${size.toFixed(1)} ${units[unitIndex]}`;
};
</script>

<style scoped>
.scale-102 {
  transform: scale(1.02);
}

.file-upload {
  width: 100%;
}
</style>
