import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";
export const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/login",
    redirect: "/user/login",
  },
  {
    path: "/register",
    redirect: "/user/register",
  },
  {
    path: "/user",
    name: "用户",
    component: () => import("@/layouts/UserLayout.vue"),
    meta: { layout: LayoutMenu.UserLayout },
    children: [
      {
        path: "login",
        name: "用户登录",
        component: () => import("@/views/user/Login.vue"),
      },
      {
        path: "register",
        name: "用户注册",
        component: () => import("@/views/user/Register.vue"),
      },
    ],
  },
  {
    path: "/home",
    name: "home",
    component: () => import("@/views/Home.vue"),
    meta: { layout: LayoutMenu.BasicLayout, isHeader: true },
  },
  {
    path: "/manager/users",
    name: "Manager Users",
    component: () => import("@/views/user/ManagerUsers.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
    },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "mian",
    component: () => import("@/views/Error.vue"),
  },
];
