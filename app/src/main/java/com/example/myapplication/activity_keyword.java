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

import android.widget.ArrayAdapter;
import android.widget.ListView;

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


public class activity_keyword extends Activity {

    private ListView listview;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        readkeywordToServer();
        listview = findViewById(R.id.listView);
        items = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);

        Button addButton = findViewById(R.id.addButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button backButton = findViewById(R.id.backButton);
        Button homeButton = findViewById(R.id.homeButton);
        Button noticeButton = findViewById(R.id.noticeButton);


        listview.setAdapter(adapter);


        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                showEditTextDialog();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = adapter.getCount();
                int checked = listview.getCheckedItemPosition();

                if (count > 0 && checked >= 0 && checked < count) {
                    String deletedKeyword = items.get(checked); // 선택된 아이템 가져오기
                    sendDeleteRequestToServer(deletedKeyword); // 서버로 삭제 요청 보내기

                    items.remove(checked); // 리스트뷰에서 아이템 삭제
                    listview.clearChoices();
                    adapter.notifyDataSetChanged();
                } else {
                    DeleteDialog(" 삭제할 예약어를 선택해주세요. ");
                }
            }
        });


        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_keyword.this, activity_main.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_keyword.this, activity_main.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        noticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_keyword.this, activity_filter.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });


    }


    private void sendRequestToServer(String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        OkHttpClient client = new OkHttpClient();
        String serverUrl = "https://41af-113-198-217-79.ngrok-free.app/register_keyword";

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
                            AddConfirmDialog();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            }
        });
    }


    private void sendDeleteRequestToServer(String keyword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        OkHttpClient client = new OkHttpClient();
        String serverUrl = "https://41af-113-198-217-79.ngrok-free.app/delete_keyword";

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBodyString = "{ \"user_id\": \"" + uid + "\", \"keyword\": \"" + keyword + "\" }";
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DeleteDialog(" 예약어가 삭제되었습니다. ");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DeleteDialog(" 예약어 삭제에 실패하였습니다. ");
                        }
                    });

                }
            }
        });
    }


    private void readkeywordToServer() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        OkHttpClient client = new OkHttpClient();
        String serverUrl = "https://41af-113-198-217-79.ngrok-free.app/return_keyword"; // FastAPI 서버의 URL을 입력하세요
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

                            }
                        });
                    }
                }
            }
        });


    }

    private void showEditTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(" 예약어 추가 ");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(" 추가 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String englishRegex = "^[a-zA-Z]+$";
                String newKeyword = input.getText().toString();
                if (!newKeyword.isEmpty()) {
                    if (isKeywordDuplicate(newKeyword)) {

                        showErrorMessage(" 이미 등록된 예약어입니다. ");
                    } else {
                        if (items.size() < 3) { // 리스트의 크기가 3개 미만인 경우에만 추가
                            if (newKeyword.matches(englishRegex)) {
                                showErrorMessage(" 영문 입력은 불가능합니다. ");
                            } else if (!(newKeyword.matches(englishRegex))) {
                                if (newKeyword.length() >= 3 && newKeyword.length() <= 5) {
                                    sendRequestToServer(newKeyword);
                                    items.add(newKeyword);
                                    adapter.notifyDataSetChanged();

                                } else if (newKeyword.length() < 3 || newKeyword.length() > 5) {
                                    showErrorMessage(" 예약어는 3글자에서 5글자까지 입력 가능합니다.");
                                }
                            }


                        } else {
                            DeleteDialog(" 최대 3개의 예약어까지만 등록 가능합니다. ");
                        }

                    }


                } else {
                    dialog.cancel();
                    showErrorMessage(" 예약어를 입력해주세요. ");

                }
            }
        });

        builder.setNegativeButton(" 취소 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private boolean isKeywordDuplicate(String keyword) {
        for (String existingKeyword : items) {
            if (existingKeyword.equalsIgnoreCase(keyword)) {
                return true; // 중복된 예약어가 이미 등록되어 있음
            }
        }
        return false; // 중복되는 예약어 없음
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom2);

        builder.setMessage(message);
        builder.setPositiveButton(" 확인 ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showEditTextDialog(); // 확인 버튼을 누르면 다시 다이얼로그 표시
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void AddConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom2);
        builder
                .setMessage(" 예약어가 추가되었습니다 ! ")
                .setPositiveButton(" 확인 ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void DeleteDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom2);
        builder
                .setMessage(message)
                .setPositiveButton(" 확인 ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
