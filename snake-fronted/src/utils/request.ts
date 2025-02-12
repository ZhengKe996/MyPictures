import axios from "axios";

const Service = axios.create({
  baseURL: import.meta.env.VITE_BASE_API,
  timeout: 5000,
  withCredentials: true,
});

/**
 * @description 响应拦截器
 */
Service.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    // TODO: 提示错误消息
    return Promise.reject(error);
  }
);
export default Service;
