import type { SpaceUsageAnalyzeResponse } from "@/generated";
import type { SpaceCategoryAnalyzeResponse } from "@/generated";
import type { SpaceSizeAnalyzeResponse } from "@/generated";
import type { SpaceTagAnalyzeResponse } from "@/generated";
import type { SpaceUserAnalyzeResponse } from "@/generated";

export type { SpaceUsageAnalyzeResponse as UsageAnalyzeeType };
export type { SpaceCategoryAnalyzeResponse as CategoryAnalyzeType };
export type { SpaceSizeAnalyzeResponse as SizeAnalyzeType };
export type { SpaceTagAnalyzeResponse as TagAnalyzeType };
export type { SpaceUserAnalyzeResponse as UserAnalyzeType };

export interface AnalyzeProps {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}
