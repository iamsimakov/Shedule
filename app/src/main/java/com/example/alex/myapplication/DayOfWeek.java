package com.example.alex.myapplication;

/**
 * Created by Alex on 01.12.2015.
 */
public enum DayOfWeek {
    Понедельник,
    Вторник,
    Среда,
    Четверг,
    Пятница,
    Суббота;


    public static DayOfWeek getDayOfWeek(int i){
        return DayOfWeek.values()[i-1];
    }
}
