package com.example.itukontenjan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    /* UYGULAMADA ALARM KURULUYOR, ÇALIYOR
       SİLMEK İÇİN BUTONLAR OLUŞTURULMUYOR
       SQL VERİ TABANI İLE İLGİLİ
       SAYFAYI TEKRAR AÇINCA ESKİLER SİLİNİYOR
       OKUMADA YA DA KAYDETMEDE SIKINTI VAR
     */


    public static TextView resultText;
    Context c1 = this;
    public static EditText crn;
    public static EditText lecture;
    Notification mynot;

    static final int Contact_Request = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultText = (TextView) findViewById(R.id.t1);

        createNotifyChannel();

        lecture = (EditText) findViewById(R.id.Ed1);
        crn = (EditText) findViewById(R.id.Ed2);

        Button button = (Button) findViewById(R.id.b1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.sis.itu.edu.tr/tr/ders_programlari/LSprogramlar/prg.php?fb=" + lecture.getText().toString().toUpperCase();
                new RequestTask_Get().execute(url);
            }
        });

        Button button1 = (Button) findViewById(R.id.b2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(c1, notifications.class);
                startActivityForResult(in, Contact_Request);
            }
        });
    }



    class RequestTask_Get extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) { // ANDROİD MANİFESTE Intrnet EKLE!!!
            String dosya="";
            HttpURLConnection connection = null;
            BufferedReader br = null;

            try {
                java.net.URL url = new URL(uri[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                while ((satir = br.readLine()) != null){
                    Log.d("satir:", satir);
                    dosya += satir;
                }
                return dosya;
            } catch (Exception e){
                e.printStackTrace();
            }
            return "hata";
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            Log.d("result",result);
            Document doc = Jsoup.parse(result);
            Elements els = doc.getElementsByClass("dersprg").get(0).child(0).getElementsByTag("tr");
            int cont = 0;
            int enroll = 0;
            for (Element e:els){
                if(e.child(0).text().equals(crn.getText().toString())){
                    resultText.setText(e.child(8).text() + "/"  + e.child(9).text());
                    cont = Integer.parseInt(e.child(8).text());
                    enroll = Integer.parseInt(e.child(9).text());
                    break;
                }
            }
            if (cont > 0){
                LinearLayout layout = (LinearLayout) findViewById(R.id.lay1);
                layout.removeAllViews();
                Button not = new Button(c1);
                not.setText("Add Notification");
                not.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                not.setGravity(Gravity.CENTER);
                layout.addView(not);
                not.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(c1, notifications.class);
                        in.putExtra("crn", crn.getText().toString());
                        in.putExtra("lecture", lecture.getText().toString());
                        startActivityForResult(in, Contact_Request);
                        layout.removeAllViews();
                    }
                });
            }
        }
    }

    public void createNotifyChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence Name = "ReminderChannel";
            String description = "reminder channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", Name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
