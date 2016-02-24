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
public class Album extends MainActivity {
    private ArrayList<HashMap<String,String>> story_list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> album_arraylist = new ArrayList<>();


    private GridView gridView;
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


    //리스트받아오기->그리드뷰
    private SHJSONParserCallback callback = new SHJSONParserCallback() {
        @Override
        public void onResult(JSONObject json, int parserTag) {
            try {
                if (json == null || (Integer) json.get("result") != 0) {
                    //TODO: error
                    return;
                }
                story_list.clear();
                JSONArray Story_arr = (JSONArray) json.get("list");
                for(int i = 0; i< Story_arr.length(); i++) {
                    JSONObject Story_single = Story_arr.getJSONObject(i);

                    //String idx = Story_single.get("idx").toString();
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

                    HashMap<String, String> map = new HashMap<>();
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

            Integer album_length = album_list.getInt("album_length", -1);

            for(int adapter_loop = 0; adapter_loop<album_length; adapter_loop++){
                try{
                    HashMap<String, String> album_map = new HashMap<>();
                    Integer idx_ = Integer.parseInt(album_list.getString(album_list.getString(Integer.toString(adapter_loop + 1), ""),""));
                    String cover_id = story_list.get(idx_).get("cover");
                    album_map.put("bookid", story_list.get(idx_).get("bookid"));
                    album_map.put("cover", cover_id);
                    album_arraylist.add(album_map);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            System.out.println(album_arraylist);
            adapter_story = new SimpleAdapter(Album.this, album_arraylist, R.layout.item_albumlist, new String[]{"bookid"}, new int[]{R.id.tv_story_name_play}){
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Integer album_length = album_list.getInt("album_length", -1);
                    for(int adapter_loop = 0; adapter_loop<album_length; adapter_loop++){
                        try{
                            Integer idx_ = Integer.parseInt(album_list.getString(album_list.getString(Integer.toString(adapter_loop + 1), ""),""));
                            String cover_id = story_list.get(idx_).get("cover");
                            int cover= getResources().getIdentifier("main_" + cover_id, "drawable", getPackageName());
                            ImageView iv_cover = (ImageView)findViewById(R.id.iv_cover_play);
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
                    Intent intent = new Intent(Album.this, Album_Play.class);
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
