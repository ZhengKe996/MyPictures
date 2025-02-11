import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const updateRoutes: RouteRecordRaw[] = [
  {
    path: "/update/picture/:id",
    name: "UPDATE Picture",
    component: () => import("@/views/pictures/AddORUpdatePicture.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: false,
    },
  },
  {
    path: "/update/space/:id",
    name: "UPDATE Space",
    component: () => import("@/views/space/AddORUpdateSpace.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: false,
    },
  },
];
