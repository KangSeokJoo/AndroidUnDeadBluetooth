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
import java.util.Calendar;

import static com.jinasoft.BlueTooth.BluetoothLeService.FK;

public class LogMain extends Service {

    public static boolean click__ = false;
    public static int cnt = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public String state = "android";
    public static String temp = "";

    int N;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        if (BLEConnectDialog.getBLEDialog() != null) {
                            BLEConnectDialog.getBLEDialog().setForkBattery(temp.replace(" ", "") + "%");
                        }
                        Log.d("확인5", temp +"/" + cnt);


                        DataTrans task = new DataTrans();
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://58.230.203.182/test/insert_data.php", state, temp + " %");
                        Thread.sleep(120000); //60초

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
            Log.d("확인3", "" + temp);
            try {

                JSONObject obj = new JSONObject(result);
                String sRES = obj.getString("Response");
                Response = sRES;

                if (Response.equals("Success")) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.clear();
                    editor.putString("state", state);
                    editor.putString("temp", temp);

                    editor.commit();


                } else {
                }


            } catch (JSONException ex) {
                ex.printStackTrace();
            }


        }


        @Override
        protected String doInBackground(String... params) {

            String state = (String) params[1];
            String temp = (String) params[2];


            String postParameters = "state=" + state + "&" + "temp=" + temp;
            try {

                URL url = new URL("http://58.230.203.182/test/insert_data.php");
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
