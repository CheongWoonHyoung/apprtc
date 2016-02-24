package org.appspot.apprtc;

import android.content.Intent;
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

    private GridView gridView;
    ListAdapter adapter_story;
    private String URL = "http://blay.eerssoft.co.kr/books/list/";

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.story_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new SHJSONParser().setCallback(callback).execute(URL);

        final Button btn_home = (Button) findViewById(R.id.btn_home);
        btn_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    btn_home.setBackgroundResource(R.drawable.btn_home_push2x);
                }else if(action == MotionEvent.ACTION_UP){
                    btn_home.setBackgroundResource(R.drawable.btn_home_normal2x);
                    finish();
                }
                return true;
            }
        });
    }


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

                    String idx = Story_single.get("idx").toString();
                    String bookid = Story_single.get("bookid").toString();
                    String title = Story_single.get("title").toString();
                    String cover = Story_single.get("image").toString();
                    String cover_small = Story_single.get("image_small").toString();
                    String download = Story_single.get("download").toString();
                    String description = Story_single.get("description").toString();
                    //String version = Story_single.get("version").toString();
                    //String displayversion = Story_single.get("displayversion").toString();

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
                    //map.put("character", character);
                    //map.put("scene", scene);

                    story_list.add(map);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            adapter_story = new SimpleAdapter(Story.this, story_list, R.layout.item_storylist, new String[]{"bookid"}, new int[]{R.id.tv_story_name}){
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    for(int adapter_loop = 0; adapter_loop<story_list.size(); adapter_loop++){
                        try{
                            String cover_Id = story_list.get(adapter_loop).get("cover");
                            Log.e("cover", cover_Id);
                            int cover= getResources().getIdentifier("main_" + cover_Id, "drawable", getPackageName());
                            ImageView iv_cover = (ImageView)findViewById(R.id.iv_cover);
                            iv_cover.setImageDrawable(getResources().getDrawable(cover));
                        }catch(Exception ex){
                            System.out.println(ex);
                        }
                    }
                    return view;
                }
            };
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
            gridView.setAdapter(adapter_story);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Story.this, Story_Single .class);
                    intent.putExtra("idx", story_list.get(position).get("idx"));
                    intent.putExtra("title", story_list.get(position).get("title"));
                    intent.putExtra("description", story_list.get(position).get("description"));
                    intent.putExtra("bookid", story_list.get(position).get("bookid"));
                    intent.putExtra("MaxPlayer", story_list.get(position).get("MaxPlayer"));
                    intent.putExtra("download", story_list.get(position).get("download"));
                    startActivity(intent);
                }
            });
        }
    }
}
