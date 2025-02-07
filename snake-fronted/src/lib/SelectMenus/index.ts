import { ref, computed, watch } from "vue";
import { useEventListener, useWindowScroll } from "@vueuse/core";

export interface SelectOption {
  id: number | string;
  name?: string;
  icon?: string;
  avatar?: string;
  description?: string;
  [key: string]: any;
}

export interface SelectMenuProps {
  modelValue?: SelectOption;
  options: SelectOption[];
  label?: string;
  placeholder?: string;
}

export interface SelectMenuEmits {
  (e: "update:modelValue", value: SelectOption | null): void;
}

export function useSelectMenu(props: SelectMenuProps, emit: SelectMenuEmits) {
  const isOpen = ref(false);
  const highlightedIndex = ref(-1);
  const containerRef = ref<HTMLElement | null>(null);

  // Generate unique IDs for accessibility
  const listboxId = computed(
    () => `listbox-${Math.random().toString(36).substr(2, 9)}`
  );

  const getOptionId = (index: number) => `option-${listboxId.value}-${index}`;

  const highlightedId = computed(() =>
    highlightedIndex.value >= 0
      ? getOptionId(highlightedIndex.value)
      : undefined
  );

  // Enhanced keyboard navigation
  const navigateOptions = (direction: "next" | "prev") => {
    if (!isOpen.value) {
      isOpen.value = true;
      highlightedIndex.value = props.modelValue
        ? props.options.findIndex((opt) => opt.id === props.modelValue?.id)
        : 0;
      return;
    }

    const newIndex =
      direction === "next"
        ? (highlightedIndex.value + 1) % props.options.length
        : highlightedIndex.value <= 0
        ? props.options.length - 1
        : highlightedIndex.value - 1;

    highlightedIndex.value = newIndex;

    // Improved scroll into view with smooth behavior
    const highlightedElement = document.getElementById(getOptionId(newIndex));
    if (highlightedElement) {
      highlightedElement.scrollIntoView({
        block: "nearest",
        behavior: "smooth",
      });
    }
  };

  const selectHighlighted = () => {
    if (isOpen.value && highlightedIndex.value >= 0) {
      selectOption(props.options[highlightedIndex.value]);
    }
  };

  const toggleDropdown = () => {
    isOpen.value = !isOpen.value;
    if (isOpen.value) {
      highlightedIndex.value = props.options.findIndex(
        (opt) => opt.id === props.modelValue?.id
      );
    }
  };

  const selectOption = (option: SelectOption) => {
    emit("update:modelValue", option);
    closeDropdown();
  };

  const closeDropdown = () => {
    isOpen.value = false;
    highlightedIndex.value = -1;
  };

  const isSelected = (option: SelectOption) =>
    props.modelValue?.id === option.id;

  // Close dropdown when clicking outside
  useEventListener(document, "click", (event) => {
    const target = event.target as HTMLElement;
    if (!containerRef.value?.contains(target)) {
      closeDropdown();
    }
  });

  // Close dropdown when scrolling window
  const { y: scrollY } = useWindowScroll();
  watch(scrollY, () => {
    if (isOpen.value) closeDropdown();
  });

  // Auto-focus management
  watch(isOpen, (newValue) => {
    if (newValue && containerRef.value) {
      containerRef.value.focus();
    }
  });

  return {
    isOpen,
    highlightedIndex,
    containerRef,
    listboxId,
    highlightedId,
    getOptionId,
    navigateOptions,
    selectHighlighted,
    toggleDropdown,
    selectOption,
    closeDropdown,
    isSelected,
  };
}

export { default } from "./SelectMenus.vue";
