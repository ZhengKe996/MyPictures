<template>
  <div class="bg-gray-50">
    <main>
      <div class="relative isolate">
        <!-- 背景装饰 -->
        <div
          class="absolute inset-x-0 top-0 -z-10 h-[64rem] w-full stroke-gray-200 [mask-image:radial-gradient(32rem_32rem_at_center,white,transparent)]"
          aria-hidden="true"
        >
          <defs>
            <pattern
              id="1f932ae7-37de-4c0a-a8b0-a6e3b4d44b84"
              width="200"
              height="200"
              x="50%"
              y="-1"
              patternUnits="userSpaceOnUse"
            >
              <path d="M.5 200V.5H200" fill="none" />
            </pattern>
          </defs>
          <svg x="50%" y="-1" class="overflow-visible fill-gray-50">
            <path
              d="M-200 0h201v201h-201Z M600 0h201v201h-201Z M-400 600h201v201h-201Z M200 800h201v201h-201Z"
              stroke-width="0"
            />
          </svg>
          <rect
            width="100%"
            height="100%"
            stroke-width="0"
            fill="url(#1f932ae7-37de-4c0a-a8b0-a6e3b4d44b84)"
          />
        </div>

        <!-- 渐变背景 -->
        <div
          class="absolute inset-0 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end opacity-10"
        ></div>
        <div class="absolute inset-0 bg-white/30 backdrop-blur-[2px]"></div>

        <!-- 主要内容区域 -->
        <div class="relative overflow-hidden pb-32 pt-16">
          <div class="mx-auto max-w-7xl px-6 lg:px-8">
            <div
              class="mx-auto max-w-2xl gap-x-14 lg:mx-0 lg:flex lg:max-w-none lg:items-center"
            >
              <!-- 左侧文本区域 -->
              <div
                class="relative w-full lg:max-w-xl lg:shrink-0 xl:max-w-2xl animate-fade-in animate-duration-500"
              >
                <h1
                  class="text-pretty text-5xl font-semibold tracking-tight text-gray-900 sm:text-7xl"
                >
                  {{ heroContent.title }}
                </h1>
                <p
                  class="mt-8 text-pretty text-lg font-medium text-gray-600 sm:max-w-md sm:text-xl/8 lg:max-w-none"
                >
                  {{ heroContent.description }}
                </p>
                <div class="mt-10 flex items-center gap-x-6">
                  <a
                    v-for="button in heroContent.buttons"
                    :key="button.text"
                    :href="button.href"
                    :class="button.class"
                  >
                    {{ button.text }}
                    <span v-if="button.arrow" aria-hidden="true">→</span>
                  </a>
                </div>
              </div>

              <!-- 图片网格区域 -->
              <div
                class="mt-14 flex justify-end gap-8 sm:-mt-44 sm:justify-start sm:pl-20 lg:mt-0 lg:pl-0"
              >
                <template v-for="(column, _) in gridImages" :key="index">
                  <div :class="column.containerClass">
                    <div
                      v-for="(image, imageIndex) in column.images"
                      :key="imageIndex"
                      class="relative group"
                    >
                      <img
                        :src="image.src"
                        :alt="image.alt"
                        class="aspect-[2/3] w-full rounded-xl bg-gray-900/5 object-cover shadow-lg transition duration-300 group-hover:scale-105"
                      />
                      <div
                        class="pointer-events-none absolute inset-0 rounded-xl ring-1 ring-inset ring-gray-900/10"
                      />
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- categories -->
      <div class="bg-gray-50">
        <div class="mx-auto max-w-7xl px-4 py-16 sm:px-6 sm:py-24 lg:px-8">
          <div class="sm:flex sm:items-baseline sm:justify-between">
            <h2 class="text-2xl font-bold tracking-tight text-gray-900">
              Image by Category
            </h2>
          </div>

          <div
            class="mt-6 grid grid-cols-1 gap-y-6 sm:grid-cols-2 sm:grid-rows-2 sm:gap-x-6 lg:gap-8"
          >
            <div
              v-for="category in categories"
              :key="category.id"
              :class="[
                'group relative overflow-hidden rounded-lg',
                category.featured
                  ? 'aspect-[2/1] sm:row-span-2 sm:aspect-square'
                  : 'aspect-[2/1] sm:aspect-auto',
              ]"
            >
              <img
                :src="category.imageSrc"
                :alt="category.imageAlt"
                class="absolute size-full object-cover group-hover:opacity-75"
              />
              <div
                aria-hidden="true"
                class="absolute inset-0 bg-gradient-to-b from-transparent to-black opacity-50"
              />
              <div class="absolute inset-0 flex items-end p-6">
                <div>
                  <h3 class="font-semibold text-white">
                    <a :href="category.href">
                      <span class="absolute inset-0" />
                      {{ category.title }}
                    </a>
                  </h3>
                  <p aria-hidden="true" class="mt-1 text-sm text-white">
                    {{ category.description }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 sm:hidden">
            <a
              href="#"
              class="block text-sm font-semibold text-indigo-600 hover:text-indigo-500"
            >
              Browse all categories
              <span aria-hidden="true"> &rarr;</span>
            </a>
          </div>
        </div>
      </div>

      <!-- 推荐区域 -->
      <div class="bg-white">
        <div class="px-6 py-24 sm:py-32 lg:px-8">
          <div class="mx-auto max-w-2xl text-center">
            <h2
              class="text-5xl font-semibold tracking-tight text-gray-900 sm:text-7xl"
            >
              About Us
            </h2>
            <p
              class="mt-8 text-pretty text-lg font-medium text-gray-500 sm:text-xl/8"
            >
              Anim aute id magna aliqua ad ad non deserunt sunt. Qui irure qui
              lorem cupidatat commodo. Elit sunt amet fugiat veniam occaecat
              fugiat.
            </p>
          </div>
        </div>

        <div class="relative py-24 sm:py-32">
          <div class="mx-auto max-w-7xl px-6 lg:px-8">
            <div
              class="overflow-hidden rounded-3xl bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end shadow-2xl"
            >
              <div
                class="mx-auto max-w-7xl px-6 pb-24 pt-16 sm:pb-32 lg:grid lg:grid-cols-2 lg:gap-x-8 lg:px-8 lg:pt-24"
              >
                <!-- 左侧用户信息 -->
                <div class="flex flex-col items-start gap-y-10 lg:pt-4">
                  <div class="flex items-center gap-x-6">
                    <div
                      class="h-24 w-24 sm:h-32 sm:w-32 rounded-full ring-4 ring-white/20 overflow-hidden"
                    >
                      <img
                        :src="featuredContent.image.src"
                        :alt="featuredContent.image.alt"
                        class="h-full w-full object-cover transition duration-300 hover:scale-105"
                      />
                    </div>
                    <div>
                      <h2
                        class="text-2xl sm:text-3xl font-bold tracking-tight text-white"
                      >
                        {{ featuredContent.author.name }}
                      </h2>
                      <p class="text-lg leading-6 text-gray-200">
                        @{{ featuredContent.author.login }}
                      </p>
                    </div>
                  </div>

                  <div class="space-y-6 text-white">
                    <div class="flex items-center gap-x-2">
                      <svg
                        class="h-6 w-6 text-gray-200"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke-width="1.5"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z"
                        />
                      </svg>
                      <span class="text-lg">{{
                        featuredContent.author.location
                      }}</span>
                    </div>

                    <div
                      class="grid grid-cols-3 gap-8 py-8 border-y border-white/20"
                    >
                      <div class="text-center">
                        <div class="text-3xl font-bold">
                          {{ featuredContent.stats.repos }}
                        </div>
                        <div class="mt-1 text-sm text-gray-200">
                          Repositories
                        </div>
                      </div>
                      <div class="text-center">
                        <div class="text-3xl font-bold">
                          {{ featuredContent.stats.followers }}
                        </div>
                        <div class="mt-1 text-sm text-gray-200">Followers</div>
                      </div>
                      <div class="text-center">
                        <div class="text-3xl font-bold">
                          {{ featuredContent.stats.following }}
                        </div>
                        <div class="mt-1 text-sm text-gray-200">Following</div>
                      </div>
                    </div>

                    <div class="flex gap-x-4">
                      <a
                        @click.prevent="
                          openInNewTab(
                            `https://github.com/${featuredContent.author.login}`
                          )
                        "
                        href="#"
                        class="inline-flex items-center rounded-lg bg-white px-4 py-2 text-sm font-semibold text-gray-900 shadow-sm hover:bg-gray-100 transition-colors duration-200"
                      >
                        <svg
                          class="mr-2 h-5 w-5"
                          fill="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path
                            d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
                          />
                        </svg>
                        View Profile
                      </a>
                      <button
                        class="inline-flex items-center rounded-lg bg-white/10 px-4 py-2 text-sm font-semibold text-white hover:bg-white/20 transition-colors duration-200"
                      >
                        Share Profile
                      </button>
                    </div>
                  </div>
                </div>

                <!-- 右侧仓库预览 -->
                <div class="mt-20 sm:mt-24 lg:mt-0 lg:flex lg:items-center">
                  <div
                    class="relative isolate overflow-hidden rounded-2xl bg-white/5 p-6 shadow-2xl backdrop-blur-sm w-full"
                  >
                    <div
                      class="absolute inset-0 bg-gradient-to-br from-white/5"
                    ></div>
                    <div class="relative space-y-6">
                      <!-- 标题部分 -->
                      <div class="flex items-center justify-between">
                        <div class="flex items-center gap-x-4">
                          <div class="h-12 w-12 rounded-full bg-white/10 p-2">
                            <svg
                              class="h-8 w-8 text-white"
                              fill="none"
                              viewBox="0 0 24 24"
                              stroke-width="1.5"
                              stroke="currentColor"
                            >
                              <path
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z"
                              />
                            </svg>
                          </div>
                          <div>
                            <p class="text-sm font-medium text-white">
                              Popular Repositories
                            </p>
                            <p class="text-sm text-gray-300">
                              Top starred projects
                            </p>
                          </div>
                        </div>
                      </div>

                      <!-- 仓库列表 -->
                      <div class="space-y-4">
                        <div
                          v-for="repo in topRepositories"
                          :key="repo.id"
                          class="group relative isolate overflow-hidden rounded-lg backdrop-blur-md transition-all duration-300"
                          @click="openInNewTab(repo.html_url)"
                        >
                          <!-- 背景渐变效果 -->
                          <div
                            class="absolute inset-0 bg-gradient-to-r from-white/[0.07] to-white/[0.03] opacity-80"
                          ></div>
                          <!-- 悬停时的光效 -->
                          <div
                            class="absolute inset-0 bg-gradient-to-r from-custom-gradient-start/20 to-custom-gradient-end/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                          ></div>

                          <div class="relative p-5 space-y-4">
                            <!-- 仓库标题和星标 -->
                            <div class="flex items-center justify-between">
                              <div class="flex items-center gap-x-3">
                                <div
                                  class="p-2 rounded-full bg-white/10 backdrop-blur-lg"
                                >
                                  <svg
                                    class="h-5 w-5 text-custom-gradient-start"
                                    fill="currentColor"
                                    viewBox="0 0 24 24"
                                  >
                                    <path
                                      d="M12.672.668a.75.75 0 00-1.345 0L8.27 6.865l-6.838.994a.75.75 0 00-.416 1.279l4.948 4.823-1.168 6.811a.75.75 0 001.088.791L12 18.347l6.117 3.216a.75.75 0 001.088-.79l-1.168-6.812 4.948-4.823a.75.75 0 00-.416-1.28l-6.838-.993L12.672.668z"
                                    />
                                  </svg>
                                </div>
                                <div>
                                  <h3
                                    class="text-lg font-semibold text-white group-hover:text-custom-gradient-start transition-colors duration-300"
                                  >
                                    {{ repo.name }}
                                  </h3>
                                </div>
                              </div>
                              <div
                                class="flex items-center gap-x-2 bg-white/10 rounded-full px-3 py-1 backdrop-blur-lg"
                              >
                                <span class="text-sm font-medium text-white">{{
                                  repo.stargazers_count
                                }}</span>
                                <svg
                                  class="h-4 w-4 text-yellow-400"
                                  viewBox="0 0 20 20"
                                  fill="currentColor"
                                >
                                  <path
                                    d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
                                  />
                                </svg>
                              </div>
                            </div>

                            <!-- 仓库描述 -->
                            <p class="text-base text-gray-100 line-clamp-2">
                              {{
                                repo.description || "No description provided"
                              }}
                            </p>

                            <!-- 底部信息 -->
                            <div class="flex items-center justify-between pt-2">
                              <div class="flex items-center gap-x-3">
                                <span
                                  v-if="repo.language"
                                  class="flex items-center gap-x-2 bg-white/10 rounded-full px-3 py-1 backdrop-blur-lg"
                                >
                                  <span
                                    class="h-2 w-2 rounded-full"
                                    :class="getLanguageColor(repo.language)"
                                  ></span>
                                  <span class="text-sm text-gray-100">{{
                                    repo.language
                                  }}</span>
                                </span>
                              </div>
                              <span
                                class="text-sm text-gray-200 bg-white/10 rounded-full px-3 py-1 backdrop-blur-lg"
                              >
                                Updated {{ formatDate(repo.updated_at) }}
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, computed } from "vue";
import { GetPictureList, GetGithubUser, GetUserRepos } from "@/services";
import { GitHubUserName, type PictureType } from "@/config";
import type { GithubRepo } from "@/services/github";

