package com.jinasoft.Main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jinasoft.BlueTooth.BluetoothLeService;

import static com.jinasoft.BlueTooth.BluetoothLeService.FK;
import static com.jinasoft.BlueTooth.BluetoothLeService.FS;
import static com.jinasoft.BlueTooth.BluetoothLeService.SP;

public class BLEConnectDialog extends AppCompatActivity {

    Switch SwitchSpoon,SwitchFork,SwitchForkSpoon;
    Button btnDone;


    static TextView tvForkBattery,tvForkSpoonBattery;
    static TextView tvSpoonBattery;

    static com.jinasoft.Main.BLEConnectDialog bleConnectDialog;

    ImageView testSP,testFS,testFK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_my_ble_dialog);


        testSP = findViewById(R.id.testSpoon);
        testFS = findViewById(R.id.testForkSpoon);
        testFK = findViewById(R.id.testFK);

        testSP.setOnClickListener(n->{
            MainActivity.getMain().Con(SP);
        });
        testFS.setOnClickListener(n->{
            MainActivity.getMain().Con(FS);
        });
        testFK.setOnClickListener(n->{
            MainActivity.getMain().Con(FK);
        });


    }
    public void ChangeStateSpoonSwitch(String state){
        if(state.equals("on")){
            SwitchSpoon.setChecked(true);
        }else {
            SwitchSpoon.setChecked(false);
        }
    }
    public void ChangeStateForkSwitch(String state){
        if(state.equals("on")){
            SwitchFork.setChecked(true);
        }else {
            SwitchFork.setChecked(false);
        }
    }
    public void ChangeStateForkSpoonSwitch(String state){
        if(state.equals("on")){
            SwitchForkSpoon.setChecked(true);
        }else {
            SwitchForkSpoon.setChecked(false);
        }
    }


    public static com.jinasoft.Main.BLEConnectDialog getBLEDialog(){
        return bleConnectDialog;
    }
    public static void setSpoonBattery(String bat){
        if(BluetoothLeService.getState(SP)==2) {
            if(bat.equals("")){
                MainActivity.getMain().setToast("SP ERROR");
            }
        }
        tvSpoonBattery.setText(bat);

    }
    public static void setForkBattery(String bat){
        if(BluetoothLeService.getState(FK)==2) {
            if(bat.equals("")){
                MainActivity.getMain().setToast("FK ERROR");
            }
        }

        tvForkBattery.setText(bat);
    }
    public static void setForkSpoonBattery(String bat){
        tvForkSpoonBattery.setText(bat);
    }


    public void NewDeviceDialogForkSpoon(BluetoothDevice device){
        AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
        builder.setTitle(getResources().getString(R.string.New_ForkSpoon_Searched));
        builder.setMessage(getResources().getString(R.string.Ask_New_ForkSpoon_Connect));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                /**Dialog 2**/
                AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
                builder.setTitle(getResources().getString(R.string.New_ForkSpoon_Connect));
                builder.setMessage(getResources().getString(R.string.Remove_Ex_ForkSpoon));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.getMain().NewDeviceDialogCheckForkSpoon(device);
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
    public void NewDeviceDialogFork(BluetoothDevice device){
        AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
        builder.setTitle(getResources().getString(R.string.New_Fork_Searched));
        builder.setMessage(getResources().getString(R.string.Ask_New_Fork_Connect));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                /**Dialog 2**/
                AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
                builder.setTitle(getResources().getString(R.string.New_Fork_Connect));
                builder.setMessage(getResources().getString(R.string.Remove_Ex_Fork));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.getMain().NewDeviceDialogCheckFork(device);
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
    public void NewDeviceDialogSpoon(BluetoothDevice device){
        AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
        builder.setTitle(getResources().getString(R.string.New_Spoon_Searched));
        builder.setMessage(getResources().getString(R.string.Ask_New_Spoon_Connect));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                /**Dialog 2**/
                AlertDialog.Builder builder = new AlertDialog.Builder(com.jinasoft.Main.BLEConnectDialog.this);
                builder.setTitle(getResources().getString(R.string.New_Fork_Connect));
                builder.setMessage(getResources().getString(R.string.Remove_Ex_Spoon));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.getMain().NewDeviceDialogCheckSpoon(device);
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

    @Override
    protected void onResume() {
        super.onResume();

        bleConnectDialog =this;

//            Toast.makeText(this,"hihi",Toast.LENGTH_SHORT).show();

        tvForkBattery = findViewById(R.id.ForkBattery);
        tvSpoonBattery = findViewById(R.id.SpoonBattery);
        tvForkSpoonBattery = findViewById(R.id.ForkSpoonBattery);


        findViewById(R.id.close).setOnClickListener(n->{
            onBackPressed();
        });

        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(BLEConnectDialog.this,"hihi",Toast.LENGTH_SHORT).show();
//                onBackPressed();
                finish();
//                Intent intent = new Intent(BLEConnectDialog.this,MainActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0,0);
            }
        });

//        MainActivity.getMain().vibration();

        SwitchFork = findViewById(R.id.switch_ble_fork);
        SwitchSpoon= findViewById(R.id.switch_ble_spoon);
        SwitchForkSpoon = findViewById(R.id.switch_ble_forkspoon);


        if(MainActivity.getMain().getStateMain(SP)==2) {
            SwitchSpoon.setChecked(true);
        }else{
            SwitchSpoon.setChecked(false);
        }
        if(MainActivity.getMain().getStateMain(FK)==2) {
            SwitchFork.setChecked(true);
        }else{
            SwitchFork.setChecked(false);
        }
        if(MainActivity.getMain().getStateMain(FS)==2) {
            SwitchForkSpoon.setChecked(true);
        }else{
            SwitchForkSpoon.setChecked(false);
        }


        SwitchSpoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                MainActivity.getMain().ConSpoon();
                Log.d("SwitchSpoon","Clicked");
//                MainActivity.getMain().ConSpoon();
//                if(!SwitchSpoon.isChecked()){
//                    MainActivity.getMain().setSPAuto(false);
//                }

            }
        });
        SwitchFork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("SwitchFork","Clicked");
