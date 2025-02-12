import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { UserLogin } from "@/services";
import { Message } from "@/lib/Message";
import { useUserStore } from "@/store/user";

export function useLogin() {
  const router = useRouter();
  const userStore = useUserStore();

  const form = reactive<{ userAccount: string; userPassword: string }>({
    userAccount: "",
    userPassword: "",
  });

  const showPasswordMode = ref(false);

  const handleSubmit = async () => {
    const { code, data, message } = await UserLogin(form);
    if (code === 0 && data) {
      Message.success(
        "Login successful, redirecting to home page in 3 seconds"
      );
      setTimeout(() => router.push("/list/pictures"), 3000);
    } else {
      Message.error(`Login failed, ${message}`);
    }

    // Get current user login information
    await userStore.setLoginInfo();
  };

  const togglePasswordVisibility = () => {
    showPasswordMode.value = !showPasswordMode.value;
  };

  return {
    form,
    showPasswordMode,
    handleSubmit,
    togglePasswordVisibility,
  };
}
