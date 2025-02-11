<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部背景区域 -->
    <div class="relative overflow-hidden pb-32">
      <div
        class="absolute inset-0 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end"
      ></div>
      <div class="absolute inset-0 bg-white/30 backdrop-blur-[2px]"></div>
      <div class="relative py-10">
        <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h1 class="text-3xl font-bold tracking-tight text-white">About Me</h1>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <main class="relative -mt-32">
      <div class="mx-auto max-w-screen-xl px-4 pb-6 sm:px-6 lg:px-8 lg:pb-16">
        <div
          class="overflow-hidden rounded-lg bg-white shadow animate-fade-in animate-duration-500"
        >
          <div class="bg-white py-12 sm:py-16">
            <div
              class="mx-auto grid max-w-7xl gap-x-8 gap-y-20 px-6 lg:px-8 xl:grid-cols-3"
            >
              <!-- 左侧个人信息 -->
              <div class="xl:col-span-1">
                <div class="flex flex-col items-center space-y-6">
                  <!-- 头像区域 -->
                  <div class="relative group">
                    <div
                      class="size-48 overflow-hidden rounded-2xl ring-4 ring-white shadow-lg transition duration-300 ease-in-out group-hover:shadow-xl"
                    >
                      <img
                        :src="userData.userAvatar || '/default-avatar.png'"
                        class="size-full object-cover transition duration-300 ease-in-out group-hover:scale-105"
                        alt="Profile photo"
                      />
                    </div>
                  </div>

                  <!-- 用户名和简介 -->
                  <div class="text-center">
                    <h2
                      class="mt-4 text-2xl font-semibold tracking-tight text-gray-900"
                    >
                      {{ userData.userName }}
                    </h2>
                    <p class="mt-2 text-gray-600 text-pretty">
                      {{
                        userData.userProfile || "No profile description yet."
                      }}
                    </p>
                  </div>

                  <!-- 统计数据 -->
                  <div
                    class="grid grid-cols-3 w-full gap-4 pt-6 border-t border-gray-100"
                  >
                    <div class="text-center">
                      <div class="text-2xl font-bold text-custom-gradient-end">
                        {{ userData.imageCount }}
                      </div>
                      <div class="mt-1 text-sm text-gray-500">Images</div>
                    </div>
                    <div class="text-center">
                      <div class="text-2xl font-bold text-custom-gradient-end">
                        {{ userData.favoritesCount }}
                      </div>
                      <div class="mt-1 text-sm text-gray-500">Favorites</div>
                    </div>
                    <div class="text-center">
                      <div class="text-2xl font-bold text-custom-gradient-end">
                        {{ userData.followersCount }}
                      </div>
                      <div class="mt-1 text-sm text-gray-500">Followers</div>
                    </div>
                  </div>

                  <!-- 社交链接 -->
                  <div class="flex gap-4 pt-6">
                    <a
                      v-for="social in socials"
                      :key="social.name"
                      :href="social.href"
                      class="text-gray-400 hover:text-custom-gradient-end transition-colors duration-200"
                      :title="social.name"
                    >
                      <i :class="['text-xl', social.icon]"></i>
                    </a>
                  </div>
                </div>
              </div>

              <!-- 右侧内容区域 -->
              <div class="xl:col-span-2">
                <!-- 最近活动标题 -->
                <div class="border-b border-gray-200 pb-5">
                  <h3 class="text-2xl font-semibold leading-7 text-gray-900">
                    Recent Activity
                  </h3>
                  <p class="mt-2 text-sm text-gray-500">
                    A summary of your recent actions and achievements.
                  </p>
                </div>

                <!-- 活动列表 -->
                <ul role="list" class="divide-y divide-gray-100 mt-8">
                  <li
                    v-for="activity in recentActivities"
                    :key="activity.id"
                    class="group relative flex gap-6 py-6 hover:bg-gray-50 transition-colors duration-200 rounded-lg px-4"
                  >
                    <div class="relative size-20 flex-none">
                      <img
                        :src="activity.imageUrl"
                        class="size-full rounded-lg object-cover shadow-sm ring-1 ring-gray-200 transition duration-300 group-hover:shadow-md"
                        alt=""
                      />
                    </div>
                    <div class="flex-auto">
                      <div class="flex items-baseline justify-between gap-4">
                        <p class="text-sm font-medium text-gray-900">
                          {{ activity.description }}
                        </p>
                        <p class="text-xs text-gray-500 whitespace-nowrap">
                          {{ activity.time }}
                        </p>
                      </div>
                      <p class="mt-1 text-sm text-gray-500 line-clamp-1">
                        {{ activity.details }}
                      </p>
                    </div>
                    <!-- 悬浮时显示的箭头 -->
                    <div
                      class="absolute right-4 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                    >
                      <i class="i-tabler-chevron-right text-gray-400"></i>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { GetLoginInfoUser } from "@/services";
import { DefaultUserAvatar } from "@/config";
import { Message } from "@/lib/Message";

// 用户数据
const userData = ref({
  userName: "",
  userProfile: "",
  userAvatar: DefaultUserAvatar,
  imageCount: 0,
  favoritesCount: 0,
  followersCount: 0,
});

// 添加社交媒体链接配置
const socials = [
  {
    name: "Twitter",
    href: "#",
    icon: "i-tabler-brand-twitter",
  },
  {
    name: "GitHub",
    href: "#",
    icon: "i-tabler-brand-github",
  },
  {
    name: "Instagram",
    href: "#",
    icon: "i-tabler-brand-instagram",
  },
];

// 扩展活动数据结构
const recentActivities = ref([
  {
    id: 1,
    imageUrl: "https://picsum.photos/200",
    description: "Uploaded a new landscape photo",
    details: "A beautiful sunset captured at the mountain peak",
    time: "2 hours ago",
  },
  {
    id: 2,
    imageUrl: "https://picsum.photos/201",
    description: "Won Daily Spotlight",
    details: "Your photo 'Morning Mist' was selected as today's spotlight",
    time: "4 hours ago",
  },
  {
    id: 3,
    imageUrl: "https://picsum.photos/202",
    description: "Added to Featured Collection",
    details: "Your work has been added to the 'Best of 2024' collection",
    time: "1 day ago",
  },
]);

const initUserData = async () => {
  try {
    const { data, message, code } = await GetLoginInfoUser();
    if (code === 0 && data) {
      userData.value = {
        userName: data.userName ?? "",
        userProfile: data.userProfile ?? "",
        userAvatar: data.userAvatar ?? DefaultUserAvatar,
        imageCount: 0, // TODO: Add these fields to API response
        favoritesCount: 0,
        followersCount: 0,
      };
    } else throw new Error(message);
  } catch (error) {
    console.error("Error fetching user data:", error);
    Message.error("Failed to fetch user data");
  }
};

onMounted(async () => {
  await initUserData();
});
</script>

<style scoped>
.text-pretty {
  text-wrap: pretty;
}
</style>
