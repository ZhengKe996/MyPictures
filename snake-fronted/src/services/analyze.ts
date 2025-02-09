import { SpaceAnalyzeControllerService as Service } from "@/generated";

export const getSpaceUsageAnalyze = async (form: {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}) => {
  console.log("getSpaceUsageAnalyze", form);
  return await Service.getSpaceUsageAnalyzeUsingPost(form);
};

export const getSpaceRankAnalyze = async (form: { topN?: number }) => {
  return await Service.getSpaceRankAnalyzeUsingPost(form);
};

export const getSpaceTagAnalyze = async (form: {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}) => {
  return await Service.getSpaceTagAnalyzeUsingPost(form);
};

export const getSpaceCategoryAnalyze = async (form: {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}) => {
  return await Service.getSpaceCategoryAnalyzeUsingPost(form);
};

export const getSpaceSizeAnalyze = async (form: {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
}) => {
  return await Service.getSpaceSizeAnalyzeUsingPost(form);
};

export const getSpaceUserAnalyze = async (form: {
  queryAll?: boolean;
  queryPublic?: boolean;
  spaceId?: string;
  timeDimension?: string;
  userId?: string;
}) => {
  return await Service.getSpaceUserAnalyzeUsingPost(form);
};
