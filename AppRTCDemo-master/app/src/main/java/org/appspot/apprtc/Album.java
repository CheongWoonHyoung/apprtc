package org.appspot.apprtc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Donghyun on 2016. 2. 8..
 */
public class Album extends MainActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.album_main);

        Button btn_home = (Button) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //파일리스트불러오기

        //리스트어댑터로뿌리기

        //온클릭리스너하면재생하기

        //인텐트 Album_Play
    }
}
