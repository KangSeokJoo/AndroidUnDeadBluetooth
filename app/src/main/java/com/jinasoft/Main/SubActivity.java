package com.jinasoft.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class SubActivity extends AppCompatActivity {

    LinearLayout UnderHome,UnderAlam,UnderData,UnderMy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        getSupportActionBar().hide();



        ListView listView = (ListView)findViewById(R.id.ChartList);

    }

    public void UnderBar(){
        UnderHome = findViewById(R.id.under_home);
        UnderAlam = findViewById(R.id.under_alam);
        UnderData = findViewById(R.id.under_data);
        UnderMy = findViewById(R.id.under_my);

        SharedPreferences pref =getSharedPreferences("info",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        UnderHome.setOnClickListener(n->{
//            Intent intnet = new Intent(MainActivity.this, MainActivity.class);
//            startActivity(intnet);
        });
        UnderAlam.setOnClickListener(n->{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("nickname",pref.getString("nickname",""));
            intent.putExtra("phoneNumber",pref.getString("phoneNumber",""));
            startActivity(intent);
            overridePendingTransition(0,0);
        });
        UnderData.setOnClickListener(n->{
            Intent intnet = new Intent(this, MainActivity.class);
            startActivity(intnet);
        });
        UnderMy.setOnClickListener(n->{
            Intent intnet = new Intent(this, MainActivity.class);
            startActivity(intnet);
            overridePendingTransition(0,0);
            // 종합분석 그래프 생성
        });


    }

}