package org.appspot.apprtc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * Created by cheongwh on 2016. 2. 12..
 */
public class Call_List extends MainActivity {

    public SharedPreferences friend_list;

    private ArrayList<HashMap<String, String>> story_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> scene_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> character_list = new ArrayList<>();
    private ArrayList<HashMap<Integer, HashMap>> script_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> friend_map_list = new ArrayList<>();

    private ListView lv_friend_selected;
    private SimpleAdapter selected_friend;

    private boolean load_chk = false;

    private String character = "";
    private String script = "";
    private Integer num_friend;
    private String friend_id_search;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.call_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        num_friend = 0;
        final String MaxPlayer = String.valueOf(getIntent().getExtras().getString("MaxPlayer"));
        final String idx = String.valueOf(getIntent().getExtras().getString("idx"));
        final String bookid = String.valueOf(getIntent().getExtras().getString("bookid"));
        String download = String.valueOf(getIntent().getExtras().getString("download"));

        download = "http://blay.eerssoft.co.kr/books/list/";
        new SHJSONParser().setCallback(callback).execute(download);

        lv_friend_selected = (ListView) findViewById(R.id.lv_friend_selected);
        selected_friend = new SimpleAdapter(Call_List.this, friend_map_list, R.layout.item_friend, new String[]{"friend_id"}, new int[]{R.id.tv_selected_friend_id});
        lv_friend_selected.setAdapter(selected_friend);

