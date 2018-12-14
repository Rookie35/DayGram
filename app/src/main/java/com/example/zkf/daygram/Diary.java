package com.example.zkf.daygram;

import java.io.Serializable;
//保存每天日记的类，序列化方便保存
public class Diary implements Serializable {
    private int year;//年份
    private int month;//月份
    private int day;//日期
    private String content=null;//日记内容

    public int getYear(){
        return this.year;
    }

    public void setYear(int year){
        this.year = year;
    }

    public int getMonth(){
        return this.month;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public int getDay(){
        return this.day;
    }

    public void setDay(int day){
        this.day = day;
    }

    public String getContent(){
        return this.content;
    }

    public void setContent(String Content){
        this.content = Content;
    }
}
