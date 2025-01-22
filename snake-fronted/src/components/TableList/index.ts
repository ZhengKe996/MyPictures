import TableList from "./TableList.vue";
export default TableList;

export interface columns<T> {
  key: keyof T;
  label: string;
}
export interface TableListProps<T> {
  // data: T[];
  columns: Array<columns<T>>;
}
