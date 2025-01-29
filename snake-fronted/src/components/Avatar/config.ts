import type { AvatarConfig } from "./types";
import { ACCESSENUM, NOLOGIN } from "@/access";
import { DefaultUserAvatar } from "@/config";

export const defaultProps = {
  userName: NOLOGIN,
  userRole: ACCESSENUM.NOLOGIN,
  userAvatar: DefaultUserAvatar,
  size: "md" as const,
  showRole: true,
  linkable: true,
  href: "#",
  customClass: "",
};

export const avatarConfig: AvatarConfig = {
  defaultSize: "md",
  sizes: {
    sm: "size-8", // 32px
    md: "size-9", // 36px
    lg: "size-12", // 48px
  },
  dimensions: {
    sm: {
      width: "32px",
      height: "32px",
    },
    md: {
      width: "36px",
      height: "36px",
    },
    lg: {
      width: "48px",
      height: "48px",
    },
  },
  transitions: {
    duration: "duration-200",
    timing: "ease-in-out",
  },
};
