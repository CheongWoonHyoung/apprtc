package org.appspot.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        script_list = (ArrayList<HashMap<Integer, HashMap>>) getIntent().getSerializableExtra("script_list");
        story_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("story");
        scene_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("scene_list");

        User_character_Id = String.valueOf(getIntent().getExtras().getString("User"));

        setContentView(R.layout.activity_album);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        playing = false;
        btnStop = (ImageView) findViewById(R.id.btnStop_play);

        btnStop.setBackgroundResource(R.drawable.btn_play_inactive3x);

        sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath = sdRootPath + "/.mp3";

        if (!script_list.isEmpty()) {
            try {
                HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);

                FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play_play);
                int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());
                fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));

                TextView tv_script = (TextView) findViewById(R.id.tv_script_play);
                tv_script.setText(script_map.get("script"));

                if (!script_list.isEmpty() && script_map.get("cid") == User_character_Id) {
                    //btnRecord.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_voice_push));
                } else {
                    //go_record(scene_loop, scid_loop);
                    //onBtnRecord();
                }

                if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
                    scid_loop++;
                    scene_chk = false;
                } else {
                    scene_chk = true;
                    scene_loop++;
                }
            } catch (Exception ex) {
                Log.e("NONO", "NONO", ex);

            }
        }


        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);
                    String scene_loop_string = String.valueOf(scene_loop);
                    String scid_loop_string = String.valueOf(scid_loop);

                    if (playing == false) {
                        btnStop.setBackgroundResource(R.drawable.btn_play_push3x);
                        String filepath = sdRootPath + "/" + scene_loop_string + scid_loop_string + ".mp3";

                        onBtnPlay(filepath);

                        playing = true;

                    } else if (playing == true) {
                        btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);

                        ///////////씬 갱신//////////////////////////////////////////////////////////////////////////////////////////////////
                        if (scene_chk == true) {
                            scid_loop = 0;
                        }

                        FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play);
                        int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());
                        fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));

                        TextView tv_script = (TextView) findViewById(R.id.tv_script);
                        tv_script.setText(script_map.get("script"));

                        if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
                            scid_loop++;
                            scene_chk = false;
                        } else {
                            scene_chk = true;
                            scene_loop++;
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                        playing = false;

                    }
                    if (!script_list.isEmpty() && !Objects.equals(script_map.get("cid"), User_character_Id)) {
                        //btnRecord.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_voice_inactive));
                        //씬 갱신
                        if (scene_chk == true) {
                            scid_loop = 0;
                        }

                        FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play_play);
                        int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());
                        fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));

                        TextView tv_script = (TextView) findViewById(R.id.tv_script_play);
                        tv_script.setText(script_map.get("script"));


                        //재생하기
                        if (Objects.equals(script_map.get("audio"), "true")) {
                            //재생
                            //onBtnPlay(mp3_filename);
                        }
                        if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
                            scid_loop++;
                            scene_chk = false;
                        } else {
                            scene_chk = true;
                            scene_loop++;
                        }

                        if (scene_loop > script_list.size()) {
                            //종료하시겠습니까?
                        }
                    }
                } catch (Exception ex) {
                    Log.e("Record", "NONO", ex);
                }
            }
        });
    }

    public void onBtnPlay(String filepath) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(filepath);
            mPlayer.prepare();
        } catch (IOException e) {
            Log.d("tag", "Audio Play error");
            return;
        }
        mPlayer.start();
    }

    public void onBtnRecord(String num) {

        Log.d("Record", "Record Started");
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(sdRootPath + num);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mRecorder.setMaxDuration(5 * 1000);
        mRecorder.setMaxFileSize(5 * 1000 * 1000);
        //mRecorder.setOnInfoListener(this);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.d("tag", "Record Prepare error");
        }
        mRecorder.start();

        // 버튼 활성/비활성 설정
        //btnRecord.setEnabled(false);
        //btnStop.setEnabled(true);
        //mBtnPlay.setEnabled(false);
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch (what) {
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                onBtnStop();
                break;
        }
    }

    public void onBtnStop() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        Log.d("Record", "Record Stopped");
        // 버튼 활성/비활성 설정
        //btnRecord.setEnabled(true);
        //btnStop.setEnabled(false);
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