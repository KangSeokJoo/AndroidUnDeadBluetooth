package com.jinasoft.BlueTooth;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jinasoft.Main.BLEConnectDialog;
import com.jinasoft.Main.MainActivity;
import com.jinasoft.Main.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.jinasoft.Setting.LogMain.temp;


public class BluetoothLeService extends Service {     // ApSlim 앱은 블루투스 기기를 이름으로 구분후 연결합니다.

    /**다른 앱과 다르게 하나의 서비스에 3개의 기기가 붙는 형식
     * 다른 앱들에 비해 블루투스의 연결이 불안정할때가 있음**/

    public final static String SP = "ApSlim-SP";            //스푼
    public final static String FS = "ApSlim-FS";            //포크스푼
    public final static String FK = "ApSlim-FK";            //포크
    public final static String DEVICE_NAME = "name";
    private final static String TAG = "확인";

    static boolean FKReCon = true;
    static boolean FSReCon = true;
    static boolean SPReCon = true;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    //    private HashMap<String, String>
    private String mBluetoothDeviceAddress;
    private HashMap<String, BluetoothGatt> mBluetoothGatts;
    private static HashMap<String, Integer> mConnectionState;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

//    public final static UUID UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
//    //
//    public final static UUID UUID_CC2640R2_SERIAL_READ =
//            UUID.fromString(SampleGattAttributes.CC2640_READ);

    public final static UUID UUID_SPOON_SERIAL_READ =
            UUID.fromString(SampleGattAttributes.SPOON_READ);    // rn4871 (BLE 모듈) 의 UUID

    public static Intent serviceIntent = null;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {         //블루투스의 연결 상태가 변경됨
            Log.w(TAG, "onConnectionStateChange received: " + status);
            String intentAction;
            String name = gatt.getDevice().getName();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState.put(name, STATE_CONNECTED);
                if(MainActivity.getMain()!=null) {
                    MainActivity.getMain().setStateCon(name);
                }
                Log.d("연결", "setConncet");
//                Toast.makeText(,"Connected!", Toast.LENGTH_SHORT).show();
//                String name = "";
//                for(Map.Entry<String, BluetoothGatt> entry : mBluetoothGatts.entrySet()) {
//                    if(gatt.getDevice().getAddress().equals(entry.getValue().getDevice().getAddress())) {
//                        name = entry.getValue().getDevice().getName();
//                        break;
//                    }
//                }


                broadcastUpdate(name, intentAction);
                gatt.discoverServices(); //디바이스의 기능을 확인한다
                Log.i(TAG, "Connected to GATT server.");
                Log.i("기능확인", String.valueOf(gatt.discoverServices()));
                // Attempts to discover services after successful connection.
//                Log.i(TAG, "Attempting to start service discovery:" +
//                        mBluetoothGatt.discovecrServices());






            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(gatt.getDevice().getName().equals(SP)) {                 //연결이 끊긴 기기의 이름을 확인하여 상태를 변경해줌
                            SPReCon = true;
                        }
                        if(gatt.getDevice().getName().equals(FS)) {
                            FSReCon = true;
                        }
                        if(gatt.getDevice().getName().equals(FK)) {
                            FKReCon = true;
                        }
                    }
                }, 5000);


                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState.put(name, STATE_DISCONNECTED);
                Log.d("@@@", "setdisConncet");
//                Toast.makeText(BluetoothLeService.this,"DisConnected!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Disconnected from GATT server. : " + gatt.getDevice().getName());
//                String name = "";
//                for(Map.Entry<String, BluetoothGatt> entry : mBluetoothGatts.entrySet()) {
//                    if(gatt.getDevice().getAddress() == entry.getValue().getDevice().getAddress()) {
//                        name = entry.getValue().getDevice().getName();
//                        break;
//                    }
//                }
                broadcastUpdate(name, intentAction);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.w(TAG, "onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                String name = "";
//                for(Map.Entry<String, BluetoothGatt> entry : mBluetoothGatts.entrySet()) {
//                    if(gatt.getDevice().getAddress() == entry.getValue().getDevice().getAddress()) {
//                        name = entry.getValue().getDevice().getName();
//                        break;
//                    }
//                }
                broadcastUpdate(gatt.getDevice().getName(), ACTION_GATT_SERVICES_DISCOVERED);
//                Log.d("deviceName",gatt.getDevice().getName());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.d("Characteristic",String.valueOf(characteristic));
                broadcastUpdate(gatt.getDevice().getName(), ACTION_DATA_AVAILABLE, characteristic);
//                Log.d("deviceName",gatt.getDevice().getName()+" : "+characteristic);
            }
        }



        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(gatt.getDevice().getName(), ACTION_DATA_AVAILABLE, characteristic);

            if(gatt.getDevice().getName().equals(SP)) {
                SPReCon = false;
            }
            if(gatt.getDevice().getName().equals(FS)) {
                FSReCon = false;
            }
            if(gatt.getDevice().getName().equals(FK)) {
                FKReCon = false;
            }





