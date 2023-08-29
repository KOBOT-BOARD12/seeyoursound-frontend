package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;


public class activity_notice extends AppCompatActivity {

    Button start_Button;
    ImageView logoImageView;
    Button backButton;
    Button homeButton;
    Button reservationButton;
    TextView noticeview ;
    private ScrollView scrollview;
    String predictionClass ;
    String keyword;
    String direction;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        backButton = findViewById(R.id.backButton) ;
        homeButton = findViewById(R.id.homeButton);
        reservationButton = findViewById(R.id.reservationButton);














        backButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(activity_notice.this, MainActivity.class);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(activity_notice.this, MainActivity.class);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        reservationButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(activity_notice.this, SendMessageActivity.class);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });






    }












}
