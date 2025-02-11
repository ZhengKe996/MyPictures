import type { RouteRecordRaw } from "vue-router";
import { ACCESSENUM } from "@/access";
import { LayoutMenu } from "@/config";

export const otherRoutes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/list/pictures",
  },
  {
    path: "/home",
    name: "Home",
    component: () => import("@/views/Home"),
    meta: {
      layout: LayoutMenu.UserLayout,
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
      isNav: false,
    },
  },
  {
    path: "/my-space",
    name: "My Space",
    component: () => import("@/views/space/MySpace"),
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: true,
      icon: "i-tabler-user",
    },
  },
  {
    path: "/detail/picture/:id",
    name: "Detail Picture",
    component: () => import("@/views/pictures/DetailPicture.vue"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: false,
    },
  },
  {
    path: "/analyze",
    name: "Analyze",
    component: () => import("@/views/analyze"),
    props: true,
    meta: {
      layout: LayoutMenu.BasicLayout,
      access: ACCESSENUM.USER,
      isHeader: false,
      isNav: true,
      icon: "i-tabler-activity",
    },
  },
];
