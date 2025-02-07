<template>
  <div class="relative overflow-hidden rounded-xl calendar-container">
    <div class="flex items-center justify-between p-4 md:p-6">
      <TransitionGroup
        enter-active-class="transition-all duration-300 ease-out-back transform-gpu"
        enter-from-class="opacity-0 -translate-x-4 blur-sm"
        enter-to-class="opacity-100 translate-x-0 blur-0"
        leave-active-class="transition-all duration-200 ease-in-back transform-gpu"
        leave-from-class="opacity-100 translate-x-0 blur-0"
        leave-to-class="opacity-0 translate-x-4 blur-sm"
        move-class="transition-all duration-500 ease-in-out"
      >
        <h2
          :key="currentMonthLabel"
          class="text-base md:text-lg font-medium calendar-title"
        >
          {{ currentMonthLabel }}
        </h2>
      </TransitionGroup>
      <div class="flex space-x-2">
        <button
          type="button"
          @click="previousMonth"
          class="p-2 rounded-full text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 hover:text-gray-600 dark:hover:text-gray-300 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 group"
        >
          <span class="sr-only">Previous month</span>
          <i
            class="i-tabler-chevron-left size-5 md:size-6 transition-transform duration-200 group-hover:-translate-x-0.5"
          ></i>
        </button>
        <button
          type="button"
          @click="nextMonth"
          class="p-2 rounded-full text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 hover:text-gray-600 dark:hover:text-gray-300 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 group"
        >
          <span class="sr-only">Next month</span>
          <i
            class="i-tabler-chevron-right size-5 md:size-6 transition-transform duration-200 group-hover:translate-x-0.5"
          ></i>
        </button>
      </div>
    </div>

    <div class="px-4 md:px-6 pb-6">
      <div class="grid grid-cols-7 mb-4">
        <div
          v-for="day in weekDays"
          :key="day"
          class="text-center text-xs md:text-sm font-medium text-gray-500 dark:text-gray-400 py-2"
        >
          {{ day }}
        </div>
      </div>

      <TransitionGroup
        tag="div"
        class="grid grid-cols-7 gap-1 md:gap-2"
        enter-active-class="transition-all duration-300 ease-out-back transform-gpu"
        enter-from-class="opacity-0 scale-90 blur-sm"
        enter-to-class="opacity-100 scale-100 blur-0"
        leave-active-class="transition-all duration-200 ease-in-back transform-gpu"
        leave-from-class="opacity-100 scale-100 blur-0"
        leave-to-class="opacity-0 scale-90 blur-sm"
        move-class="transition-all duration-300 ease-in-out"
      >
        <div
          v-for="day in calendarDays"
          :key="day.date"
          class="aspect-square p-0.5"
        >
          <button
            type="button"
            @click="handleDateClick(day)"
            :disabled="!day.isCurrentMonth && disableOutsideDays"
            class="relative w-full h-full flex items-center justify-center rounded-full transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            :class="[
              // Single selection classes
              props.mode === 'single' &&
                day.isSelected &&
                'bg-blue-500 text-white shadow-lg scale-105',

              // Range selection classes
              props.mode === 'range' &&
                (day.isRangeStart || day.isRangeEnd) &&
                'bg-blue-500 text-white shadow-md z-10',
              props.mode === 'range' &&
                day.isInRange &&
                'bg-blue-100 dark:bg-blue-900/30',

              // Common classes
              !day.isSelected &&
                !day.isInRange &&
                day.isToday &&
                'text-blue-600 font-semibold',
              !day.isSelected &&
                !day.isInRange &&
                !day.isToday &&
                day.isCurrentMonth &&
                'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700',
              !day.isSelected &&
                !day.isInRange &&
                !day.isToday &&
                !day.isCurrentMonth &&
                'text-gray-300 dark:text-gray-600',
              day.isCurrentMonth ? 'cursor-pointer' : 'cursor-default',
            ]"
          >
            <time
              :datetime="day.date"
              class="relative z-10 text-sm md:text-base"
              :class="[
                (day.isSelected || day.isRangeStart || day.isRangeEnd) &&
                  'font-semibold',
                day.isToday &&
                  !day.isSelected &&
                  !day.isRangeStart &&
                  !day.isRangeEnd &&
                  'font-semibold',
              ]"
            >
              {{ calendarUtils.getDayNumber(day.date) }}
            </time>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import dayjs from "dayjs";
import type { DayType, CalendarProps, CalendarEmits } from ".";
import { calendarUtils } from ".";

const props = withDefaults(defineProps<CalendarProps>(), {
  initialDate: () => dayjs().format("YYYY-MM-DD"),
  dateFormat: "YYYY-MM-DD",
  disableOutsideDays: true,
  locale: "en",
  mode: "single",
  startDate: undefined,
  endDate: undefined,
});

const emit = defineEmits<CalendarEmits>();

const currentDate = ref(dayjs(props.initialDate));
const selectedDate = ref(props.initialDate);
const rangeStart = ref<string | null>(props.startDate || null);
const rangeEnd = ref<string | null>(props.endDate || null);

const weekDays = computed(() => calendarUtils.getWeekDays(props.locale));

const currentMonthLabel = computed(() =>
  calendarUtils.getMonthLabel(currentDate.value)
);

const calendarDays = computed(() => {
  if (props.mode === "range") {
    return calendarUtils.getCalendarDaysWithRange(
      currentDate.value,
      selectedDate.value,
      rangeStart.value,
      rangeEnd.value
    );
  }
  return calendarUtils.getCalendarDays(currentDate.value, selectedDate.value);
});

const handleDateClick = (day: DayType) => {
  if (!day.isCurrentMonth && props.disableOutsideDays) return;

  if (props.mode === "single") {
    selectedDate.value = day.date;
    emit(
      "update:selectedDate",
      calendarUtils.formatDate(day.date, props.dateFormat)
    );
    emit("dateSelect", calendarUtils.formatDate(day.date, props.dateFormat));
  } else {
    if (!rangeStart.value || (rangeStart.value && rangeEnd.value)) {
      // Start new selection
      rangeStart.value = day.date;
      rangeEnd.value = null;
      emit(
        "update:startDate",
        calendarUtils.formatDate(day.date, props.dateFormat)
      );
      emit("update:endDate", "");
    } else {
      // Complete selection
      if (dayjs(day.date).isBefore(rangeStart.value)) {
        rangeEnd.value = rangeStart.value;
        rangeStart.value = day.date;
      } else {
        rangeEnd.value = day.date;
      }
      emit(
        "update:startDate",
        calendarUtils.formatDate(rangeStart.value, props.dateFormat)
      );
      emit(
        "update:endDate",
        calendarUtils.formatDate(rangeEnd.value, props.dateFormat)
      );
      emit(
        "rangeSelect",
        calendarUtils.formatDate(rangeStart.value, props.dateFormat),
        calendarUtils.formatDate(rangeEnd.value, props.dateFormat)
      );
    }
  }
};

const previousMonth = () => {
  currentDate.value = currentDate.value.subtract(1, "month");
};

const nextMonth = () => {
  currentDate.value = currentDate.value.add(1, "month");
};

onMounted(() => {
  if (props.initialDate) {
    currentDate.value = dayjs(props.initialDate);
    selectedDate.value = props.initialDate;
  }
});
</script>

<style scoped>
.calendar-container {
  will-change: transform, opacity;
  animation: calendarAppear 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.calendar-title {
  will-change: transform, opacity;
}

@keyframes calendarAppear {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 确保硬件加速和性能优化 */
* {
  backface-visibility: hidden;
  -webkit-font-smoothing: antialiased;
}
</style>
