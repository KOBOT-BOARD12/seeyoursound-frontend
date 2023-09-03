package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class activity_help extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Button backButton = findViewById(R.id.backButton);



        backButton.setOnClickListener(v -> {
            Intent intent2 = new Intent(activity_help.this, activity_main.class);
            startActivity(intent2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }
}

