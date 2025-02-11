<template>
  <div class="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-48 lg:flex-col">
    <div
      class="flex grow flex-col gap-y-5 overflow-y-auto border-r border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 px-6 pb-4 transition-colors duration-300"
    >
      <div class="flex h-16 shrink-0 items-center">
        <router-link
          to="/"
          class="flex items-center space-x-2 group transition-all duration-300"
        >
          <span
            class="text-4xl font-bold text-custom-gradient-start transition-transform duration-300 group-hover:scale-105 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end bg-clip-text text-transparent"
          >
            Snake
          </span>
          <span
            class="text-base/6 font-semibold text-gray-900 dark:text-gray-100 transition-all duration-200 group-hover:text-custom-gradient-end"
          >
            Images
          </span>
        </router-link>
      </div>

      <nav class="flex flex-1 flex-col">
        <ul role="list" class="flex flex-1 flex-col gap-y-4">
          <li>
            <ul role="list" class="-mx-2 space-y-1">
              <li
                v-for="item in filteredNavigation"
                :key="item.name"
                class="group"
              >
                <router-link
                  :to="item.route"
                  :class="[
                    isActiveRoute(item.route)
                      ? 'bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg py-2 px-4 text-[15px] hover:scale-105'
                      : 'text-[14px] font-semibold text-gray-900 dark:text-gray-100 hover:text-custom-gradient-end relative group',
                    'flex items-center gap-x-3 transition-all duration-300 ease-in-out w-full',
                  ]"
                >
                  <div
                    :class="[
                      item.icon,
                      isActiveRoute(item.route)
                        ? 'text-white size-[20px] shrink-0'
                        : 'text-gray-400 dark:text-gray-500 group-hover:text-custom-gradient-end size-[18px] shrink-0',
                      'transition-all duration-300 ease-in-out',
                    ]"
                  ></div>
                  <span class="truncate">{{ item.name }}</span>
                  <span
                    v-if="!isActiveRoute(item.route)"
                    class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end transition-all duration-300 group-hover:w-full"
                  ></span>
                </router-link>
              </li>
            </ul>
          </li>

          <li class="mt-auto group">
            <router-link
              to="/account/settings"
              :class="[
                route.path === '/account/settings'
                  ? 'text-custom-gradient-end'
                  : 'text-gray-900 dark:text-gray-100',
                'group flex items-center gap-x-3 rounded-md p-2 font-bold hover:text-custom-gradient-end transition-all duration-300 relative',
              ]"
            >
              <i
                :class="[
                  'i-tabler-settings size-6 shrink-0',
                  route.path === '/account/settings'
                    ? 'text-custom-gradient-end'
                    : 'text-gray-400 dark:text-gray-500',
                  'transition-colors duration-300 group-hover:text-custom-gradient-end',
                ]"
              ></i>
              <span class="flex-1 text-[15px]">Settings</span>
              <span
                v-if="route.path !== '/account/settings'"
                class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end transition-all duration-300 group-hover:w-full"
              ></span>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ACCESSENUM, CheckACCESS } from "@/access";
import { routes } from "@/router/routes";
import { LayoutMenu } from "@/config";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const LoginUserInfo = computed(() => userStore.getLoginInfo);

const filteredNavigation = computed(() => [
  {
    name: "Public",
    route: "/",
    icon: "i-tabler-photo",
  },
  ...routes
    .filter((route) => {
      const NeedACCESS: ACCESSENUM =
        (route.meta?.access as ACCESSENUM) ?? ACCESSENUM.NOLOGIN;
      return (
        !route.redirect &&
        route.meta?.layout === LayoutMenu.BasicLayout &&
        route.meta?.isNav === true &&
        CheckACCESS(LoginUserInfo.value.userRole, NeedACCESS)
      );
    })
    .map((route) => ({
      name: route.name,
      route: route.path,
      icon: route.meta?.icon || "i-tabler-photo",
      // icon: route.meta?.icon || "i-tabler-activity",
    })),
]);

const isActiveRoute = (path: string): boolean => {
  // 如果是设置页面，只激活设置按钮
  if (route.path === "/account/settings") {
    return path === "/account/settings";
  }

  // 如果当前路径没有匹配到任何导航项且不是设置页面，则激活 Public
  if (
    path === "/" &&
    !filteredNavigation.value.some((item) => item.route === route.path)
  ) {
    return true;
  }
  // 精确匹配当前路径
  return route.path === path;
};
</script>

<style scoped>
@keyframes subtleFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

.group:hover .nav-icon {
  animation: subtleFloat 0.5s ease-in-out;
}

@keyframes subtleGradient {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.active-nav-item {
  background-size: 200% auto;
  animation: subtleGradient 3s ease infinite;
  transform-origin: center;
  transition: transform 0.3s ease;
}

.active-nav-item:hover {
  transform: scale(1.05);
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}
</style>
