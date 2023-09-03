package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthResult;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.common.api.ApiException;

public class activity_first extends AppCompatActivity {


    private FirebaseAuth mAuth;
    int RC_SIGN_IN = 123;
    Button sign;
    Button log;
    String TAG = "MyTag";
    // 구글 계정
    ImageView logoImageView;
    String default_web_client_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        logoImageView = findViewById(R.id.logoimageview); // 이미지뷰 찾기
        default_web_client_id = new String("887151907918-8nmcei1bs48151h0f9uj136td6bsl0e8.apps.googleusercontent.com");
        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        logoImageView.startAnimation(rotationAnimation);

        SignInButton signInButton = findViewById(R.id.btn_google_sign_in);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("구글로 로그인");


        sign = findViewById(R.id.signin);
        log = findViewById(R.id.signin2) ;
        mAuth = FirebaseAuth.getInstance();



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });



        log.setOnClickListener(new View.OnClickListener() {

            // 로그인 버튼을 클릭했을 때 실행할 코드를 여기에 작성하세요
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_first.this, activity_login.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

        });


        // 회원가입 버튼 클릭시, 회원가입 페이지로 이동
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_first.this, activity_signup.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }


        });









    }


    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Firebase 콘솔에서 발급한 웹 클라이언트 ID
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // ... (기존 코드)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // 로그인 성공 처리 (예: 다음 화면으로 이동)
                            Intent intent = new Intent(activity_first.this, activity_main.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }


                        };
                    });
                };



}

