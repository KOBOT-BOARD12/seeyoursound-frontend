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
import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.app.NotificationCompat;

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



public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;

    private Sensor sensor;
    private float[] gyroscopeValues = new float[3];
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)*25;

    private boolean isRecording = false;
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





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        logoImageView = findViewById(R.id.logo); // 이미지뷰 찾기

        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);




        // 자이로스코프 센서를 사용하기 위해 센서 매니저를 생성합니다.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 자이로스코프 센서를 가져옵니다.
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);






        Button recordingButton = findViewById(R.id.recordingButton);
        Button reservationButton = findViewById(R.id.reservationButton);
        Button noticeButton = findViewById(R.id.noticeButton);







        recordingButton.setOnClickListener(v -> {
            logoImageView.startAnimation(rotationAnimation);
            // 자이로스코프 센서를 사용하기 위해 센서 매니저를 생성합니다.
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            // 자이로스코프 센서를 가져옵니다.
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


            String serverUrl = "ws://10.30.112.144:8000/ws"; // FastAPI 서버의 WebSocket 엔드포인트 URL

            client = new OkHttpClient();
            request = new Request.Builder()
                    .url(serverUrl)
                    .build();


            listener = new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                    runOnUiThread(() -> updateStatusText("연결되었습니다."));
                    createNotificationChannel();
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {

                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(text);
                            prediction_class = jsonObject.optString("prediction_class", "unknown");
                            keyword = jsonObject.optString("keyword", "unknown");
                            direction = jsonObject.optString("direction","unknown");



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


                            sendNotification();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });
                }


                // Notification Builder를 만드는 메소드


                // Notification을 보내는 메소드
                public void sendNotification(){
                    NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
                    mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                }

                //채널을 만드는 메소드
                public void createNotificationChannel()
                {
                    // notification manager 생성
                    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                                "Test Notification", NotificationManager.IMPORTANCE_HIGH);

                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.enableVibration(true);
                        notificationChannel.setDescription("Notification from Mascot");

                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }

                }

                private NotificationCompat.Builder getNotificationBuilder() {
                    NotificationCompat.Builder Builder = new NotificationCompat.Builder(MainActivity.this, PRIMARY_CHANNEL_ID)
                            .setContentTitle("새로운 음성이 탐색되었습니다!")
                            .setSmallIcon(R.drawable.eyes);

                    if(keyword.equals("unknown")){
                        Builder.setContentText(direction + " 에서 " + prediction_class + "가 탐지 되었습니다 ! ") ;
                    } else {
                        Builder.setContentText(direction + " 에서 등록된 KEYWORD " + keyword + "가 탐지 되었습니다 ! ") ;
                    }


                    return Builder;
                }

















                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    runOnUiThread(() -> updateStatusText("WebSocket connection closed: " + code + ", " + reason));
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