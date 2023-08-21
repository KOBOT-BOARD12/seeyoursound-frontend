package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
public class ReservationListActivity extends Activity {
    private TextView responseTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Button sendRequestButton = findViewById(R.id.sendRequestButton);
        TextView responseTextView = findViewById(R.id.responseTextView);
        Button backButton = findViewById(R.id.backButton) ;
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestToServer(responseTextView);
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationListActivity.this, SendMessageActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }


    private void sendRequestToServer(final TextView responseTextView) {
        OkHttpClient client = new OkHttpClient();
        String serverUrl = "http://10.30.118.74:8000/return_keyword"; // FastAPI 서버의 URL을 입력하세요
        String id = new String() ;
        id = "20191621" ;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + id + "\"}" ;
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseTextView.setText("Request failed");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseTextView.setText("Request failed");
                        }
                    });
                } else {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray keywordsArray = jsonObject.getJSONArray("keywords");

                        StringBuilder keywordsText = new StringBuilder("Keywords:\n");

                        for (int i = 0; i < keywordsArray.length(); i++) {
                            keywordsText.append(keywordsArray.getString(i)).append("\n");
                        }

                        final String resultText = keywordsText.toString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                responseTextView.setText(resultText);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                responseTextView.setText("Response parsing error");
                            }
                        });
                    }
                }
            }
        });


    }
}