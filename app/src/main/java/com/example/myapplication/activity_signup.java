package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class activity_signup extends AppCompatActivity {

    EditText id, pw, pw2;
    Button pwcheck, submit;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        id = findViewById(R.id.signID);
        pw = findViewById(R.id.signPW);
        pw2 = findViewById(R.id.signPW2);

        pwcheck = findViewById(R.id.pwcheckbutton);
        submit = findViewById(R.id.signupbutton);

        pwcheck.setOnClickListener(v -> {
            if (pw.getText().toString().equals(pw2.getText().toString())) {
                pwcheck.setText("일치");
            } else {
                Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = id.getText().toString();
                String password = pw.getText().toString();


                // 이메일 주소의 형식 검사
                if (!isValidEmail(emailStr)) {
                    showErrorMessage("유효하지 않은 이메일 형식입니다.");
                    return;
                }

                // 여기서 필요한 입력 유효성 검사를 수행
                if (emailStr.isEmpty() || password.isEmpty()) {
                    showErrorMessage("모든 필드를 정확히 입력하세요.");
                    return;
                }


                auth.createUserWithEmailAndPassword(emailStr, password)
                        .addOnCompleteListener(activity_signup.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                Toast.makeText(activity_signup.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                // 회원가입 성공 후의 동작 구현
                                Intent intent = new Intent(activity_signup.this, activity_main.class);

                                startActivity(intent);
                                finish();

                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthUserCollisionException) {
                                    // 이미 가입된 이메일인 경우
                                    showErrorMessage("이미 가입된 이메일입니다.");
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    // 이메일 형식이 올바르지 않은 경우
                                    showErrorMessage("유효하지 않은 이메일 형식입니다.");
                                } else {
                                    // 그 외의 실패 사유
                                    showErrorMessage("회원가입 실패: " + exception.getMessage());
                                }
                            }
                        });
            }

            private boolean isValidEmail(CharSequence target) {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            }

            private void showErrorMessage(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_signup.this, R.style.AlertDialogCustom);
                builder
                        .setMessage(message)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }
}
