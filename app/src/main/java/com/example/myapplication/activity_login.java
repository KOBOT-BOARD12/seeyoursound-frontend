package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class activity_login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth auth;
    Button signtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editID);
        passwordEditText = findViewById(R.id.ediPassword);
        loginButton = findViewById(R.id.loginbutton);
        signtext = findViewById(R.id.signtext);


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



                                Intent intent = new Intent(activity_login.this, activity_main.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(activity_login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


            private void showErrorMessage(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_login.this, R.style.AlertDialogCustom);
                builder.setMessage(message)

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        signtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_login.this, activity_signup.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }


        });


    }
}
