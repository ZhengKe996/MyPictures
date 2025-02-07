import { Dayjs } from "dayjs";
import dayjs from "dayjs";
import isBetween from "dayjs/plugin/isBetween";

dayjs.extend(isBetween);

/**
 * Calendar day item type definition
 * 日历天数项类型定义
 */
export interface DayType {
  /** Date string in YYYY-MM-DD format 日期字符串，格式为YYYY-MM-DD */
  date: string;
  /** Whether the day belongs to current month 是否属于当前月份 */
  isCurrentMonth?: boolean;
  /** Whether the day is today 是否为今天 */
  isToday?: boolean;
  /** Whether the day is selected 是否被选中 */
  isSelected?: boolean;
  /** Whether the day is in range 是否在范围内 */
  isInRange?: boolean;
  /** Whether the day is range start 是否为范围开始 */
  isRangeStart?: boolean;
  /** Whether the day is range end 是否为范围结束 */
  isRangeEnd?: boolean;
}

/**
 * Calendar component props interface
 * 日历组件属性接口
 */
export interface CalendarProps {
  /** Initial date for calendar (YYYY-MM-DD) 日历初始日期 */
  initialDate?: string;
  /** Date format for output 输出的日期格式 */
  dateFormat?: string;
  /** Whether to disable days outside current month 是否禁用当前月份以外的日期 */
  disableOutsideDays?: boolean;
  /** Locale for calendar 日历的语言设置 */
  locale?: string;
  /** Selection mode: 'single' or 'range' 选择模式：单选或范围 */
  mode?: "single" | "range";
  /** Start date for range selection 范围选择的开始日期 */
  startDate?: string;
  /** End date for range selection 范围选择的结束日期 */
  endDate?: string;
}

/**
 * Calendar component emits interface
 * 日历组件事件接口
 */
export interface CalendarEmits {
  /** Emit when selected date updates 当选中日期更新时触发 */
  (e: "update:selectedDate", date: string): void;
  /** Emit when date is selected 当日期被选中时触发 */
  (e: "dateSelect", date: string): void;
  /** Emit when range selection updates 当范围选择更新时触发 */
  (e: "update:startDate", date: string): void;
  (e: "update:endDate", date: string): void;
  /** Emit when range is selected 当范围选择完成时触发 */
  (e: "rangeSelect", start: string, end: string): void;
}

/**
 * Calendar utility functions
 * 日历工具函数集合
 */
export const calendarUtils = {
  /**
   * Get week days array based on locale
   * 根据语言设置获取星期数组
   * @param locale - Locale string 语言设置
   * @returns Array of week day names 星期名称数组
   */
  getWeekDays(locale: string): string[] {
    return ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map((day) =>
      day.substring(0, locale === "en" ? 1 : 2)
    );
  },

  /**
   * Get formatted month and year label
   * 获取格式化的月份和年份标签
   * @param date - Dayjs date object 日期对象
   * @returns Formatted month and year string 格式化的月份年份字符串
   */
  getMonthLabel(date: Dayjs): string {
    return date.format("MMMM YYYY");
  },

  /**
   * Get day number from date string
   * 从日期字符串中获取日期数字
   * @param dateStr - Date string 日期字符串
   * @returns Day number as string 日期数字字符串
   */
  getDayNumber(dateStr: string): string {
    return dayjs(dateStr).format("D");
  },

  /**
   * Format date string according to specified format
   * 根据指定格式格式化日期字符串
   * @param date - Date string to format 待格式化的日期字符串
   * @param format - Target date format 目标日期格式
   * @returns Formatted date string 格式化后的日期字符串
   */
  formatDate(date: string, format: string): string {
    return dayjs(date).format(format);
  },

  /**
   * Generate calendar days array for current month view
   * 生成当前月份视图的日历天数数组
   * @param currentDate - Current month's date 当前月份的日期
   * @param selectedDate - Selected date 选中的日期
   * @returns Array of calendar day items 日历天数项数组
   *
   * This function will:
   * 该函数将会：
   * 1. Get the first and last day of current month 获取当前月份的第一天和最后一天
   * 2. Extend to complete weeks 扩展至完整的星期
   * 3. Generate day objects with proper flags 生成带有适当标记的日期对象
   */
  getCalendarDays(currentDate: Dayjs, selectedDate: string): DayType[] {
    const firstDayOfMonth = currentDate.startOf("month");
    const lastDayOfMonth = currentDate.endOf("month");
    const startDate = firstDayOfMonth.startOf("week");
    const endDate = lastDayOfMonth.endOf("week");

    const days: DayType[] = [];
    let day = startDate;

    while (day.isBefore(endDate) || day.isSame(endDate, "day")) {
      days.push({
        date: day.format("YYYY-MM-DD"),
        isCurrentMonth: day.month() === currentDate.month(),
        isToday: day.format("YYYY-MM-DD") === dayjs().format("YYYY-MM-DD"),
        isSelected: day.format("YYYY-MM-DD") === selectedDate,
      });
      day = day.add(1, "day");
    }

    return days;
  },

  /**
   * Check if a date is within a range
   * 检查日期是否在范围内
   */
  isDateInRange(
    date: string,
    start: string | null,
    end: string | null
  ): boolean {
    if (!start || !end) return false;
    return dayjs(date).isBetween(start, end, "day", "[]");
  },

  /**
   * Get calendar days with range selection support
   * 获取支持范围选择的日历天数数组
   */
  getCalendarDaysWithRange(
    currentDate: Dayjs,
    selectedDate: string,
    startDate: string | null,
    endDate: string | null
  ): DayType[] {
    const days = calendarUtils.getCalendarDays(currentDate, selectedDate);
    return days.map((day) => ({
      ...day,
      isInRange: calendarUtils.isDateInRange(day.date, startDate, endDate),
      isRangeStart: day.date === startDate,
      isRangeEnd: day.date === endDate,
    }));
  },
};

import Calendars from "./Calendars.vue";
export default Calendars;
