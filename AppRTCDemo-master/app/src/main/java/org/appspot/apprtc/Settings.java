package org.appspot.apprtc;


import android.app.Activity;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by cheongwh on 2016. 2. 12..
 */

public class Settings extends MainActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.settings);

        Button btn_home = (Button) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
