package com.jinasoft.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Alarm extends AppCompatActivity {

    ConstraintLayout alarm_layout;
    float pressedX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarm_layout = (ConstraintLayout)findViewById(R.id.alarm_layout);
        alarm_layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {             // 리니어를 스크롤 했을때 다음화면으로 Intent 하기 위해서 드래그 시작과 끝 좌표를 저장해서 지정해둔 만큼 차이가 나면 Intent를 발생 시킨다.
                float distance = 0;

                switch (event.getAction()) {
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

                Log.d("distance", String.valueOf(distance));
                if (Math.abs(distance) < 50) {
                    return false;
                }
                if (distance > 0) {

// 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(Alarm.this, DataChart.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);

                } else {
// 손가락을 오른쪽으로 움직였으면 왼쪽 화면이 나타나야 한다.
                    Intent intent = new Intent(Alarm.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);

                }

                return true;
            }
        });
    }
}