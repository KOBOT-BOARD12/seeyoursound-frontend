package com.example.myapplication;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;


import android.os.Handler;
import android.content.Intent;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.hardware.SensorManager;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


public class activity_main extends Activity {

    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 25;
    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private WebSocket webSocket;
    ImageView logoImageView;

    private OkHttpClient client;
    TextView statusTextView;
    private Request request;
    private WebSocketListener listener;

    private String prediction_class;
    private String keyword;
    private String direction;
    ImageView eastImageView;
    ImageView westImageView;
    ImageView southImageView;
    ImageView northImageView;
    boolean sound0;
    boolean sound1;
    boolean sound2;
    boolean sound3;
    int count = 1;
    private boolean isAnimating = false;
    Button recordingButton;

    String[] url;
    String serverurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = getResources().getStringArray(R.array.url);
        serverurl = url[0];
        logoImageView = findViewById(R.id.logo);
        eastImageView = findViewById(R.id.east);
        westImageView = findViewById(R.id.west);
        southImageView = findViewById(R.id.south);
        northImageView = findViewById(R.id.north);
        statusTextView = findViewById(R.id.statusTextView);

        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        Animation fadein_Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeout_Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation firstout_Animation = AnimationUtils.loadAnimation(this, R.anim.first_out);
        Animation east_rotate = AnimationUtils.loadAnimation(this, R.anim.east_rotate);
        Animation west_rotate = AnimationUtils.loadAnimation(this, R.anim.west_rotate);
        Animation north_rotate = AnimationUtils.loadAnimation(this, R.anim.north_rotate);
        Animation south_rotate = AnimationUtils.loadAnimation(this, R.anim.south_rotate);


        if (count == 1) {
            eastImageView.setAnimation(firstout_Animation);
            westImageView.setAnimation(firstout_Animation);
            southImageView.setAnimation(firstout_Animation);
            northImageView.setAnimation(firstout_Animation);
            count++;
        }

        logoImageView.startAnimation(rotationAnimation);






