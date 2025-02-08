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
                      ? 'bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg py-1.5 px-3'
                      : 'text-base/6 font-semibold text-gray-900 dark:text-gray-100 hover:text-custom-gradient-end relative group',
                    'flex items-center gap-x-3 transition-all duration-300 ease-in-out',
                  ]"
                >
                  <div
                    :class="[
                      item.icon,
                      isActiveRoute(item.route)
                        ? 'text-white'
                        : 'text-gray-400 dark:text-gray-500 group-hover:text-custom-gradient-end',
                      'size-6 transition-colors duration-300 ease-in-out',
                    ]"
                  ></div>
                  {{ item.name }}
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
              to="/settings"
              class="group flex gap-x-3 rounded-md p-2 text-base/6 font-semibold text-gray-900 dark:text-gray-100 hover:text-custom-gradient-end transition-all duration-300 relative"
            >
              <i
                class="i-tabler:settings size-6 shrink-0 text-gray-400 dark:text-gray-500 transition-colors duration-300 group-hover:text-custom-gradient-end"
              ></i>
              Settings
              <span
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
import { ACCESSENUM } from "@/access";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const navigation = [
  {
    name: "Public",
    route: "/",
    icon: "i-tabler-photo",
  },
  {
    name: "My Space",
    route: "/my-space",
    icon: "i-tabler-user-scan",
    requiresAuth: true,
  },
];

const filteredNavigation = computed(() =>
  navigation.filter(
    (item) =>
      !item.requiresAuth ||
      (item.requiresAuth && userStore.getUserRole !== ACCESSENUM.NOLOGIN)
  )
);

const isActiveRoute = (path: string): boolean => route.path === path;
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
}
</style>
