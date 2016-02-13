package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.appspot.apprtc.ConnectActivity;
import org.appspot.apprtc.CallActivity;
import org.appspot.apprtc.util.DataChannel;
import org.appspot.apprtc.Story_Connect;
/**
 * Created by Donghyun on 2016. 2. 8..
 */
public class Story extends MainActivity {

    private ArrayList<HashMap<String,String>> story_list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> scene_list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> character_list = new ArrayList<>();
    private ArrayList<HashMap<Integer,HashMap>> script_list = new ArrayList<>();
    private GridView gridView;
    private ImageView iv_cover;
    ListAdapter adapter_story;
    private String URL = "http://blay.eerssoft.co.kr/books/list/";

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.story_main);
        adapter_story = new SimpleAdapter(Story.this, story_list, R.layout.item_storylist, new String[]{"bookid","cover_small"}, new int[]{R.id.tv_story_name, R.id.iv_cover});

        new SHJSONParser().setCallback(callback).execute(URL);

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
                    System.out.println("error");
                    return;
                }

                story_list.clear();
                JSONArray Story_arr = (JSONArray) json.get("list");
                for(int i = 0; i< Story_arr.length(); i++) {
                    JSONObject Story_single = Story_arr.getJSONObject(i);

                    Integer idx;
                    String bookid = Story_single.get("bookid").toString();
                    String title = Story_single.get("title").toString();
                    String cover = Story_single.get("image").toString();
                    String cover_small = Story_single.get("image_small").toString();
                    int cover_s= getResources().getIdentifier("org.appspot.apprtc:drawable/main_"+cover_small,null,null);
                    ImageView iv_cover = (ImageView)findViewById(R.id.iv_cover);
                    //iv_cover.setImageDrawable(getResources().getDrawable(cover_s));
                    String download = Story_single.get("download").toString();
                    String description = Story_single.get("description").toString();
                    String version;
                    String displayversion;
                    //String maxPlayer = Story_single.get("maxPlayer").toString();
                    String MaxPlayer = "{\"maxPlayer\":\"6\"}";
                    //String character = Story_single.get("character").toString();
                    String Character = "{\"character\":[{\"cid\":\"c1\",\"name\":\"빨간 모자\",\"image\":{\"main\":\"c.jpg\"}},{\"cid\":\"c2\",\"name\":\"늑대\",\"image\":{\"main\":\"c2.jpg\"}},{\"cid\":\"c3\",\"name\":\"할머니\",\"image\":{\"main\":\"c3.jpg\"}},{\"cid\":\"c4\",\"name\":\"사냥꾼\",\"image\":{\"main\":\"c4.jpg\"}},{\"cid\":\"c5\",\"name\":\"엄마\",\"image\":{\"main\":\"c5.jpg\"}},{\"cid\":\"c6\",\"name\":\"내레이션\",\"image\":{\"main\":\"c6.jpg\"}}]}";
                    //String scene = Story_single.get("scene").toString();
                    String Scene = "{\"scene\":[{\"sid\":\"s001\",\"image\":{\"main\":\"s001.jpg\",\"preview\":\"s001.jpg\"},\"scripts\":[{\"scid\":\"s001c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood\"}]},{\"sid\":\"s002\",\"image\":{\"main\":\"s002.jpg\",\"preview\":\"s002.jpg\"},\"scripts\":[{\"scid\":\"s002c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood lived in the red roof house.\"},{\"scid\":\"s002c002\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red lived with Mom there.\"},{\"scid\":\"s002c003\",\"cid\":\"c5\",\"audio\":true,\"script\":\"Take Jam and bread to Grandma.\"},{\"scid\":\"s002c004\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c005\",\"cid\":\"c5\",\"audio\":true,\"script\":\"And milk, too?\"},{\"scid\":\"s002c006\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c007\",\"cid\":\"c6\",\"audio\":true,\"script\":\"The wolf lived in the woods.\"},{\"scid\":\"s002c008\",\"cid\":\"c5\",\"audio\":true,\"script\":\"The wolf is big and bad. It can eat you.\"},{\"scid\":\"s002c009\",\"cid\":\"c1\",\"audio\":true,\"script\":\"I'm not scared. I can take food to Grandma.\"}]}]}";

                    Log.e("bookid", bookid);

                    HashMap<String, String> map = new HashMap<>();
                    //map.put("idx",idx);
                    map.put("bookid", bookid);
                    map.put("title", title);
                    map.put("cover", cover);
                    map.put("cover_small", cover_small);
                    map.put("download", download);
                    map.put("description", description);
                    //map.put("verson", version);
                    //map.put("displayversion", displayversion);
                    map.put("MaxPlayer", "6");
                    //map.put("character", character);
                    //map.put("scene", scene);

                    try {
                        JSONObject scene_ = new JSONObject(Scene);
                        JSONArray scene = scene_.getJSONArray("scene");
                        JSONObject character_ = new JSONObject(Character);
                        JSONArray character = character_.getJSONArray("character");

                        //캐릭터 파싱
                        for (int k = 0; k < character.length(); k++){
                            //파싱
                            JSONObject sceneObj = character.getJSONObject(k);
                            System.out.println(sceneObj);
                            String cid = sceneObj.getString("cid");
                            String name = sceneObj.getString("name");
                            String image = sceneObj.getJSONObject("image").getString("main");
                            System.out.println("here1");

                            HashMap<String, String> character_map = new HashMap<>();
                            character_map.put("cid", cid);
                            character_map.put("name", name);
                            character_map.put("image", image);

                            character_list.add(character_map);
                        }

                        //스크립트 파싱
                        for(int k = 0; k < scene.length(); k++){
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
                            for (int j = 0; j < scripts.length(); j++){
                                JSONObject scriptObj = scripts.getJSONObject(j);
                                String scid = scriptObj.getString("scid");
                                String cid = scriptObj.getString("cid");
                                String audio = scriptObj.getString("audio");
                                String script = scriptObj.getString("script");

                                HashMap<String, String> script_map = new HashMap<>();
                                script_map.put("scid", scid);
                                script_map.put("cid", cid);
                                script_map.put("audio", audio);
                                script_map.put("script", script);
                                System.out.println("here3");
                                scene_map_main.put(i,script_map);
                            }

                            script_list.add(scene_map_main);
                        }
                    }catch (Exception ex) {
                        System.out.println(ex);
                    }
                    story_list.add(map);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            setStory_list();
        }
        @Override
        public void exceptionOccured(Exception e) {
        }
        @Override
        public void cancelled() {
        }
    };

    private void setStory_list(){
        gridView = (GridView)findViewById(R.id.gv_storylist);
        if(gridView != null) {

            //final String cover_small_2 = String.valueOf(getIntent().getExtras().getString("image_small"));
            //iv_cover.setImageDrawable(getResources().getDrawable(cover_s));
            gridView.setAdapter(adapter_story);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Story.this, Story_Single .class);
                    intent.putExtra("title", story_list.get(position).get("title"));
                    intent.putExtra("description", story_list.get(position).get("description"));
                    intent.putExtra("bookid", story_list.get(position).get("bookid"));
                    intent.putExtra("script", script_list.toString());
                    intent.putExtra("character", character_list.toString());
                    intent.putExtra("MaxPlayer", story_list.get(position).get("MaxPlayer"));
                    startActivity(intent);
                }
            });
        } else{
            Log.e("error", "gridView is null");
        }
    }
}
