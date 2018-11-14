package com.geariot.platform.freelycar.utils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Youhaidong(游海东)
 * @version V1.0
 * @Title LastDayOfMonth.java
 * @Package com.you.freemarker.model
 * @Description 获取某月的最后一天
 * @date 2014-5-29 下午10:58:20
 */
public class LastDayOfMonth {
    /**
     * 获取某月的最后一天
     *
     * @throws
     * @Title:getLastDayOfMonth
     * @Description:
     * @param:@param year
     * @param:@param month
     * @param:@return
     * @return:String
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
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
        String lastDay = getLastDayOfMonth(2014, 5);
        System.out.println("脚本之家测试结果：");
        System.out.println("获取当前月的最后一天：" + lastDay);
    }
}