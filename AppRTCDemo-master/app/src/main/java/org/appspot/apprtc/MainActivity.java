package org.appspot.apprtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button main01 = (Button)findViewById(R.id.main_btn01);
        final Button main02 = (Button)findViewById(R.id.main_btn02);
        final Button main03 = (Button)findViewById(R.id.main_btn03);

        main01.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                main01.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn01_push));

                Intent intent = new Intent(v.getContext(),Story.class);
                startActivity(intent);

                main01.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn01_normal));
            }
        });

        main02.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                main02.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn02_push));

                Intent intent = new Intent(MainActivity.this,Album.class);
                startActivity(intent);

                main02.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn02_normal));
            }
        });

        main03.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                main03.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn03_push));

                Intent intent = new Intent(MainActivity.this,Settings.class);
                startActivity(intent);

                main03.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_btn03_normal));
            }
        });
    }
}