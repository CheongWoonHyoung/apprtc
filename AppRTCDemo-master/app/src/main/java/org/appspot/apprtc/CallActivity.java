/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import org.appspot.apprtc.AppRTCClient.RoomConnectionParameters;
import org.appspot.apprtc.AppRTCClient.SignalingParameters;
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionParameters;
import org.appspot.apprtc.util.LooperExecutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallActivity extends Activity
        implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents,MediaRecorder.OnInfoListener {





  public static final String EXTRA_ROOMID =
          "org.appspot.apprtc.ROOMID";
  public static final String EXTRA_LOOPBACK =
          "org.appspot.apprtc.LOOPBACK";
  public static final String EXTRA_VIDEO_CALL =
          "org.appspot.apprtc.VIDEO_CALL";
  public static final String EXTRA_VIDEO_WIDTH =
          "org.appspot.apprtc.VIDEO_WIDTH";
  public static final String EXTRA_VIDEO_HEIGHT =
          "org.appspot.apprtc.VIDEO_HEIGHT";
  public static final String EXTRA_VIDEO_FPS =
          "org.appspot.apprtc.VIDEO_FPS";
  public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
          "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
  public static final String EXTRA_VIDEO_BITRATE =
          "org.appspot.apprtc.VIDEO_BITRATE";
  public static final String EXTRA_VIDEOCODEC =
          "org.appspot.apprtc.VIDEOCODEC";
  public static final String EXTRA_HWCODEC_ENABLED =
          "org.appspot.apprtc.HWCODEC";
  public static final String EXTRA_AUDIO_BITRATE =
          "org.appspot.apprtc.AUDIO_BITRATE";
  public static final String EXTRA_AUDIOCODEC =
          "org.appspot.apprtc.AUDIOCODEC";
  public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
          "org.appspot.apprtc.NOAUDIOPROCESSING";
  public static final String EXTRA_OPENSLES_ENABLED =
          "org.appspot.apprtc.OPENSLES";
  public static final String EXTRA_DISPLAY_HUD =
          "org.appspot.apprtc.DISPLAY_HUD";
  public static final String EXTRA_CMDLINE =
          "org.appspot.apprtc.CMDLINE";
  public static final String EXTRA_RUNTIME =
          "org.appspot.apprtc.RUNTIME";
  private static final String TAG = "CallRTCClient";

  // List of mandatory application permissions.
  private static final String[] MANDATORY_PERMISSIONS = {
          "android.permission.MODIFY_AUDIO_SETTINGS",
          "android.permission.RECORD_AUDIO",
          "android.permission.INTERNET"
  };

  // Peer connection statistics callback period in ms.
  private static final int STAT_CALLBACK_PERIOD = 1000;
  // Local preview screen position before call is connected.
  private static final int LOCAL_X_CONNECTING = 0;
  private static final int LOCAL_Y_CONNECTING = 0;
  private static final int LOCAL_WIDTH_CONNECTING = 100;
  private static final int LOCAL_HEIGHT_CONNECTING = 100;
  // Local preview screen position after call is connected.
  private static final int LOCAL_X_CONNECTED = 72;
  private static final int LOCAL_Y_CONNECTED = 72;
  private static final int LOCAL_WIDTH_CONNECTED = 25;
  private static final int LOCAL_HEIGHT_CONNECTED = 25;
  // Remote video screen position
  private static final int REMOTE_X = 0;
  private static final int REMOTE_Y = 0;
  private static final int REMOTE_WIDTH = 100;
  private static final int REMOTE_HEIGHT = 100;
  private PeerConnectionClient peerConnectionClient = null;
  private AppRTCClient appRtcClient;
  private SignalingParameters signalingParameters;
  private AppRTCAudioManager audioManager = null;
  private EglBase rootEglBase;
  private SurfaceViewRenderer localRender;
  private SurfaceViewRenderer remoteRender;
  private PercentFrameLayout localRenderLayout;
  private PercentFrameLayout remoteRenderLayout;
  private ScalingType scalingType;
  private Toast logToast;
  private boolean commandLineRun;
  private boolean active;
  private int runTimeMs;
  private boolean activityRunning;
  private RoomConnectionParameters roomConnectionParameters;
  private PeerConnectionParameters peerConnectionParameters;
  private boolean iceConnected;
  private boolean isError;
  private boolean callControlFragmentVisible = true;
  private long callStartedTimeMs = 0;
  //custom
  private ArrayList<HashMap<Integer,HashMap>> script_list;
  private ArrayList<HashMap<String,String>> story_list;
  private ArrayList<HashMap<String, String>> scene_list;

  public SharedPreferences album_list;
  public SharedPreferences.Editor album_list_editor;

  private String sdRootPath;
  private String User_character_Id;
  private String idx;
  private String bookid;
  String mFilePath;

  int scid_loop = 0;
  int scene_loop = 0;
  boolean scene_chk = false;

  ImageView btnRecord;
  ImageView btnStop;
  MediaPlayer mPlayer = null;
  MediaRecorder mRecorder = null;
  boolean myturn = false;

  // Controls
  CallFragment callFragment;
  HudFragment hudFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    script_list = (ArrayList<HashMap<Integer, HashMap>>) getIntent().getSerializableExtra("script");
    story_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("story");
    scene_list = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("scene_list");

    User_character_Id = String.valueOf(getIntent().getExtras().getString("User"));
    idx= String.valueOf(getIntent().getExtras().getString("idx"));
    bookid = String.valueOf(getIntent().getExtras().getString("bookid"));
    Log.d("ghost", "initial" + String.valueOf(scene_loop));
    Log.d("ghost", "initial" + String.valueOf(scid_loop));
    active = false;

    Thread.setDefaultUncaughtExceptionHandler(
            new UnhandledExceptionHandler(this));

    setContentView(R.layout.activity_call);
    btnRecord = (ImageView) findViewById(R.id.btnRecord);
    btnStop = (ImageView) findViewById(R.id.btnStop);

    //////////initial setting//////////////////////////////////////////////
    btnRecord.setBackgroundResource(R.drawable.btn_voice_inactive3x);
    btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
    btnStop.setEnabled(true);
    btnRecord.setEnabled(false);
    ///////////////////////////////////////////////////////////////////////

    album_list = this.getSharedPreferences(getPackageName(),
            Activity.MODE_PRIVATE);
    album_list_editor = album_list.edit();

    sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    mFilePath = sdRootPath + "/.mp3";

    iceConnected = false;
    signalingParameters = null;
    scalingType = ScalingType.SCALE_ASPECT_FILL;

    // Create UI controls.
    localRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
    remoteRender = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
    localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
    remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
    callFragment = new CallFragment();
    hudFragment = new HudFragment();

    // Show/hide call control fragment on view click.
    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggleCallControlFragmentVisibility();
      }
    };

    localRender.setOnClickListener(listener);
    remoteRender.setOnClickListener(listener);

    // Create video renderers.
    rootEglBase = new EglBase();
    remoteRender.init(rootEglBase.getContext(), null);

    // Check for mandatory permissions.
    for (String permission : MANDATORY_PERMISSIONS) {
      if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        logAndToast("Permission " + permission + " is not granted");
        setResult(RESULT_CANCELED);
        finish();
        return;
      }
    }

    // Get Intent parameters.
    final Intent intent = getIntent();
    Uri roomUri = intent.getData();
    if (roomUri == null) {
      logAndToast(getString(R.string.missing_url));
      Log.e(TAG, "Didn't get any URL in intent!");
      setResult(RESULT_CANCELED);
      finish();
      return;
    }
    String roomId = intent.getStringExtra(EXTRA_ROOMID);
    if (roomId == null || roomId.length() == 0) {
      logAndToast(getString(R.string.missing_url));
      Log.e(TAG, "Incorrect room ID in intent!");
      setResult(RESULT_CANCELED);
      finish();
      return;
    }
    boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
    peerConnectionParameters = new PeerConnectionParameters(
            intent.getBooleanExtra(EXTRA_VIDEO_CALL, true),
            loopback,
            intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0),
            intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0),
            intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
            intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0),
            intent.getStringExtra(EXTRA_VIDEOCODEC),
            intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
            intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0),
            intent.getStringExtra(EXTRA_AUDIOCODEC),
            intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
            intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false));
    commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
    runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

    // Create connection client and connection parameters.
    appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
    roomConnectionParameters = new RoomConnectionParameters(
            roomUri.toString(), roomId, loopback);

    // Send intent arguments to fragments.
    callFragment.setArguments(intent.getExtras());
    hudFragment.setArguments(intent.getExtras());
    // Activate call and HUD fragments and start the call.
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.add(R.id.call_fragment_container, callFragment);
    ft.add(R.id.hud_fragment_container, hudFragment);
    ft.commit();
    startCall();

    // For command line execution run connection for <runTimeMs> and exit.
    if (commandLineRun && runTimeMs > 0) {
      (new Handler()).postDelayed(new Runnable() {
        public void run() {
          disconnect();
        }
      }, runTimeMs);
    }

    //연결시작
    peerConnectionClient = PeerConnectionClient.getInstance();
    peerConnectionClient.createPeerConnectionFactory(
            CallActivity.this, peerConnectionParameters, CallActivity.this);

    //SharedPref: 친구목록
    album_list = this.getSharedPreferences(getPackageName(),
            Activity.MODE_PRIVATE);
    album_list_editor = album_list.edit();


    if (!script_list.isEmpty()){
      try {
        HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);

        FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play);
        int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());
        fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));

        TextView tv_script = (TextView) findViewById(R.id.tv_script);
        tv_script.setText(script_map.get("script"));


      }catch(Exception ex){
        Log.e("NONO", "NONO", ex);

      }
    }


    btnRecord.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {

        Log.d("ghost", "btnRecord" + String.valueOf(scene_loop));
        Log.d("ghost", "btnRecord" + String.valueOf(scid_loop));

        try {
          HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);

          String scene_loop_string = String.valueOf(scene_loop);
          String scid_loop_string = String.valueOf(scid_loop);

          Log.e("scene_loop", scene_loop_string);
          Log.e("scid_loop", scid_loop_string);
          String mp3_filename ="/"+ script_map.get("scid") +".mp3";
          Log.d("flow", "section click");

          //내차례

          if(Objects.equals(script_map.get("cid"), User_character_Id)) {

            Log.d("flow", "section C");
            if (active == false) {
              Log.d("flow", "section D");
              active = true;
              onBtnRecord(mp3_filename);
              btnRecord.setBackgroundResource(R.drawable.btn_voice_push3x);

            } else if (active == true) {
              Log.d("flow", "section E");
              active = false;
              onBtnStop();
              btnRecord.setBackgroundResource(R.drawable.btn_voice_inactive3x);
              btnRecord.setEnabled(false);
              btnStop.setBackgroundResource(R.drawable.btn_play_normal3x);
              btnStop.setEnabled(true);
              if (scene_loop == script_list.size()) {
                Log.e("loop_end", "here_ended");
                Save(idx);
              }
            }
            Log.d("flow", "section F");
          }
          Log.d("flow", "section G");
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });

    btnStop.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Log.d("ghost", "btnStop" + String.valueOf(scene_loop));
        Log.d("ghost", "btnStop" + String.valueOf(scid_loop));
        try {
          if(scene_loop == script_list.size()){
            Log.e("loop_end", "here_ended");
            Save(idx);
          }

          HashMap<String, String> script_map = script_list.get(scene_loop).get(scid_loop);
          ///////////씬 갱신//////////////////////////////////////////////////////////////////////////////////////////////////
          if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
            scid_loop++;
            scene_chk = false;
          } else {
            scene_chk = true;
            scene_loop++;
          }
          Log.d("ghost", "btnStop" + String.valueOf(scene_loop));
          Log.d("ghost", "btnStop" + String.valueOf(scid_loop));
          if (scene_chk == true) {
            scid_loop = 0;
          }

          HashMap<String, String> script_map_bef = script_list.get(scene_loop).get(scid_loop);
          FrameLayout fl_play = (FrameLayout) findViewById(R.id.fl_play);
          int play_bg = getResources().getIdentifier(scene_list.get(scene_loop).get("sid"), "drawable", getPackageName());

          fl_play.setBackgroundDrawable(getResources().getDrawable(play_bg));
          TextView tv_script = (TextView) findViewById(R.id.tv_script);
          tv_script.setText(script_map_bef.get("script"));
          

          if (scid_loop < Integer.parseInt(script_map.get("script_length")) - 1) {
            scid_loop++;
            scene_chk = false;
          } else {
            scene_chk = true;
            scene_loop++;
          }


          ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          HashMap<String, String> script_map_aft = script_list.get(scene_loop).get(scid_loop);
          Log.d("flow", "section A");
          if (!script_list.isEmpty() && Objects.equals(script_map_aft.get("cid"), User_character_Id)) {
            Log.d("flow", "section B");
            Log.e("cid_play_loop", script_map_aft.get("cid"));
            btnRecord.setBackgroundResource(R.drawable.btn_voice_normal3x);
            btnRecord.setEnabled(true);
            btnStop.setBackgroundResource(R.drawable.btn_play_inactive3x);
            btnStop.setEnabled(false);
          }
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
  }

  public void Save(final String idx){
    AlertDialog.Builder aDialog = new AlertDialog.Builder(CallActivity.this);
    aDialog.setTitle("종료하시겠습니까");

    aDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        //저장하기
        //앨범 리스트 갯수 업데이트
        album_list_editor.putInt("album_length", album_list.getInt("album_length", 0) + 1);
        //앨범 인덱스 & Key
        String album_key = Integer.toString(album_list.getInt("album_length", 0));
        if (bookid != null && idx != null) {
          album_list_editor.putString(album_key, bookid);
          album_list_editor.putString(bookid, idx);
          album_list_editor.commit();
        }
        finish();
      }
    });

    aDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        //Do Nothing
      }
    });
    AlertDialog ad = aDialog.create();
    ad.show();
  }

  public void onBtnPlay(String mFilePath) {
    if( mPlayer != null ) {
      mPlayer.stop();
      mPlayer.release();
      mPlayer = null;
    }
    mPlayer = new MediaPlayer();

    try {
      mPlayer.setDataSource(mFilePath);
      Log.e("path", mFilePath);
      mPlayer.prepare();
    } catch(IOException e) {
      Log.d("tag", "Audio Play error");
      return;
    }
    mPlayer.start();
  }

  public void onBtnRecord(String num) {

    Log.d("Record", "Record Started");
    if( mRecorder != null ) {
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
    mRecorder.setOnInfoListener(this);

    try {
      mRecorder.prepare();
    } catch(IOException e) {
      Log.d("tag", "Record Prepare error");
    }
    mRecorder.start();

    // 버튼 활성/비활성 설정
    //btnRecord.setEnabled(false);
    //btnStop.setEnabled(true);
    //mBtnPlay.setEnabled(false);
  }

  public void onInfo(MediaRecorder mr, int what, int extra) {
    switch( what ) {
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED :
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED :
        onBtnStop();
        break;
    }
  }


  public void onBtnStop() {
    mRecorder.stop();
    mRecorder.reset();
    mRecorder.release();
    Log.d("Record","Record Stopped");
    // 버튼 활성/비활성 설정
    //btnRecord.setEnabled(true);
    //btnStop.setEnabled(false);
  }

  // Activity interfaces
  @Override
  public void onPause() {
    super.onPause();
    activityRunning = false;
    if (peerConnectionClient != null) {
      peerConnectionClient.stopVideoSource();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    activityRunning = true;
    if (peerConnectionClient != null) {
      peerConnectionClient.startVideoSource();
    }
  }

  @Override
  protected void onDestroy() {
    disconnect();
    if (logToast != null) {
      logToast.cancel();
    }
    activityRunning = false;
    rootEglBase.release();
    super.onDestroy();
  }

  // CallFragment.OnCallEvents interface implementation.
  @Override
  public void onCallHangUp() {
    disconnect();
  }

  @Override
  public void onCameraSwitch() {
    if (peerConnectionClient != null) {
      peerConnectionClient.switchCamera();
    }
  }

  @Override
  public void onVideoScalingSwitch(ScalingType scalingType) {
    this.scalingType = scalingType;
    updateVideoView();
  }

  @Override
  public void onCaptureFormatChange(int width, int height, int framerate) {
    if (peerConnectionClient != null) {
      peerConnectionClient.changeCaptureFormat(width, height, framerate);
    }
  }

  // Helper functions.
  private void toggleCallControlFragmentVisibility() {
    if (!iceConnected || !callFragment.isAdded()) {
      return;
    }
    // Show/hide call control fragment
    callControlFragmentVisible = !callControlFragmentVisible;
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    if (callControlFragmentVisible) {
      ft.show(callFragment);
      ft.show(hudFragment);
    } else {
      ft.hide(callFragment);
      ft.hide(hudFragment);
    }
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    ft.commit();
  }

  private void updateVideoView() {
    remoteRenderLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
    remoteRender.setScalingType(scalingType);
    remoteRender.setMirror(false);

    if (iceConnected) {
      localRenderLayout.setPosition(
              LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
      localRender.setScalingType(ScalingType.SCALE_ASPECT_FIT);
    } else {
      localRenderLayout.setPosition(
              LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING);
      localRender.setScalingType(scalingType);
    }
    localRender.setMirror(true);

    localRender.requestLayout();
    remoteRender.requestLayout();
  }

  private void startCall() {
    if (appRtcClient == null) {
      Log.e(TAG, "AppRTC client is not allocated for a call.");
      return;
    }
    callStartedTimeMs = System.currentTimeMillis();

    // Start room connection.
    logAndToast(getString(R.string.connecting_to,
            roomConnectionParameters.roomUrl));
    appRtcClient.connectToRoom(roomConnectionParameters);

    // Create and audio manager that will take care of audio routing,
    // audio modes, audio device enumeration etc.
    audioManager = AppRTCAudioManager.create(this, new Runnable() {
              // This method will be called each time the audio state (number and
              // type of devices) has been changed.
              @Override
              public void run() {
                onAudioManagerChangedState();
              }
            }
    );
    // Store existing audio settings and change audio mode to
    // MODE_IN_COMMUNICATION for best possible VoIP performance.
    Log.d(TAG, "Initializing the audio manager...");
    audioManager.init();
  }

  // Should be called from UI thread
  private void callConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    Log.i(TAG, "Call connected: delay=" + delta + "ms");
    if (peerConnectionClient == null || isError) {
      Log.w(TAG, "Call is connected in closed or error state");
      return;
    }
    // Update video view.
    updateVideoView();
    // Enable statistics callback.
    peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
  }

  private void onAudioManagerChangedState() {
    // TODO(henrika): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
    // is active.
  }

  // Disconnect from remote resources, dispose of local resources, and exit.
  private void disconnect() {
    activityRunning = false;
    if (appRtcClient != null) {
      appRtcClient.disconnectFromRoom();
      appRtcClient = null;
    }
    if (peerConnectionClient != null) {
      peerConnectionClient.close();
      peerConnectionClient = null;
    }
    if (localRender != null) {
      localRender.release();
      localRender = null;
    }
    if (remoteRender != null) {
      remoteRender.release();
      remoteRender = null;
    }
    if (audioManager != null) {
      audioManager.close();
      audioManager = null;
    }
    if (iceConnected && !isError) {
      setResult(RESULT_OK);
    } else {
      setResult(RESULT_CANCELED);
    }
    finish();
  }

  private void disconnectWithErrorMessage(final String errorMessage) {
    if (commandLineRun || !activityRunning) {
      Log.e(TAG, "Critical error: " + errorMessage);
      disconnect();
    } else {
      new AlertDialog.Builder(this)
              .setTitle(getText(R.string.channel_error_title))
              .setMessage(errorMessage)
              .setCancelable(false)
              .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                  dialog.cancel();
                  disconnect();
                }
              }).create().show();
    }
  }

  // Log |msg| and Toast about it.
  private void logAndToast(String msg) {
    Log.d(TAG, msg);
    if (logToast != null) {
      logToast.cancel();
    }
    logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    logToast.show();
  }

  private void reportError(final String description) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError) {
          isError = true;
          disconnectWithErrorMessage(description);
        }
      }
    });
  }

  // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
  // All callbacks are invoked from websocket signaling looper thread and
  // are routed to UI thread.
  private void onConnectedToRoomInternal(final SignalingParameters params) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;

    signalingParameters = params;
    logAndToast("Creating peer connection, delay=" + delta + "ms");
    peerConnectionClient.createPeerConnection(rootEglBase.getContext(),
            localRender, remoteRender, signalingParameters);

    if (signalingParameters.initiator) {
      logAndToast("Creating OFFER...");
      // Create offer. Offer SDP will be sent to answering client in
      // PeerConnectionEvents.onLocalDescription event.
      peerConnectionClient.createOffer();
    } else {
      if (params.offerSdp != null) {
        peerConnectionClient.setRemoteDescription(params.offerSdp);
        logAndToast("Creating ANSWER...");
        // Create answer. Answer SDP will be sent to offering client in
        // PeerConnectionEvents.onLocalDescription event.
        peerConnectionClient.createAnswer();
      }
      if (params.iceCandidates != null) {
        // Add remote ICE candidates from room.
        for (IceCandidate iceCandidate : params.iceCandidates) {
          peerConnectionClient.addRemoteIceCandidate(iceCandidate);
        }
      }
    }
  }

  @Override
  public void onConnectedToRoom(final SignalingParameters params) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        onConnectedToRoomInternal(params);
      }
    });
  }

  @Override
  public void onRemoteDescription(final SessionDescription sdp) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (peerConnectionClient == null) {
          Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
          return;
        }
        logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
        peerConnectionClient.setRemoteDescription(sdp);
        if (!signalingParameters.initiator) {
          logAndToast("Creating ANSWER...");
          // Create answer. Answer SDP will be sent to offering client in
          // PeerConnectionEvents.onLocalDescription event.
          peerConnectionClient.createAnswer();
        }
      }
    });
  }

  @Override
  public void onRemoteIceCandidate(final IceCandidate candidate) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (peerConnectionClient == null) {
          Log.e(TAG,
                  "Received ICE candidate for non-initilized peer connection.");
          return;
        }
        peerConnectionClient.addRemoteIceCandidate(candidate);
      }
    });
  }

  @Override
  public void onChannelClose() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        logAndToast("Remote end hung up; dropping PeerConnection");
        disconnect();
      }
    });
  }

  @Override
  public void onChannelError(final String description) {
    reportError(description);
  }

  // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
  // Send local peer connection SDP and ICE candidates to remote party.
  // All callbacks are invoked from peer connection client looper thread and
  // are routed to UI thread.
  @Override
  public void onLocalDescription(final SessionDescription sdp) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (appRtcClient != null) {
          logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
          if (signalingParameters.initiator) {
            appRtcClient.sendOfferSdp(sdp);
          } else {
            appRtcClient.sendAnswerSdp(sdp);
          }
        }
      }
    });
  }

  @Override
  public void onIceCandidate(final IceCandidate candidate) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (appRtcClient != null) {
          appRtcClient.sendLocalIceCandidate(candidate);
        }
      }
    });
  }

  @Override
  public void onIceConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        logAndToast("ICE connected, delay=" + delta + "ms");
        iceConnected = true;
        callConnected();
      }
    });
  }

  @Override
  public void onIceDisconnected() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        logAndToast("ICE disconnected");
        iceConnected = false;
        disconnect();
      }
    });
  }

  @Override
  public void onPeerConnectionClosed() {
  }

  @Override
  public void onPeerConnectionStatsReady(final StatsReport[] reports) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError && iceConnected) {
          hudFragment.updateEncoderStatistics(reports);
        }
      }
    });
  }

  @Override
  public void onPeerConnectionError(final String description) {
    reportError(description);
  }
}
