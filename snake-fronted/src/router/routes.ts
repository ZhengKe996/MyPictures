import type { RouteRecordRaw } from "vue-router";

// Import route modules
import { userRoutes } from "./modules/userRoutes";
import { managerRoutes } from "./modules/managerRoutes";
import { addRoutes } from "./modules/addRoutes";
import { updateRoutes } from "./modules/updateRoutes";
import { accountRoutes } from "./modules/accountRoutes";
import { otherRoutes } from "./modules/otherRoutes";

export const routes: RouteRecordRaw[] = [
  ...userRoutes,
  ...managerRoutes,
  ...addRoutes,
  ...updateRoutes,
  ...accountRoutes,
  ...otherRoutes,
  {
    path: "/:pathMatch(.*)*",
    name: "mian",
    component: () => import("@/views/Error.vue"),
  },
];
