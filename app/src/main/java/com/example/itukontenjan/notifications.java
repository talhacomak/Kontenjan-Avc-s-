package com.example.itukontenjan;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class notifications extends AppCompatActivity {

    Context c1 = this;
    static AlarmManager alarmManager;
    public static String crn;
    public static String lecture;
    List<lectureCRN> list;

    public static NotificationManagerCompat notificationManagerCompat;

    SQLiteHelper db = new SQLiteHelper(c1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify);

        db.onCreate(db.getWritableDatabase());

        createNotifyChannel();

        Intent in = getIntent();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey("crn")){
                crn = in.getStringExtra("crn");
                lecture = in.getStringExtra("lecture");
                List<lectureCRN> check = db.get_row_list();
                if(check.size() == 0){
                    Calendar calendar = Calendar.getInstance();
                    long butClickTime = System.currentTimeMillis();
                    //int min = 1;
                    //int milisec = min*1000;
                    //int sec = 60 - calendar.SECOND;
                    int milisec = 10000;

                    Toast.makeText(c1, Integer.toString(milisec), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(c1, checkQuota.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c1, 12147 , intent, 0);
                    alarmManager =  (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,butClickTime+milisec,
                                1000 * 60, pendingIntent);

                    }
                }

                db.insert_row(new lectureCRN(crn, lecture));
            }
        }

        list = db.get_row_list();


        if(list.size() != 0){
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);

            Button [] nots = new Button[17];
            LinearLayout [] lays = new LinearLayout[17];
            TextView [] texts = new TextView[17];

            int j = 0;

            for(lectureCRN lc:list){
                final int i = j;
                lays[i] = new LinearLayout(c1);
                lays[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                lays[i].setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(lays[i]);

                texts[i] = new TextView(c1);
                texts[i].setText(lc.getLecture() + ": " + lc.getCrn());
                texts[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                texts[i].setGravity(Gravity.LEFT);
                texts[i].setPadding(15, 0 , 0, 0);
                lays[i].addView(texts[i]);

                nots[i] = new Button(c1);
                nots[i].setText("Delete Notification");
                nots[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                nots[i].setGravity(Gravity.RIGHT);
                nots[i].setPadding(0, 0 , 15, 0);
                lays[i].addView(nots[i]);
                nots[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.delete(lc);
                        if(db.get_row_list().size() == 0){
                            Intent intent = new Intent(c1, checkQuota.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(c1, 12147, intent, 0);
                            if(alarmManager != null){
                                alarmManager.cancel(pendingIntent);
                            }
                        }
                        layout.removeView(lays[i]);
                    }
                });
                j++;
            }
        }
    }

    public void createNotifyChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence Name = "LemubitReminderChannel";
            String description = "Channel for Lemubit reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", Name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}