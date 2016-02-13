package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by cheongwh on 2016. 2. 12..
 */
public class Call_List extends MainActivity{
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.call_list);

        final String character = String.valueOf(getIntent().getExtras().getString("character"));
        final String script = String.valueOf(getIntent().getExtras().getString("script"));
        final String MaxPlayer = String.valueOf(getIntent().getExtras().getString("MaxPlayer"));
        // 콜 리스트 뿌려주기 => 설정에서 등록한 사람만 뿌려주면 댐 ㅇㅇ
        // 친구 선택

        Button btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView
                //appRTC 실행

                //원래는 랜덤생성임, 테스트용 임의 생성
                String roomId = "357357354";
                //String roomId = Integer.toString((new Random()).nextInt(100000000));
                Intent intent = new Intent(Call_List.this, ConnectActivity.class);
                intent.putExtra("character", character);
                intent.putExtra("script", script);
                intent.putExtra("roomId", roomId);
                intent.putExtra("MaxPlayer", MaxPlayer);
                //친구도 room Id 넘겨주기
                //친구한테 푸시 메시지
                //여부 상관없이 일단 입장 ㄱㄱ
                startActivity(intent);

                //받은 값으로 뷰만 바꾸면 댐
            }
        });
    }
}
