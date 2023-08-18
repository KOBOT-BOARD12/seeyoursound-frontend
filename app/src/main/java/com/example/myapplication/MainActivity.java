package com.example.myapplication;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.content.Intent;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private float[] gyroscopeValues = new float[3];
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)*25;

    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private WebSocket webSocket;

    public EditText messageEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // 자이로스코프 센서를 사용하기 위해 센서 매니저를 생성합니다.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 자이로스코프 센서를 가져옵니다.
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        String serverUrl = "ws://10.30.118.111:8000/ws"; // FastAPI 서버의 WebSocket 엔드포인트 URL

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverUrl)
                .build();


        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                runOnUiThread(() -> updateStatusText("연결되었습니다."));
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                // 서버로부터 바이트 스트림 메시지를 수신할 때 실행되는 코드 (필요에 따라 추가 작업 수행)
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

        Button startRecordingButton = findViewById(R.id.startRecordingButton);
        Button stopRecordingButton = findViewById(R.id.stopRecordingButton);
        Button reservationButton = findViewById(R.id.reservationButton);


        startRecordingButton.setOnClickListener(v -> startRecording());



        stopRecordingButton.setOnClickListener(v -> stopRecording());



        // 예약어 버튼 클릭 이벤트 처리
        reservationButton.setOnClickListener(v -> {

                // SendMessageActivity로 넘어가는 코드
                Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
                startActivity(intent);


        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 재개될 때 센서 리스너를 등록합니다.
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 일시 정지될 때 센서 리스너를 해제합니다.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 자이로스코프 값 가져오기
            gyroscopeValues = event.values;


            // 이제 x, y, z 값을 가지고 방향을 처리할 수 있습니다.



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


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("top_channel", Base64.encodeToString(leftChannelData, Base64.DEFAULT));
                        jsonObject.put("bottom_channel", Base64.encodeToString(rightChannelData, Base64.DEFAULT));
                        jsonObject.put("gy_x", x);
                        jsonObject.put("gy_y", y);
                        jsonObject.put("gy_z", z);

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