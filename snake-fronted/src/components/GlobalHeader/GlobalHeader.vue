<template>
  <header class="w-full bg-white">
    <nav
      class="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8"
      aria-label="Global"
    >
      <div class="hidden lg:flex lg:gap-x-12">
        <router-link v-for="item in filteredRoutes" :to="item.path">
          <div
            v-if="selectKey === item.path"
            class="mx-auto text-base/6 lg:mx-0 hover:underline bg-gradient-to-r from-custom-gradient-start to-custom-gradient-end text-white font-bold rounded-lg py-1 px-2 focus:outline-none focus:shadow-outline transform transition hover:scale-102 duration-300 ease-in-out animated animated-duration-500 animated-flip-in-y"
          >
            {{ item.name }}
          </div>
          <div class="text-base/6 font-semibold text-gray-900" v-else>
            {{ item.name }}
          </div>
        </router-link>
      </div>
      <div
        class="hidden lg:flex lg:flex-1 lg:justify-end relative"
        @click="hoverHandler"
      >
        <router-link
          v-if="LoginUserInfo.userRole == ACCESSENUM.NOLOGIN"
          class="text-base/6 font-semibold text-gray-900"
          to="/user/login"
          >Log in <span aria-hidden="true">&rarr;</span></router-link
        >
        <Avatar
          v-else
          :userName="userStore.getUserName"
          :userRole="userStore.getUserRole"
          :userAvatar="userStore.getUserAvatar"
        ></Avatar>

        <div
          v-if="LoginUserInfo.userRole != ACCESSENUM.NOLOGIN"
          class="absolute top-12 right-8 animated animated-duration-800 overflow-hidden rounded-lg bg-white shadow"
          :class="
            isHovered ? 'animated-back-in-right ' : 'animated-back-out-right'
          "
        >
          <div class="px-2 py-3 sm:p-6" @click="handleLogout">
            <span
              class="text-base/6 font-semibold text-gray-900 flex items-center justify-center bg-white rounded-md focus:outline-none focus:shadow-outline transform transition hover:scale-102 duration-800 ease-in-out"
            >
              Log out <i class="i-tabler:logout size-4 mx-2"></i
            ></span>
          </div>
        </div>
      </div>
      <div class="" v-if="LoginUserInfo.userRole != ACCESSENUM.NOLOGIN">
        <i v-if="isHovered" class="i-tabler:chevron-up size-8"></i>
        <i v-else class="i-tabler:chevron-down size-8"></i>
      </div>
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
import Avatar from "../Avatar";
import { promiseTimeout } from "@vueuse/core";

const hoverHandler = async () => {
  isHovered.value = true;
  await promiseTimeout(2000);
  isHovered.value = false;
};

const isHovered = ref(false);

const handleLogout = async () => {
  await UserLogout();
  userStore.setLoginInfo();
  router.push("/login");
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
</script>
