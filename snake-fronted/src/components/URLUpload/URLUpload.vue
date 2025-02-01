<template>
  <div class="w-full space-y-4">
    <div
      class="relative bg-white rounded-lg shadow-sm p-4 border border-gray-200 transition-all duration-300 hover:shadow-md"
    >
      <div class="flex flex-col gap-2">
        <label for="url-input" class="text-sm font-medium text-gray-700">
          Image URL
        </label>
        <div class="flex gap-2">
          <div class="flex-1 relative">
            <input
              id="url-input"
              v-model="urlInput"
              type="url"
              :placeholder="placeholder"
              @keyup.enter="handleUrlSubmit"
              :disabled="isLoading"
              class="w-full p-3 pr-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 transition-all duration-300"
            />
            <div
              v-if="isLoading"
              class="absolute right-3 top-1/2 -translate-y-1/2"
            >
              <div
                class="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"
              ></div>
            </div>
          </div>
          <button
            @click="handleUrlSubmit"
            :disabled="isLoading || !isValidUrl"
            class="px-6 py-3 bg-blue-500 text-white rounded-lg transition-all duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed transform hover:scale-105 active:scale-95 hover:bg-blue-600 disabled:hover:scale-100 shadow-md hover:shadow-lg"
          >
            <div class="flex items-center gap-2">
              <span v-if="isLoading" class="inline-block">
                <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                    fill="none"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                  />
                </svg>
              </span>
              <span>{{ isLoading ? "Processing..." : "Upload URL" }}</span>
            </div>
          </button>
        </div>
      </div>

      <TransitionGroup name="fade" tag="div" class="mt-3">
        <div
          v-if="statusMessage"
          :key="statusMessage"
          :class="{
            'bg-green-50 text-green-700 border-green-200':
              uploadStatus === 'success',
            'bg-red-50 text-red-700 border-red-200': uploadStatus === 'error',
          }"
          class="p-3 rounded-lg border flex items-center gap-2 animate-fadeIn"
        >
          <svg
            v-if="uploadStatus === 'success'"
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
            />
          </svg>
          <svg
            v-if="uploadStatus === 'error'"
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
          {{ statusMessage }}
        </div>
      </TransitionGroup>
    </div>

    <Transition name="fade">
      <div
        v-if="imageUrl"
        class="relative bg-white rounded-lg shadow-lg overflow-hidden group"
      >
        <img
          :src="imageUrl"
          alt="Uploaded Image"
          class="w-full h-64 object-cover transition-all duration-500 group-hover:scale-105"
          @error="handleImageError"
        />
        <button
          @click="removeImage"
          class="absolute top-2 right-2 bg-red-500 text-white w-8 h-8 rounded-full shadow-md hover:bg-red-600 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 flex items-center justify-center transform hover:scale-110 active:scale-95 opacity-0 group-hover:opacity-100"
        >
          ×
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { DefaultImage } from "@/config";

interface Props {
  imageUrl: string;
  isLoading?: boolean;
  placeholder?: string;
}

interface Emits {
  (e: "update:imageUrl", url: string): void;
  (e: "upload", url: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  isLoading: false,
  placeholder: "",
});

const emit = defineEmits<Emits>();

const urlInput = ref("");
const statusMessage = ref("");
const uploadStatus = ref<"success" | "error" | "">("");

const isValidUrl = computed(() => {
  try {
    new URL(urlInput.value);
    return true;
  } catch {
    return false;
  }
});

const showStatus = (message: string, status: "success" | "error") => {
  statusMessage.value = message;
  uploadStatus.value = status;
  setTimeout(() => {
    statusMessage.value = "";
    uploadStatus.value = "";
  }, 3000);
};

const handleUrlSubmit = async () => {
  if (!isValidUrl.value || props.isLoading) return;

  try {
    emit("upload", urlInput.value);
    showStatus("URL submitted successfully", "success");
    urlInput.value = "";
  } catch (error) {
    showStatus("Failed to process URL", "error");
  }
};

const handleImageError = () => {
  showStatus("Failed to load image", "error");
  emit("update:imageUrl", DefaultImage);
};

const removeImage = () => {
  emit("update:imageUrl", "");
  showStatus("Image removed", "success");
};

defineExpose({
  showStatus,
  clearInput: () => (urlInput.value = ""),
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
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

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
</style>