//                MainActivity.getMain().ConFork();
//                if(!SwitchFork.isChecked()){
//                    MainActivity.getMain().setSPAuto(false);
//                }
            }
        });
        SwitchForkSpoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("SwitchForkSpoon","Clicked");
//                MainActivity.getMain().ConForkSpoon();
//                if(!SwitchFork.isChecked()){
//                    MainActivity.getMain().setSPAuto(false);
//                }

            }
        });


        SwitchSpoon.setOnClickListener(n->{
            Log.d("SwitchSpoon","Clicked");

            MainActivity.getMain().ScanStop(SP);
//            MainActivity.getMain().ConSpoon();
            MainActivity.getMain().Con(SP);

            if(SwitchSpoon.isChecked()){
                MainActivity.getMain().setSPAuto(true);
            }else{
                MainActivity.getMain().setSPAuto(false);
            }

//            if(MainActivity.getMain().getStateMain(SP)==2) {
//                MainActivity.getMain().setSPAuto(true);
//            }else{
//                MainActivity.getMain().setSPAuto(false);
//            }
        });
//
        SwitchFork.setOnClickListener(n->{
            Log.d("SwitchFork","Clicked");
            MainActivity.getMain().ScanStop(FK);
//            MainActivity.getMain().ConFork();
            MainActivity.getMain().Con(FK);

            if(SwitchFork.isChecked()){
                MainActivity.getMain().setFKAuto(true);
            }else{
                MainActivity.getMain().setFKAuto(false);
            }


//            if(MainActivity.getMain().getStateMain(FK)==2) {
//                MainActivity.getMain().setFKAuto(true);
//            }else{
//                MainActivity.getMain().setFKAuto(false);
//            }


        });
        SwitchForkSpoon.setOnClickListener(n->{
            Log.d("SwitchForkSpoon","Clicked");
            MainActivity.getMain().ScanStop(FS);
//            MainActivity.getMain().ConForkSpoon();
            MainActivity.getMain().Con(FS);

            if(SwitchForkSpoon.isChecked()){
                MainActivity.getMain().setFSAuto(true);
            }else{
                MainActivity.getMain().setFSAuto(false);
            }


//            if(MainActivity.getMain().getStateMain(FS)==2) {
//                MainActivity.getMain().setFSAuto(true);
//            }else{
//                MainActivity.getMain().setFSAuto(false);
//            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();


        bleConnectDialog = null;
    }
}

