package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;



public class activity_filter extends AppCompatActivity {


    Button backButton;
    Button homeButton;
    Button reservationButton;
    Button saveButton;
    CheckBox car_check;
    CheckBox dog_check;
    CheckBox siren_check;
    CheckBox scream_check;
    boolean sound0;
    boolean sound1;
    boolean sound2;
    boolean sound3;
    String uid;
    String[] url ;
    String serverurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        url = getResources().getStringArray(R.array.url);

        backButton = findViewById(R.id.backButton) ;
        homeButton = findViewById(R.id.homeButton);
        reservationButton = findViewById(R.id.reservationButton);
        saveButton = findViewById(R.id.saveButton);
        car_check = findViewById(R.id.car_check);
        dog_check = findViewById(R.id.dog_check);
        siren_check = findViewById(R.id.siren_check);
        scream_check = findViewById(R.id.scream_check);


        readCheckToServer();



        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_filter.this, activity_main.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_filter.this, activity_main.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        reservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_filter.this, activity_keyword.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        saveButton.setOnClickListener(v -> {
            sendCheckToServer();

        });




    }

    private void sendCheckToServer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        OkHttpClient client = new OkHttpClient();
        serverurl = url[2];
        String serverUrl = serverurl ;

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // Checkbox 상태에 따라 JSON 객체 생성
        JSONObject soundClass = new JSONObject();
        try {
            soundClass.put("0", car_check.isChecked());
            soundClass.put("1", dog_check.isChecked());
            soundClass.put("2", siren_check.isChecked());
            soundClass.put("3", scream_check.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 전체 JSON 객체 생성
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("user_id", uid);
            requestBodyJson.put("sound_class", soundClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(mediaType, requestBodyJson.toString());

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
                            confirmDialog();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run(){}
                    });

                }
            }
        });
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder
                .setMessage(" 저장 되었습니다 ! ")
                .setPositiveButton(" 확인 ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void readCheckToServer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        serverurl = url[1] ;
        OkHttpClient client = new OkHttpClient();
        String serverUrl = serverurl ;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + uid + "\"}" ;
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
                    public void run() {}
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run(){}
                    });
                } else {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);


                        sound0 = jsonObject.optBoolean("0");
                        sound1 = jsonObject.optBoolean("1");
                        sound2 = jsonObject.optBoolean("2");
                        sound3 = jsonObject.optBoolean("3");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                car_check.setChecked(sound0);
                                dog_check.setChecked(sound1);
                                siren_check.setChecked(sound2);
                                scream_check.setChecked(sound3);

                            }
                        });

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


    }












}