        //Button: 친구초대
        final Button btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (load_chk == true) {

                    String roomId = Integer.toString((new Random()).nextInt(100000000));
                    Intent intent = new Intent(Call_List.this, ConnectActivity.class);
                    intent.putExtra("idx", idx);
                    intent.putExtra("character", character_list);
                    intent.putExtra("script", script_list);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("MaxPlayer", MaxPlayer);
                    intent.putExtra("story", story_list);
                    intent.putExtra("scene_list", scene_list);
                    intent.putExtra("bookid", bookid);
                    //친구도 room Id 넘겨주기
                    //친구한테 푸시 메시지
                    startActivity(intent);
                } else {
                    Log.e("Error", "Friend Inviting");
                }
            }
        });
        btn_invite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btn_invite.setBackgroundResource(R.drawable.btn_invite_complete_push3x);
                } else if (action == MotionEvent.ACTION_UP) {
                    btn_invite.setBackgroundResource(R.drawable.btn_invite_complete_normal3x);
                    if (load_chk == true) {

                        String roomId = Integer.toString((new Random()).nextInt(100000000));
                        Intent intent = new Intent(Call_List.this, ConnectActivity.class);
                        intent.putExtra("idx", idx);
                        intent.putExtra("character", character_list);
                        intent.putExtra("script", script_list);
                        intent.putExtra("roomId", roomId);
                        intent.putExtra("MaxPlayer", MaxPlayer);
                        intent.putExtra("story", story_list);
                        intent.putExtra("scene_list", scene_list);
                        intent.putExtra("bookid", bookid);
                        //친구도 room Id 넘겨주기
                        //친구한테 푸시 메시지
                        startActivity(intent);
                    } else {
                        Log.e("Error", "Friend Inviting");
                    }
                }
                return true;
            }
        });

        //Button: 뒤로가기
        final Button btn_back2 = (Button) findViewById(R.id.btn_back2);
        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_back2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btn_back2.setBackgroundResource(R.drawable.btn_back_full_push3x);
                } else if (action == MotionEvent.ACTION_UP) {
                    btn_back2.setBackgroundResource(R.drawable.btn_back_full_normal3x);
                    finish();
                }
                return true;
            }
        });
        //SharedPref: 친구 목록
        friend_list = this.getSharedPreferences(getPackageName(),
                Activity.MODE_PRIVATE);
        //친구 찾기
        Button btn_search_friend = (Button) findViewById(R.id.btn_search_friend);
        btn_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView et_search_friend = (AutoCompleteTextView) findViewById(R.id.et_search_friend);
                friend_id_search = friend_list.getString(et_search_friend.getText().toString(), "");

                if (Objects.equals(friend_id_search, "")) {
                    Toast.makeText(getApplicationContext(), "검색결과가 없습니다", Toast.LENGTH_SHORT);
                } else {
                    final TextView tv_searched_friend_id = (TextView) findViewById(R.id.tv_searched_friend_id);
                    tv_searched_friend_id.setText(friend_id_search);
                    tv_searched_friend_id.setVisibility(View.VISIBLE);
                    tv_searched_friend_id.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_searched_friend_id.setVisibility(View.INVISIBLE);
                            boolean friend_chk = false;
                            for(int i = 0; i< friend_map_list.size(); i++){
                                if(friend_map_list.get(i).get("friend_id") == friend_id_search)
                                    friend_chk = true;
                            }

                            //Exception: 기존에 있는친구인지, 최대 초대 인원 미만인지
                            if(friend_chk == false && friend_map_list.size() < Integer.parseInt(MaxPlayer)) {
                                HashMap<String, String> friend_map = new HashMap<>();
                                friend_map.put("friend_id", friend_id_search);
                                num_friend++;
                                friend_id_search = "";
                                friend_map_list.add(friend_map);
                                System.out.println(friend_map);
                                selected_friend.notifyDataSetChanged();
                            }
                            if(friend_chk == true)
                                Toast.makeText(getApplicationContext(), "이미 추가한 친구입니다", Toast.LENGTH_SHORT).show();
                            if(friend_map_list.size() + 1 == Integer.parseInt(MaxPlayer))
                                Toast.makeText(getApplicationContext(), "더 이상 초대할 수 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    /*
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("book.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    } */

    //리스트받아오기->그리드뷰
    private SHJSONParserCallback callback = new SHJSONParserCallback() {
        @Override
        public void onResult(JSONObject json, int parserTag) {
            try {
                if (json == null || (Integer) json.get("result") != 0) {
                    //TODO: error
                    return;
                }

                JSONArray Story_arr = (JSONArray) json.get("list");
                for (int i = 0; i < Story_arr.length(); i++) {
                    JSONObject Story_single = Story_arr.getJSONObject(i);

                    String idx = Story_single.get("idx").toString();
                    String bookid = Story_single.get("bookid").toString();
                    String title = Story_single.get("title").toString();
                    String cover = Story_single.get("image").toString();
                    String cover_small = Story_single.get("image_small").toString();

                    String download = Story_single.get("download").toString();
                    String description = Story_single.get("description").toString();
                    String version;
                    String displayversion;
                    //String maxPlayer = Story_single.get("maxPlayer").toString();
                    //String character = Story_single.get("character").toString();
                    String Character = "{\"character\":[{\"cid\":\"c1\",\"name\":\"빨간 모자\",\"image\":{\"main\":\"c.jpg\"}},{\"cid\":\"c2\",\"name\":\"늑대\",\"image\":{\"main\":\"c2.jpg\"}},{\"cid\":\"c3\",\"name\":\"할머니\",\"image\":{\"main\":\"c3.jpg\"}},{\"cid\":\"c4\",\"name\":\"사냥꾼\",\"image\":{\"main\":\"c4.jpg\"}},{\"cid\":\"c5\",\"name\":\"엄마\",\"image\":{\"main\":\"c5.jpg\"}},{\"cid\":\"c6\",\"name\":\"내레이션\",\"image\":{\"main\":\"c6.jpg\"}}]}";
                    //String scene = Story_single.get("scene").toString();
                    String Scene = "{\"scene\":[{\"sid\":\"s001\",\"image\":{\"main\":\"s001.jpg\",\"preview\":\"s001.jpg\"},\"scripts\":[{\"scid\":\"s001c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood\"}]},{\"sid\":\"s002\",\"image\":{\"main\":\"s002.jpg\",\"preview\":\"s002.jpg\"},\"scripts\":[{\"scid\":\"s002c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood lived in the red roof house.\"},{\"scid\":\"s002c002\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red lived with Mom there.\"},{\"scid\":\"s002c003\",\"cid\":\"c5\",\"audio\":true,\"script\":\"Take Jam and bread to Grandma.\"},{\"scid\":\"s002c004\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c005\",\"cid\":\"c5\",\"audio\":true,\"script\":\"And milk, too?\"},{\"scid\":\"s002c006\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c007\",\"cid\":\"c6\",\"audio\":true,\"script\":\"The wolf lived in the woods.\"},{\"scid\":\"s002c008\",\"cid\":\"c5\",\"audio\":true,\"script\":\"The wolf is big and bad. It can eat you.\"},{\"scid\":\"s002c009\",\"cid\":\"c1\",\"audio\":true,\"script\":\"I'm not scared. I can take food to Grandma.\"}]}]}";

                    Log.e("bookid", bookid);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("idx",idx);
                    map.put("bookid", bookid);
                    map.put("title", title);
                    map.put("cover", cover);
                    map.put("cover_small", cover_small);
                    map.put("download", download);
                    map.put("description", description);
                    //map.put("verson", version);
                    //map.put("displayversion", displayversion);
                    map.put("MaxPlayer", "5");
                    //map.put("character", character);
                    //map.put("scene", scene);

                    try {
                        JSONObject scene_ = new JSONObject(Scene);
                        JSONArray scene = scene_.getJSONArray("scene");
                        JSONObject character_ = new JSONObject(Character);
                        JSONArray character = character_.getJSONArray("character");

                        //캐릭터 파싱
                        for (int k = 0; k < character.length(); k++) {
                            //파싱
                            JSONObject sceneObj = character.getJSONObject(k);
                            System.out.println(sceneObj);
                            String cid = sceneObj.getString("cid");
                            String name = sceneObj.getString("name");
                            String image = sceneObj.getJSONObject("image").getString("main");

                            HashMap<String, String> character_map = new HashMap<>();
                            character_map.put("cid", cid);
                            character_map.put("name", name);
                            character_map.put("image", image);

                            character_list.add(character_map);
                        }

                        //스크립트 파싱
                        for (int k = 0; k < scene.length(); k++) {
                            //파싱
                            JSONObject characterObj = scene.getJSONObject(k);
                            String sid = characterObj.getString("sid");
                            String image_main = characterObj.getJSONObject("image").getString("main");
                            String image_preview = characterObj.getJSONObject("image").getString("preview");

                            HashMap<String, String> scene_map = new HashMap<>();
                            scene_map.put("sid", sid);
                            scene_map.put("image_main", image_main);
                            scene_map.put("image_preview", image_preview);
                            scene_list.add(scene_map);

                            JSONArray scripts = (JSONArray) characterObj.get("scripts");

                            HashMap<Integer, HashMap> scene_map_main = new HashMap<>();
                            for (int j = 0; j < scripts.length(); j++) {
                                JSONObject scriptObj = scripts.getJSONObject(j);
                                String scid = scriptObj.getString("scid");
                                String cid = scriptObj.getString("cid");
                                String audio = scriptObj.getString("audio");
                                String script = scriptObj.getString("script");
                                String script_length = Integer.toString(scripts.length());

                                Log.e("스크립트", script);
                                HashMap<String, String> script_map = new HashMap<>();
                                script_map.put("scid", scid);
                                script_map.put("cid", cid);
                                script_map.put("audio", audio);
                                script_map.put("script", script);
                                script_map.put("script_length", script_length);
                                scene_map_main.put(j, script_map);
                            }
                            script_list.add(scene_map_main);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    story_list.add(map);
                    load_chk = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void exceptionOccured(Exception e) {
        }

        @Override
        public void cancelled() {
        }
    };
}
