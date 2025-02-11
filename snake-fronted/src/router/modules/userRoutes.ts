import type { RouteRecordRaw } from "vue-router";
import { LayoutMenu } from "@/config";

export const userRoutes: RouteRecordRaw[] = [
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
        component: () => import("@/views/user/Login"),
      },
      {
        path: "register",
        name: "用户注册",
        component: () => import("@/views/user/Register"),
      },
    ],
  },
];
