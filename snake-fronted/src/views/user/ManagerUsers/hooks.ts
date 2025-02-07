import { ref, watchEffect } from "vue";
import { useThrottleFn } from "@vueuse/core";
import { Message } from "@/lib/Message";
import { AdminGetUserList, DeleteUserById } from "@/services";
import { DefaultUserAvatar, type UserType } from "@/config";

export interface UserInfoInterface {
  current: number;
  pageSize: number;
  id?: string;
  sortField?: string;
  sortOrder?: string;
  userAccount?: string;
  userName?: string;
  userProfile?: string;
  userRole?: string;
}

export function useUserManagement() {
  const total = ref<number>(0);
  const PageInfo = ref<UserInfoInterface>({
    current: 1,
    pageSize: 20,
  });

  const ListInfo = ref<UserType[]>([]);
  const showDeleteDialog = ref(false);
  const currentItem = ref<UserType | null>(null);

  const ChangeCurrentPageHandle = (current: number) =>
    (PageInfo.value = { ...PageInfo.value, current: current });

  const LoadList = useThrottleFn(async () => {
    const { data, code, message } = await AdminGetUserList(PageInfo.value);
    if (code === 0 && data) {
      total.value = Number(data.total) ?? 0;

      ListInfo.value = Array.isArray(data.records)
        ? data.records.map((item: UserType) => ({
            id: item.id ? String(item.id) : "",
            userAccount: item.userAccount ?? "",
            userName: item.userName ?? "",
            userRole: item.userRole ?? "",
            userAvatar: item.userAvatar ?? DefaultUserAvatar,
            userProfile: item.userProfile ?? "",
          }))
        : [];
    } else Message.error(`Failed to fetch data, reason: ${message}`);
  }, 1000);

  const handleKeyPress = useThrottleFn((event: KeyboardEvent) => {
    if (event.key === "Enter") LoadList();
  }, 1000);

  const handleAdd = () => {
    console.log("Add new space");
  };

  const handleDelete = (id: string) => {
    const item = ListInfo.value.find((item) => item.id === id);
    if (item) {
      currentItem.value = item;
      showDeleteDialog.value = true;
    }
  };

  const confirmDelete = async () => {
    if (!currentItem.value?.id) return;

    try {
      const { code } = await DeleteUserById(currentItem.value.id);
      if (code === 0) {
        Message.success("Delete successful");
        await LoadList();
      } else {
        Message.error("Delete failed");
      }
    } catch (error) {
      Message.error("An error occurred during deletion");
    } finally {
      currentItem.value = null;
      showDeleteDialog.value = false;
    }
  };

  const handleCancelDelete = () => {
    showDeleteDialog.value = false;
    currentItem.value = null;
    Message.warning("Delete operation cancelled");
  };

  watchEffect(() => LoadList());

  return {
    total,
    PageInfo,
    ListInfo,
    showDeleteDialog,
    currentItem,
    ChangeCurrentPageHandle,
    handleKeyPress,
    handleAdd,
    handleDelete,
    confirmDelete,
    handleCancelDelete,
  };
}
