package com.example.alex.myapplication;

import android.app.Activity;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    ProgressBar myProgressBar;
    RelativeLayout relativeLayout;
    ScrollView scrollView;
    LinearLayout info;

    private String jsonShedule = "";
    private String titleShedule = "";
    private String weekShedule = "";
    private long startWeek;
    private long endWeek;

    private final String SITE = "http://gu-unpk.ru/schedule";
    private String group = "3094";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myProgressBar.setVisibility(View.VISIBLE);

        new ParseTask().execute();

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout.setPadding(0,0,0,0);
        scrollView = new ScrollView(getApplicationContext());
        scrollView.setPadding(0,0,0,0);
        relativeLayout.addView(scrollView);
        info = new LinearLayout(getApplicationContext());
        info.setPadding(0,0,0,0);
        info.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(info);

    }

    private class ParseTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            String resultJson = "";
            try {
                setStartAndEndWeek();
                Connection conn = Jsoup.connect(SITE);
                conn.method(Connection.Method.GET);
                Document doc = conn.url(SITE + "/" + group + "////" + String.valueOf(startWeek) + "/printschedule").ignoreContentType(true).get();
                resultJson = doc.toString();
                jsonShedule = resultJson;
                Document document = conn.url("http://gu-unpk.ru/schedule").get();
                titleShedule = document.getElementById("page-wrap").getElementById("inside").getElementById("content").getElementById("content_table").getElementsByAttribute("title_schedule").html();
            } catch (Exception e) {
                addWidget("Ошибка чтения: " + e.toString(), R.color.color_dayWeekStyle);
                printException(e);
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            myProgressBar.setProgress(100);
            myProgressBar.setVisibility(View.INVISIBLE);
            try {
                String input = "";
                if (strJson.length() > 45) input = strJson.substring(strJson.indexOf("["), strJson.indexOf("]") + 1);
                JSONArray mainArray = new JSONArray();
                if (input.length() != 0) mainArray = new JSONArray(input);
                if (mainArray.length() != 0) {
                    printShedule(mainArray);
                } else {
                    addWidget("<h3>Расписание на текущую неделю не найдено</h3>", R.color.color_firstPairStyle);
                    throw new Exception();
                }
                addWidget("http://gu-unpk.ru/schedule", R.color.color_dayWeekStyle);
            } catch (Exception e) {
                addWidget("Ошибка вывода: " + e.toString(), R.color.color_dayWeekStyle);
                printException(e);
            }
        }
    }

    public void setStartAndEndWeek(){
        Calendar calendar = new GregorianCalendar();
        startWeek = calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK)-2) * 24 * 60 * 60 * 1000L - (calendar.get(Calendar.HOUR_OF_DAY) - 3) * 60 * 60 * 1000L - calendar.get(Calendar.MINUTE) * 60 * 1000L - calendar.get(Calendar.SECOND) * 1000L;
        endWeek = startWeek + 6 * 24 * 60 * 60 * 1000L;
    }

    public void printException(Exception e){
        String stackTrace = "";
        for (StackTraceElement element : e.getStackTrace()){
            stackTrace += element.toString() + "\t\n";
        }
        addWidget(stackTrace, R.color.color_dayWeekStyle);
        addWidget(titleShedule, R.color.color_dayWeekStyle);
        addWidget(weekShedule, R.color.color_secondPairStyle);
        addWidget(jsonShedule, R.color.color_dayWeekStyle);
    }

    public void printShedule(JSONArray mainArray) throws JSONException{
        Format format = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        addWidget(String.format("<h3>Расписание занятий на период</h3>%s - %s", format.format(startWeek), format.format(endWeek)), R.color.color_dayWeekStyle);
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < mainArray.length(); i++) {
            JSONObject item = mainArray.getJSONObject(i);
            Pair pair = new Pair(item);
            pairs.add(pair);
        }
        for (int i = 1; i < 7; i++) {
            addWidget(DayOfWeek.getDayOfWeek(i).toString(), R.color.color_dayWeekStyle);

            List<Pair> pairsOnThisDay = new ArrayList<>();
            for (Pair pair : pairs) {
                if (pair.DayWeek == i) pairsOnThisDay.add(pair);
            }
            Collections.sort(pairsOnThisDay, new Comparator<Pair>() {
                @Override
                public int compare(Pair lhs, Pair rhs) {
                    return lhs.NumberLesson - rhs.NumberLesson;
                }
            });
            for (int j = 0; j < pairsOnThisDay.size(); j++) {
                if (j % 2 == 0)
                    addWidget(pairsOnThisDay.get(j).toString(), R.color.color_firstPairStyle);
                else
                    addWidget(pairsOnThisDay.get(j).toString(), R.color.color_secondPairStyle);
            }
        }
    }

    public void addWidget(String str, int color){
        TextView text = new TextView(this);
        text.setTextColor(getResources().getColor(R.color.color_text));
        text.setText(Html.fromHtml(str));
        text.setBackgroundResource(color);
        Linkify.addLinks(text, Linkify.WEB_URLS);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        info.addView(text);
    }

}