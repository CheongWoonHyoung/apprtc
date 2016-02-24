/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class ConnectActivity extends Activity {
  private static final String TAG = "ConnectActivity";
  private static final int CONNECTION_REQUEST = 1;
  private static boolean commandLineRun = false;

  private ImageButton addRoomButton;
  private ImageButton removeRoomButton;
  private ImageButton connectButton;
  private ImageButton connectLoopbackButton;
  private EditText roomEditText;
  private ListView roomListView;
  private SharedPreferences sharedPref;
  private String keyprefVideoCallEnabled;
  private String keyprefResolution;
  private String keyprefFps;
  private String keyprefCaptureQualitySlider;
  private String keyprefVideoBitrateType;
  private String keyprefVideoBitrateValue;
  private String keyprefVideoCodec;
  private String keyprefAudioBitrateType;
  private String keyprefAudioBitrateValue;
  private String keyprefAudioCodec;
  private String keyprefHwCodecAcceleration;
  private String keyprefNoAudioProcessingPipeline;
  private String keyprefOpenSLES;
  private String keyprefDisplayHud;
  private String keyprefRoomServerUrl;
  private String keyprefRoom;
  private String keyprefRoomList;
  private ArrayList<String> roomList;
  private ArrayAdapter<String> adapter;

  //Custom
  private ArrayList<HashMap<Integer,HashMap>> script_list;
  private ArrayList<HashMap<String,String>> character_list;
  private ArrayList<HashMap<String,String>> story_list;
  ArrayList<HashMap<String, String>> scene_list;

  private String roomId_;
  private Integer MaxPlayer;
  private String User_character_Id;
  private String idx;
  private String bookid;
  private boolean character_select;

  boolean loopback;
  int runTimeMs;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Custom
    script_list = (ArrayList<HashMap<Integer,HashMap>>) getIntent().getSerializableExtra("script");
    character_list = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("character");
    story_list = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("story");
    scene_list = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("scene_list");
    bookid = String.valueOf(getIntent().getExtras().getString("bookid"));
    idx = String.valueOf(getIntent().getExtras().getString("idx"));

    roomId_ = String.valueOf(getIntent().getExtras().getString("roomId"));
    MaxPlayer = Integer.parseInt(String.valueOf(getIntent().getExtras().getString("MaxPlayer")));


    // Get setting keys.
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
    keyprefResolution = getString(R.string.pref_resolution_key);
    keyprefFps = getString(R.string.pref_fps_key);
    keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
    keyprefVideoBitrateType = getString(R.string.pref_startvideobitrate_key);
    keyprefVideoBitrateValue = getString(R.string.pref_startvideobitratevalue_key);
    keyprefVideoCodec = getString(R.string.pref_videocodec_key);
    keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
    keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
    keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
    keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
    keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
    keyprefOpenSLES = getString(R.string.pref_opensles_key);
    keyprefDisplayHud = getString(R.string.pref_displayhud_key);
    keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
    keyprefRoom = getString(R.string.pref_room_key);
    keyprefRoomList = getString(R.string.pref_room_list_key);

    //화면 가로 고정
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    setContentView(R.layout.activity_connect);

    //custom
    //캐릭터 이미지 + 텍스트 뿌려주기
    for (int character_loop = 0; character_loop < 6; character_loop++) {
      if (character_loop < MaxPlayer) {
        String img_value = Integer.toString(character_loop + 1);

        //c1,c2, 이미지 이름 찾기
        int im_temp_ = getResources().getIdentifier(character_list.get(character_loop).get("cid"), "drawable", getPackageName());
        //이미지 뷰 찾기
        int iv_temp_ = getResources().getIdentifier("iv_character_"+img_value,"id", getPackageName());
        int tv_temp_ = getResources().getIdentifier("tv_character_"+img_value, "id", getPackageName());


        ImageView iv_temp = (ImageView) findViewById(iv_temp_);
        iv_temp.setImageDrawable(getResources().getDrawable(im_temp_));

        TextView tv_temp = (TextView) findViewById(tv_temp_);
        tv_temp.setText(character_list.get(character_loop).get("name"));
      }
    }

    character_select = false;

    // If an implicit VIEW intent is launching the app, go directly to that URL.
    final Intent intent = getIntent();
    loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false);
    runTimeMs = intent.getIntExtra(CallActivity.EXTRA_RUNTIME, 0);

    LinearLayout.OnClickListener mClickListener = new View.OnClickListener() {
      public void onClick(View v) {
        character_select = true;
        switch (v.getId()) {
          case R.id.character_1:
            User_character_Id = character_list.get(0).get("cid");
            break;
          case R.id.character_2:
            User_character_Id = character_list.get(1).get("cid");
            break;
          case R.id.character_3:
            User_character_Id = character_list.get(2).get("cid");
            break;
          case R.id.character_4:
            User_character_Id = character_list.get(3).get("cid");
            break;
          case R.id.character_5:
            User_character_Id = character_list.get(4).get("cid");
            break;
          case R.id.character_6:
            User_character_Id = character_list.get(5).get("cid");
            break;
        }
        System.out.println(User_character_Id);
        Story_Start();
      }
    };
    findViewById(R.id.character_1).setOnClickListener(mClickListener);
    findViewById(R.id.character_2).setOnClickListener(mClickListener);
    findViewById(R.id.character_3).setOnClickListener(mClickListener);
    findViewById(R.id.character_4).setOnClickListener(mClickListener);
    findViewById(R.id.character_5).setOnClickListener(mClickListener);
    findViewById(R.id.character_6).setOnClickListener(mClickListener);
  }

  public void Story_Start(){
    //시작하기버튼누르면 작동하도록변경하기
    TextView tv_startplay = (TextView) findViewById(R.id.tv_storyplay);
    tv_startplay.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        if (!commandLineRun && character_select == true) {
          commandLineRun = true;
          //서로 연결하기
          connectToRoom(loopback, runTimeMs);
          return;
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.connect_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle presses on the action bar items.
    if (item.getItemId() == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  protected void onActivityResult(
      int requestCode, int resultCode, Intent data) {
    if (requestCode == CONNECTION_REQUEST && commandLineRun) {
      Log.d(TAG, "Return: " + resultCode);
      setResult(resultCode);
      commandLineRun = false;
      finish();
    }
  }

  private final OnClickListener connectListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      boolean loopback = false;
      commandLineRun = false;
      connectToRoom(loopback, 0);
    }
  };

  public void connectToRoom(boolean loopback, int runTimeMs) {
    // Get room name (random for loopback).

    String roomId = roomId_;
    String roomUrl = sharedPref.getString(
        keyprefRoomServerUrl,
        getString(R.string.pref_room_server_url_default));

    // Video call enabled flag.
    boolean videoCallEnabled = sharedPref.getBoolean(keyprefVideoCallEnabled,
        Boolean.valueOf(getString(R.string.pref_videocall_default)));

    // Get default codecs.
    String videoCodec = sharedPref.getString(keyprefVideoCodec,
        getString(R.string.pref_videocodec_default));
    String audioCodec = sharedPref.getString(keyprefAudioCodec,
        getString(R.string.pref_audiocodec_default));

    // Check HW codec flag.
    boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration,
        Boolean.valueOf(getString(R.string.pref_hwcodec_default)));

    // Check Disable Audio Processing flag.
    boolean noAudioProcessing = sharedPref.getBoolean(
        keyprefNoAudioProcessingPipeline,
        Boolean.valueOf(getString(R.string.pref_noaudioprocessing_default)));

    // Check OpenSL ES enabled flag.
    boolean useOpenSLES = sharedPref.getBoolean(
        keyprefOpenSLES,
        Boolean.valueOf(getString(R.string.pref_opensles_default)));

    // Get video resolution from settings.
    int videoWidth = 0;
    int videoHeight = 0;
    String resolution = sharedPref.getString(keyprefResolution,
        getString(R.string.pref_resolution_default));
    String[] dimensions = resolution.split("[ x]+");
    if (dimensions.length == 2) {
      try {
        videoWidth = Integer.parseInt(dimensions[0]);
        videoHeight = Integer.parseInt(dimensions[1]);
      } catch (NumberFormatException e) {
        videoWidth = 0;
        videoHeight = 0;
        Log.e(TAG, "Wrong video resolution setting: " + resolution);
      }
    }

    // Get camera fps from settings.
    int cameraFps = 0;
    String fps = sharedPref.getString(keyprefFps,
        getString(R.string.pref_fps_default));
    String[] fpsValues = fps.split("[ x]+");
    if (fpsValues.length == 2) {
      try {
        cameraFps = Integer.parseInt(fpsValues[0]);
      } catch (NumberFormatException e) {
        Log.e(TAG, "Wrong camera fps setting: " + fps);
      }
    }

    // Check capture quality slider flag.
    boolean captureQualitySlider = sharedPref.getBoolean(keyprefCaptureQualitySlider,
        Boolean.valueOf(getString(R.string.pref_capturequalityslider_default)));

    // Get video and audio start bitrate.
    int videoStartBitrate = 0;
    String bitrateTypeDefault = getString(
        R.string.pref_startvideobitrate_default);
    String bitrateType = sharedPref.getString(
        keyprefVideoBitrateType, bitrateTypeDefault);
    if (!bitrateType.equals(bitrateTypeDefault)) {
      String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue,
          getString(R.string.pref_startvideobitratevalue_default));
      videoStartBitrate = Integer.parseInt(bitrateValue);
    }
    int audioStartBitrate = 0;
    bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
    bitrateType = sharedPref.getString(
        keyprefAudioBitrateType, bitrateTypeDefault);
    if (!bitrateType.equals(bitrateTypeDefault)) {
      String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue,
          getString(R.string.pref_startaudiobitratevalue_default));
      audioStartBitrate = Integer.parseInt(bitrateValue);
    }

    // Check statistics display option.
    boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud,
        Boolean.valueOf(getString(R.string.pref_displayhud_default)));

    // Start AppRTCDemo activity.
    Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
    if (validateUrl(roomUrl)) {
      Uri uri = Uri.parse(roomUrl);
      Intent intent = new Intent(this, CallActivity.class);
      intent.setData(uri);
      intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
      intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
      intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
      intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
      intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
      intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
      intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
          captureQualitySlider);
      intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
      intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
      intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
      intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED,
          noAudioProcessing);
      intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
      intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
      intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
      intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
      intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
      intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

      //custom
      intent.putExtra("character", character_list);
      intent.putExtra("script", script_list);
      intent.putExtra("story", story_list);
      intent.putExtra("User", User_character_Id);
      intent.putExtra("scene_list", scene_list);
      intent.putExtra("idx", idx);
      intent.putExtra("bookid", bookid);

      startActivityForResult(intent, CONNECTION_REQUEST);
    }
  }

  private boolean validateUrl(String url) {
    if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
      return true;
    }

    new AlertDialog.Builder(this)
        .setTitle(getText(R.string.invalid_url_title))
        .setMessage(getString(R.string.invalid_url_text, url))
        .setCancelable(false)
        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
            }
          }).create().show();
    return false;
  }

  private final OnClickListener addRoomListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      String newRoom = roomEditText.getText().toString();
      if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
        adapter.add(newRoom);
        adapter.notifyDataSetChanged();
      }
    }
  };

  private final OnClickListener removeRoomListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      String selectedRoom = getSelectedItem();
      if (selectedRoom != null) {
        adapter.remove(selectedRoom);
        adapter.notifyDataSetChanged();
      }
    }
  };

  private String getSelectedItem() {
    int position = AdapterView.INVALID_POSITION;
    if (roomListView.getCheckedItemCount() > 0 && adapter.getCount() > 0) {
      position = roomListView.getCheckedItemPosition();
      if (position >= adapter.getCount()) {
        position = AdapterView.INVALID_POSITION;
      }
    }
    if (position != AdapterView.INVALID_POSITION) {
      return adapter.getItem(position);
    } else {
      return null;
    }
  }

}
