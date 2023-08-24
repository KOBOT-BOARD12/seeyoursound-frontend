package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.AlertDialog;
import android.content.DialogInterface;
public class activity_login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editID);
        passwordEditText = findViewById(R.id.ediPassword);
        loginButton = findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    showErrorMessage("이메일과 비밀번호를 입력하세요.");
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity_login.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Toast.makeText(activity_login.this, "로그인 성공", Toast.LENGTH_SHORT).show();


                                // 로그인 성공 후의 동작 구현
                                Intent intent = new Intent(activity_login.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // 로그인 화면을 종료
                            } else {
                                Toast.makeText(activity_login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            private void showErrorMessage(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_login.this, R.style.AlertDialogCustom);
                builder.setMessage(message)
                        .setTitle("알림")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();



                dialog.show();
            }

        });



    }
}
