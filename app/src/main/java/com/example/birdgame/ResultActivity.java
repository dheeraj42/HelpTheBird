package com.example.birdgame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;




public class ResultActivity extends AppCompatActivity {

    private TextView textViewMyScore, textViewHighestScore, textViewResultInfo;
    private Button buttonAgain, buttonQuit;
    private int score;
    private TextView textViewRequiredScore;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewHighestScore = findViewById(R.id.textViewHighestScore);
        textViewMyScore = findViewById(R.id.textViewMyScore);
        textViewResultInfo = findViewById(R.id.textViewResultInfo);
        buttonAgain = findViewById(R.id.buttonAgain);
        buttonQuit = findViewById(R.id.buttonQuit);
        textViewRequiredScore = findViewById(R.id.textViewRequiredScore);
        score = getIntent().getIntExtra("score", 0);
        textViewMyScore.setText("Your Score : " + score);

        sharedPreferences = this.getSharedPreferences("Score", Context.MODE_PRIVATE);
        int highestScore = sharedPreferences.getInt("highestScore", 0);

        // ✅ Handle required score or reward logic
        if (score < 70) {
            int required = 70 - score;
            textViewRequiredScore.setText("You need " + required + " more score to UNLOCK next LEVEL.");
        } else {
            // if score >= 150, add +10 reward

            textViewRequiredScore.setText("Congratulations! You crossed 150 points.");
            Intent intent = new Intent(ResultActivity.this, GameActivity2.class);
            startActivity(intent);
        }

        // ✅ Update result info and highest score
        if (score >= 160) {
            textViewResultInfo.setText("You won the game");
            textViewHighestScore.setText("Highest Score : " + score);
            sharedPreferences.edit().putInt("highestScore", score).apply();
        } else if (score >= highestScore) {
            textViewResultInfo.setText("You lost the game");
            textViewHighestScore.setText("Highest Score : " + score);
            sharedPreferences.edit().putInt("highestScore", score).apply();
        } else {
            textViewResultInfo.setText("You lost the game");
            textViewHighestScore.setText("Highest Score : " + highestScore);
        }

        buttonAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonQuit.setOnClickListener(v -> showQuitDialog());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showQuitDialog();
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("Help The Bird");
        builder.setMessage("Are you sure, you want to quit(❌) the game?");
        builder.setCancelable(false);
        builder.setNegativeButton("Quit Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        });
        builder.setPositiveButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.create().show();
    }
}

