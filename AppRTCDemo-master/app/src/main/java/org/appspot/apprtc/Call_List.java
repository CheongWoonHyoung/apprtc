package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by cheongwh on 2016. 2. 12..
 */
public class Call_List extends MainActivity {

    private ArrayList<HashMap<String, String>> story_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> scene_list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> character_list = new ArrayList<>();
    private ArrayList<HashMap<Integer, HashMap>> script_list = new ArrayList<>();
    private boolean load_chk = false;

    private String character = "";
    private String script = "";

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.call_list);

        final String MaxPlayer = String.valueOf(getIntent().getExtras().getString("MaxPlayer"));
        String download = String.valueOf(getIntent().getExtras().getString("download"));
        // 콜 리스트 뿌려주기 => 설정에서 등록한 사람만 뿌려주면 댐 ㅇㅇ
        // 친구 선택

        download = "http://blay.eerssoft.co.kr/books/list/";
        new SHJSONParser().setCallback(callback).execute(download);

        Button btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView
                //다운로드시작
                if (load_chk == true) {
                    //원래는 랜덤생성임, 테스트용 임의 생성
                    //String roomId = "357357352";
                    String roomId = Integer.toString((new Random()).nextInt(100000000));
                    Intent intent = new Intent(Call_List.this, ConnectActivity.class);
                    intent.putExtra("character", character_list);
                    intent.putExtra("script", script_list);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("MaxPlayer", MaxPlayer);
                    intent.putExtra("story", story_list);
                    intent.putExtra("scene_list", scene_list);
                    //친구도 room Id 넘겨주기
                    //친구한테 푸시 메시지
                    startActivity(intent);
                } else {
                    Log.e("Loading", "Loading");
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
                    System.out.println("error");
                    return;
                }

                JSONArray Story_arr = (JSONArray) json.get("list");
                for (int i = 0; i < Story_arr.length(); i++) {
                    JSONObject Story_single = Story_arr.getJSONObject(i);

                    Integer idx;
                    String bookid = Story_single.get("bookid").toString();
                    String title = Story_single.get("title").toString();
                    String cover = Story_single.get("image").toString();
                    String cover_small = Story_single.get("image_small").toString();
                    int cover_s = getResources().getIdentifier("org.appspot.apprtc:drawable/main_" + cover_small, null, null);
                    ImageView iv_cover = (ImageView) findViewById(R.id.iv_cover);
                    //iv_cover.setImageDrawable(getResources().getDrawable(cover_s));
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
                    //map.put("idx",idx);
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

                                HashMap<String, String> script_map = new HashMap<>();
                                script_map.put("scid", scid);
                                script_map.put("cid", cid);
                                script_map.put("audio", audio);
                                script_map.put("script", script);
                                script_map.put("script_length", script_length);
                                scene_map_main.put(i, script_map);
                            }

                            script_list.add(scene_map_main);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    story_list.add(map);
                    load_chk = true;
                    setStory_list();
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

    private void setStory_list() {

    }

}
