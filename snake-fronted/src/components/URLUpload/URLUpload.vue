<template>
  <div class="w-full space-y-4 animated animated-duration-500 animated-fade-in">
    <div
      class="w-full bg-white rounded-lg shadow-sm transition-all duration-300"
    >
      <!-- URL Input Section -->
      <div class="p-4">
        <label
          for="url-input"
          class="block text-sm/6 font-medium text-gray-900 mb-2"
        >
          Image URL:
        </label>
        <div class="flex gap-3">
          <div class="flex-grow">
            <div
              class="flex rounded-md bg-white outline outline-1 -outline-offset-1 outline-gray-300 focus-within:outline focus-within:outline-2 focus-within:-outline-offset-2 focus-within:outline-custom-gradient-end"
            >
              <input
                id="url-input"
                v-model="urlInput"
                type="url"
                :placeholder="placeholder"
                :disabled="isLoading"
                @keypress.enter="handleUrlSubmit"
                class="block min-w-0 grow px-3 py-1.5 text-base text-gray-900 placeholder:text-gray-400 focus:outline focus:outline-0 sm:text-sm/6 disabled:bg-gray-50"
              />
              <div class="flex py-1.5 pr-1.5">
                <kbd
                  class="inline-flex items-center rounded border border-gray-200 px-1 font-sans text-xs text-gray-400"
                >
                  ↵
                </kbd>
              </div>
            </div>
          </div>

          <button
            @click="handleUrlSubmit"
            :disabled="isLoading || !isValidUrl"
            class="whitespace-nowrap w-auto inline-flex items-center justify-center px-4 py-2 gradient-primary text-white font-bold rounded-lg hover:shadow-lg transition-all duration-300 ease-out hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
          >
            <div v-if="isLoading" class="mr-2">
              <div
                class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
              ></div>
            </div>
            {{ isLoading ? "Processing..." : "Upload URL" }}
          </button>
        </div>
      </div>

      <!-- Status Message -->
      <TransitionGroup name="message" tag="div" class="px-4 pb-4">
        <div
          v-if="statusMessage"
          :key="statusMessage"
          :class="{
            'bg-success-100 text-success-300 border-success-200':
              uploadStatus === 'success',
            'bg-error-100 text-error-300 border-error-200':
              uploadStatus === 'error',
          }"
          class="p-3 rounded-lg border flex items-center gap-2 animated animated-faster animated-fade-in"
        >
          <i v-if="uploadStatus === 'success'" class="i-tabler:check size-5" />
          <i v-if="uploadStatus === 'error'" class="i-tabler:x size-5" />
          {{ statusMessage }}
        </div>
      </TransitionGroup>
    </div>

    <!-- Preview Section -->
    <Transition name="fade">
      <div
        v-if="imageUrl"
        class="relative bg-white rounded-lg shadow-lg overflow-hidden group animated animated-duration-500 animated-fade-in"
      >
        <img
          :src="imageUrl"
          alt="Uploaded Image"
          class="w-full h-64 object-cover transition-all duration-500 group-hover:scale-105"
          @error="handleImageError"
        />
        <button
          @click="removeImage"
          class="absolute top-2 right-2 bg-error-200 text-white w-8 h-8 rounded-full shadow-md hover:bg-error-100 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-error-200 focus:ring-offset-2 flex items-center justify-center transform hover:scale-110 active:scale-95 opacity-0 group-hover:opacity-100"
        >
          <i class="i-tabler:x size-5" />
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
.message-enter-active,
.message-leave-active {
  transition: all 0.3s ease;
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
