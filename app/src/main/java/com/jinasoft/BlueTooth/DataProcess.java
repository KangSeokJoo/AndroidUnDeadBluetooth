package com.jinasoft.BlueTooth;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.jinasoft.Main.BLEConnectDialog;
import com.jinasoft.Setting.LogMain;
import com.jinasoft.Main.MainActivity;
import com.jinasoft.Main.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_PRIVATE;
import static com.jinasoft.BlueTooth.BluetoothLeService.FK;
import static com.jinasoft.BlueTooth.BluetoothLeService.FS;
import static com.jinasoft.BlueTooth.BluetoothLeService.SP;

import static com.jinasoft.Main.MainActivity.SET_ALLCOUNT;
import static com.jinasoft.Main.MainActivity.SET_AVRGTIME;
import static com.jinasoft.Main.MainActivity.SET_FAIL;
import static com.jinasoft.Main.MainActivity.SET_NOWTIME;
import static com.jinasoft.Main.MainActivity.SET_PREVTIME;
import static com.jinasoft.Main.MainActivity.SET_STARTTIME;
import static com.jinasoft.Main.MainActivity.SET_SUCCESS;
import static com.jinasoft.Main.MainActivity.SET_SUCCESSCOUNT;
import static com.jinasoft.Main.MainActivity.SET_SUCCESSPERCENT;
import static com.jinasoft.Main.MainActivity.SET_USETIME;

public class DataProcess {

    // 데이터 변환

    private String name;
    boolean isRecv = false, isSucceed = true;
    private String datas = "";
    private static long startTime = 0, endTime = 0;
    private static long startMouse = 0;
    private static Date startDate;
    private static int prevTime = 0;
    private static int allCount = 0, successCount = 0;
    private int rawMouse = 0;
    private static int MOUSE_STATUS = 0;
    private int mouseStatus = 0, initMouse = 0;
    private static int targetTime;

    private static ArrayList<Integer> mTimeArray = new ArrayList<>();

    String DayExTimer;
    Timer ExTimer;
    private int c = 0;


    public static ArrayList<String> dateList = new ArrayList<>();
    public static ArrayList<String> batteryList = new ArrayList<>();
    public static boolean Saving = false;

    public static ArrayList<String> dateList2 = new ArrayList<>();
    public static ArrayList<String> batteryList2 = new ArrayList<>();

    public static void clear() {
        startTime = 0;
        endTime = 0;
        startMouse = 0;
        allCount = 0;
        successCount = 0;
        prevTime = 0;
        MOUSE_STATUS = 0;
        mTimeArray.clear();

    }

    public DataProcess(String _name) {
        name = _name;
        mouseStatus = 0;
//        startTime = 0;
//        endTime = 0;
//        startMouse = 0;
//        allCount = 0;
//        successCount = 0;
//        prevTime = 0;
//        mTimeArray.clear();
    }

