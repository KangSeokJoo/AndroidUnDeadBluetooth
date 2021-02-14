package com.jinasoft.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {

    LinearLayout UnderHome,UnderAlam,UnderData,UnderMy;
    ArrayList<String> list = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        getSupportActionBar().hide();

        for (int i = 0; i < 30; i++){
            list.add("반복문" + i + "번째");
            Log.d("확인" ,"" + i);
        }
        RecyclerView recyclerView = findViewById(R.id.ChartList);

//        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerList adapter = new RecyclerList(list);


        recyclerView.setAdapter(adapter);


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
//            intent.putExtra("nickname",pref.getString("nickname",""));
//            intent.putExtra("phoneNumber",pref.getString("phoneNumber",""));
//            startActivity(intent);
//            overridePendingTransition(0,0);
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