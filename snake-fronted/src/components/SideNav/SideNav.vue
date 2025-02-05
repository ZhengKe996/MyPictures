<template>
  <div class="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-48 lg:flex-col">
    <div
      class="flex grow flex-col gap-y-5 overflow-y-auto border-r border-gray-200 bg-white px-6 pb-4"
    >
      <div class="flex h-16 shrink-0 items-center">
        <a href="#" class="-m-1.5 p-1.5">
          <span class="text-sm/6 mx-2 font-semibold text-gray-900">
            <span class="text-4xl">Snake</span>Images
          </span>
        </a>
      </div>
      <nav class="flex flex-1 flex-col">
        <ul role="list" class="flex flex-1 flex-col gap-y-7">
          <li>
            <ul role="list" class="-mx-2 space-y-1">
              <li v-for="item in filteredNavigation" :key="item.name">
                <router-link
                  :to="item.route"
                  :class="[
                    isActiveRoute(item.route)
                      ? 'bg-gray-50 text-indigo-600'
                      : 'text-gray-700 hover:bg-gray-50 hover:text-indigo-600',
                    'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold transition-all duration-200 ease-in-out',
                  ]"
                >
                  <div
                    :class="[
                      item.icon,
                      isActiveRoute(item.route)
                        ? 'text-indigo-600'
                        : 'text-gray-400',
                      'size-6 transition-colors duration-200 ease-in-out group-hover:text-indigo-600',
                    ]"
                  ></div>
                  {{ item.name }}
                </router-link>
              </li>
            </ul>
          </li>
          <li class="mt-auto">
            <a
              href="#"
              class="group -mx-2 flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold text-gray-700 hover:bg-gray-50 hover:text-indigo-600"
            >
              <i
                class="i-tabler:settings size-6 shrink-0 text-gray-400 group-hover:text-indigo-600"
              ></i>
              Settings
            </a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user"; // Assume you have a user store
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

const filteredNavigation = computed(() => {
  return navigation.filter(
    (item) =>
      !item.requiresAuth ||
      (item.requiresAuth && userStore.getUserRole !== ACCESSENUM.NOLOGIN)
  );
});

const isActiveRoute = (path: string): boolean => {
  return route.path === path;
};
</script>
