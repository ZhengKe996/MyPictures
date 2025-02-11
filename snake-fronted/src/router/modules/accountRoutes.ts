import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const accountRoutes: RouteRecordRaw[] = [
  {
    path: "/account/home",
    name: "Home",
    component: () => import("@/views/account/Home"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: false,
    },
  },
  {
    path: "/account/settings",
    name: "Settings",
    component: () => import("@/views/account/Settings"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: false,
    },
  },
];
