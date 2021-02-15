package com.jinasoft.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DataChart extends AppCompatActivity {

    public ArrayList<String> list = new ArrayList<>();
    public LinearLayout UnderHome,UnderAlam,UnderData,UnderMy;
    public FragmentPagerAdapter fragmentPagerAdapter;
    LinearLayout data_layout,under_bar;
    float pressedX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_chart);

        getSupportActionBar().hide();

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        fragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(fragmentPagerAdapter);

        UnderBar();
        data_layout = (LinearLayout)findViewById(R.id.scollstop);
        data_layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {             // 리니어를 스크롤 했을때 다음화면으로 Intent 하기 위해서 드래그 시작과 끝 좌표를 저장해서 지정해둔 만큼 차이가 나면 Intent를 발생 시킨다.
                float distance = 0;

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 손가락을 touch 했을 떄 x 좌표값 저장
                        pressedX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // 손가락을 떼었을 때 저장해놓은 x좌표와의 거리 비교
                        distance = pressedX - event.getX();
                        break;
                    //                        return true;
                }
                // 해당 거리가 100이 되지 않으면 이벤트 처리 하지 않는다.

                Log.d("distance",String.valueOf(distance));
                if (Math.abs(distance)< 50) {
                    return false;
                }
                if (distance > 0) {

// 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(DataChart.this, My.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);

                } else {
// 손가락을 오른쪽으로 움직였으면 왼쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(DataChart.this, Alarm.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);

                }

                return true;
            }
        });

    }
    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstFragment.newInstance(0);
                case 1:
                    return SecondFragment.newInstance(1);
//                case 2:
//                    return ThirdFragment.newInstance(2, "Page # 3");
//                case 3:
//                    return FourthFragment.newInstance(2, "Page # 4");
//                case 4:
//                    return FifthFragment.newInstance(2, "Page # 5");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    public void UnderBar(){
        UnderHome = findViewById(R.id.under_home);
        UnderAlam = findViewById(R.id.under_alarm);
        UnderData = findViewById(R.id.under_data);
        UnderMy = findViewById(R.id.under_my);
        under_bar = (LinearLayout)findViewById(R.id.underbar);
        under_bar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {             // 리니어를 스크롤 했을때 다음화면으로 Intent 하기 위해서 드래그 시작과 끝 좌표를 저장해서 지정해둔 만큼 차이가 나면 Intent를 발생 시킨다.
                float distance = 0;

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 손가락을 touch 했을 떄 x 좌표값 저장
                        pressedX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        // 손가락을 떼었을 때 저장해놓은 x좌표와의 거리 비교
                        distance = pressedX - event.getX();
                        break;
                    //                        return true;
                }
                // 해당 거리가 100이 되지 않으면 이벤트 처리 하지 않는다.

                Log.d("distance",String.valueOf(distance));
                if (Math.abs(distance)< 50) {
                    return false;
                }
                if (distance > 0) {

// 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(DataChart.this, My.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);

                } else {
// 손가락을 오른쪽으로 움직였으면 왼쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(DataChart.this, Alarm.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);

                }

                return true;
            }
        });
//        SharedPreferences pref = getSharedPreferences("info",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();

        UnderHome.setOnClickListener(n->{
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        });
        UnderAlam.setOnClickListener(n->{
            Intent intent = new Intent(this, Alarm.class);
//            intent.putExtra("nickname",pref.getString("nickname",""));
//            intent.putExtra("phoneNumber",pref.getString("phoneNumber",""));
            startActivity(intent);
//            overridePendingTransition(0,0);
        });
        UnderData.setOnClickListener(n->{
            Intent intent = new Intent(this, DataChart.class);
            startActivity(intent);
//            overridePendingTransition(0,0);
//            // 종합분석 그래프 생성
        });
        UnderMy.setOnClickListener(n->{
            Intent intent = new Intent(this, My.class);
            startActivity(intent);

        });


    }
}