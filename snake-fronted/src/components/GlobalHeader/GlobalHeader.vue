<template>
  <header
    class="w-full bg-white/80 backdrop-blur-sm sticky top-0 z-50 transition-all duration-300 ease-in-out border-b border-gray-100"
  >
    <nav
      class="mx-auto flex max-w-7xl items-center justify-between p-4 lg:px-8"
      aria-label="Global"
    >
      <div class="hidden lg:flex lg:gap-x-12">
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
            class="text-base/6 font-semibold text-gray-900 transition-all duration-200 hover:text-custom-gradient-end relative"
          >
            {{ item.name }}
            <span
              class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end transition-all duration-300 group-hover:w-full"
            ></span>
          </div>
        </router-link>
      </div>

      <popover placement="bottom" :delay="150">
        <template #reference>
          <div class="flex justify-center items-center">
            <router-link
              v-if="LoginUserInfo.userRole == ACCESSENUM.NOLOGIN"
              class="text-base/6 font-medium text-gray-900 hover:text-custom-gradient-end transition-all duration-300 flex items-center gap-1 hover:gap-2"
              to="/user/login"
            >
              Log in
              <span aria-hidden="true" class="transition-transform duration-300"
                >&rarr;</span
              >
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
            ></Avatar>
          </div>
        </template>

        <div
          v-if="LoginUserInfo.userRole != ACCESSENUM.NOLOGIN"
          class="w-[140px] overflow-hidden rounded-lg shadow-sm bg-white/90 backdrop-blur-[2px] divide-y divide-gray-50"
        >
          <div class="py-1">
            <div
              v-for="(item, index) in menuItems"
              :key="index"
              class="group flex items-center px-3 py-1.5 cursor-pointer transition-colors duration-150 hover:bg-gray-50/80"
              @click="item.action"
            >
              <i
                :class="[
                  item.icon,
                  'size-[18px] transition-colors duration-150',
                  item.iconColor || 'text-gray-400 group-hover:text-gray-500',
                ]"
              ></i>
              <span
                :class="[
                  'ml-2 text-[13px] font-normal transition-colors duration-150',
                  item.textColor || 'text-gray-600 group-hover:text-gray-700',
                ]"
              >
                {{ item.label }}
              </span>
            </div>
          </div>
        </div>
      </popover>
    </nav>
  </header>
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

const handleLogout = async () => {
  await UserLogout();
  userStore.setLoginInfo();
  router.push("/login");
};

const handleMySpace = () => {
  router.push("/my-space");
};

// 状态管理 Pinia
const userStore = useUserStore();
const LoginUserInfo = computed(() => userStore.getLoginInfo);

// filter routes
const filteredRoutes = computed(() => {
  return routes.filter((route) => {
    const NeedACCESS: ACCESSENUM =
      (route.meta?.access as ACCESSENUM) ?? ACCESSENUM.NOLOGIN;
    return (
      !route.redirect &&
      route.meta?.layout === LayoutMenu.BasicLayout &&
      route.meta?.isHeader === true &&
      CheckACCESS(LoginUserInfo.value.userRole, NeedACCESS)
    ); // 排除包含 redirect 属性的路由和通配符路由
  });
});

// 默认主页
const selectKey = ref<string>("/home");

// 路由跳转时，更新选中的菜单项
const router = useRouter();
router.afterEach((to) => (selectKey.value = to.path));

// 解决 页面刷新时，菜单项未选择的小问题
onMounted(() => (selectKey.value = router.currentRoute.value.path));

// Add menu items configuration
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
.router-link-active .nav-indicator {
  width: 100%;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.popover-enter-active {
  animation: slideIn 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}

.popover-leave-active {
  animation: fadeIn 0.1s cubic-bezier(0.4, 0, 0.2, 1) reverse;
}

:deep(.avatar) {
  @apply transition-transform duration-200;
}

:deep(.avatar:hover) {
  @apply scale-105;
}
</style>
