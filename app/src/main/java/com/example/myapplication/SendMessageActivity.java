package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;


public class SendMessageActivity extends Activity  {
    private EditText messageEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);
        Button reservationButton = findViewById(R.id.reservationButton);
        Button backButton = findViewById(R.id.backButton) ;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (message.isEmpty()) {
                    // 텍스트 창이 비어있는 경우
                    showEmptyMessageDialog();
                } else {
                    sendRequestToServer(message);
                }


            }
        });



        reservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessageActivity.this, ReservationListActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessageActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });







    }


    private void sendRequestToServer(String message) {
        String id = new String() ;
        id = "20191621" ;
        OkHttpClient client = new OkHttpClient();
        String serverUrl = "http://10.30.113.200:8000/register_keyword"; // FastAPI 서버의 URL을 입력하세요

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + id + "\", \"keyword\": \"" + message + "\" }";
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyString);

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateStatusText("Success");// 응답을 처리하거나 UI 업데이트를 수행할 수 있습니다.
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateStatusText("Failed"); // "Failed" 텍스트로 업데이트
                        }
                    });
                    // 서버 응답이 실패한 경우에 대한 처리
                }
            }
        });
    }







    private void updateStatusText(String message) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText(message);
    }



    private void showEmptyMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder
                .setMessage("예약어를 입력해주세요!")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 확인 버튼을 눌렀을 때의 동작
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
