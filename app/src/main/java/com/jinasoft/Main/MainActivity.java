package com.jinasoft.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinasoft.BlueTooth.BluetoothLeService;
import com.jinasoft.BlueTooth.DataProcess;
import com.jinasoft.BlueTooth.SampleGattAttributes;
import com.jinasoft.Setting.LogMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jinasoft.BlueTooth.BluetoothLeService.DEVICE_NAME;
import static com.jinasoft.BlueTooth.BluetoothLeService.FK;
import static com.jinasoft.BlueTooth.BluetoothLeService.FS;
import static com.jinasoft.BlueTooth.BluetoothLeService.SP;
import static com.jinasoft.BlueTooth.BluetoothLeService.STATE_CONNECTED;

public class MainActivity extends AppCompatActivity {

    private int mStatus;
    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED = 1;
    private static final int SUCCESS = 2;
    private static final int FAIL = 3;

    public static final int SET_PREVTIME = 101;
    public static final int SET_NOWTIME = 102;
    public static final int SET_TARGETTIME = 103;

    public static final int SET_STARTTIME = 11;
    public static final int SET_USETIME = 12;
    public static final int SET_ALLCOUNT = 13;
    public static final int SET_SUCCESSCOUNT = 14;
    public static final int SET_SUCCESSPERCENT = 15;
    public static final int SET_AVRGTIME = 16;

    public static final int SET_SUCCESS = 17;
    public static final int SET_FAIL = 18;
    public String PrevTime,StartTime,UseTime,FoodCount, SuccessFoodCount, SuccessPercent, AvgFoodSpeed;
    public String NowFoodState;
    public String NowTime;

    static int FKStateCon = 0;
    static int FSStateCon = 0;
    static int SPStateCon = 0;

    private static MainActivity My = null;

    private Handler nHandler;

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mLocalBluetoothGattCharacteristic;

    String NotifyState;

    SharedPreferences pref ;
    SharedPreferences.Editor editor;

    private Vibrator vibrator;


    int SPConCount = 0;
    int FKConCount = 0;
    int FSConCount = 0;

    private Handler mHandler, initHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, Boolean> mScanning;

    private final static int REQUEST_PERMISSION_REQ_CODE = 34; // any 8-bit number
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final long SCAN_PERIOD = 180000;

    private String mDeviceAddress;

    boolean FKAuto = true;
    boolean FSAuto = true;
    boolean SPAuto = true;

    LinearLayout BLELayout;
    private static final int INIT = 100;

    private HashMap<String, DataProcess> mDataSet;

    private final String LIST_NAME = "DEVICE_NAME";
    private final String LIST_UUID = "UUID";

    private static final int REQUEST_PERMISSIONS = 2;
    private Intent foregroundServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();

        pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
        editor = pref.edit();


        editor.putString("AutoPair","1");
        editor.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
//        getSupportActionBar().hide();

        if (null == BluetoothLeService.serviceIntent) {
            foregroundServiceIntent = new Intent(this, BluetoothLeService.class);
            startService(foregroundServiceIntent);
            Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
        } else {
            foregroundServiceIntent = BluetoothLeService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
        }
        Button uibtn = (Button)findViewById(R.id.btn3);
        uibtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, DataChart.class);
            startActivity(intent);
        });

        mStatus = NOT_CONNECTED;

        nHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case SET_STARTTIME: {
                        String startTime = (String)msg.obj;
//                        Log.d("@@@@","startTime : " + startTime);
                        StartTime = startTime;
                        if(mStatus == NOT_CONNECTED)
                            mStatus = CONNECTED;
                    }
                    break;
                    case SET_USETIME :{
                        String useTime = (String)msg.obj;
//                        Log.d("@@@@","useTime : "+useTime);
                        UseTime = useTime;
                    }
                    break;
                    case SET_NOWTIME:{
                        int nowTime= (int) msg.obj;
//                        Log.d("@@@@","nowTime : " + nowTime);
                        NowTime = nowTime +getResources().getString(R.string.second);
                    }
                    break;
                    case SET_SUCCESS:{
                        mStatus = SUCCESS;
                    }
                    break;
                    case SET_FAIL :{
                        mStatus = FAIL;
                    }
                    break;
                }

            }
        };
        PrevTime = "-";

        Button button = (Button)findViewById(R.id.btn1);
        button.setOnClickListener(view -> {
            if(BluetoothLeService.getState(FK)!=2){
                if(BLEConnectDialog.getBLEDialog()!=null){
                    BLEConnectDialog.getBLEDialog().setForkBattery(DataProcess.batteryList.get(DataProcess.batteryList.size()-1).replace(" ", "") + "%");
                }
            }
            Intent intent = new Intent(this, LogMain.class);
            startService(intent);
        });

        Button button1 = (Button)findViewById(R.id.btn2);
        button1.setOnClickListener(view -> {
            TextView textView = (TextView)findViewById(R.id.tv1);
            if (DataProcess.batteryList.size() != 0) {
                textView.setText("포크 배터리 확인 : " + DataProcess.batteryList.get(DataProcess.batteryList.size()-1) + " %");
            }else {
            }
        });

        BLELayout = findViewById(R.id.my_page_action_ble);
        BLELayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BLEConnectDialog.class);
                startActivity(intent);
