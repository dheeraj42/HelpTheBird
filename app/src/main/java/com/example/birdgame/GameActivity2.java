package com.example.birdgame;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity2 extends AppCompatActivity {

    private com.example.birdgame.GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        gameView = findViewById(R.id.gameView);

        setupButton(R.id.btnUpLeft,   new Runnable() { public void run() { gameView.moveUp(); } });
        setupButton(R.id.btnLeft,     new Runnable() { public void run() { gameView.moveLeft(); } });
        setupButton(R.id.btnDownLeft, new Runnable() { public void run() { gameView.moveDown(); } });

        setupButton(R.id.btnUpRight,  new Runnable() { public void run() { gameView.moveUp(); } });
        setupButton(R.id.btnRight,    new Runnable() { public void run() { gameView.moveRight(); } });
        setupButton(R.id.btnDownRight,new Runnable() { public void run() { gameView.moveDown(); } });
    }

    private void setupButton(int id, final Runnable action) {
        Button button = findViewById(id);
        button.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        action.run();
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.stopMoving();
                        break;
                }
                return true;
            }
        });
    }
}