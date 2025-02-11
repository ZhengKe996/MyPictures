import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const managerRoutes: RouteRecordRaw[] = [
  {
    path: "/manager/users",
    name: "Manager Users",
    component: () => import("@/views/user/ManagerUsers"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
      isNav: false,
    },
  },
  {
    path: "/manager/pictures",
    name: "Manager Pictures",
    component: () => import("@/views/pictures/ManagerPictures.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
      isNav: false,
    },
  },
  {
    path: "/manager/space",
    name: "Manager Space",
    component: () => import("@/views/space/SpaceManager.vue"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.ADMIN,
      isHeader: true,
      isNav: false,
    },
  },
];
