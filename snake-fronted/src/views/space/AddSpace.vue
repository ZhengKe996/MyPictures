<template>
  <div class="min-h-[700px] flex items-center justify-center bg-gray-100">
    <div
      class="max-w-2xl w-full mx-4 max-h-[700px] bg-white rounded-xl shadow-lg transform transition-all duration-300 hover:shadow-xl"
    >
      <div class="p-6">
        <h2 class="text-2xl font-bold text-center text-gray-800 mb-6">
          Create New Space
        </h2>

        <div class="space-y-6">
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">Space Name</label>
            <input
              v-model="spaceName"
              type="text"
              placeholder="Enter your space name"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none hover:bg-white"
              :class="{ 'border-red-500': errors.spaceName }"
            />
            <p v-if="errors.spaceName" class="text-red-500 text-xs mt-1">
              {{ errors.spaceName }}
            </p>
          </div>

          <div class="space-y-4">
            <label class="text-sm font-medium text-gray-700">Space Level</label>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div
                v-for="level in spaceLevels"
                :key="level.level"
                @click="spaceLevel = level.level"
                class="p-4 border-2 rounded-lg cursor-pointer transition-all duration-200 hover:shadow-md"
                :class="
                  spaceLevel === level.level
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 hover:border-blue-300'
                "
              >
                <div class="flex justify-between items-center mb-2">
                  <h3 class="font-semibold text-gray-800">{{ level.name }}</h3>
                  <span class="text-sm text-gray-500"
                    >Level {{ level.level }}</span
                  >
                </div>
                <div class="space-y-2 text-sm text-gray-600">
                  <p class="flex items-center">
                    <span class="mr-2">📸</span>
                    Max Images: {{ level.maxImages }}
                  </p>
                  <p class="flex items-center">
                    <span class="mr-2">💾</span>
                    Max Storage: {{ formatStorage(level.maxStorage) }}
                  </p>
                </div>
              </div>
            </div>
            <p v-if="errors.spaceLevel" class="text-red-500 text-xs mt-1">
              {{ errors.spaceLevel }}
            </p>
          </div>

          <Button
            type="primary"
            size="lg"
            block
            :disabled="isButtonDisabled"
            @click="handleSubmit"
          >
            Create Space
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import Button from "@/lib/Button";
import { AddSpace } from "@/services";
import { Message } from "@/lib/Message";

interface SpaceLevel {
  name: string;
  level: number;
  maxImages: number;
  maxStorage: number;
}

const spaceLevels: SpaceLevel[] = [
  {
    name: "普通版",
    level: 0,
    maxImages: 100,
    maxStorage: 104857600,
  },
  {
    name: "专业版",
    level: 1,
    maxImages: 1000,
    maxStorage: 1048576000,
  },
  {
    name: "旗舰版",
    level: 2,
    maxImages: 10000,
    maxStorage: 10485760000,
  },
];

const spaceName = ref("");
const spaceLevel = ref<number>(0);
const errors = reactive({
  spaceName: "",
  spaceLevel: "",
});

/**
 * 格式化存储容量函数
 * 将给定的字节数转换为更易读的格式，返回一个字符串表示的存储容量
 * 如果容量大于等于1GB，则以GB为单位表示；否则以MB为单位表示
 *
 * @param bytes {number} - 需要格式化的字节数
 * @returns {string} - 格式化后的存储容量字符串
 */
const formatStorage = (bytes: number): string => {
  // 将字节数转换为GB
  const gb = bytes / (1024 * 1024 * 1024);
  // 如果转换后的GB值大于等于1，则使用GB作为单位
  if (gb >= 1) {
    return `${gb.toFixed(1)} GB`;
  }
  // 如果GB值小于1，则将字节数转换为MB
  const mb = bytes / (1024 * 1024);
  // 使用MB作为单位，并返回格式化后的字符串
  return `${mb.toFixed(0)} MB`;
};

const validateForm = (): boolean => {
  let isValid = true;
  errors.spaceName = "";
  errors.spaceLevel = "";

  if (!spaceName.value.trim()) {
    errors.spaceName = "Space name is required";
    isValid = false;
  }

  if (spaceLevel.value < 0 || spaceLevel.value > 2) {
    errors.spaceLevel = "Please select a valid space level";
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (validateForm()) {
    const { data, code, message } = await AddSpace({
      spaceName: spaceName.value,
      spaceLevel: spaceLevel.value,
      maxSize: spaceLevels[spaceLevel.value].maxStorage,
      maxCount: spaceLevels[spaceLevel.value].maxImages,
    });
    if (code === 0 && data) Message.success("Space created successfully");
    else Message.error(message || "Failed to create space");
  }
};

const isButtonDisabled = computed(() => {
  return !spaceName.value.trim();
});
</script>
