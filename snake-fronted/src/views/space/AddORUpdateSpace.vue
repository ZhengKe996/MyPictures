<template>
  <div
    class="min-h-[640px] flex items-center justify-center bg-gray-100 animate-fade-in animate-duration-500"
  >
    <div
      class="max-w-2xl w-full mx-4 max-h-[640px] bg-white rounded-xl shadow-lg transform transition-all duration-300 hover:shadow-xl border border-gray-200"
    >
      <div class="p-6">
        <h2
          class="text-2xl font-bold text-center text-gray-800 mb-6 gradient-primary bg-clip-text text-transparent"
        >
          {{ isUpdateMode ? "Update Space" : "Create New Space" }}
        </h2>

        <div class="space-y-6">
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">Space Name</label>
            <input
              v-model="spaceName"
              type="text"
              placeholder="Enter your space name"
              class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-custom-gradient-end transition-all duration-200 hover:border-custom-gradient-start"
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
                class="p-4 border-2 rounded-lg cursor-pointer transition-all duration-200 hover:shadow-md group"
                :class="
                  spaceLevel === level.level
                    ? 'border-custom-gradient-end gradient-primary/10'
                    : 'border-gray-200 hover:border-custom-gradient-start'
                "
              >
                <div class="flex justify-between items-center mb-2">
                  <h3 class="font-semibold text-gray-800">{{ level.name }}</h3>
                  <span
                    class="text-sm text-gray-500 group-hover:text-custom-gradient-end transition-colors"
                  >
                    Level {{ level.level }}
                  </span>
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
            class="gradient-primary text-white hover:shadow-lg transition-all duration-300 ease-out hover:scale-105"
          >
            {{ isUpdateMode ? "Update Space" : "Create Space" }}
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from "vue";
import Button from "@/lib/Button";
import { AddORUpdateSpace } from "@/services";
import { Message } from "@/lib/Message";
import { useRoute, useRouter } from "vue-router";
import { GetSpaceInfoById } from "@/services";

const route = useRoute();
const router = useRouter();
const { id } = defineProps<{
  id?: string;
}>();

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

/**
 * 提交表单的异步函数
 * 该函数处理表单提交逻辑，包括验证表单和提交数据
 */
const handleSubmit = async () => {
  // 获取路由参数中的id，如果是数组则取第一个元素
  const routeId = Array.isArray(route.params.id)
    ? route.params.id[0]
    : route.params.id;

  // 如果表单验证通过，则执行添加或更新空间的操作
  if (validateForm()) {
    // 调用AddORUpdateSpace函数，传递必要的空间信息
    const { data, code, message } = await AddORUpdateSpace({
      id: routeId,
      spaceName: spaceName.value,
      spaceLevel: spaceLevel.value,
      maxSize: spaceLevels[spaceLevel.value].maxStorage,
      maxCount: spaceLevels[spaceLevel.value].maxImages,
    });

    // 根据返回的code和data，显示相应的成功或错误消息
    if (code === 0 && data) Message.success("Space created successfully");
    else Message.error(message || "Failed to create space");
    router.push("/mamager/space");
  }
};

const isButtonDisabled = computed(() => {
  return !spaceName.value.trim();
});

// UPDATE
const isUpdateMode = computed(() => route.path.includes("update"));
const getOldSpaceInfo = async (id: string) => {
  const { data, code, message } = await GetSpaceInfoById(id);
  if (code === 0 && data) {
    if (data.spaceName) {
      spaceName.value = data.spaceName;
    }
    spaceLevel.value = data.spaceLevel ?? 0;
  } else {
    Message.error(message || "Failed to get space info");
  }
};
const resetForm = () => {
  spaceName.value = "";
  spaceLevel.value = 0;
};
onMounted(() => {
  nextTick(() => {
    if (isUpdateMode && id) {
      if (typeof id === "string") {
        getOldSpaceInfo(id);
      } else {
        Message.error("无效的图片ID");
      }
    } else resetForm();
  });
});
</script>
