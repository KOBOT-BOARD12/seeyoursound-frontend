package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView responseTextView ;
    private ListView listview;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private StringBuilder keywordsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        readkeywordToServer();
        listview = (ListView) findViewById(R.id.listView) ;
        items = new ArrayList<String>() ;
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;






        Button addButton = findViewById(R.id.addButton);
        Button modifyButton = (Button)findViewById(R.id.modifyButton) ;
        Button deleteButton = (Button)findViewById(R.id.deleteButton) ;


        Button backButton = findViewById(R.id.backButton) ;
        Button homeButton = findViewById(R.id.homeButton) ;
        Button noticeButton = findViewById(R.id.noticeButton);


        // 빈 데이터 리스트 생성.


        listview.setAdapter(adapter) ;







        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showEditTextDialog();
            }
        }) ;


        modifyButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        // 아이템 수정
                        items.set(checked, Integer.toString(checked+1) + "번 아이템 수정") ;

                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }) ;

        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        // 아이템 삭제
                        items.remove(checked) ;

                        // listview 선택 초기화.
                        listview.clearChoices();

                        // listview 갱신.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }) ;





        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessageActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessageActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        noticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessageActivity.this, activity_notice.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });








    }


    private void sendRequestToServer(String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String uid = user.getUid();
        String uid= "testtest";
        OkHttpClient client = new OkHttpClient();
        String serverUrl = "https://72d3-113-198-217-79.ngrok-free.app/register_keyword";

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + uid + "\", \"keyword\": \"" + message + "\" }";
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



    private void readkeywordToServer() {



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String uid = user.getUid();
        String uid = "testtest";
        OkHttpClient client = new OkHttpClient();
        String serverUrl = "https://72d3-113-198-217-79.ngrok-free.app/return_keyword"; // FastAPI 서버의 URL을 입력하세요
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






                        // 키워드를 items 리스트에 추가
                        for (int i = 0; i < keywordsArray.length(); i++) {
                            items.add(keywordsArray.getString(i));
                        }


                        // 어댑터에 데이터 변경 알림
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
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

    private void showEditTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("예약어 추가");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newKeyword = input.getText().toString();
                if (!newKeyword.isEmpty()) {
                    // 새로운 예약어를 리스트에 추가하고 어댑터에 변경 내용 알림
                    sendRequestToServer(newKeyword);
                    items.add(newKeyword);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
