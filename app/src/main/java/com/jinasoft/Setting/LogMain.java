package com.jinasoft.Setting;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.jinasoft.BlueTooth.AlarmReceiver;
import com.jinasoft.BlueTooth.BluetoothLeService;
import com.jinasoft.BlueTooth.DataProcess;
import com.jinasoft.Main.BLEConnectDialog;
import com.jinasoft.Main.MainActivity;
import com.jinasoft.Main.R;
import com.jinasoft.Setting.Alarm.DeviceBootReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jinasoft.BlueTooth.BluetoothLeService.FK;

public class LogMain extends Service {

    public static boolean click__ = false;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public String state = "android_3";
    public static String temp = "";
    public int cnt = 0;
    public int cnt2 = 0;
    public int j = 0;
    public ArrayList<String> dl = new ArrayList<>();
    public ArrayList<String> bl = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        join();
        initialize();
        Log.d("많이", "찍히나");
        return START_STICKY;
    }

    public void join() {

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        MainActivity mainActivity = new MainActivity();

        pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
        editor = pref.edit();
        ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3600000); // 300000 5분 3600000시간

                        if (0 < DataProcess.dateList2.size()) {

                            for (int i = 0; i < DataProcess.dateList2.size(); i++) {
                                Log.d("확인5",i + "번째");
                                cnt++;
                                    dl.add(DataProcess.dateList2.get(i));
                                    bl.add(DataProcess.batteryList2.get(i));
                                DataTrans task = new DataTrans();task.executeOnExecutor(THREAD_POOL, "http://58.230.203.182/test/insert_data_2.php",
                                        state, dl.get(i),bl.get(i) + " %");
                                }
                            for (int i = 0; i < DataProcess.dateList.size(); i++) {
                                Log.d("확인5",i + "번째");
                                cnt++;
                                dl.add(DataProcess.dateList.get(i));
                                bl.add(DataProcess.batteryList.get(i));
                                DataTrans task = new DataTrans();task.executeOnExecutor(THREAD_POOL, "http://58.230.203.182/test/insert_data_2.php",
                                        state, dl.get(i),bl.get(i) + " %");
                            }

                        }else{

                            for (int i = 0; i < DataProcess.dateList.size(); i++) {
                                Log.d("확인5",i + "번째");
                                cnt++;
                                dl.add(DataProcess.dateList.get(i));
                                bl.add(DataProcess.batteryList.get(i));

                                DataTrans task = new DataTrans();task.executeOnExecutor(THREAD_POOL, "http://58.230.203.182/test/insert_data_2.php",
                                        state, dl.get(i),bl.get(i) + " %");
                            }

                        }DataProcess.Saving = true;

                        Log.d("확인2", "배열크기 날짜 :" + DataProcess.dateList.size() +"\n배열크기 배터리"+ DataProcess.batteryList.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    public class DataTrans extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String Response;
            Log.d("확인3","cnt:" + cnt + "  cnt2:"+cnt2+"    d1:"+dl.size());
            try {

                JSONObject obj = new JSONObject(result);
                String sRES = obj.getString("Response");
                Response = sRES;

                if (Response.equals("Success")) {

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();


                    editor.clear();
                    editor.putString("state", state);
                    editor.putString("ble_date", dl.get(cnt2));
                    editor.putString("battery", bl.get(cnt2));

                    editor.commit();
                    cnt2++;
                    Log.d("확인7", ""+cnt);
                    if (cnt2 == cnt && j == 0){
                            DataProcess.dateList.clear();
                            DataProcess.batteryList.clear();
                            dl.clear();
                            bl.clear();
                            cnt = 0 ;
                            cnt2 = 0;
                            DataProcess.Saving = false;
                            j = 1;

                    }else if (j == 1 && cnt == cnt2
                    ){
                        DataProcess.dateList.clear();
                        DataProcess.batteryList.clear();
                        DataProcess.dateList2.clear();
                        DataProcess.batteryList2.clear();
                        dl.clear();
                        bl.clear();
                        cnt = 0 ;
                        cnt2 = 0;
                        DataProcess.Saving = false;
                        j=0;

                    }


                }else {
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String state = (String) params[1];
            String ble_date = (String) params[2];
            String battery = (String) params[3];


            String postParameters = "state=" + state + "&" + "ble_date=" + ble_date + "&" + "battery=" + battery;
            try {

                URL url = new URL("http://58.230.203.182/test/insert_data_2.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();


                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();


            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean initialize() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요.");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1", "undead_service", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);


        return true;
    }
        @Override
        public void onDestroy() {
            super.onDestroy();
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        }

        @Override
        public void onTaskRemoved (Intent rootIntent){
            super.onTaskRemoved(rootIntent);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
