import type { PictureVO, PictureEditRequest } from "@/generated";

export type { PictureVO as PictureType, PictureEditRequest as PictureEditType };

import type { CustomColumn } from "./index";

export const PictureManagerColumns: CustomColumn[] = [
  { key: "id", label: "ID" },
  { key: "picture", label: "Picture" },
  { key: "name", label: "Name" },
  { key: "introduction", label: "Introduction" },
  { key: "category", label: "Category" },
  { key: "tags", label: "Tags" },
  // { key: "picInfo", label: "Info" },
  { key: "user", label: "User" },
  { key: "editTime", label: "EditTime" },
  { key: "detail", label: "Detail" },
  { key: "edit", label: "Edit" },
  { key: "remove", label: "Remove" },
];
