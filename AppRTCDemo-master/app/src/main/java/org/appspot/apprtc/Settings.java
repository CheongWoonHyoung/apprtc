package org.appspot.apprtc;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


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

        SharedPreferences pref = this.getSharedPreferences(getPackageName(),
                Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        //친구한테 초대받기 설정
        Switch switch_invite = (Switch)findViewById(R.id.switch_invite);
        switch_invite.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking) {
                    editor.putString("invite", "true");
                    editor.commit();
                } else{
                    editor.putString("invite", "false");
                    editor.commit();
                }
            }
        });

        //모르는 사람으로 부터의 초대 설정
        Switch switch_new = (Switch)findViewById(R.id.switch_new);
        switch_new.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking) {
                    editor.putString("new", "true");
                    editor.commit();
                } else{
                    editor.putString("new", "false");
                    editor.commit();
                }
            }
        });

        //기타 설정
        Switch switch_etc = (Switch)findViewById(R.id.switch_etc);
        switch_etc.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean isChecking) {
                if (isChecking) {
                    editor.putString("etc", "true");
                    editor.commit();
                } else{
                    editor.putString("etc", "false");
                    editor.commit();
                }
            }
        });
    }

}
