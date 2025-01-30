export interface TabItem {
  name: string;
  href?: string;
  disabled?: boolean;
  content?: string;
  [key: string]: any;
}

import Tabs from "./Tabs.vue";
export default Tabs;
