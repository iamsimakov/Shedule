package com.example.alex.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Alex on 30.10.2015.
 */
public class Pair {
    public int NumberSubGruop;
    public String TitleSubject;
    public String TypeLesson;
    public int NumberLesson;
    public int DayWeek;
    public String Korpus;
    public String NumberRoom;
    public String special;
    public String title;
    public int employee_id;
    public String fam;
    public String im;
    public String otch;
    public Date StartDate;
    public Date FinishDate;

    public Pair(JSONObject item) throws JSONException{
        this.NumberSubGruop = item.getInt("NumberSubGruop");
        this.NumberLesson = item.getInt("NumberLesson");
        this.DayWeek = item.getInt("DayWeek");
        this.employee_id = item.getInt("employee_id");

        this.TitleSubject = item.getString("TitleSubject");
        this.TypeLesson = item.getString("TypeLesson");
        this.Korpus = item.getString("Korpus");
        this.NumberRoom = item.getString("NumberRoom");
        this.special = item.getString("special");
        this.title = item.getString("title");
        this.fam = item.getString("fam");
        this.im = item.getString("im");
        this.otch = item.getString("otch");

        Long longStartDate = 0L;
//        if (item.get("StartDate") != null) longStartDate = item.getLong("StartDate");
        this.StartDate = new Date(longStartDate);

        Long longFinishDate = 0L;
//        if (item.get("FinishDate") != null) longFinishDate = item.getLong("FinishDate");
        this.FinishDate = new Date(longFinishDate);

    }

    @Override
    public String toString() {
        return "<center><b>" + TitleSubject + "</b><br>" + TypeLesson + " " + fam + " " + "<b>" + Korpus + "-" + NumberRoom + "</b></center>";
    }
}
