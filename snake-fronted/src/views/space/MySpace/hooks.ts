import { ref, computed, watchEffect, type Ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { saveAs } from "file-saver";
import { Message } from "@/lib/Message";
import dayjs from "dayjs";
import type { PictureType } from "@/config";
export type { PictureType };
import { useThrottleFn } from "@vueuse/core";
import {
  GetPictureList,
  GetSpaceByUserId,
  DeletePictureById,
  GetTagCategory,
} from "@/services";

import type { SelectOption } from "@/lib/SelectMenus";

export interface SpaceInfoInterface {
  current: number;
  pageSize: number;
  category?: string;
  id?: string;
  name?: string;
  userId?: string;
  picFormat?: string;
  tags?: Array<string>;
  spaceId?: string;
  picColor?: string;
  startEditTime?: string;
  endEditTime?: string;
}

export interface SpaceStats {
  maxCount: number;
  maxSize: number;
  totalCount: number;
  totalSize: number;
  countUsagePercent: number;
  sizeUsagePercent: number;
}

/**
 * 用于空间管理的自定义钩子
 * 它提供了检查用户是否有空间、创建空间和创建照片所需的方法和属性
 */
export const useSpaceManagement = () => {
  // 使用用户存储以获取当前用户的信息
  const userStore = useUserStore();
  // 使用路由以导航到不同页面
  const router = useRouter();
  // 是否存在的标志，用于指示用户是否有空间
  const isExit = ref(false);
  // 空间ID，用于存储用户空间的唯一标识
  const spaceId = ref<string>("");
  // 空间统计信息
  const spaceStats = ref<SpaceStats | null>(null);

  // 计算属性，用于获取当前用户的用户名，如果获取失败，则默认为"Unknown User"
  const username = computed(() => userStore.getUserName || "Unknown User");

  // 格式化文件大小
  const formatSize = (bytes: number) => {
    if (bytes === 0) return "0 B";
    const k = 1024;
    const sizes = ["B", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${(bytes / Math.pow(k, i)).toFixed(1)} ${sizes[i]}`;
  };

  /**
   * 检查用户是否有空间的方法
   * 它根据用户ID获取空间信息，并更新isExit和spaceId的值
   * 同时初始化空间统计信息
   * @returns {Promise<string | undefined>} 如果用户有空间，则返回空间ID，否则返回undefined
   */
  const CheckSpace = async () => {
    const userId = userStore.getUserID;
    const { data, code } = await GetSpaceByUserId(userId);
    if (code !== 0 && data === null) {
      isExit.value = false;
      spaceStats.value = null;
    } else {
      isExit.value = true;
      if (data && data.id) {
        spaceId.value = data.id;

        // 计算使用率
        const countUsagePercent = Math.round(
          ((data.totalCount ?? 0) / (data.maxCount ?? 1)) * 100
        );
        const sizeUsagePercent = Math.round(
          ((data.totalSize ?? 0) / (data.maxSize ?? 1)) * 100
        );

        // 更新空间统计信息
        spaceStats.value = {
          maxCount: data.maxCount ?? 0,
          maxSize: data.maxSize ?? 0,
          totalCount: data.totalCount ?? 0,
          totalSize: data.totalSize ?? 0,
          countUsagePercent,
          sizeUsagePercent,
        };

        return data.id;
      }
    }
  };

  /**
   * 处理创建空间的函数
   * 它将用户重定向到添加空间的页面
   */
  const handleCreateSpace = () => router.push("/add/space");

  /**
   * 处理创建照片的函数
   * 它将用户重定向到添加照片的页面，并携带空间ID作为查询参数
   */
  const handleCreatePhoto = () =>
    router.push(`/add/picture?spaceId=${spaceId.value}`);

  // 添加格式化时间的计算属性
  const formatTime = computed(() => {
    return dayjs().format("YYYY-MM-DD HH:mm:ss");
  });

  // 返回用于空间管理的相关属性和方法
  return {
    isExit,
    spaceId,
    username,
    spaceStats, // 添加到返回值中
    formatSize, // 添加到返回值中
    formatTime, // 添加到返回值中
    CheckSpace,
    handleCreateSpace,
    handleCreatePhoto,
  };
};

/**
 * 出口一个用于文件处理的钩子函数
 * 该钩子函数提供了处理文件（目前仅限图片）的编辑、删除、下载和预览等功能
 */
export const useFileHandling = () => {
  // 获取路由实例，用于导航到不同页面
  const router = useRouter();
  // 控制删除对话框的显示状态
  const showDeleteDialog = ref(false);
  // 存储当前操作的图片信息
  const currentPicture = ref<PictureType | null>(null);

  /**
   * 处理编辑操作
   * @param picture 需要编辑的图片信息
   * @param spaceId 图片所在的空间ID
   * 导航到图片更新页面
   */
  const handleEdit = async (picture: PictureType, spaceId: string) =>
    router.push(`/update/picture/${picture.id}?spaceId=${spaceId}`);

  /**
   * 处理删除操作的准备阶段
   * @param picture 需要删除的图片信息
   * 设置当前操作的图片，并显示删除对话框
   */
  const handleDelete = async (picture: PictureType) => {
    currentPicture.value = picture;
    showDeleteDialog.value = true;
  };

  /**
   * 确认删除操作
   * 发起删除图片的请求，并根据结果提供反馈
   * @returns {Promise<boolean>} 删除操作是否成功
   */
  const confirmDelete = async () => {
    if (!currentPicture.value?.id) return;

    try {
      const { code } = await DeletePictureById(
        currentPicture.value.id.toString()
      );
      if (code === 0) {
        Message.success("Delete successful");
        return true;
      }
      Message.error("Delete failed");
      return false;
    } catch (error) {
      Message.error("Delete operation error");
      return false;
    } finally {
      currentPicture.value = null;
    }
  };

  /**
   * 取消删除操作
   * 隐藏删除对话框，重置当前操作的图片，并提供取消操作的提示
   */
  const handleCancelDelete = () => {
    showDeleteDialog.value = false;
    currentPicture.value = null;
    Message.warning("Delete operation cancelled");
  };

  /**
   * 处理下载操作
   * @param picture 需要下载的图片信息
   * 检查图片信息的完整性，然后发起下载请求
   */
  const handleDownload = async (picture: PictureType) => {
    if (!picture?.url) {
      Message.warning("No image available for download");
      return;
    }

    const { url, name, user, picFormat } = picture;

    if (!url || !name || !user?.userName || !picFormat) {
      Message.warning("Incomplete image information, cannot download");
      return;
    }

    try {
      const fileName = `${name}-作者:${user.userName}.${picFormat}`;
      await saveAs(url, fileName);
      Message.success("Download successful");
    } catch (error) {
      console.error("下载失败:", error);
      Message.error("Download failed, please try again later");
    }
  };

  /**
   * 处理预览操作
   * @param picture 需要预览的图片信息
   * 导航到图片详情页面
   */
  const handlePreview = (picture: PictureType) =>
    router.push(`/detail/picture/${picture.id}`);

  // 返回文件处理的各项功能
  return {
    showDeleteDialog,
    currentPicture,
    handleEdit,
    handleDelete,
    confirmDelete,
    handleCancelDelete,
    handleDownload,
    handlePreview,
  };
};

/**
 * 导出一个用于列表管理的钩子函数
 * 该函数主要用于管理图片列表的加载和显示，包括响应空间 ID 的变化、格式化日期、节流加载等
 * @param spaceIdRef 一个包含空间 ID 的 Ref 对象，用于响应式地更新空间 ID
 * @returns 返回包含加载状态、完成状态、总数量、图片列表信息、分页信息和加载列表函数的对象
 */
export const useListManagement = (spaceIdRef: Ref<string>) => {
  // 使用用户存储来获取当前用户信息
  const userStore = useUserStore();
  // 加载状态
  const loading = ref(false);
  // 加载完成状态
  const isFinished = ref(false);
  // 图片总数量
  const total = ref(0);
  // 图片列表信息
  const PictureListInfo = ref<PictureType[]>([]);
  // 分页信息，包括当前页、每页大小、用户 ID 和空间 ID
  const PageInfo = ref<SpaceInfoInterface>({
    current: 1,
    pageSize: 20,
    userId: userStore.getUserID,
  });

  // 使用 watchEffect 来响应 spaceId 的变化
  watchEffect(() => {
    if (spaceIdRef.value) {
      PageInfo.value.spaceId = spaceIdRef.value;
    }
  });

  /**
   * 格式化记录函数
   * 该函数主要用于格式化从后端获取的图片记录，将日期字符串转换为更易读的格式
   * @param records 图片记录数组
   * @returns 返回格式化后的图片记录数组
   */
  const formatRecords = (records: PictureType[]) => {
    return records.map((item: PictureType) => ({
      ...item,
      createTime: dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
      editTime: dayjs(item.editTime).format("YYYY-MM-DD HH:mm:ss") ?? "",
    }));
  };

  /**
   * 加载列表的节流函数
   * 该函数用于节流对后端接口的请求，避免频繁请求导致的问题
   * 它会根据分页信息获取图片列表，并更新状态
   */
  const LoadList = useThrottleFn(async () => {
    const { data, message, code } = await GetPictureList(PageInfo.value);
    if (code === 0 && data) {
      total.value = Number(data.total) ?? 0;

      const formattedRecords = Array.isArray(data.records)
        ? formatRecords(data.records)
        : [];

      if (Number(PageInfo.value.current) === 1) {
        PictureListInfo.value = formattedRecords;
      } else {
        PictureListInfo.value.push(...formattedRecords);
      }

      if (PictureListInfo.value.length >= total.value) {
        isFinished.value = true;
      }
    } else Message.error(`Failed to get data: ${message}`);
  }, 500);

  return {
    loading,
    isFinished,
    total,
    PictureListInfo,
    PageInfo,
    LoadList,
  };
};

/**
 * 导出一个用于获取过滤选项的钩子函数
 * 该钩子用于获取标签和分类选项，以便在应用中使用
 * @returns 返回包含分类选项、自定义颜色和加载函数的对象
 */
export const useFilterOptions = () => {
  // 定义一个可变的分类选项数组
  const categoryOptions = ref<SelectOption[]>([]);
  // 定义一个可变的自定义颜色数组
  const customColors = ref<string[]>();

  /**
   * 加载过滤选项的异步函数
   * 该函数通过调用GetTagCategory API来获取标签和分类的数据，并据此更新分类选项和自定义颜色
   */
  const loadFilterOptions = async () => {
    // 调用GetTagCategory API并解构返回的响应数据
    const { code, data, message } = await GetTagCategory();
    // 如果响应代码为0且数据存在，则处理数据以更新分类选项和自定义颜色
    if (code === 0 && data) {
      // 更新分类选项，将接收到的类别列表转换为具有id和name属性的对象数组，并在列表前添加"所有类别"选项
      categoryOptions.value = (data.categoryList ?? []).map((data: string) => {
        return { id: data, name: data };
      });
      categoryOptions.value.unshift({ id: "all", name: "All Categories" });
      // 更新自定义颜色数组
      customColors.value = (data.colorList ?? []).map((data: string) => data);
    } else {
      // 如果获取数据失败，则显示错误消息
      Message.error(`获取标签和分类选项失败${message}`);
    }
  };

  return {
    categoryOptions,
    customColors,
    loadFilterOptions,
  };
};

/**
 * 自定义钩子，用于处理日期过滤逻辑
 * @param PageInfo - 一个引用类型参数，用于存储空间信息
 * @param loadCallback - 加载回调函数，当日期筛选条件改变时调用
 * @returns 返回一个对象，包含日期筛选相关的响应式属性和处理函数
 */
export const useDateFilter = (
  PageInfo: Ref<SpaceInfoInterface>,
  loadCallback: () => void
) => {
  // 响应式属性，用于存储开始日期
  const startDate = ref("");
  // 响应式属性，用于存储结束日期
  const endDate = ref("");
  // 响应式属性，用于存储选定的日期范围选项
  const selectedDateRange = ref<SelectOption | undefined>(undefined);

  // 日期范围选项数组
  const dateRangeOptions = ref<SelectOption[]>([
    { id: "7", name: "Last 7 days" },
    { id: "14", name: "Last 14 days" },
    { id: "30", name: "Last 30 days" },
    { id: "90", name: "Last 90 days" },
  ]);

  // 计算属性，用于生成日期标签
  const dateLabel = computed(() => {
    if (!startDate.value && !endDate.value) {
      return "Select Date Range";
    }
    const start = dayjs(startDate.value).format("MM/DD");
    const end = dayjs(endDate.value).format("MM/DD");
    const year = dayjs(startDate.value).format("YYYY");
    return `${year}年 ${start} - ${end}`;
  });

  /**
   * 处理日期范围选择的函数
   * @param start - 开始日期
   * @param end - 结束日期
   */
  const handleDateRangeSelect = (start: string, end: string) => {
    selectedDateRange.value = undefined;
    startDate.value = start;
    endDate.value = end;

    PageInfo.value = {
      ...PageInfo.value,
      current: 1,
      startEditTime: start ? dayjs(start).startOf("day").format() : undefined,
      endEditTime: end ? dayjs(end).endOf("day").format() : undefined,
    };
    loadCallback(); // 添加加载回调
  };

  /**
   * 处理日期范围选项选择的函数
   * @param option - 选定的日期范围选项
   */
  const handleDateRangeOptionSelect = (option: SelectOption | null) => {
    if (!option) {
      selectedDateRange.value = undefined;
      startDate.value = "";
      endDate.value = "";
      PageInfo.value = {
        ...PageInfo.value,
        current: 1,
        startEditTime: undefined,
        endEditTime: undefined,
      };
    } else {
      const days = parseInt(String(option.id));
      const end = dayjs();
      const start = end.subtract(days, "day");

      startDate.value = start.format("YYYY-MM-DD");
      endDate.value = end.format("YYYY-MM-DD");

      PageInfo.value = {
        ...PageInfo.value,
        current: 1,
        startEditTime: start.startOf("day").format(),
        endEditTime: end.endOf("day").format(),
      };
    }
    loadCallback(); // 添加加载回调
  };

  // 返回日期筛选相关的响应式属性和处理函数
  return {
    startDate,
    endDate,
    selectedDateRange,
    dateRangeOptions,
    dateLabel,
    handleDateRangeSelect,
    handleDateRangeOptionSelect,
  };
};

/**
 * 自定义钩子，用于处理搜索和过滤逻辑
 * @param PageInfo - 一个响应式对象，包含页面信息和搜索/过滤参数
 * @param loadCallback - 加载数据的回调函数，当搜索或过滤条件改变时调用
 * @returns 返回包含搜索和过滤功能的对象
 */
export const useSearchAndFilter = (
  PageInfo: Ref<SpaceInfoInterface>,
  loadCallback: () => void
) => {
  // 选中的类别
  const selectedCategory = ref<SelectOption | undefined>(undefined);
  // 颜色选择
  const oxColor = ref();

  /**
   * 处理搜索逻辑
   * 重置当前页码为1，并调用加载回调以应用搜索条件
   */
  const handleSearch = () => {
    PageInfo.value.current = 1;
    loadCallback(); // 添加加载回调
  };

  /**
   * 清除搜索条件
   * 重置名称为一个空字符串，并调用加载回调以应用更改
   */
  const handleClear = () => {
    PageInfo.value.name = "";
    loadCallback(); // 添加加载回调
  };

  /**
   * 处理颜色变化
   * 更新页面信息的颜色，并重置当前页码为1，然后调用加载回调
   * @param color - 新的颜色值
   */
  const handleColorChange = (color: string) => {
    PageInfo.value = {
      ...PageInfo.value,
      current: 1,
      picColor: color || undefined,
    };
    loadCallback(); // 添加加载回调
  };

  /**
   * 处理类别选择
   * 更新页面信息的类别，并重置当前页码为1，然后调用加载回调
   * @param option - 选中的类别选项
   */
  const handleCategorySelect = (option: SelectOption | null) => {
    PageInfo.value = {
      ...PageInfo.value,
      current: 1,
      category: option && option.id !== "all" ? String(option.id) : undefined,
    };
    loadCallback(); // 添加加载回调
  };

  /**
   * 重置所有过滤条件
   * 重置页面信息到默认状态，清除选中的类别和颜色，然后调用加载回调
   */
  const handleReset = () => {
    PageInfo.value = {
      current: 1,
      pageSize: 20,
      spaceId: PageInfo.value.spaceId,
      userId: PageInfo.value.userId,
      name: "",
    };
    selectedCategory.value = undefined;
    oxColor.value = undefined;
    Message.success("All filters have been reset");
    loadCallback(); // 添加加载回调
  };

  return {
    selectedCategory,
    oxColor,
    handleSearch,
    handleClear,
    handleColorChange,
    handleCategorySelect,
    handleReset,
  };
};
