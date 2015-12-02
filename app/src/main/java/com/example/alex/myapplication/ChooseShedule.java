package com.example.alex.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Alex on 02.12.2015.
 */
public class ChooseShedule extends AppCompatActivity {

    RelativeLayout activity_steps;
    ProgressBar myProgressBar;
    ScrollView scrollView;
    LinearLayout info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        activity_steps = (RelativeLayout) findViewById(R.id.activity_steps);
        activity_steps.setPadding(0,0,0,0);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myProgressBar.setVisibility(View.VISIBLE);
        scrollView = new ScrollView(getApplicationContext());
        scrollView.setPadding(0,0,0,0);
        activity_steps.addView(scrollView);
        info = new LinearLayout(getApplicationContext());
        info.setPadding(0,0,0,0);
        info.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(info);

        Button buttonStart = new Button(getApplicationContext());
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
        buttonStart.setText("Start");
        activity_steps.addView(buttonStart);

        String url = "http://gu-unpk.ru/schedule/divisionlistforstuds";
        MyTask task = new MyTask(url);
        task.execute();


    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        private String result = "";
        private String url = "";

        public MyTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            String resultJson = "";
            try {
                Connection conn = Jsoup.connect(url);
                conn.method(Connection.Method.GET);
                Document doc = conn.url(url).ignoreContentType(true).get();
                resultJson = doc.toString();

            } catch (Exception e) {

            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            result = strJson;
            myProgressBar.setVisibility(View.INVISIBLE);
            TextView textView = new TextView(getApplicationContext());
            textView.setText(result);
            info.addView(textView);
        }

    }
}