    public void process () {
//            Log.d(name,data);
//
//        Log.d("@@@@", name + " : " + data);

        final SharedPreferences pref = MainActivity.getMain().getSharedPreferences("info", MODE_PRIVATE);

        String _data = "";

        isRecv = false;

        String[] _datas = datas.split("2C");
        // 0 : 44 02 0B
        // 1 : Mouse
        // 2 : X
        // 3 : Y
        // 4 : Z
        // 5 : Battery, FF 73 0D 0A
        String battery = new String(BluetoothLeService.hexStringToByteArray(_datas[5].replace(" ", "").substring(0, 6)));


            new Thread(new Runnable() {
                @Override
                public void run() {
                    c++;
                        long now = System.currentTimeMillis(); // 현재시간 받아오기
                        Date date = new Date(now); // Date 객체 생성
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String nowTime = sdf.format(date);

                       if (c == 47 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                        if (Saving == true){
                            dateList2.add(nowTime);
                            batteryList2.add(battery);
                            c = 0;
                        }else {
                            dateList.add(nowTime);
                            batteryList.add(battery);
                            c = 0;
                        }
                           Log.d("확인10", "" + dateList.size()+"     "+dateList2.size());
                        }if (c == 30 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                        if (Saving == true) {
                            dateList2.add(nowTime);
                            batteryList2.add(battery);
                            c = 0;
                        }else{
                            dateList.add(nowTime);
                            batteryList.add(battery);
                            c = 0;
                        }
                        Log.d("확인10", "" + dateList.size()+"     "+dateList2.size());
                    }
                }
            }).start();









        if(name.contains("SP")) {
            if(BLEConnectDialog.getBLEDialog() != null){
                if(BluetoothLeService.getState(SP) ==2 ) {
                    BLEConnectDialog.getBLEDialog().setSpoonBattery(battery.replace(" ", "") + "%");
                    BLEConnectDialog.getBLEDialog().ChangeStateSpoonSwitch("on");
//                        BLEConnectDialog.getBLEDialog().setSpoonBattery("80%");
                }
            }
        }else if(name.contains("FK")){
            if(BLEConnectDialog.getBLEDialog() != null){
                if(BluetoothLeService.getState(FK) ==2 ) {
                    BLEConnectDialog.getBLEDialog().setForkBattery(battery.replace(" ", "") + "%");
                    BLEConnectDialog.getBLEDialog().ChangeStateForkSwitch("on");
                }
            }
        }else if(name.contains("FS")){
            if(BLEConnectDialog.getBLEDialog() != null){
                if(BluetoothLeService.getState(FS) ==2 ) {
                    BLEConnectDialog.getBLEDialog().setForkSpoonBattery(battery.replace(" ", "") + "%");
                    BLEConnectDialog.getBLEDialog().ChangeStateForkSpoonSwitch("on");
                }
            }
        }
//                if(BLEConnectDialog.getBLEDialog() != null) {
//                    BLEConnectDialog.getBLEDialog().setSpoonBattery(battery + "%");
//                }

//                Log.d("3번 변환후 데이터",battery);
//                MainActivity.getMain().ConSuccess();
//                if(RealTimeFoodInfoActivity.getRealTimeActivity() !=null) {
//                    RealTimeFoodInfoActivity.getRealTimeActivity().ConSuccess();
//                }
//                Log.d("@@@@", this.name + " Battery : " + battery);
//                String Mouse2 = new String(BluetoothLeService2.hexStringToByteArray(_datas[1]));


        // 16진수 String 을 byte 배열로 변경   -> byte 배열을 String 으로 변경  -> int로 변환
        String MOUSE = new String(BluetoothLeService.hexStringToByteArray(_datas[1].replace(" ", ""))).replace(" ", "");
        MOUSE = MOUSE.replaceAll("D", "");
//                Log.d("StringMouse",MOUSE);
        String X = new String(BluetoothLeService.hexStringToByteArray(_datas[2].replace(" ", ""))).replace(" ", "");
//                Log.d("StringX",X);
        X = X.replaceAll("D", "");
        String Y = new String(BluetoothLeService.hexStringToByteArray(_datas[3].replace(" ", ""))).replace(" ", "");
//                Log.d("StringY",Y);
        Y = Y.replaceAll("D", "");
        String Z = new String(BluetoothLeService.hexStringToByteArray(_datas[4].replace(" ", ""))).replace(" ", "");
//                Log.d("StringZ",Z);
        Z = Z.replaceAll("D", "");


        int mouse = Integer.parseInt(MOUSE);
        rawMouse = mouse;
        if (initMouse == 0) {
            initMouse = mouse;
            mouse -= initMouse;
        } else
            mouse -= initMouse;

        int x = Integer.parseInt(X);

        int y = Integer.parseInt(Y);

        int z = Integer.parseInt(Z);


        _data += String.format(this.name + " X raw : %d, Y raw : %d, Z raw : %d, Mouse raw : %d\n", x, y, z, mouse);
//                _data += String.format("apSlim" + " X raw : %d, Y raw : %d, Z raw : %d, Mouse raw : %d\n", x, y, z, mouse);

        if (mouse > 32768) mouse -= 65536;                                                       // 65536이 x,y,z 의 최대값
        if (x > 32768) x -= 65536;                                                                    // 중간이상으로 값이 올라가면 최대값을 뺀후 절대값으로 roll & pitch를 측정
        if (y > 32768) y -= 65536;
        if (z > 32768) z -= 65536;
        // Log.d("@@@@", this.name + " mouse : " + mouse);
//                // Log.d("@@@@", this.name + " X : " + x);
//                // Log.d("@@@@", this.name + " Y : " + y);
//                // Log.d("@@@@", this.name + " Z : " + z);

//                Log.d("mouse2",String.valueOf(mouse));

        if(z == 0) z = 1;
        double roll = (Math.atan(y / z)) * 180 / Math.PI;
        double pitch = (Math.atan(x / z)) * 180 / Math.PI;
//                Log.d("@!@!","roll : " +roll +"  pitch : "+ pitch);
        roll = Math.abs(roll);
        pitch = Math.abs(pitch);
        // Log.d("@@@@", this.name + " roll : " + roll); // 70
        // Log.d("@@@@", this.name + " pitch : " + pitch); // > 70
        _data += String.format(this.name + " Roll : %.3f, Pitch : %.3f\n", roll, pitch);

//                sendHandler();

        // Log.d("@@@@", this.name + " Init Mouse : " + initMouse);
        // Log.d("@@@@", this.name + " Mouse Status : " + mouseStatus);
        // Log.d("@@@@", this.name + " ALL Status : " + MOUSE_STATUS);



        _data += String.format(this.name + " Mouse Status : %d\n", mouseStatus);
        _data += String.format(this.name + " All Mouse Status : %d\n", MOUSE_STATUS);
        _data += String.format(this.name + " Time : %d\n", (endTime - startTime));


        datas = "";


//                Log.d("MOUTH_STATUS1", String.valueOf(mouseStatus));
//                Log.d("MOUTH_STATUS2", String.valueOf(MOUSE_STATUS));
//                Log.d(name,data);

        if(mouseStatus != MOUSE_STATUS) {
            if(name.equals("ApSlim-SP")){
                if(MOUSE_STATUS==2){
                    mouseStatus = MOUSE_STATUS;
                }else if(MOUSE_STATUS ==0){
                    mouseStatus = MOUSE_STATUS;
                }
            }
            if(name.equals("ApSlim-FK")){
                if(MOUSE_STATUS==2){
                    mouseStatus = MOUSE_STATUS;
                }else if(MOUSE_STATUS ==0){
                    mouseStatus = MOUSE_STATUS;
                }
            }
            if(name.equals("ApSlim-FS")){
                if(MOUSE_STATUS==2){
                    mouseStatus = MOUSE_STATUS;
                }else if(MOUSE_STATUS ==0){
                    mouseStatus = MOUSE_STATUS;
                }
            }
            return;
        }
        if(startTime!=0) {                                  // 단계가 올라가야 카운트가 시작됨
            endTime = System.currentTimeMillis();

            if (startTime == 0) {
                startTime = endTime;
                startDate = new Date();
                startDate.setTime(startTime);
            }
        }

        if (MOUSE_STATUS == 0 && mouse >= 100 && mouse <= 303 && (roll > 45 || pitch > 45)) {  //마우스 단계 (섭취단계) 0  && 저항값이 100보다 크고 303 보다 작음 && R or P 45 이상이면

            endTime = System.currentTimeMillis();
            if(startTime == 0) {

                startTime = endTime;
                startDate = new Date();
                startDate.setTime(startTime);
            }

            MOUSE_STATUS = 1;                                                                                           //섭취 단계 1
            mouseStatus = MOUSE_STATUS;

        } else if (MOUSE_STATUS == 0 && mouse >= 303 && (roll > 45 || pitch > 45)) {

            endTime = System.currentTimeMillis();
            if(startTime == 0) {

                startTime = endTime;
                startDate = new Date();
                startDate.setTime(startTime);
            }



            //마우스 단계 (섭취단계) 0 && 저항값 303이상  && R&P 45 이상
            MOUSE_STATUS = 1;                                                                                           //섭취 단계 1
            mouseStatus = MOUSE_STATUS;
        }
//                if(MOUSE_STATUS == 1 && prevMouse >= 100 && prevMouse < 200 && mouse > 200) {
        if (MOUSE_STATUS == 1 && mouse <= 80) {
            //섭취단계 1 &&  저항 80이하
            MOUSE_STATUS = 2;                                                                                          //단계 2
            mouseStatus = MOUSE_STATUS;
        }
//                Log.d("Mouth", String.valueOf(mouse));
        if (MOUSE_STATUS == 2 && mouse >= 310 && roll <= 45) {

            //섭취단계 2 && 저항값 310 이상 && roll 45이상
            MOUSE_STATUS = 3;                                                                                            //단계 3
            mouseStatus = MOUSE_STATUS;
            startMouse = endTime;                                                                                       // startMouse = 단계가 3으로 넘어간 시간
//                    Log.d("endTime", String.valueOf(endTime))
//                    Log.d("startMouse", String.valueOf(startMouse));
        }
        if (MOUSE_STATUS == 3) {
            //섭취 단계 3 에서
            if (endTime - startMouse >= (2 * 1000)) {                                                               // 한번 섭취 끝 시간 - 3단계로 넘어온 시간
//                        Log.d("3endTime", String.valueOf(endTime));                                                  //3단계에서(입에 숟가락을 넣음) 2초이상 지나가면 false
//                        Log.d("3startMouse", String.valueOf(startMouse));
//                        Log.d("@@@@@", "2 second : " + isSucceed);
                isSucceed = false;
            }
        }


        // Log.d("@@@@", this.name + " Time : " + (endTime - startTime));
        // Log.d("@@@@", this.name + " array_mouse : " + array_mouse.size());

        if (MOUSE_STATUS == 3 && mouse < 90) {
            //섭취 단계  3 && 저항 90이상
            int foodTime = (int) ((endTime - startTime) / 1000);                                                    // (끝시간 - 시작시간) / 1000 =  한번 섭취 시간 (foodTime)
//                    Log.d("endTime", String.valueOf(endTime));
//                    Log.d("startMouse", String.valueOf(startMouse));
//                    Log.d("startTime", String.valueOf(startTime));
//                    Log.d("foodTime", String.valueOf(foodTime));
            mTimeArray.add(foodTime);                                                                                   // mTimeArray 배열에 섭취 시간 추가
            prevTime = foodTime;                                                                                          // prevTime =  방금전 섭취 시간
            initMouse = rawMouse;                                                                                          //initmouse
            // Log.d("@@@@", this.name + " foodTime : " + foodTime);
//                    Log.d("@@@@@", "succeed : " + isSucceed);
            startTime = endTime;                                                                                             // 한번 섭취가 끝나면 끝시간이 다시 시작시간이 됨
            MOUSE_STATUS = 0;
            mouseStatus = 0;
            allCount++;                                                                                                         //섭취 횟수 +1
//                    Log.d("allCount", String.valueOf(allCount));
            Handler handler = null;
            if(MainActivity.getMain() != null)
                handler = MainActivity.getMain().getHandler();
            Handler handler1 = null;
            if (isSucceed && foodTime > targetTime) {                                                                  // 1회 섭취 시간이 목표시간 보다 길면 (성공)
                successCount++;                                                                                                // 성공 횟수 +1
                MainActivity.getMain().writeBLE(name, "44 00 06 03 02 05 00 00 FF 73");
                MainActivity.getMain().writeBLE(name, "44 00 04 04 00 00 FF 73");
                if(handler != null)
                    handler.sendEmptyMessage(SET_SUCCESS);
                if (handler1 != null)
                    handler1.sendEmptyMessage(SET_SUCCESS);
            } else {
                MainActivity.getMain().writeBLE(name, "44 00 06 03 00 00 02 05 FF 73");
//                        MainActivity.getMain().vibration(name);
                if(handler != null)
                    handler.sendEmptyMessage(SET_FAIL);
                if (handler1 != null)
                    handler1.sendEmptyMessage(SET_FAIL);
                // 알림음 설정


                Toast.makeText(MainActivity.getMain(), R.string.FasterThenTargetTime, Toast.LENGTH_LONG).show();
//                        }
            }
            isSucceed = true;                                                       //false로 바뀌어 있었다면 true로 바꿔주기
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        }
        if(startTime != 0) {
            sendHandler();
        }

        _data += String.format(this.name + " Count : %d, Success : %d\n", allCount, successCount);

    }