        recordingButton = findViewById(R.id.recordingButton);
        Button reservationButton = findViewById(R.id.reservationButton);
        Button noticeButton = findViewById(R.id.noticeButton);
        Button helpButton = findViewById(R.id.helpButton);


        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_main.this, activity_help.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        recordingButton.setOnClickListener(v -> {



            String serverUrl = serverurl ;


            client = new OkHttpClient();
            request = new Request.Builder()
                    .url(serverUrl)
                    .build();


            listener = new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                    runOnUiThread(() -> updateStatusText("음성 인식이 시작되었습니다."));
                    statusTextView.startAnimation(fadeout_Animation);

                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {

                    runOnUiThread(() -> {
                        try {

                            JSONObject jsonObject = new JSONObject(text);
                            prediction_class = jsonObject.optString("prediction_class", "unknown");
                            keyword = jsonObject.optString("keyword", "unknown");
                            direction = jsonObject.optString("direction", "unknown");


                            if (prediction_class.equals("0")) {
                                prediction_class = "자동차 경적 소리";
                            } else if (prediction_class.equals("1")) {
                                prediction_class = "개 짖는 소리";
                            } else if (prediction_class.equals("2")) {
                                prediction_class = "사이렌 소리";
                            } else if (prediction_class.equals("3")) {
                                prediction_class = "비명 소리";
                            } else if (prediction_class.equals("4")) {
                                prediction_class = "말 소리";
                            }


                            if (direction.equals("동쪽")) {
                                animateWithFadeInOut(eastImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(east_rotate);


                                if (keyword.equals("unknown")) {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 " + prediction_class + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                }



                            } else if (direction.equals("서쪽")) {
                                animateWithFadeInOut(westImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(west_rotate);


                                if (keyword.equals("unknown")) {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 " + prediction_class + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                }

                            } else if (direction.equals("남쪽")) {
                                animateWithFadeInOut(southImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(south_rotate);


                                if (keyword.equals("unknown")) {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 " + prediction_class + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                }
                            } else if (direction.equals("북쪽")) {
                                animateWithFadeInOut(northImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(north_rotate);

                                if (keyword.equals("unknown")) {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 " + prediction_class + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + "에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                    statusTextView.startAnimation(fadeout_Animation);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });
                }

                private void animateWithFadeInOut(ImageView imageView, Animation fadeinAnimation, Animation fadeoutAnimation) {
                    if (!isAnimating) {
                        isAnimating = true;

                        imageView.startAnimation(fadeinAnimation);


                        new Handler().postDelayed(() -> {
                            imageView.startAnimation(fadeoutAnimation);

                            isAnimating = false;
                        }, 1000);
                    }
                }




                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    runOnUiThread(() -> updateStatusText("음성 탐색이 중단되었습니다."));
                    statusTextView.startAnimation(fadeout_Animation);
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                    runOnUiThread(() -> updateStatusText("연결에 실패했습니다."));
                    statusTextView.startAnimation(fadeout_Animation);
                }




            };


            webSocket = client.newWebSocket(request, listener);

            if (!isRecording) {
                startRecording();
                isRecording = true;
                recordingButton.setBackgroundResource(R.drawable.during_mic);

            } else {
                // Stop 버튼은 녹음 중에만 활성화되도록 설정
                recordingButton.setEnabled(false);
                stopRecording();
                rotationAnimation.cancel();
                float currentRotation = logoImageView.getRotation();
                logoImageView.setRotation(currentRotation);
                logoImageView.clearAnimation();
                recordingButton.setBackgroundResource(R.drawable.mic); // 추가된 부분

            }

        });


        noticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_main.this, activity_filter.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });


        reservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_main.this, activity_keyword.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

    }


    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();


        if (isRecording) {
            stopRecording();
        }
    }




    private void startRecording() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }
        String serverurl = url[1];
        OkHttpClient client = new OkHttpClient();
        String serverUrl = serverurl ;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + uid + "\"}";
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
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } else {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);


                        sound0 = jsonObject.optBoolean("0");
                        sound1 = jsonObject.optBoolean("1");
                        sound2 = jsonObject.optBoolean("2");
                        sound3 = jsonObject.optBoolean("3");


                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }
            }
        });

        // 오디오 녹음 및 전송 부분
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        byte[] buffer = new byte[BUFFER_SIZE]; // buffer 생성

        isRecording = true;
        audioRecord.startRecording();

        new Thread(() -> {
            while (isRecording) {
                int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    // 왼쪽 오른쪽으로 음성을 나눔
                    byte[] leftChannelData = new byte[bytesRead / 2];
                    byte[] rightChannelData = new byte[bytesRead / 2];

                    for (int i = 0, j = 0; i < bytesRead; i += 4, j += 2) {

                        leftChannelData[j] = buffer[i];
                        leftChannelData[j + 1] = buffer[i + 1];
                        rightChannelData[j] = buffer[i + 2];
                        rightChannelData[j + 1] = buffer[i + 3];

                    }





                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("top_channel", Base64.encodeToString(leftChannelData, Base64.DEFAULT));
                        jsonObject.put("bottom_channel", Base64.encodeToString(rightChannelData, Base64.DEFAULT));
                        jsonObject.put("uid", uid);
                        jsonObject.put("class_0", sound0);
                        jsonObject.put("class_1", sound1);
                        jsonObject.put("class_2", sound2);
                        jsonObject.put("class_3", sound3);

                        // JSON 객체를 문자열로 변환하고 웹소켓을 통해 서버로 전송
                        webSocket.send(jsonObject.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
            audioRecord.stop();
            audioRecord.release();
        }).start();
    }

    private void stopRecording() {

        isRecording = false;
        if (webSocket != null) {
            webSocket.close(1000, "Recording stopped");
        }

        // 녹음 중지 및 관련 작업 수행
        runOnUiThread(() -> {
            recordingButton.setEnabled(true); // 버튼 다시 활성화
        });
    }


    private void updateStatusText(String message) {
        statusTextView.setText(message);
    }
}