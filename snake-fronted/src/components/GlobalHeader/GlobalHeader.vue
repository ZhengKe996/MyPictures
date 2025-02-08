<template>
  <nav
    class="w-full flex items-center justify-between px-4 sm:px-6 lg:px-8"
    aria-label="Global Navigation"
  >
    <div class="hidden lg:flex lg:gap-x-12 items-center">
      <router-link
        v-for="item in filteredRoutes"
        :key="item.path"
        :to="item.path"
        class="relative group"
      >
        <div
          v-if="selectKey === item.path"
          class="mx-auto text-base/6 lg:mx-0 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg py-1.5 px-3 transition-all duration-300 ease-out hover:shadow-lg hover:scale-105"
        >
          {{ item.name }}
        </div>
        <div
          v-else
          class="text-base/6 font-semibold text-gray-900 dark:text-gray-100 transition-all duration-200 hover:text-custom-gradient-end relative"
        >
          {{ item.name }}
          <span
            class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end transition-all duration-300 group-hover:w-full"
          ></span>
        </div>
      </router-link>
    </div>

    <div class="flex items-center space-x-4">
      <popover
        placement="bottom"
        :delay="150"
        class="transition-all duration-300 ease-in-out"
      >
        <template #reference>
          <div class="flex justify-center items-center">
            <router-link
              v-if="LoginUserInfo.userRole == ACCESSENUM.NOLOGIN"
              class="text-base/6 font-medium text-gray-900 dark:text-gray-100 hover:text-custom-gradient-end transition-all duration-300 flex items-center gap-1 hover:gap-2"
              to="/user/login"
            >
              Log in
              <span
                aria-hidden="true"
                class="transition-transform duration-300"
              >
                &rarr;
              </span>
            </router-link>

            <Avatar
              v-else
              :userName="userStore.getUserName"
              :userRole="userStore.getUserRole"
              :userAvatar="userStore.getUserAvatar"
              size="sm"
              showRole
              linkable
              class="transition-all duration-300 hover:ring-2 hover:ring-offset-2 hover:ring-custom-gradient-start/30 rounded-full"
            />
          </div>
        </template>

        <div
          v-if="LoginUserInfo.userRole != ACCESSENUM.NOLOGIN"
          class="w-[140px] overflow-hidden rounded-lg shadow-sm bg-white/90 dark:bg-gray-800/90 backdrop-blur-[2px] divide-y divide-gray-50 dark:divide-gray-700"
        >
          <div class="py-1">
            <div
              v-for="(item, index) in menuItems"
              :key="index"
              class="group flex items-center px-3 py-1.5 cursor-pointer transition-colors duration-150 hover:bg-gray-50 dark:hover:bg-gray-700"
              @click="item.action"
            >
              <i
                :class="[
                  item.icon,
                  'size-[18px] transition-colors duration-150',
                  item.iconColor ||
                    'text-gray-400 dark:text-gray-500 group-hover:text-gray-500 dark:group-hover:text-gray-300',
                ]"
              ></i>
              <span
                :class="[
                  'ml-2 text-[13px] font-normal transition-colors duration-150',
                  item.textColor ||
                    'text-gray-600 dark:text-gray-300 group-hover:text-gray-700 dark:group-hover:text-gray-100',
                ]"
              >
                {{ item.label }}
              </span>
            </div>
          </div>
        </div>
      </popover>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { LayoutMenu } from "@/config";
import { routes } from "@/router/routes";
import { useUserStore } from "@/store/user";
import { ACCESSENUM, CheckACCESS } from "@/access";
import { UserLogout } from "@/services";
import Popover from "@/lib/Popover";
import Avatar from "@/components/Avatar";

const userStore = useUserStore();
const router = useRouter();

const LoginUserInfo = computed(() => userStore.getLoginInfo);

const handleLogout = async () => {
  await UserLogout();
  userStore.setLoginInfo();
  router.push("/login");
};

const handleMySpace = () => {
  router.push("/my-space");
};

const filteredRoutes = computed(() =>
  routes.filter((route) => {
    const NeedACCESS: ACCESSENUM =
      (route.meta?.access as ACCESSENUM) ?? ACCESSENUM.NOLOGIN;
    return (
      !route.redirect &&
      route.meta?.layout === LayoutMenu.BasicLayout &&
      route.meta?.isHeader === true &&
      CheckACCESS(LoginUserInfo.value.userRole, NeedACCESS)
    );
  })
);

const selectKey = ref<string>("/home");

router.afterEach((to) => (selectKey.value = to.path));

onMounted(() => (selectKey.value = router.currentRoute.value.path));

const menuItems = [
  {
    label: "My Space",
    icon: "i-tabler:user",
    action: handleMySpace,
    iconColor: "text-blue-400 group-hover:text-blue-500",
    textColor: "text-gray-600 group-hover:text-gray-800",
  },
  {
    label: "Log out",
    icon: "i-tabler:logout",
    action: handleLogout,
    iconColor: "text-gray-400 group-hover:text-red-500",
    textColor: "text-gray-600 group-hover:text-red-600",
  },
];
</script>

<style scoped>
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

@keyframes softPulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.hover-scale:hover {
  animation: softPulse 0.3s ease-in-out;
}
</style>
