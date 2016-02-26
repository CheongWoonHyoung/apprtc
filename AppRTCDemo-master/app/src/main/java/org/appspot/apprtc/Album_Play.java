package org.appspot.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Donghyun on 2016. 2. 15..
 */
public class Album_Play extends Activity {

    String mFilePath;
    //custom
    private ArrayList<HashMap<Integer, HashMap>> script_list;
    private ArrayList<HashMap<String, String>> story_list;
    private ArrayList<HashMap<String, String>> scene_list;

    private boolean playing;
    private String sdRootPath;
    private String User_character_Id;
    private String record_path;

    int scid_loop = 0;
    int scene_loop = 0;
    boolean scene_chk = false;

    ImageView btnRecord_grey;
    ImageView btnStop_grey;
    ImageView btnRecord;
    ImageView btnStop;
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    boolean btnRecord_clicked = false;

    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        script_list = (ArrayList<HashMap<Integer, HashMap>>) getIntent().getSerializableExtra("script_list");
        story_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("story");
        scene_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("scene_list");

        User_character_Id = String.valueOf(getIntent().getExtras().getString("User"));
        record_path = String.valueOf(getIntent().getExtras().getString("record_path"));

        setContentView(R.layout.activity_album);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        playing = false;
        btnStop = (ImageView) findViewById(R.id.btnStop_play);
        btnStop.setBackgroundResource(R.drawable.btn_play_inactive3x);

        sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        btnStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    btnStop.setBackgroundResource(R.drawable.btn_play_push3x);
                }else if(action == MotionEvent.ACTION_UP){
                    btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
                    System.out.println("loop");
                    System.out.println(scene_loop);
                    System.out.println(scid_loop);
                    HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);
                    if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
                        scid_loop++;
                        scene_chk = false;
                    } else {
                        scene_chk = true;
                        scene_loop++;
                    }
                    if (scene_chk == true) {
                        scid_loop = 0;
                    }
                    HashMap<String, String> script_map_bef = script_list.get(scene_loop).get(scid_loop);
                    FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play_play);
                    int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());

                    //fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));
                    TextView tv_script = (TextView) findViewById(R.id.tv_script_play);
                    tv_script.setText(script_map_bef.get("script"));
                    HashMap<String, String> script_map_aft = script_list.get(scene_loop).get(scid_loop);
                    if (!script_list.isEmpty() && Objects.equals(script_map_aft.get("cid"), User_character_Id)) {
                        String scene_loop_string = String.valueOf(scene_loop);
                        String scid_loop_string = String.valueOf(scid_loop);
                        String mp3_filename = "/" + scene_loop_string + scid_loop_string + ".mp3";
                        mFilePath = sdRootPath + mp3_filename;

                        onBtnPlay(mFilePath);
                    } else {
                        Play(script_map_aft);
                    }
                }
                return true;
            }
        });


        if (!script_list.isEmpty()){
            try {
                HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);
                System.out.println("loop_A");
                System.out.println(scene_loop);
                System.out.println(scid_loop);
                System.out.println(script_map);
                System.out.println(script_map.get("script"));

                FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play_play);
                int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());
                fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));

                TextView tv_script = (TextView) findViewById(R.id.tv_script_play);
                tv_script.setText(script_map.get("script"));
                Play(script_map);

            }catch(Exception ex){
                Log.e("NONO", "NONO", ex);

            }
        }
    }

    public void Play(HashMap<String, String> script_map){
        if( mp == null){
            mp = new MediaPlayer();
        }
        try{
            int tmpID = getApplicationContext().getResources().getIdentifier(script_map.get("scid"), "raw", getPackageName());
            Context con = getApplicationContext();
            mp = new MediaPlayer();
            mp = MediaPlayer.create(con, tmpID);
            mp.start();
            btnStop.setEnabled(false);
            btnStop.setBackgroundResource(R.drawable.btn_play_inactive3x);
        }catch (Exception e){
            btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
                btnStop.setEnabled(true);
            }
        });
    }

    public void onBtnPlay(String filepath) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        try {
            mPlayer.setDataSource(filepath);
            mPlayer.prepare();
            mPlayer.start();
            btnStop.setEnabled(false);
            btnStop.setBackgroundResource(R.drawable.btn_play_inactive3x);
        } catch (IOException e) {
            Log.d("tag", "Audio Play error");
            return;
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
                btnStop.setEnabled(true);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}