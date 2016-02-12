package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

/**
 * Created by Donghyun on 2016. 2. 8..
 */
public class Story extends MainActivity {

    private ArrayList<HashMap<String,String>> story_list = new ArrayList<>();
    GridView gridView;
    //String URL = "http://blay.eerssoft.co.kr/books/list/";
    //저기서 그 뭐냐 book.jaon 을 불러와야 함
    String URL = "/data/local/tmp/com.example.donghyunna.myapplication/";


    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.story_main);
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
                System.out.println("here");
                JSONArray Story_arr = (JSONArray) json.get("list");
                System.out.println(Story_arr);
                for(int i = 0; i< Story_arr.length(); i++) {
                    JSONObject Story_single = Story_arr.getJSONObject(i);

                    Integer idx;
                    String bookid = Story_single.get("bookid").toString();
                    String title = Story_single.get("title").toString();
                    String cover = Story_single.get("image").toString();
                    String cover_small = Story_single.get("image_small").toString();
                    String download = Story_single.get("download").toString();
                    String description = Story_single.get("description").toString();
                    String version;
                    String displayversion;
                    //String maxPlayer = Story_single.get("maxPlayer").toString();
                    String maxPlayer = "{\"maxPlayer\":\"6\"}";
                    //String character = Story_single.get("character").toString();
                    String character = "{\"character\":[{\"cid\":\"c1\",\"name\":\"빨간 모자\",\"image\":{\"main\":\"c.jpg\"}},{\"cid\":\"c2\",\"name\":\"늑대\",\"image\":{\"main\":\"c2.jpg\"}},{\"cid\":\"c3\",\"name\":\"할머니\",\"image\":{\"main\":\"c3.jpg\"}},{\"cid\":\"c4\",\"name\":\"사냥꾼\",\"image\":{\"main\":\"c4.jpg\"}},{\"cid\":\"c5\",\"name\":\"엄마\",\"image\":{\"main\":\"c5.jpg\"}},{\"cid\":\"c6\",\"name\":\"내레이션\",\"image\":{\"main\":\"c6.jpg\"}}]}";
                    //String scene = Story_single.get("scene").toString();
                    String scene = "{\"scene\":[{\"sid\":\"s001\",\"image\":{\"main\":\"s001.jpg\",\"preview\":\"s001.jpg\"},\"scripts\":[{\"scid\":\"s001c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood\"}]},{\"sid\":\"s002\",\"image\":{\"main\":\"s002.jpg\",\"preview\":\"s002.jpg\"},\"scripts\":[{\"scid\":\"s002c001\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red Riding Hood lived in the red roof house.\"},{\"scid\":\"s002c002\",\"cid\":\"c6\",\"audio\":true,\"script\":\"Little Red lived with Mom there.\"},{\"scid\":\"s002c003\",\"cid\":\"c5\",\"audio\":true,\"script\":\"Take Jam and bread to Grandma.\"},{\"scid\":\"s002c004\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c005\",\"cid\":\"c5\",\"audio\":true,\"script\":\"And milk, too?\"},{\"scid\":\"s002c006\",\"cid\":\"c1\",\"audio\":true,\"script\":\"Yes, Mom\"},{\"scid\":\"s002c007\",\"cid\":\"c6\",\"audio\":true,\"script\":\"The wolf lived in the woods.\"},{\"scid\":\"s002c008\",\"cid\":\"c5\",\"audio\":true,\"script\":\"The wolf is big and bad. It can eat you.\"},{\"scid\":\"s002c009\",\"cid\":\"c1\",\"audio\":true,\"script\":\"I'm not scared. I can take food to Grandma.\"}]}]}";

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
                    map.put("maxPlayer", maxPlayer);
                    map.put("character", character);
                    map.put("scene", scene);

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
        ListAdapter adapter_story = new SimpleAdapter(Story.this, story_list, R.layout.item_storylist, new String[]{"bookid","cover_small"}, new int[]{R.id.tv_story_name, R.id.iv_cover});
        gridView = (GridView)findViewById(R.id.gv_storylist);
        if(gridView != null) {
            gridView.setAdapter(adapter_story);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setContentView(R.layout.story_single);
                    TextView tv_single_name = (TextView) findViewById(R.id.tv_single_name);
                    tv_single_name.setText(story_list.get(position).get("title"));
                    TextView tv_single_description = (TextView) findViewById(R.id.tv_single_description);
                    tv_single_description.setText(story_list.get(position).get("description"));
                    ImageView iv_main = (ImageView) findViewById(R.id.iv_main);
                    String bookid = story_list.get(position).get("bookid");
                    int title_single= getResources().getIdentifier("com.example.donghyunna.myapplication:drawable/main_"+bookid,null,null);
                    iv_main.setImageDrawable(getResources().getDrawable(title_single));

                    //CAll_LIST 진행
                    TextView tv_start = (TextView) findViewById(R.id.tv_start);
                    tv_start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setContentView(R.layout.call_list);
                            //친구검색
                            //초대 => 푸시

                            //인텐트로 appRTC 진행
                            //max player, character, scene => getExtras(jsonObj, jsonArray, jsonArray)
                        }
                    });

                    //Story_MAIN 뒤로가기 버튼
                    TextView tv_back = (TextView) findViewById(R.id.tv_back);
                    tv_back.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new SHJSONParser().setCallback(callback).execute(URL);
                            setContentView(R.layout.story_main);
                        }
                    });
                }
            });
        } else {
            Log.e("error", "gridView is null");
        }
    }
}