interface Button {
  text: string;
  href: string;
  class: string;
  arrow?: boolean;
}

interface HeroContent {
  title: string;
  description: string;
  buttons: Button[];
}

interface Image {
  src: string;
  alt: string;
}

interface ImageColumn {
  containerClass: string;
  images: Image[];
}

interface FeaturedContent {
  image: Image;
  author: {
    name: string;
    login: string;
    location: string;
  };
  stats: {
    repos: number;
    followers: number;
    following: number;
  };
}

interface Category {
  id: number;
  title: string;
  description: string;
  href: string;
  imageSrc: string;
  imageAlt: string;
  featured?: boolean;
}

const heroContent = ref<HeroContent>({
  title: "Intelligent Cloud Gallery",
  description:
    "Based on Vue 3 +Type Script + Spring Boot + COS intelligent collaborative cloud image library platform.",
  buttons: [
    {
      text: "Get started",
      href: "/",
      class:
        "rounded-md bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:opacity-90 transition-opacity duration-200",
    },
    {
      text: "Login",
      href: "/user/login",
      class:
        "text-sm font-semibold text-gray-900 hover:text-custom-gradient-end transition-colors duration-200",
      arrow: true,
    },
  ],
});

const gridImages = ref<ImageColumn[]>([
  {
    containerClass:
      "ml-auto w-44 flex-none space-y-8 pt-32 sm:ml-0 sm:pt-80 lg:order-last lg:pt-36 xl:order-none xl:pt-80",
    images: [
      {
        src: "https://images.unsplash.com/photo-1557804506-669a67965ba0?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&h=528&q=80",
        alt: "Gallery image 1",
      },
    ],
  },
  {
    containerClass:
      "mr-auto w-44 flex-none space-y-8 sm:mr-0 sm:pt-52 lg:pt-36",
    images: [
      {
        src: "https://images.unsplash.com/photo-1485217988980-11786ced9454?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&h=528&q=80",
        alt: "Gallery image 2",
      },
      {
        src: "https://images.unsplash.com/photo-1559136555-9303baea8ebd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&crop=focalpoint&fp-x=.4&w=396&h=528&q=80",
        alt: "Gallery image 3",
      },
    ],
  },
  {
    containerClass: "w-44 flex-none space-y-8 pt-32 sm:pt-0",
    images: [
      {
        src: "https://images.unsplash.com/photo-1670272504528-790c24957dda?ixlib=rb-4.0.3&ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&crop=left&w=400&h=528&q=80",
        alt: "Gallery image 4",
      },
      {
        src: "https://images.unsplash.com/photo-1670272505284-8faba1c31f7d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&h=528&q=80",
        alt: "Gallery image 5",
      },
    ],
  },
]);