//                Log.d("@!@!@!!!!", String.valueOf(mBluetoothLeService.getState(SP)));
            }
        });

        My = this;
        mHandler = new Handler();
        mDataSet = new HashMap<>();
        mScanning = new HashMap<>();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        // 블루투스 여부
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "블루투스 안댐", Toast.LENGTH_SHORT).show();
//            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "블루투스 안댐", Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }



        AllTimeConnect();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                initHandler.sendEmptyMessage(INIT);
            }

        },1500);

// 페어링
        initHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch(msg.what) {
                    case INIT: {
                        if(checkPermission()) {
//                            init();
                            if (pref.getString("AutoPair", "1").equals("1")) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.Search_Around_Device), Toast.LENGTH_SHORT).show();

                                scanLeDevice(true, mLeScanCallbackSP);
                                scanLeDevice(true, mLeScanCallbackFS);
                                scanLeDevice(true, mLeScanCallbackFK);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        if(mBluetoothLeService.getState(SP) != 2 ){
                                            mBluetoothAdapter.stopLeScan(mLeScanCallbackSP);
                                            scanLeDevice(true, mLeScanCallbackSP);
                                            Log.d("scan","SP");

                                        }
                                        if(mBluetoothLeService.getState(FS) != 2 ){
                                            mBluetoothAdapter.stopLeScan(mLeScanCallbackFS);
                                            scanLeDevice(true, mLeScanCallbackFS);
                                            Log.d("scan","FS");
                                        }

                                        if(mBluetoothLeService.getState(FK) != 2 ){
                                            mBluetoothAdapter.stopLeScan(mLeScanCallbackFK);
                                            scanLeDevice(true, mLeScanCallbackFK);
                                            Log.d("scan","FK");
                                        }
                                    }

                                },5000);
                            }


                            AllTimeConnect();


                        }
                    }
                    break;
                }
                return false;
            }
        });



        if(!isIgnoringBatteryOptimizations(this)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

//        Log.d("확인", ""+ tvHeight.getText().toString());

    }
    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ){
                Log.d("@@@@", "권한 설정 완료");
                return true;
            } else {
                Log.d("@@@@", "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS);
                return false;
            }

        } else {
            return true;
        }

    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final String name = intent.getStringExtra(DEVICE_NAME);