    public void displayData (String data){
//        Log.d("@@@@", name + " : " + data);
        try {
//            if (Layout06TestActivity.getTestActivity() != null) {
//                Layout06TestActivity.getTestActivity().addData(this.name, data);
//            }
            if(data.contains("44") && data.contains("FF 73")) {
                datas += data;
                process();
            }
            else {
                if (data.contains("44")) {
                    if (isRecv) {
//                        isRecv = false;
                        datas = data;
                    } else {
                        isRecv = true;
                        datas += data;
                    }
                }
                if (isRecv && data.contains("FF 73")) {     //FF 73 을 포함하면
                    datas += data;
                    process();
                }
            }
        } catch (Exception e) {
            datas = "";
//            Log.d("@@@@", this.name + " Datas : " + datas);
            e.printStackTrace();
//            if(getTestActivity() != null) {
//                getTestActivity().addData(this.name, datas);
//                getTestActivity().addData(this.name, e.toString());
//            }

//            if(Layout06TestActivity.getTestActivity() != null) {
//                Layout06TestActivity.getTestActivity().addData(this.name, datas);
//                Layout06TestActivity.getTestActivity().addData(this.name, e.toString());
//            }
        }
    }

    private void sendHandler() {

        if (MainActivity.getMain() != null) {    //RealTimeActivity의 Thread에서 메시지를 전달하게함.
            Handler handler = MainActivity.getMain().getHandler();
            {
                Message msg = handler.obtainMessage();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                msg.what = SET_STARTTIME;
                msg.obj = sdf.format(startDate);
                handler.sendMessage(msg);
            }
            {
                Message msg = handler.obtainMessage();
                long _startTime = startDate.getTime();
                long _useTime = endTime - _startTime;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                msg.what = SET_USETIME;
                msg.obj = sdf.format(new Date(_useTime));
                handler.sendMessage(msg);
            }
            {
                Message _msg = handler.obtainMessage();
                _msg.what = SET_PREVTIME;
                _msg.obj = prevTime;
                handler.sendMessage(_msg);
            }
            {
                Message msg = handler.obtainMessage();
                long _foodTime = endTime - startTime;
                msg.what = SET_NOWTIME;
                msg.obj = (int) (_foodTime / 1000);
                handler.sendMessage(msg);
            }
            {
                Message msg = handler.obtainMessage();
                msg.what = SET_ALLCOUNT;
                msg.obj = allCount;
                handler.sendMessage(msg);
            }
            {
                Message msg = handler.obtainMessage();
                msg.what = SET_SUCCESSCOUNT;
                msg.obj = successCount;
                handler.sendMessage(msg);
            }
            {
                Message msg = handler.obtainMessage();
                msg.what = SET_SUCCESSPERCENT;
                msg.obj = ((successCount > 0) ? ((double) successCount / (double) allCount) : 0);
                handler.sendMessage(msg);
            }
            {
                int sum = 0;
                double avrg;
                for (int i : mTimeArray) {
                    sum += i;
                }
                if (mTimeArray.size() >= 1) {
                    avrg = sum / mTimeArray.size();
                    Message msg = handler.obtainMessage();
                    msg.what = SET_AVRGTIME;
                    msg.obj = avrg;
                    handler.sendMessage(msg);
                }else{
                    avrg = 0;
                    Message msg = handler.obtainMessage();
                    msg.what = SET_AVRGTIME;
                    msg.obj = avrg;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    public static void setStartTimeZero(){
        startTime = 0;
    }


    public void ClearAll(){
//        isRecv = false;
//        isSucceed = true;
//        startTime = 0;
        endTime = 0;                                   //starttime = ble연결시간 , endtime = 현재시간
        startMouse = 0;                                                    //입에 들어간 시간 ( 3단계로 넘어간 시간)
        prevTime = 0;                                                        //방금 섭취의 소요시간
        allCount = 0;
        successCount = 0;                                 //전체 섭취 횟수 , 성공적 섭취 횟수(입에서 2초이상 x , 목표시간 보다 길음)
        rawMouse = 0;                                                                  //mouse 와 같음 ( 저항값)
        MOUSE_STATUS = 0;                                                 // 현재 섭취 단계
        mouseStatus = 0;
        initMouse = 0;                                         //현재 섭취단계  ,  initMouse = 저항값
        mTimeArray.clear();


//        sendHandler();
    }

}
