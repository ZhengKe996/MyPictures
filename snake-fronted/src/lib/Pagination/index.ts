import Pagination from "./Pagination.vue";

export interface PaginationProps {
  total: number;
  current: number;
  pageSize?: number;
  disabled?: boolean;
  showQuickJumper?: boolean;
}

export default Pagination;
