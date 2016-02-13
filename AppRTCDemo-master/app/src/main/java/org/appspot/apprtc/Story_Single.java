package org.appspot.apprtc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Donghyun on 2016. 2. 10..
 */
public class Story_Single extends MainActivity {

    //스토리에대한설명및별점보여주기
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        final String title = String.valueOf(getIntent().getExtras().getString("title"));
        final String description = String.valueOf(getIntent().getExtras().getString("description"));
        final String bookid = String.valueOf(getIntent().getExtras().getString("bookid"));
        final String character = String.valueOf(getIntent().getExtras().getString("character"));
        final String script = String.valueOf(getIntent().getExtras().getString("script"));
        final String MaxPlayer = String.valueOf(getIntent().getExtras().getString("MaxPlayer"));

        if (getIntent().getExtras() == null)
            finish();

        setContentView(R.layout.story_single);
        TextView tv_single_name = (TextView) findViewById(R.id.tv_single_name);
        tv_single_name.setText(title);
        TextView tv_single_description = (TextView) findViewById(R.id.tv_single_description);
        tv_single_description.setText(description);
        ImageView iv_main = (ImageView) findViewById(R.id.iv_main);
        int title_single= getResources().getIdentifier("org.appspot.apprtc:drawable/main_"+bookid,null,null);
        //iv_main.setImageDrawable(getResources().getDrawable(title_single));

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),title_single);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        ImageView circularImageView = (ImageView)findViewById(R.id.iv_main);
        circularImageView.setImageBitmap(circularBitmap);


        Button tv_start = (Button) findViewById(R.id.tv_start);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Story_Single.this,Call_List.class);
                intent.putExtra("character", character);
                intent.putExtra("script", script);
                intent.putExtra("MaxPlayer", MaxPlayer);
                startActivity(intent);
            }
        });
    }

    //시작하기
    //인텐트
    //친구추가목록 인텐트
}