//            Log.d("@@@@", "action Name : " + name);
//            Log.d("@@@@", "action : " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if(name.contains(SP)){

                    setCustomToast(true, getResources().getString(R.string.Spoon_Connect_Success));
                }
                else if(name.contains(FS)){
                    setCustomToast(true,  getResources().getString(R.string.ForkSpoon_Connect_Success));
                }
                else if(name.contains(FK)){
                    setCustomToast(true, getResources().getString(R.string.Fork_Connect_Success));
                }

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if(name.contains(SP)){
                    if(SPAuto) {
                        if (pref.getString("AutoPair", "1").equals("1")) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothAdapter.stopLeScan(mLeScanCallbackSP);
                                    scanLeDevice(true, mLeScanCallbackSP);
                                }
                            }, 2000);
                        }
                    }
                    setCustomToast(false,  getResources().getString(R.string.Spoon_Disconnected));
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().ChangeStateSpoonSwitch("off");
                    }
                }
                else if(name.contains(FS)){
                    if(FSAuto) {
                        if (pref.getString("AutoPair", "1").equals("1")) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothAdapter.stopLeScan(mLeScanCallbackFS);
                                    scanLeDevice(true, mLeScanCallbackFS);
                                }
                            }, 2000);
                        }
                    }
                    setCustomToast(false,  getResources().getString(R.string.ForkSpoon_Disconnected));
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().ChangeStateForkSpoonSwitch("off");
                    }
                }
                else if(name.contains(FK)){
                    if(FKAuto) {
                        if (pref.getString("AutoPair", "1").equals("1")) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothAdapter.stopLeScan(mLeScanCallbackFK);
                                    scanLeDevice(true, mLeScanCallbackFK);
                                }
                            }, 2000);
                        }
                    }
                    setCustomToast(false, getResources().getString(R.string.Fork_Disconnected));
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().ChangeStateForkSwitch("off");
                    }
                }
                mDataSet.remove(name);
                if(mDataSet.size() < 1)
                    DataProcess.clear();
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics;
                        gattCharacteristics = displayGattServices(mBluetoothLeService.getSupportedGattServices(name));
                        if(gattCharacteristics != null) {
                            for (int i = 0; i < gattCharacteristics.size(); i++) {
                                for (int j = 0; j < gattCharacteristics.get(i).size(); j++) {
                                    Log.d("@@@@", i + " " + j + " @ " + gattCharacteristics.get(i).get(j).getUuid().toString());
                                }
                            }
                            if (gattCharacteristics.size() > 3) {
                                mLocalBluetoothGattCharacteristic =
                                        (BluetoothGattCharacteristic) ((ArrayList) gattCharacteristics.get(3)).get(0);
                                int result = 0;

                                mBluetoothLeService.setCharacteristicNotification(name, mLocalBluetoothGattCharacteristic, true);
                                mDataSet.put(name, new DataProcess(name));
//                                Log.d("@@@@", "mDataSet : " + mDataSet.size());
                            }
                        }
                    }
                }, 1000L);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if(mDataSet.containsKey(name))
                    mDataSet.get(name).displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        Log.d("@@@@", "makeGattUpdateIntentFilter");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);   //블루투스가 연결 되는 액션
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED); //블루투스가 해지되는 액션
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);  // 블루투스 서비스가 발견됨을 알린다.
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("BLE통신", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public void AllTimeConnect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(BluetoothLeService.getState(SP) ==2 || BluetoothLeService.getState(FK) ==2 || BluetoothLeService.getState(FS) ==2){
                    BLELayout.setBackground(getResources().getDrawable(R.drawable.button_back_bright_green_circle));
                    if(getMain()!=null){
                        ConSuccess();
                    }
                }else{
                    BLELayout.setBackground(getResources().getDrawable(R.drawable.button_back_gray_circle));
                    if(getMain()!=null){
                        ConFail();
                    }
                }

                if(BluetoothLeService.getState(SP)!=2){
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().setSpoonBattery("- %");
                    }
                }
                if(BluetoothLeService.getState(FK)!=2){
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().setForkBattery("- %");
                    }
                }
                if(BluetoothLeService.getState(FS)!=2){
                    if(BLEConnectDialog.getBLEDialog()!=null){
                        BLEConnectDialog.getBLEDialog().setForkSpoonBattery("- %");
                    }
                }


                if(BluetoothLeService.getState(SP)==2){
                    ScanStop(SP);
                }
                if(BluetoothLeService.getState(FK)==2){
                    ScanStop(FK);
                }
                if(BluetoothLeService.getState(FS)==2){
                    ScanStop(FS);
                }

                AllTimeConnect();





            }
        },1000);
    }

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return null;
        ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacterisitcs = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        String uuid = null;
        String unknownServiceString = "실패";
        String unknownCharaString = "실패";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
