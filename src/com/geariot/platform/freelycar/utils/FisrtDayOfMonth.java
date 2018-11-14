package com.geariot.platform.freelycar.utils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Youhaidong(游海东)
 * @version V1.0
 * @Title FisrtDayOfMonth.java
 * @Package com.you.freemarker.model
 * @Description 获取某年某月的第一天
 */
public class FisrtDayOfMonth {
    /**
     * 获取某年某月的第一天
     *
     * @throws
     * @Title:getFisrtDayOfMonth
     * @Description:
     * @param:@param year
     * @param:@param month
     * @param:@return
     * @return:String
     */
    public static String getFisrtDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * @throws
     * @Title:main
     * @Description:
     * @param:@param args
     * @return: void
     */
    @Test
    public static void main(String[] args) {
        String firstDay = getFisrtDayOfMonth(2014, 5);
        System.out.println("脚本之家测试结果：");
        System.out.println("获取当前月的第一天：" + firstDay);
    }
}
