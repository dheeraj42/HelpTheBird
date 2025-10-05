package com.example.birdgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        TextView tvFinal = findViewById(R.id.tvFinalScore);
        Button btnAgain = findViewById(R.id.btnPlayAgain);
        Button btnExit = findViewById(R.id.btnExit);

        int score = getIntent().getIntExtra("score", 0);
        tvFinal.setText("Score: " + score);

        btnAgain.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity2.this, GameActivity2.class);
            startActivity(i);
            finish();
        });

//        btnExit.setOnClickListener(v -> {
//            finishAffinity();
//        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close all activities and exit the app
                finishAffinity();  // closes all activities in this task
                System.exit(0);    // optional, ensures the process is killed
            }
        });


    }
}