//        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            gattCharacterisitcs.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        return gattCharacterisitcs;
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);
        }

    }
    public void setStateCon(String name){
        if(name.equals(SP)){
            SPStateCon =2;
        }
        else if(name.equals(FK)){
            FKStateCon =2;
        }
        else if(name.equals(FS)){
            FSStateCon =2;
        }
    }

    public static MainActivity getMain() {
        return My;
    }

    public void setToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public Handler getHandler(){
        return nHandler;
    }

    public void writeBLE(final String name, final String data) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mBluetoothLeService != null) {
                        int result = mBluetoothLeService.writeCharacteristic(name, mLocalBluetoothGattCharacteristic, data);
                        while (result == 1) {
                            Thread.sleep(100);
                            result = mBluetoothLeService.writeCharacteristic(name, mLocalBluetoothGattCharacteristic, data);
//                            Log.d("writeBLE",String.valueOf(result));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void vibration(){
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }


    public void NewDeviceDialogCheckSpoon(BluetoothDevice device){
        mBluetoothLeService.connect(SP, device.getAddress());
        Log.d("hi","hi5");
        setCustomToast(true,getResources().getString(R.string.Spoon_Connect_Success));
        SPConCount++;
        editor.putString("SpoonAddress",device.getAddress());
        editor.putString("ExSpoonAddress",device.getAddress());
        editor.commit();
    }
    public void NewDeviceDialogCheckForkSpoon(BluetoothDevice device){
        mBluetoothLeService.connect(FS, device.getAddress());
        setCustomToast(true,getResources().getString(R.string.ForkSpoon_Connect_Success));
        FSConCount++;
        editor.putString("ForkSpoonAddress",device.getAddress());
        editor.putString("ExForkSpoonAddress",device.getAddress());
        editor.commit();
    }
    public void NewDeviceDialogCheckFork(BluetoothDevice device){
        mBluetoothLeService.connect(FK, device.getAddress());
        setCustomToast(true,getResources().getString(R.string.Fork_Connect_Success));
        FKConCount++;
        editor.putString("ForkAddress",device.getAddress());
        editor.putString("ExForkAddress",device.getAddress());
        editor.commit();
    }

    Toast toast;
    public void setCustomToast(boolean State, String text){


        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView custom_toast_text = layout.findViewById(R.id.custom_toast_text);
        ImageView custom_toast_image = layout.findViewById(R.id.custom_toast_image);

        if(State) {
            custom_toast_image.setBackground(getResources().getDrawable(R.drawable.ic_chck));
        }else{
            custom_toast_image.setBackground(getResources().getDrawable(R.drawable.ic_close));
        }
        if(toast !=null){
//                    if(!text.contains("연결 성공")) {
            toast.cancel();
//                    }
        }

        toast = new Toast(this);
        custom_toast_text.setText(text);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public void Con(String name){
        String SpoonAddress = pref.getString("SpoonAddress","");
        String ForkAddress = pref.getString("ForkAddress","");
        String ForkSpoonAddress = pref.getString("ForkSpoonAddress","");


        if(name.equals(SP)){
            if(BluetoothLeService.getState(SP)==2){
                discon(SP);
            }else {
                scanLeDevice(true, mLeScanCallbackSP);
            }
        }
        else if(name.equals(FS)){
            if(BluetoothLeService.getState(FS)==2) {
                discon(FS);
            }else {
                scanLeDevice(true, mLeScanCallbackFS);
            }
        }
        else if(name.equals(FK)){
            if(BluetoothLeService.getState(FK)==2){
                discon(FK);
            }else {
                scanLeDevice(true, mLeScanCallbackFK);
            }
        }

    }public void discon(String name){
        mBluetoothLeService.disconnect(name);
    }

    private void scanLeDevice(final boolean enable, final BluetoothAdapter.LeScanCallback leScanCallback) {
        int i = 0;
        String _name = "";
        if(leScanCallback == mLeScanCallbackSP) {
            i = 0;
            _name = SP;
        }
        else if(leScanCallback == mLeScanCallbackFS) {
            i = 1;
            _name = FS;
        }
        else if(leScanCallback == mLeScanCallbackFK) {
            i = 2;
            _name = FK;
        }
        final int ii = i;
        final String name = _name;
        Log.d("@@@@", "scanLeDevice i : " + i);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning.put(name, false);
                    if(mBluetoothLeService.getState(name) != STATE_CONNECTED)
                        mBluetoothAdapter.stopLeScan(leScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning.put(name, true);
            mBluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning.put(name, false);
            mBluetoothAdapter.stopLeScan(leScanCallback);
            mHandler.removeCallbacksAndMessages(null);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallbackSP =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getName() != null) {
//                                Log.d("@@@@", device.getName());
//                                Log.d("@@@@", device.getAddress());
                                String name = device.getName();
                                if(name.contains("ApSlim-SP")) {
                                    //ApSlim-FS
                                    //ApSlim-SP
//                            final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());


                                    if (mScanning.containsKey(device.getName()) && mScanning.get(device.getName())) {
                                        mBluetoothAdapter.stopLeScan(mLeScanCallbackSP);
                                        mScanning.put(name, false);
//                                startActivity(intent);
//                                        mDeviceAddress = device.getAddress();
//                                        Log.d("@@@@", "mBluetoothLeService.connect" + mDeviceAddress);
                                        if (device.getAddress().equals(pref.getString("SpoonAddress", ""))) {
                                            mBluetoothLeService.connect(device.getName(), device.getAddress());
                                            editor.putString("SpoonAddress", device.getAddress());
                                            editor.commit();
                                        } else {   // 이전 기기 주소와 다름
                                            if (BLEConnectDialog.getBLEDialog() != null) {
                                                BLEConnectDialog.getBLEDialog().NewDeviceDialogSpoon(device);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle(getResources().getString(R.string.New_Spoon_Searched));
                                                builder.setMessage(getResources().getString(R.string.Ask_New_Spoon_Connect));
                                                builder.setCancelable(true);
                                                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        /**Dialog 2**/
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                        builder.setTitle(getResources().getString(R.string.New_Spoon_Connect));
                                                        builder.setMessage(getResources().getString(R.string.Remove_Ex_Spoon));
                                                        builder.setCancelable(true);
                                                        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                mBluetoothLeService.connect("ApSlim-SP", device.getAddress());
//                                                                setCustomToast(true,"스푼연결 성공!");
                                                                SPConCount++;
                                                                editor.putString("SpoonAddress", device.getAddress());
                                                                editor.putString("ExSpoonAddress", device.getAddress());
                                                                editor.commit();
                                                            }
                                                        });
                                                        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                        builder.create().show();
                                                    }
                                                });
                                                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            };
    //포크 콜백
    private BluetoothAdapter.LeScanCallback mLeScanCallbackFK =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getName() != null) {
//                                Log.d("@@@@", device.getName());
//                                Log.d("@@@@", device.getAddress());
                                String name = device.getName();
                                if(name.contains("ApSlim-FK")) {
                                    //ApSlim-FS
                                    //ApSlim-SP
//                            final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

                                    if (mScanning.containsKey(device.getName()) && mScanning.get(device.getName())) {
                                        mBluetoothAdapter.stopLeScan(mLeScanCallbackFS);
                                        mScanning.put(name, false);
//                                startActivity(intent);
                                        mDeviceAddress = device.getAddress();
//                                        Log.d("@@@@", "mBluetoothLeService.connect" + mDeviceAddress);

                                        if(device.getAddress().equals(pref.getString("ForkAddress",""))) {

                                            mBluetoothLeService.connect(device.getName(), device.getAddress());
                                            editor.putString("ForkAddress", device.getAddress());
                                            editor.commit();
                                        }else {  //이전 기기 주소와 다름
                                            if (BLEConnectDialog.getBLEDialog() != null) {
                                                BLEConnectDialog.getBLEDialog().NewDeviceDialogFork(device);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle(getResources().getString(R.string.New_Fork_Searched));
                                                builder.setMessage(getResources().getString(R.string.Ask_New_Fork_Connect));
                                                builder.setCancelable(true);
                                                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        /**Dialog 2**/
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                        builder.setTitle(getResources().getString(R.string.New_Spoon_Connect));
                                                        builder.setMessage(getResources().getString(R.string.Remove_Ex_Fork));
                                                        builder.setCancelable(true);
                                                        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                mBluetoothLeService.connect("ApSlim-FK", device.getAddress());
//                                                                setCustomToast(true,"포크연결 성공!");
                                                                FKConCount++;
                                                                editor.putString("ForkAddress", device.getAddress());
                                                                editor.putString("ExForkAddress", device.getAddress());
                                                                editor.commit();
                                                            }
                                                        });
                                                        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                        builder.create().show();
                                                    }
                                                });
                                                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            };


    //포크스푼 콜백
    private BluetoothAdapter.LeScanCallback mLeScanCallbackFS =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getName() != null) {
//                                Log.d("@@@@", device.getName());
//                                Log.d("@@@@", device.getAddress());
                                String name = device.getName();
                                if(name.contains("ApSlim-FS")) {
                                    //ApSlim-FS
                                    //ApSlim-SP
//                            final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

                                    if (mScanning.containsKey(device.getName()) && mScanning.get(device.getName())) {
                                        mBluetoothAdapter.stopLeScan(mLeScanCallbackFS);
                                        mScanning.put(name, false);
//                                startActivity(intent);
                                        mDeviceAddress = device.getAddress();
//                                        Log.d("@@@@", "mBluetoothLeService.connect" + mDeviceAddress);

                                        if(device.getAddress().equals(pref.getString("ForkSpoonAddress",""))) {
                                            mBluetoothLeService.connect(device.getName(), device.getAddress());
                                            editor.putString("ForkSpoonAddress", device.getAddress());
                                            editor.commit();
                                        }else{   // 이전 기기와 다를때
                                            if (BLEConnectDialog.getBLEDialog() != null) {
                                                BLEConnectDialog.getBLEDialog().NewDeviceDialogForkSpoon(device);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle(getResources().getString(R.string.New_ForkSpoon_Searched));
                                                builder.setMessage(getResources().getString(R.string.Ask_New_ForkSpoon_Connect));
                                                builder.setCancelable(true);
                                                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        /**Dialog 2**/
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                        builder.setTitle(getResources().getString(R.string.New_ForkSpoon_Connect));
                                                        builder.setMessage(getResources().getString(R.string.Remove_Ex_ForkSpoon));
                                                        builder.setCancelable(true);
                                                        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                mBluetoothLeService.connect("ApSlim-FS", device.getAddress());
//                                                                setCustomToast(true,"포크스푼 연결 성공!");
                                                                editor.putString("ForkSpoonAddress", device.getAddress());
                                                                editor.commit();
                                                            }
                                                        });
                                                        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                        builder.create().show();
                                                    }
                                                });
                                                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            };
    public int getStateMain(String name){
        return mBluetoothLeService.getState(name);
    }
    public void ScanStop(String name){ //여기서부터
        if(name.equals(SP)) {
            mBluetoothAdapter.stopLeScan(mLeScanCallbackSP);
        }else if(name.equals(FK)) {
            mBluetoothAdapter.stopLeScan(mLeScanCallbackFK);
        }else{
            mBluetoothAdapter.stopLeScan(mLeScanCallbackFS);
        }
    }
    public void setSPAuto(boolean state){
        SPAuto = state;
    }
    public void setFKAuto(boolean state){
        FKAuto = state;
    }
    public void setFSAuto(boolean state){
        FSAuto = state;
    }

    static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager powerManager =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true;
    }

    public void ConSuccess(){
        BLELayout.setBackground(getResources().getDrawable(R.drawable.button_back_bright_green_circle));
    }
    public void ConFail(){
        BLELayout.setBackground(getResources().getDrawable(R.drawable.button_back_gray_circle));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != foregroundServiceIntent){
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }

    }
}