//            Log.d("deviceName",gatt.getDevice().getName()+" : "+characteristic.getValue());
        }
    };

    private void broadcastUpdate(String name, final String action) {
//        Log.d("@@@@", "sendBroadcast : " + action);
        final Intent intent = new Intent(action);
        intent.putExtra(DEVICE_NAME, name);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String name,
                                 final String action,
                                 final BluetoothGattCharacteristic characteristic) {
//        Log.d("@@@@", "sendBroadcast : " + action);
        final Intent intent = new Intent(action);
        intent.putExtra(DEVICE_NAME, name);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        } else {
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            intent.putExtra(EXTRA_DATA, stringBuilder.toString());
        }
//        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;

        initialize();

        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
//        close();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothGatts = new HashMap<>();
        mConnectionState = new HashMap<>();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.d("확인6", "아직살아있음" + BLEConnectDialog.getBLEDialog() + temp);
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
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String name, final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatts.containsKey(name)) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatts.get(name).connect()) {
                mConnectionState.put(name, STATE_CONNECTING);
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatts.put(name, device.connectGatt(this, false, mGattCallback));
        Log.d(TAG, "Trying to create a new connection.");
        Log.d(TAG, "name : " + name);
        Log.d(TAG, "address : " + address);
        Log.d(TAG, "size : " + mBluetoothGatts.size());

//        mBluetoothDeviceAddress = address;
        mConnectionState.put(name, STATE_CONNECTING);
        return true;
    }


    public static int getState(String name) {               //연결 상태 가져오기
        try {
            if (!mConnectionState.containsKey(name)) {
                return STATE_DISCONNECTED;
            } else {
                return mConnectionState.get(name);
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(String name) {           //연결 해제  ( 연결 해제할 기기의 이름을 전송)

        if(!mBluetoothGatts.containsKey(name)) {
            Log.w(TAG, "Not Have " + name);
            return;
        }
//        mConnectionState.put(name, STATE_DISCONNECTED);
        mBluetoothGatts.get(name).disconnect();
//        mBluetoothGatts.get(name).close();
        mBluetoothGatts.remove(name);
    }





    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {               // 연결 소켓 닫기

        for(Map.Entry<String, BluetoothGatt> entry : mBluetoothGatts.entrySet()) {
//            Log.d("@@@@", "close : " + entry.getKey());
            entry.getValue().close();
            mBluetoothGatts.remove(entry.getKey());
        }

    }
    public void close(String name) {

        mBluetoothGatts.get(name).close();
//        mBluetoothGatts.remove(name);
    }

    public void readCharacteristic(String name, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || !mBluetoothGatts.containsKey(name)) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatts.get(name).readCharacteristic(characteristic);
    }

    public static byte[] hexStringToByteArray(String s) {  //바이너리 스트링을 바이트 배열로 변환
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public int writeCharacteristic(String name, BluetoothGattCharacteristic characteristic, String datas) {
        if (mBluetoothAdapter == null || !mBluetoothGatts.containsKey(name)) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return 0;
        }
        String data = datas.replace(" ", "");
//        Log.d("@@@@", "write : " + data);
        byte[] b = hexStringToByteArray(data);
//        Log.d("@@@@", "bb length : " + b.length);
//        byte[] b = new byte[7];
//        b[0] = 0x44;
//        b[1] = 0x00;
//        b[2] = 0x03;
//        b[3] = 0x02;
//        b[4] = 0x02;
//        b[5] = (byte)0xFF;
//        b[6] = 0x73;
        characteristic.setValue(b);
        boolean result = mBluetoothGatts.get(name).writeCharacteristic(characteristic);
//        boolean result = mBluetoothGatts.get(name).
//        Log.d("@@@@", "result : " + result);
        return result ? 2 : 1;
//        for(Map.Entry<String, BluetoothGatt> entry : mBluetoothGatts.entrySet()) {
//            boolean result = entry.getValue().writeCharacteristic(characteristic);
//            Log.d("@@@@", "result : " + result);
////            entry.getValue().close();
////            mBluetoothGatts.remove(entry.getKey());
//        }
//        boolean result = mBluetoothGatt.writeCharacteristic(characteristic);
//        Log.d("@@@@", "result : " + result);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(String name, BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || !mBluetoothGatts.containsKey(name)) {              // 백그라운드에서 서비스 사용시 포그라운드 노티피케이션 필수
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        Log.d("@@@@", "" + characteristic.getUuid());
        if(UUID_SPOON_SERIAL_READ.equals(characteristic.getUuid())) {     //rn4871의 uuid와 GATT에서 가져온 UUid가 같으면
            mBluetoothGatts.get(name).setCharacteristicNotification(characteristic, enabled);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatts.get(name).writeDescriptor(descriptor);
        }



    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices(String name) {
//        Log.d("1234", "getSupportedGattServices " + name + " : " + mBluetoothGatts.containsKey(name));
        if (!mBluetoothGatts.containsKey(name)) return null;
        return mBluetoothGatts.get(name).getServices();
    }

    public static boolean getReCon(String name){
        if(name.equals(SP)){
            return SPReCon;
        }else if(name.equals(FS)){
            return FSReCon;
        }else if(name.equals(FK)){
            return FKReCon;
        }else{
            return true;
        }
    }
}
