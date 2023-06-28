package com.lycoo.commons;


import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 * xxx
 *
 * Created by lancy on 2019/5/14
 */
public class Java8Test {

    @Test
    public void test_localDataTime() {
        // 获取当前的日期时间
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("当前时间: " + currentTime);

        LocalDate date1 = currentTime.toLocalDate();
        System.out.println("date1: " + date1);

        Month month = currentTime.getMonth();
        int day = currentTime.getDayOfMonth();
        int seconds = currentTime.getSecond();

        System.out.println("月: " + month + ", 日: " + day + ", 秒: " + seconds);

        LocalDateTime date2 = currentTime.withDayOfMonth(10).withYear(2012);
        System.out.println("date2: " + date2);

        // 12 december 2014
        LocalDate date3 = LocalDate.of(2014, Month.DECEMBER, 12);
        System.out.println("date3: " + date3);

        // 22 小时 15 分钟
        LocalTime date4 = LocalTime.of(22, 15);
        System.out.println("date4: " + date4);

        // 解析字符串
        LocalTime date5 = LocalTime.parse("20:15:30");
        System.out.println("date5: " + date5);
    }

    @Test
    public void test_localDataTime2(){
        LocalDateTime currentTime = LocalDateTime.now();
//        LocalDateTime remoteTime = LocalDateTime.parse("1970-01-01 00:00:01", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        LocalDateTime remoteTime = LocalDateTime.parse("1970-01-01 00:00:01", "yyyy-MM-dd HH:mm:ss");
        LocalDateTime remoteTime = LocalDateTime.parse("1970-01-01 00:00:01", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.printf("currentTime: " + currentTime);
        System.out.printf("remoteTime: " + remoteTime);
        System.out.printf("11: " + remoteTime.isAfter(currentTime));
    }


}
