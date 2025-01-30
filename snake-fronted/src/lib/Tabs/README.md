# Tabs Component Documentation

## Overview

The Tabs component is a versatile Vue 3 component that provides both desktop and mobile-friendly tab navigation interfaces. It supports both controlled and uncontrolled usage, with responsive design and accessibility features built-in.

## Features

- 🖥️ Responsive design with different views for desktop and mobile
- ♿️ Full accessibility support with ARIA attributes
- 🎨 Customizable tab appearance through slots
- 🔄 Two-way binding support with v-model
- 📱 Mobile-friendly dropdown select for small screens

## Installation

```bash
# Add the Tabs component to your project
cp Tabs.vue /your-project-path/components/
```

## Basic Usage

```vue
<template>
  <Tabs
    v-model="activeTab"
    :tabs="['Tab 1', 'Tab 2', 'Tab 3']"
    @change="handleTabChange"
  >
    <template #Tab1> Content for Tab 1 </template>
    <template #Tab2> Content for Tab 2 </template>
    <template #Tab3> Content for Tab 3 </template>
  </Tabs>
</template>

<script setup>
import { ref } from "vue";
import Tabs from "./components/Tabs.vue";

const activeTab = ref("Tab 1");
const handleTabChange = (tab) => {
  console.log("Tab changed:", tab);
};
</script>
```

## Advanced Usage

```vue
<template>
  <Tabs v-model="activeTab" :tabs="advancedTabs" @change="handleTabChange">
    <template #tab="{ tab }"> {{ tab.name }} {{ tab.badge }} </template>
    <template #first> Custom content for first tab </template>
  </Tabs>
</template>

<script setup>
import { ref } from "vue";

const activeTab = ref("first");
const advancedTabs = [
  { name: "first", badge: "(new)", disabled: false },
  { name: "second", badge: "(2)", disabled: true },
  { name: "third", content: "Default content" },
];
</script>
```

## Props

| Prop        | Type                     | Default   | Description                              |
| ----------- | ------------------------ | --------- | ---------------------------------------- |
| modelValue  | string                   | undefined | Current active tab (for v-model binding) |
| tabs        | Array<TabItem \| string> | required  | Array of tab items or strings            |
| showContent | boolean                  | true      | Whether to show tab content panels       |
| defaultTab  | string                   | ''        | Default active tab when uncontrolled     |

## Events

| Event             | Payload | Description                               |
| ----------------- | ------- | ----------------------------------------- |
| update:modelValue | string  | Emitted when active tab changes (v-model) |
| change            | TabItem | Emitted when tab selection changes        |
| click             | TabItem | Emitted when a tab is clicked             |

## Types

```typescript
interface TabItem {
  name: string;
  href?: string;
  disabled?: boolean;
  content?: string;
  [key: string]: any;
}
```

## Slots

| Name      | Props            | Description              |
| --------- | ---------------- | ------------------------ |
| tab       | { tab: TabItem } | Custom tab rendering     |
| [tabName] | { tab: TabItem } | Content for specific tab |

## CSS Classes

The component uses Tailwind CSS classes for styling and includes:

- Responsive design (sm: breakpoint)
- Color transitions
- Hover and focus states
- Disabled states

## Accessibility

- Proper ARIA roles and attributes
- Keyboard navigation support
- Screen reader friendly structure

## Browser Support

- All modern browsers
- Mobile responsive design
- Progressive enhancement for older browsers
