package org.appspot.apprtc;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;


/**
 * Created by cheongwh on 2016. 2. 12..
 */

public class Settings extends MainActivity {
    private EditText et_friend_id;
    public SharedPreferences friend_list;
    public SharedPreferences.Editor friend_list_editor;

    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.settings);

        Button btn_home = (Button) findViewById(R.id.btn_home3);
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

        friend_list = this.getSharedPreferences(getPackageName(),
                Activity.MODE_PRIVATE);
        friend_list_editor = friend_list.edit();
        friend_list_editor.putString("nadong","nadong");
        friend_list_editor.commit();
        System.out.println(friend_list_editor);
        Button btn_invite_friend = (Button)findViewById(R.id.btn_invite_friend);
        btn_invite_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(Settings.this);
                aDialog.setTitle("친구 아이디를 입력해 주세요");
                aDialog.setView(R.layout.invite_friend);

                aDialog.setPositiveButton("친구추가", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            et_friend_id = (EditText) findViewById(R.id.et_friend_id);
                            String friend_id = et_friend_id.getText().toString();
                            friend_list_editor.putString(friend_id, friend_id);
                        } catch (Exception ex) {
                            Log.e("error", "null", ex);
                        }
                    }
                });
                aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = aDialog.create();
                ad.show();
            }
        });
    }

}
