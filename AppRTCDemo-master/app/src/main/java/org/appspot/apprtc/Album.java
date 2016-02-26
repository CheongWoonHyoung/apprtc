package org.appspot.apprtc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Donghyun on 2016. 2. 8..
 */
public class Album extends Activity {
    private ArrayList<HashMap<String,String>> story_list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> album_arraylist = new ArrayList<>();
    private ArrayList<HashMap<String, String>> scene_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> character_list = new ArrayList<>();
    private ArrayList<HashMap<Integer, HashMap>> script_list = new ArrayList<>();

    private GridView gridView;
    private boolean load_chk = false;
    ListAdapter adapter_story;
    private final String URL = "http://blay.eerssoft.co.kr/books/list/";

    public SharedPreferences album_list;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.album_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        album_list = this.getSharedPreferences(getPackageName(),
                Activity.MODE_PRIVATE);

        new SHJSONParser().setCallback(callback).execute(URL);

        final Button btn_home = (Button) findViewById(R.id.btn_home2);

        btn_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    btn_home.setBackgroundResource(R.drawable.btn_home_push2x);
                } else if (action == MotionEvent.ACTION_UP) {
                    btn_home.setBackgroundResource(R.drawable.btn_home_normal2x);
                    finish();
                }
                return true;
            }
        });
    }

    //리스트받아오기
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


                    cover = cover.replace("/static/","");
                    cover = cover.replace(".jpg","");

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

                    try {
                        JSONObject scene_ = new JSONObject(Scene);
                        JSONArray scene = scene_.getJSONArray("scene");
                        JSONObject character_ = new JSONObject(Character);
                        JSONArray character = character_.getJSONArray("character");

                        //캐릭터 파싱
                        for (int k = 0; k < character.length(); k++) {
                            //파싱
                            JSONObject sceneObj = character.getJSONObject(k);
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

            Integer album_length = album_list.getInt("album_length", -1);

            for(int adapter_loop = 0; adapter_loop<album_length; adapter_loop++){
                try{
                    HashMap<String, String> album_map = new HashMap<>();
                    Integer idx_ = Integer.parseInt(album_list.getString(album_list.getString(Integer.toString(adapter_loop), ""),"")) - 1;
                    String cover_id = story_list.get(idx_).get("cover");
                    album_map.put("bookid", story_list.get(idx_).get("bookid"));
                    album_map.put("cover", cover_id);
                    album_arraylist.add(album_map);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            adapter_story = new SimpleAdapter(Album.this, album_arraylist, R.layout.item_albumlist, new String[]{"bookid"}, new int[]{R.id.tv_story_name_play}){
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Integer album_length = album_list.getInt("album_length", -1);
                    for(int adapter_loop = 0; adapter_loop<album_length; adapter_loop++){
                        try{
                            Integer idx_ = Integer.parseInt(album_list.getString(album_list.getString(Integer.toString(adapter_loop), ""),"")) - 1;
                            String cover_id = story_list.get(idx_).get("cover");
                            int cover= getResources().getIdentifier(cover_id, "drawable", getPackageName());
                            ImageView iv_cover = (ImageView)findViewById(R.id.iv_album_cover);
                            iv_cover.setImageDrawable(getResources().getDrawable(cover));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    return view;
                }
            };
            setAlbum_list();
        }
        @Override
        public void exceptionOccured(Exception e) {
        }
        @Override
        public void cancelled() {
        }
    };

    private void setAlbum_list(){
        gridView = (GridView)findViewById(R.id.gv_playlist);
        if(gridView != null) {
                gridView.setAdapter(adapter_story);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(load_chk == true) {
                        Intent intent = new Intent(Album.this, Album_Play.class);
                        Integer idx_ = Integer.parseInt(album_list.getString(album_list.getString(Integer.toString(position), ""), "")) - 1;

                        intent.putExtra("title", story_list.get(idx_).get("title"));
                        intent.putExtra("description", story_list.get(idx_).get("description"));
                        intent.putExtra("bookid", story_list.get(idx_).get("bookid"));
                        intent.putExtra("MaxPlayer", story_list.get(idx_).get("MaxPlayer"));
                        intent.putExtra("download", story_list.get(idx_).get("download"));
                        intent.putExtra("character", character_list);
                        intent.putExtra("story", story_list);
                        intent.putExtra("scene_list", scene_list);
                        intent.putExtra("script_list", script_list);
                        startActivity(intent);
                    }
                }
            });

        }
    }
}
