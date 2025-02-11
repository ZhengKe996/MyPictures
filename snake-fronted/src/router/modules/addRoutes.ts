import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const addRoutes: RouteRecordRaw[] = [
  {
    path: "/add/picture",
    name: "ADD Picture",
    component: () => import("@/views/pictures/AddORUpdatePicture.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: true,
      isNav: false,
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
      isNav: false,
    },
  },
  {
    path: "/add/bing",
    name: "ADD By Bing",
    component: () => import("@/views/pictures/BatchByBatch.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: false,
      isNav: true,
    },
  },
  {
    path: "/add/pexels",
    name: "ADD By Pexels",
    component: () => import("@/views/pictures/BatchByBatch.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: false,
      isNav: true,
    },
  },
];