const featuredContent = ref<FeaturedContent>({
  image: {
    src: "",
    alt: "",
  },
  author: {
    name: "",
    login: "",
    location: "",
  },
  stats: {
    repos: 0,
    followers: 0,
    following: 0,
  },
});

const categories = ref<Category[]>([
  {
    id: 1,
    title: "New Arrivals",
    description: "",
    href: "#",
    imageSrc:
      "https://images.pexels.com/photos/773294/pexels-photo-773294.jpeg",
    imageAlt: "",
    featured: true,
  },
  {
    id: 2,
    title: "",
    description: "",
    href: "#",
    imageSrc:
      "https://images.pexels.com/photos/9522666/pexels-photo-9522666.jpeg",
    imageAlt: "",
  },
  {
    id: 3,
    title: "Workspace",
    description: "Shop now",
    href: "#",
    imageSrc:
      "https://images.pexels.com/photos/7161189/pexels-photo-7161189.jpeg",
    imageAlt: "",
  },
]);

// 添加repositories相关的响应式变量
const repositories = ref<GithubRepo[]>([]);

// 计算属性：获取排序后的热门仓库
const topRepositories = computed(() => {
  return repositories.value
    .filter((repo) => !repo.fork) // 过滤掉fork的仓库
    .sort((a, b) => b.stargazers_count - a.stargazers_count) // 按星标数排序
    .slice(0, 3); // 只取前3个
});

