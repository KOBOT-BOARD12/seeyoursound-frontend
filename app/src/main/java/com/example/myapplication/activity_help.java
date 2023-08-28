package com.example.myapplication;

import android.app.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class activity_help extends AppCompatActivity {


    ImageView logoImageView;


    ImageView eastImageView;
    ImageView westImageView;
    ImageView southImageView;
    ImageView northImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        Button backButton = findViewById(R.id.backButton);



        backButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(activity_help.this, MainActivity.class);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }
}

