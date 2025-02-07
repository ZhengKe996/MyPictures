import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@/lib/Message";
import { UserRegister } from "@/services";

interface RegisterForm {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}

export const useRegisterForm = () => {
  const router = useRouter();
  const form = reactive<RegisterForm>({
    userAccount: "",
    userPassword: "",
    checkPassword: "",
  });

  const handleSubmit = async () => {
    try {
      const { code, data, message } = await UserRegister(form);
      if (code === 0 && data) {
        Message.success("Registration successful, please login");
        setTimeout(() => router.push("/user/login"), 1000);
      } else {
        Message.error(`Registration failed: ${message}`);
      }
    } catch (error) {
      Message.error("Registration failed, please try again later");
      console.error("Register error:", error);
    }
  };

  return {
    form,
    handleSubmit,
  };
};

export const usePasswordVisibility = () => {
  const showPasswordMode = ref(false);
  const showCheckPasswordMode = ref(false);

  const togglePasswordVisibility = (mode: "password" | "checkPassword") => {
    if (mode === "password") {
      showPasswordMode.value = !showPasswordMode.value;
    } else {
      showCheckPasswordMode.value = !showCheckPasswordMode.value;
    }
  };

  return {
    showPasswordMode,
    showCheckPasswordMode,
    togglePasswordVisibility,
  };
};
