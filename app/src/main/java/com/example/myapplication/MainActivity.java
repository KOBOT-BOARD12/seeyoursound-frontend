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
import android.os.Vibrator;
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

import android.app.NotificationChannel;
import android.app.NotificationManager;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;

    private Sensor sensor;
    private float[] gyroscopeValues = new float[3];
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)*25;

    private boolean isRecording = false;
    private boolean isColor = true;
    private AudioRecord audioRecord;
    private WebSocket webSocket;
    ImageView logoImageView;

    private OkHttpClient client;
    private Request request;
    private WebSocketListener listener;
    private float[] magneticFieldValues = new float[3];
    private String prediction_class ;
    private String keyword ;
    private String direction ;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;

    ImageView eastImageView ;
    ImageView westImageView ;
    ImageView southImageView ;
    ImageView northImageView ;
    private activity_notice noticeActivity;
    int count = 1;
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        logoImageView = findViewById(R.id.logo); // 이미지뷰 찾기
        eastImageView = findViewById(R.id.east);
        westImageView = findViewById(R.id.west);
        southImageView = findViewById(R.id.south);
        northImageView = findViewById(R.id.north);
        noticeActivity = new activity_notice();
        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        Animation fadein_Animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        Animation fadeout_Animation = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        Animation firstout_Animation = AnimationUtils.loadAnimation(this,R.anim.first_out);
        Animation east_rotate = AnimationUtils.loadAnimation(this,R.anim.east_rotate);
        Animation west_rotate = AnimationUtils.loadAnimation(this,R.anim.west_rotate);
        Animation north_rotate = AnimationUtils.loadAnimation(this,R.anim.north_rotate);
        Animation south_rotate = AnimationUtils.loadAnimation(this,R.anim.south_rotate);


        if (count == 1){
            eastImageView.setAnimation(firstout_Animation);
            westImageView.setAnimation(firstout_Animation);
            southImageView.setAnimation(firstout_Animation);
            northImageView.setAnimation(firstout_Animation);
            count ++;
        }

        logoImageView.startAnimation(rotationAnimation);


        // 자이로스코프 센서를 사용하기 위해 센서 매니저를 생성합니다.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 자이로스코프 센서를 가져옵니다.
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);






        Button recordingButton = findViewById(R.id.recordingButton);
        Button reservationButton = findViewById(R.id.reservationButton);
        Button noticeButton = findViewById(R.id.noticeButton);
        Button helpButton = findViewById(R.id.helpButton) ;


        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, activity_help.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });





        recordingButton.setOnClickListener(v -> {

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if(isColor ==true){
                recordingButton.setBackgroundResource(R.drawable.during_mic);
                isColor = false;
            } else{
                recordingButton.setBackgroundResource(R.drawable.mic);
                isColor = true;
            }


            String serverUrl = "https://38cf-113-198-217-79.ngrok-free.app/ws";

            client = new OkHttpClient();
            request = new Request.Builder()
                    .url(serverUrl)
                    .build();


            listener = new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                    runOnUiThread(() -> updateStatusText("음성 인식 대기중 ...."));

                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {

                    runOnUiThread(() -> {
                        try {

                            JSONObject jsonObject = new JSONObject(text);
                            prediction_class = jsonObject.optString("prediction_class", "unknown");
                            keyword = jsonObject.optString("keyword", "unknown");
                            direction = jsonObject.optString("direction","unknown");

                            //vibrator.vibrate(500);

                            if (prediction_class.equals("0")){
                                prediction_class = "자동차 경적 소리";
                            } else if (prediction_class.equals("1")) {
                                prediction_class = "개 짖는 소리";
                            } else if (prediction_class.equals("2")) {
                                prediction_class = "사이렌 소리" ;
                            } else if (prediction_class.equals("3")) {
                                prediction_class = "비명 소리";
                            } else if (prediction_class.equals("4")){
                                prediction_class = "말 소리";
                            }



                            if (direction.equals("동쪽")) {
                                animateWithFadeInOut(eastImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(east_rotate);

                                if(keyword.equals("unknown")){
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 " + prediction_class + "가 탐지 되었습니다 ! ") );
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                }
                            } else if (direction.equals("서쪽")) {
                                animateWithFadeInOut(westImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(west_rotate);
                                if(keyword.equals("unknown")){
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 " + prediction_class + "가 탐지 되었습니다 ! ") );
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                }
                            } else if (direction.equals("남쪽")) {
                                animateWithFadeInOut(southImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(south_rotate);
                                if(keyword.equals("unknown")){
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 " + prediction_class + "가 탐지 되었습니다 ! ") );
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
                                }
                            } else if (direction.equals("북쪽")) {
                                animateWithFadeInOut(northImageView, fadein_Animation, fadeout_Animation);
                                logoImageView.startAnimation(north_rotate);
                                if(keyword.equals("unknown")){
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 " + prediction_class + "가 탐지 되었습니다 ! ") );
                                } else {
                                    runOnUiThread(() -> updateStatusText(direction + " 에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! "));
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

                        // Wait for 1 second and then start fade out animation
                        new Handler().postDelayed(() -> {
                            imageView.startAnimation(fadeoutAnimation);

                            isAnimating = false; // Reset animation flag
                        }, 1000);
                    }
                }







                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    runOnUiThread(() -> updateStatusText("음성 탐색이 중단되었습니다."));
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                    runOnUiThread(() -> updateStatusText("연결 중입니다..."));
                }


            };


            webSocket = client.newWebSocket(request, listener);


            if (!isRecording) {
                startRecording();
                isRecording = true; // 녹음 상태를 true로 설정


            } else {
                // Stop 버튼은 녹음 중에만 활성화되도록 설정
                recordingButton.setEnabled(false);
                stopRecording();
                rotationAnimation.cancel();
                float currentRotation = logoImageView.getRotation(); // 현재 회전 각도 가져오기
                logoImageView.setRotation(currentRotation); // 정지된 각도로 설정
                logoImageView.clearAnimation(); // 새로운 애니메이션을 설정하지 않도록 애니메이션을 지움
                isRecording = false; // 녹음 상태를 false로 설정


            }

        });



        noticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, activity_notice.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });






        reservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

    }



    @Override
    protected void onResume() {
        super.onResume();


        // 액티비티가 재개될 때 센서 리스너를 등록합니다.
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 일시 정지될 때 센서 리스너를 해제합니다.
        sensorManager.unregisterListener(this);

        if (isRecording) {
            stopRecording();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
    }

    

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서의 정확도가 변경되었을 때 호출됩니다.
        // 이 예제에서는 사용하지 않으므로 구현할 필요가 없습니다.
    }







    private void startRecording() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }



        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        byte[] buffer = new byte[BUFFER_SIZE];

        isRecording = true;
        audioRecord.startRecording();

        new Thread(() -> {
            while (isRecording) {
                int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {


                    byte[] leftChannelData = new byte[bytesRead / 2];
                    byte[] rightChannelData = new byte[bytesRead / 2];

                    for (int i = 0, j = 0; i < bytesRead; i += 4, j += 2) {

                        leftChannelData[j] = buffer[i];
                        leftChannelData[j + 1] = buffer[i + 1];
                        rightChannelData[j] = buffer[i + 2];
                        rightChannelData[j + 1] = buffer[i + 3];

                    }





                    // 방향 데이터 수집
                    float x = gyroscopeValues[0]; // x축 값 (pitch)
                    float y = gyroscopeValues[1]; // y축 값 (roll)
                    float z = gyroscopeValues[2]; // z축 값 (yaw)

                    float x1 = magneticFieldValues[0];
                    float y2 = magneticFieldValues[1];
                    float z3 = magneticFieldValues[2];


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("top_channel", Base64.encodeToString(leftChannelData, Base64.DEFAULT));
                        jsonObject.put("bottom_channel", Base64.encodeToString(rightChannelData, Base64.DEFAULT));
                        jsonObject.put("uid", uid);
                        jsonObject.put("gy_x", x);
                        jsonObject.put("gy_y", y);
                        jsonObject.put("gy_z", z);
                        jsonObject.put("ma_x", x1);
                        jsonObject.put("ma_y", y2);
                        jsonObject.put("ma_z", z3);

                        // JSON 객체를 문자열로 변환하고 웹소켓을 통해 서버로 전송합니다.
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
            webSocket.close(1000, "Recording stopped"); // 웹소켓 연결 끊기
        }

    }




    private void updateStatusText(String message) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText(message);
    }
}