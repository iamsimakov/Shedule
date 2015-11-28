package com.example.alex.myapplication;

import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ProgressBar myProgressBar;
    RelativeLayout relativeLayout;
    ScrollView scrollView;
    LinearLayout info;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);  //связываем прогресс бар с переменной такого же типа
        myProgressBar.setVisibility(View.VISIBLE); //отображаем прогресс бар, изначально он невидим,

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

        private String jsonShedule = "";
        private String titleShedule = "";
        private String weekShedule = "";

        @Override
        protected String doInBackground(Void... params) {

            String resultJson = "";

            // получаем данные с внешнего ресурса
            try {
                Connection conn = Jsoup.connect("http://gu-unpk.ru/schedule/");
                conn.method(Connection.Method.GET);
                conn.header("x-requested-with", "XMLHttpRequest");
                conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0");
                conn.header("Referer", "http://gu-unpk.ru/schedule");
                conn.header("Host", "gu-unpk.ru");
                conn.header("Cookie", "blind-font-size=fontsize-normal; blind-colors=color1; blind-font=sans-serif; blind-spacing=spacing-small; blind-images=imagesoff; _ga=GA1.2.1710176856.1446161737; _gat=1; _ym_visorc_16652224=w; CurrentSchedule=lesson; StudPrepAudit=1; divisionForStuds=12; kurs=5; group=3094");
                conn.header("Connection", "keep-alive");
                conn.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
                conn.header("Accept", "*/*");
                Document doc = conn.url("http://gu-unpk.ru/schedule/3094////").ignoreContentType(true).get();
                resultJson = doc.toString(); //буфер - это объект, переводим в строку

                jsonShedule = resultJson;

                Document document = conn.url("http://gu-unpk.ru/schedule").get();

                titleShedule = document.getElementById("page-wrap").getElementById("inside").getElementById("content").getElementById("content_table").getElementsByAttribute("title_schedule").html();
            } catch (Exception e) {
                resultJson = "Ошибка чтения: " + e.toString();
            }
            return resultJson; //возвращаем строку и передаем ее в onPostExecute
        }


        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            myProgressBar.setProgress(100);  //прогресс бар ставим 100%
            myProgressBar.setVisibility(View.INVISIBLE); //убираем его с глаз
            try {  //оборачиваем все в try ... catch на случай ошибок
                String input = "";
                if (strJson.length() > 45) input = strJson.substring(strJson.indexOf("["), strJson.indexOf("]") + 1);
                JSONArray mainArray = new JSONArray();
                if (input.length() != 0) mainArray = new JSONArray(input);

                if (mainArray.length() != 0) {
                    List<Pair> pairs = new ArrayList<>();

                    for (int i = 0; i < mainArray.length(); i++) {
                        JSONObject item = mainArray.getJSONObject(i);
                        Pair pair = new Pair(item);
                        pairs.add(pair);
                    }

                    for (int i = 1; i < 7; i++) {
                        if (i == 1) addWidget("Понедельник", R.color.color_dayWeekStyle);
                        if (i == 2) addWidget("Вторник", R.color.color_dayWeekStyle);
                        if (i == 3) addWidget("Среда", R.color.color_dayWeekStyle);
                        if (i == 4) addWidget("Четверг", R.color.color_dayWeekStyle);
                        if (i == 5) addWidget("Пятница", R.color.color_dayWeekStyle);
                        if (i == 6) addWidget("Суббота", R.color.color_dayWeekStyle);
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
                } else addWidget("<h2>Расписание на текущую неделю не найдено</h2>", R.color.color_firstPairStyle);

//                addWidget(titleShedule, R.color.color_dayWeekStyle);
//                addWidget(weekShedule, R.color.color_secondPairStyle);
//                addWidget(jsonShedule, R.color.color_dayWeekStyle);

            } catch (Exception e) {
                String stackTrace = "";
                for (StackTraceElement element : e.getStackTrace()){
                    stackTrace += element.toString() + "\t\n";
                }
                addWidget("Ошибка вывода: " + e.toString() + "\n" + stackTrace, R.color.color_dayWeekStyle);

                addWidget(titleShedule, R.color.color_dayWeekStyle);
                addWidget(weekShedule, R.color.color_secondPairStyle);
                addWidget(jsonShedule, R.color.color_dayWeekStyle);

            }
        }
    }


    public void addWidget(String str, int color){
        TextView text = new TextView(getApplicationContext());
        text.setTextColor(getResources().getColor(R.color.color_text));
        text.setText(Html.fromHtml(str));
        text.setBackgroundResource(color);
        info.addView(text);
    }

}