// 格式化日期的函数
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return new Intl.RelativeTimeFormat("en", { numeric: "auto" }).format(
    Math.ceil((date.getTime() - Date.now()) / (1000 * 60 * 60 * 24)),
    "day"
  );
};

const LoadPictureList = async () => {
  const { data, code, message } = await GetPictureList({
    pageSize: 10,
    current: 1,
  });
  if (code === 0 && data) {
    const apiImages = data.records.map((record: PictureType) => ({
      src: record.url,
      alt: record.name,
    }));

    if (apiImages.length >= 5) {
      // 前5张图片用于gridImages
      gridImages.value[0].images = [apiImages[0]];
      gridImages.value[1].images = [apiImages[1], apiImages[2]];
      gridImages.value[2].images = [apiImages[3], apiImages[4]];

      // 后5张图片用于categories
      categories.value = [
        {
          id: 1,
          title: "Featured Gallery",
          description: "View gallery",
          href: "#",
          imageSrc: apiImages[5].src,
          imageAlt: apiImages[5].alt,
          featured: true,
        },
        {
          id: 2,
          title: "Popular Photos",
          description: "Browse collection",
          href: "#",
          imageSrc: apiImages[6].src,
          imageAlt: apiImages[6].alt,
        },
        {
          id: 3,
          title: "New Uploads",
          description: "Latest additions",
          href: "#",
          imageSrc: apiImages[7].src,
          imageAlt: apiImages[7].alt,
        },
      ];
    }
  } else throw new Error(message);
};

