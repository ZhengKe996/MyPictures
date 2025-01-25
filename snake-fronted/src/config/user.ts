import type { CustomColumn } from "./index";

export const UserManagerColumns: CustomColumn[] = [
  { key: "id", label: "ID" },
  { key: "userAccount", label: "Account" },
  { key: "userName", label: "Name" },
  { key: "userRole", label: "Role" },
  { key: "userAvatar", label: "Avatar" },
  { key: "userProfile", label: "Profile" },
  { key: "edit", label: "Edit" },
  { key: "remove", label: "Remove" },
];

import { type UserVO } from "@/generated";

export type { UserVO as UserType };
