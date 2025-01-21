import { defineStore } from "pinia";
import { ACCESSENUM, NOLOGIN } from "@/access";
import { GetLoginInfoUser } from "@/services";
import { DefaultUserAvatar } from "@/config";

interface UserInfo {
  userName: string;
  userRole: ACCESSENUM;
  userAvatar: string;
  userProfile: string;
}
interface State {
  loginInfo: UserInfo;
}

export const useUserStore = defineStore("user", {
  state: (): State => ({
    loginInfo: {
      userName: NOLOGIN,
      userRole: ACCESSENUM.NOLOGIN,
      userAvatar: DefaultUserAvatar,
      userProfile: "",
    },
  }),
  getters: {
    getUserName(): string {
      return this.loginInfo.userName;
    },
    getUserRole(): ACCESSENUM {
      return this.loginInfo.userRole;
    },
    getUserAvatar(): string {
      return this.loginInfo.userAvatar;
    },
    getLoginInfo(): UserInfo {
      return this.loginInfo;
    },
  },
  actions: {
    async setLoginInfo() {
      const { code, data } = await GetLoginInfoUser();
      if (code === 0 && data) {
        this.loginInfo.userName = data.userName as string;
        this.loginInfo.userRole = data.userRole as ACCESSENUM;
        this.loginInfo.userAvatar = data.userAvatar
          ? data.userAvatar
          : DefaultUserAvatar;
        this.loginInfo.userProfile = data.userProfile as string;
      } else {
        this.loginInfo.userName = NOLOGIN;
        this.loginInfo.userRole = ACCESSENUM.NOLOGIN;
        this.loginInfo.userAvatar = DefaultUserAvatar;
        this.loginInfo.userProfile = "";
      }
    },
  },
  persist: {
    key: "user",
    storage: sessionStorage,
  },
});
