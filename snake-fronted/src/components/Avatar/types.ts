import { ACCESSENUM } from "@/access";

export interface AvatarProps {
  userName?: string;
  userRole?: ACCESSENUM;
  userAvatar?: string;
  size?: "sm" | "md" | "lg";
  showRole?: boolean;
  linkable?: boolean;
  href?: string;
  customClass?: string;
}

export interface AvatarConfig {
  defaultSize: "sm" | "md" | "lg";
  sizes: {
    sm: string;
    md: string;
    lg: string;
  };
  dimensions: {
    [key in "sm" | "md" | "lg"]: {
      width: string;
      height: string;
    };
  };
  transitions: {
    duration: string;
    timing: string;
  };
}
