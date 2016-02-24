package org.appspot.apprtc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button main01 = (Button)findViewById(R.id.main_btn01);
        final Button main02 = (Button)findViewById(R.id.main_btn02);
        final Button main03 = (Button)findViewById(R.id.main_btn03);

        main01.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    main01.setBackgroundResource(R.drawable.main_btn01_push2x);
                }else if(action == MotionEvent.ACTION_UP){
                    main01.setBackgroundResource(R.drawable.main_btn01_normal2x);
                    Intent intent = new Intent(MainActivity.this,Story.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        main02.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    main02.setBackgroundResource(R.drawable.main_btn02_push2x);
                } else if (action == MotionEvent.ACTION_UP) {
                    main02.setBackgroundResource(R.drawable.main_btn02_normal2x);
                    Intent intent = new Intent(MainActivity.this, Album.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        main03.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    main03.setBackgroundResource(R.drawable.main_btn03_push2x);
                } else if (action == MotionEvent.ACTION_UP) {
                    main03.setBackgroundResource(R.drawable.main_btn03_normal2x);
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}