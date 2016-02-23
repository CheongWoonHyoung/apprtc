package org.appspot.apprtc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        final String title = String.valueOf(getIntent().getExtras().getString("title"));
        final String description = String.valueOf(getIntent().getExtras().getString("description"));
        final String bookid = String.valueOf(getIntent().getExtras().getString("bookid"));
        final String MaxPlayer = String.valueOf(getIntent().getExtras().getString("MaxPlayer"));
        final String download = String.valueOf(getIntent().getExtras().getString("download"));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getExtras() == null)
            finish();

        setContentView(R.layout.story_single);
        TextView tv_single_name = (TextView) findViewById(R.id.tv_single_name);
        tv_single_name.setText(title);
        TextView tv_single_description = (TextView) findViewById(R.id.tv_single_description);
        tv_single_description.setText(description);
        int title_single= getResources().getIdentifier("org.appspot.apprtc:drawable/main_" + bookid, null, null);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), title_single);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        ImageView circularImageView = (ImageView)findViewById(R.id.iv_main);
        circularImageView.setImageBitmap(circularBitmap);


        Button tv_start = (Button) findViewById(R.id.tv_start);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Story_Single.this,Call_List.class);
                intent.putExtra("MaxPlayer", MaxPlayer);
                intent.putExtra("download", download);
                intent.putExtra("bookid", bookid);
                startActivity(intent);
            }
        });

        Button tv_back = (Button) findViewById(R.id.tv_back);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
