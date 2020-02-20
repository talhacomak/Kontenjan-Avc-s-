package com.example.itukontenjan;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class checkQuota extends BroadcastReceiver {

    List<lectureCRN> list;
    Context c1;
    SQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent){
        c1 = context;
        db = new SQLiteHelper(c1);
        list = db.get_row_list();
        List<String> li = new ArrayList<>();
        for (lectureCRN lec:list){
            li.add(lec.lecture);
        }

        LinkedHashSet<String> hashSet = new LinkedHashSet<>(li);

        List<String> listWithoutDuplicates = new ArrayList<>(hashSet);

        for (String str:listWithoutDuplicates){
            String url = "http://www.sis.itu.edu.tr/tr/ders_programlari/LSprogramlar/prg.php?fb=" + str.toUpperCase();
            new RequestTask_Get().execute(url);
        }
    }

    class RequestTask_Get extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) { // ANDROİD MANİFESTE EKLE!!!
            String dosya = "";
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
                while ((satir = br.readLine()) != null) {
                    Log.d("satir:", satir);
                    dosya += satir;
                }
                return dosya;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "hata";
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            Log.d("result", result);
            Document doc = Jsoup.parse(result);
            Elements els = doc.getElementsByClass("dersprg").get(0).child(0).getElementsByTag("tr");
            int cont = 0;
            int enroll = 0;

            list = db.get_row_list();

            for(lectureCRN lec:list){
                for (Element e : els) {
                    if (e.child(0).text().equals(lec.getCrn())) {
                        cont = Integer.parseInt(e.child(8).text());
                        enroll = Integer.parseInt(e.child(9).text());
                        if (cont > 0 & cont > enroll) {
                            final String url = "http://www.sis.itu.edu.tr/tr/ders_programlari/LSprogramlar/prg.php?fb=" + lec.getLecture().toUpperCase();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            PendingIntent pendingIntent = PendingIntent.getActivity(c1, 0, intent, Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(c1, "notifyLemubit")
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setContentTitle("Kontenjan detected")
                                    .setContentText(lec.getLecture().toUpperCase() + "- CRN: " + lec.getCrn())
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            notifications.notificationManagerCompat = NotificationManagerCompat.from(c1);
                            notifications.notificationManagerCompat.notify(Integer.parseInt(lec.getCrn()), builder.build());
                        }
                        break;
                    }
                }
            }

        }
    }
}
