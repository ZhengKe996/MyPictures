import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/list/pictures",
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
    path: "/mamager/pictures",
    name: "Manager Pictures",
    component: () => import("@/views/pictures/ManagerPictures.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
    },
  },
  {
    path: "/mamager/space",
    name: "Manager Space",
    component: () => import("@/views/space/SpaceManager.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
    },
  },
  {
    path: "/add/picture",
    name: "ADD Picture",
    component: () => import("@/views/pictures/AddORUpdatePicture.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: true,
    },
  },
  {
    path: "/add/space",
    name: "ADD Space",
    component: () => import("@/views/space/AddORUpdateSpace.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
    },
  },
  {
    path: "/list/pictures",
    name: "List Pictures",
    component: () => import("@/views/pictures/ListPicture.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: true,
    },
  },
  {
    path: "/my-space",
    name: "My Space",
    component: () => import("@/views/space/MySpace.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
    },
  },
  {
    path: "/detail/picture/:id",
    name: "Detail Picture",
    component: () => import("@/views/pictures/DetailPicture.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      isHeader: false,
    },
  },
  {
    path: "/add/bing",
    name: "ADD By Bing",
    component: () => import("@/views/pictures/BatchByBatch.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
    },
  },
  {
    path: "/add/pexels",
    name: "ADD By Pexels",
    component: () => import("@/views/pictures/BatchByBatch.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
    },
  },
  {
    path: "/update/picture/:id",
    name: "UPDATE Picture",
    component: () => import("@/views/pictures/AddORUpdatePicture.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      isHeader: false,
    },
  },

  {
    path: "/update/space/:id",
    name: "UPDATE Space",
    component: () => import("@/views/space/AddORUpdateSpace.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      isHeader: false,
    },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "mian",
    component: () => import("@/views/Error.vue"),
  },
];
