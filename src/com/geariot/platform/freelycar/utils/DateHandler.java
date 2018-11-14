package com.geariot.platform.freelycar.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author mxy940127
 *
 */
public class DateHandler {
	
	private static final Calendar CAL;
	private static final Date DATE;
	
	//静态初始化器
	static{
		CAL = Calendar.getInstance();
		DATE = new Date();
	}
	
	public static Date getCurrentDate() {
		DATE.setTime(System.currentTimeMillis());
		return DATE;
	}
	
	// date -> Calendar
	public static Calendar toCalendar(Date date) {
		CAL.setTime(date);
		return CAL;
	}

	// 使用当前时间的date
	public static Calendar toCalendar() {
		DATE.setTime(System.currentTimeMillis());
		CAL.setTime(DATE);
		return CAL;
	}
	
	/**
	 * 设置月初
	 */
	public static Calendar setTimeToBeginningOfMonth(Calendar calendar) {
		if(calendar == null) return null;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static Date setTimeToBeginningOfMonth(Date date) {
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToBeginningOfMonth(calendar);
		return calendar.getTime();
	}
	
	public static Date setTimeToBeginningOfMonth() {
		Calendar calendar = toCalendar();
		setTimeToBeginningOfMonth(calendar);
		return calendar.getTime();
	}
	
	/**
	 * 设置月末
	 */
	public static Calendar setTimeToEndOfMonth(Calendar calendar) {
		if(calendar == null) return null;
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar;
	}
	
	public static Date setTimeToEndOfMonth(Date date) {
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToEndOfMonth(calendar);
		return calendar.getTime();
	}
	
	public static Date setTimeToEndOfMonth() {
		Calendar calendar = toCalendar();
		setTimeToEndOfMonth(calendar);
		return calendar.getTime();
	}

	
	/**
	 * 设置一天的开始
	 */
	public static Calendar setTimeToBeginningOfDay(Calendar calendar) {
		if(calendar == null) return null;
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static Date setTimeToBeginningOfDay(Date date) {
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToBeginningOfDay(calendar);
		return calendar.getTime();
	}
	
	public static Date setTimeToBeginningOfDay() {
		Calendar calendar = toCalendar();
		setTimeToBeginningOfDay(calendar);
		return calendar.getTime();
	}

	/**
	 * 设置一天的结束
	 */
	public static Calendar setTimeToEndofDay(Calendar calendar) {
		if(calendar == null) return null;
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar;
	}
	
	public static Date setTimeToEndofDay(Date date) {
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToEndofDay(calendar);
		return calendar.getTime();
	}
	
	public static Date setTimeToEndofDay() {
		Calendar calendar = toCalendar();
		setTimeToEndofDay(calendar);
		return calendar.getTime();
	}
	
	
	/**
	 * 设置以今天为基准的 某一天的时间 的开始 负数表示昨天 前天...
	 * @param calendar
	 * @param oneDay
	 * @return
	 */
	public static Calendar setTimeToBeginningOfOneDay(Calendar calendar,int oneDay){
		if(calendar == null) return null;
		calendar.add(Calendar.DAY_OF_YEAR, oneDay);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	
	public static Date setTimeToBeginningOfOneDay(Date date,int oneDay){
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToBeginningOfOneDay(calendar,oneDay);
		return calendar.getTime();
	}
	
	public static Date setTimeToBeginningOfOneDay(int oneDay){
		Calendar calendar = toCalendar();
		setTimeToBeginningOfOneDay(calendar,oneDay);
		return calendar.getTime();
	}
	
	/**
	 * 设置以今天为基准的 某一天的时间 的结束 负数表示昨天 前天...
	 * @param calendar
	 * @param oneDay
	 * @return
	 */
	public static Calendar setTimeToEndOfOneDay(Calendar calendar,int oneDay){
		if(calendar == null) return null;
		calendar.add(Calendar.DAY_OF_YEAR, oneDay);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar;
	}
	
	
	public static Date setTimeToEndOfOneDay(Date date,int oneDay){
		if(date == null) return null;
		Calendar calendar = toCalendar(date);
		setTimeToEndOfOneDay(calendar,oneDay);
		return calendar.getTime();
	}
	
	public static Date setTimeToEndOfOneDay(int oneDay){
		Calendar calendar = toCalendar();
		setTimeToEndOfOneDay(calendar,oneDay);
		return calendar.getTime();
	}
	
	
	public static Calendar addValidMonth(Calendar calendar , int validMonth){
		calendar.add(Calendar.MONTH, validMonth);
		return calendar;
	}
	
	public static Calendar addValidYear(Calendar calendar, int validYear){
		calendar.add(Calendar.YEAR, validYear);
		return calendar;
	}
	
	public static boolean insuranceCheck(Calendar now, Calendar checkTime){
		if(now.before(checkTime)){
			now.add(Calendar.MONTH, 1);
			return now.after(checkTime);
		}
		return false;
	}
	
	public static int annualCheck(Calendar now, Calendar checkTime){
		//如果现在时间在年检日期之前
		if(now.before(checkTime)){
			now.add(Calendar.MONTH, 1);
				//如果现在时间加一月大于年检日期,返回1;
				if(now.after(checkTime)){
					return 1;
				}
				return 0;
		}
		//则 现在时间大于 年检日期
		else{
			checkTime.add(Calendar.YEAR, 2);
				//如果现在时间在两年后年检日期之前
				if(now.before(checkTime)){
					now.add(Calendar.MONTH, 1);
					if(now.after(checkTime)){
						return 2;
					}
					return 0;
				}
				else{
					checkTime.add(Calendar.YEAR, 2);
					//如果现在时间在四年后年检日期之前
					if(now.before(checkTime)){
						now.add(Calendar.MONTH, 1);
						if(now.after(checkTime)){
							return 4;
						}
						return 0;
					}
					else{
						checkTime.add(Calendar.YEAR, 2);
						//如果现在时间在6年后年检日期之前
						if(now.before(checkTime)){
							now.add(Calendar.MONTH, 1);
							if(now.after(checkTime)){
								return 6;
							}
							return 0;
						}
						return 0;
					}
				}
		}
	}
}