const LoadGithubUserInfo = async () => {
  const {
    login,
    avatar_url,
    name,
    location,
    public_repos,
    followers,
    following,
  } = await GetGithubUser(GitHubUserName);

  featuredContent.value = {
    image: {
      src: avatar_url,
      alt: login,
    },
    author: {
      name: name || login,
      login,
      location: location || "Location not specified",
    },
    stats: {
      repos: public_repos,
      followers,
      following,
    },
  };
};
const LoadGithubRepos = async () => {
  try {
    const repos = await GetUserRepos(GitHubUserName);
    repositories.value = repos;
  } catch (error) {
    console.error("Failed to load repositories:", error);
  }
};

// 使用VueUse的window API
// 创建打开链接的函数
const openInNewTab = (url: string) => {
  window.open(url, "_blank");
};

// 添加编程语言颜色映射函数
const getLanguageColor = (language: string) => {
  const colors: { [key: string]: string } = {
    JavaScript: "bg-yellow-400",
    TypeScript: "bg-blue-400",
    Vue: "bg-emerald-400",
    Python: "bg-blue-500",
    Java: "bg-orange-500",
    "C++": "bg-pink-500",
    PHP: "bg-indigo-400",
    Ruby: "bg-red-500",
    Go: "bg-cyan-400",
    Rust: "bg-orange-600",
    default: "bg-gray-400",
  };
  return colors[language] || colors.default;
};

onMounted(() => nextTick(() => LoadPictureList()));
onMounted(() => nextTick(() => LoadGithubUserInfo()));
onMounted(() => nextTick(() => LoadGithubRepos()));
</script>

<style scoped>
.text-pretty {
  text-wrap: pretty;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 添加卡片悬停动画 */
.group:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}
</style>
