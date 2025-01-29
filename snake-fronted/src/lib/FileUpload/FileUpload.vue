<template>
  <div
    class="flex items-center justify-center w-full"
    @drop="onDrop"
    @dragover="onDragOver"
    @dragleave="onDragLeave"
    :class="[
      containerClass,
      {
        'bg-gray-200/50 scale-102': isDragging,
        'border-2 border-dashed border-gray-300 rounded-lg': !file?.url,
      },
    ]"
  >
    <label
      v-if="!file?.url"
      :for="inputId"
      class="flex flex-col items-center justify-center w-full h-64 cursor-pointer transition-all duration-300 ease-in-out"
      :class="[
        dragZoneClass,
        {
          'opacity-60': disabled,
          'cursor-not-allowed': disabled,
        },
      ]"
    >
      <div class="flex flex-col items-center justify-center pt-5 pb-6">
        <slot name="icon">
          <svg
            class="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400 transition-transform duration-300 group-hover:scale-110"
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
        </slot>
        <slot name="label">
          <p class="mb-2 text-sm text-gray-500 dark:text-gray-400">
            <span class="font-semibold">Click to upload</span> or drag and drop
          </p>
          <p class="text-xs text-gray-500 dark:text-gray-400">
            {{ acceptedTypesMessage }}
          </p>
        </slot>
      </div>
      <input
        :id="inputId"
        ref="fileInput"
        @change="handleFileSelect"
        type="file"
        :accept="accept"
        class="hidden"
        :disabled="disabled"
      />
    </label>
    <div v-else class="relative group">
      <img
        :src="file.url || defaultImage"
        :alt="file.name || 'Uploaded Image'"
        :class="[
          previewClass,
          'transition-all duration-300 ease-in-out group-hover:scale-105',
        ]"
      />
      <button
        v-if="!disabled"
        @click.prevent="removeImage"
        class="absolute top-2 right-2 bg-red-500 text-white p-2 rounded-full shadow-md opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500"
        aria-label="Remove Image"
      >
        <slot name="remove-icon">×</slot>
      </button>
    </div>
    <div
      v-if="error"
      class="absolute bottom-2 left-2 right-2 bg-red-100 text-red-600 px-4 py-2 rounded-md text-sm"
      role="alert"
    >
      {{ error }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { type PictureType } from "@/config";
import { DEFAULT_CONFIG, ERROR_MESSAGES } from "./config";

interface Props {
  file: PictureType;
  defaultImage?: string;
  accept?: string;
  maxSize?: number;
  disabled?: boolean;
  inputId?: string;
  containerClass?: string;
  dragZoneClass?: string;
  previewClass?: string;
  allowedTypes?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  defaultImage: "",
  accept: "image/*",
  maxSize: DEFAULT_CONFIG.maxSize,
  disabled: false,
  inputId: "dropzone-file",
  containerClass: "",
  dragZoneClass:
    "bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600",
  previewClass: "w-full h-64 object-cover rounded-lg shadow-lg",
  allowedTypes: () => DEFAULT_CONFIG.acceptedTypes,
});

const emits = defineEmits<{
  (e: "Upload", file: File | null): void;
  (e: "error", message: string): void;
}>();

const isDragging = ref(false);
const error = ref("");
const fileInput = ref<HTMLInputElement | null>(null);

const acceptedTypesMessage = computed(() => {
  const types = props.allowedTypes
    .map((type) => type.replace("image/", "").toUpperCase())
    .join(", ");
  return `Accepted files: ${types} (Max. ${props.maxSize / (1024 * 1024)}MB)`;
});

const validateFile = (file: File): boolean => {
  if (!props.allowedTypes.includes(file.type)) {
    error.value = ERROR_MESSAGES.INVALID_TYPE;
    emits("error", ERROR_MESSAGES.INVALID_TYPE);
    return false;
  }

  if (file.size > props.maxSize) {
    error.value = ERROR_MESSAGES.SIZE_EXCEEDED;
    emits("error", ERROR_MESSAGES.SIZE_EXCEEDED);
    return false;
  }

  error.value = "";
  return true;
};

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    const file = target.files[0];
    if (validateFile(file)) {
      emits("Upload", file);
    }
  }
};

const onDrop = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = false;
  if (event.dataTransfer && event.dataTransfer.files.length > 0) {
    const file = event.dataTransfer.files[0];
    if (validateFile(file)) {
      emits("Upload", file);
    }
  }
};

const onDragOver = (event: DragEvent) => {
  event.preventDefault();
  isDragging.value = true;
};

const onDragLeave = () => (isDragging.value = false);

const removeImage = () => {
  if (props.disabled) return;
  if (fileInput.value) fileInput.value.value = "";
  emits("Upload", null);
};
</script>

<style scoped>
.scale-102 {
  transform: scale(1.02);
}
</style>
