package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class activity_model extends AppCompatActivity {

    Button start_Button;
    ImageView logoImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        logoImageView = findViewById(R.id.logoImageView);
        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        logoImageView.startAnimation(rotationAnimation);

        start_Button = findViewById(R.id.start_Button);


        start_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity_model.this, MainActivity.class);

                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);




            }

        });















    }





}
