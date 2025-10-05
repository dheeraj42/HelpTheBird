package com.example.birdgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    // Player
    private float px, py;
    private float vx = 0, vy = 0;
    private float playerRadius = 50f;
    private float maxSpeed = 600f; // pixels/sec

    private float enemyBaseSpeed = 1.0f;   // initial multiplier
    private float speedIncrement = 0.2f;   // increase per threshold


    // Coins
    private class Coin { float x, y; boolean collected; }
    private List<Coin> coins = new ArrayList<>();
    private int totalCoins = 100;

    // Enemies
    private class Enemy { float x, y, ex, ey, radius = 40f; }
    private List<Enemy> enemies = new ArrayList<>();
    private int initialEnemies = 7 ;

    // Score
    private int score = 0;
    private int coinsCollected = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private Random random = new Random();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        px = 500; py = 500;
        coins.clear();

        // Initialize enemies
        for (int i = 0; i < initialEnemies; i++) {
            Enemy enemy = new Enemy();
            enemy.x = random.nextInt(900) + 100;
            enemy.y = random.nextInt(1600) + 100;
            enemy.ex = randomSpeed();
            enemy.ey = randomSpeed();
            enemies.add(enemy);
        }

        // Game loop
        runnable = new Runnable() {
            @Override
            public void run() {
                update(0.016f);
                invalidate();
                handler.postDelayed(this, 16);
            }
        };
        handler.post(runnable);
    }



//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh){
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        coins.clear();
//        int padding = 100; // keep coins inside the screen, not touching edges
//
//        for (int i = 0; i < totalCoins; i++) {
//            Coin coin = new Coin();
//            coin.x = padding + random.nextInt(w - 2 * padding);
//            coin.y = padding + random.nextInt(h - 2 * padding);
//            coin.collected = false;
//            coins.add(coin);
//        }
//    }
//  for equal distribution of coins and no overlapping

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        coins.clear();
        int padding = 100;     // keep coins away from edges
        int coinRadius = 40;   // radius/size of each coin (adjust if different)

        for (int i = 0; i < totalCoins; i++) {
            boolean validPosition = false;
            int x = 0, y = 0;

            while (!validPosition) {
                // pick random position
                x = padding + random.nextInt(w - 2 * padding);
                y = padding + random.nextInt(h - 2 * padding);

                validPosition = true;

                // check overlap with already placed coins
                for (Coin other : coins) {
                    double distance = Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
                    if (distance < coinRadius * 2) { // overlap detected
                        validPosition = false;
                        break;
                    }
                }
            }

            // place coin once valid
            Coin coin = new Coin();
            coin.x = x;
            coin.y = y;
            coin.collected = false;
            coins.add(coin);
        }
    }


    private float randomSpeed() { return random.nextInt(400) - 200; }

    // Movement methods
    public void moveUp() { vx = 0; vy = -maxSpeed; }
    public void moveDown() { vx = 0; vy = maxSpeed; }
    public void moveLeft() { vx = -maxSpeed; vy = 0; }
    public void moveRight() { vx = maxSpeed; vy = 0; }
    public void stopMoving() { vx = 0; vy = 0; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint playerPaint = new Paint(); playerPaint.setColor(Color.BLUE);
        Paint enemyPaint = new Paint(); enemyPaint.setColor(Color.RED);
        Paint coinPaint = new Paint(); coinPaint.setColor(Color.YELLOW);

        for (Coin coin : coins) if (!coin.collected)
            canvas.drawCircle(coin.x, coin.y, 30, coinPaint);

        canvas.drawCircle(px, py, playerRadius, playerPaint);

        for (Enemy enemy : enemies)
            canvas.drawCircle(enemy.x, enemy.y, enemy.radius, enemyPaint);
    }

    private void update(float dt) {
        int width = getWidth();
        int height = getHeight();

        // Safety: wait until layout is ready
        if (width == 0 || height == 0) return;

        // Move player
        px += vx * dt;
        py += vy * dt;

        // Clamp player to screen
        if (px < playerRadius) px = playerRadius;
        if (px > width - playerRadius) px = width - playerRadius;
        if (py < playerRadius) py = playerRadius;
        if (py > height - playerRadius) py = height - playerRadius;


        // Move enemies
        for (Enemy enemy : enemies) {
            float currentSpeed = enemyBaseSpeed + (score / 20) * speedIncrement;
            // increases gradually: every 20 points = +0.2 speed

            enemy.x += enemy.ex * dt * currentSpeed;
            enemy.y += enemy.ey * dt * currentSpeed;

            if (enemy.x < enemy.radius || enemy.x > width - enemy.radius) enemy.ex = -enemy.ex;
            if (enemy.y < enemy.radius || enemy.y > height - enemy.radius) enemy.ey = -enemy.ey;
        }


        // Coin collisions
        for (Coin coin : coins) {
            if (!coin.collected) {
                float dx = coin.x - px, dy = coin.y - py;
                if (Math.sqrt(dx*dx + dy*dy) < playerRadius + 30) {
                    coin.collected = true;
                    coinsCollected++;
                    score += 5;

                    if (coinsCollected % 4 == 0) {
                        Enemy newEnemy = new Enemy();
                        newEnemy.x = random.nextInt(width - 200) + 100;
                        newEnemy.y = random.nextInt(height - 200) + 100;
                        newEnemy.ex = randomSpeed();
                        newEnemy.ey = randomSpeed();
                        enemies.add(newEnemy);
                    }
                }
            }
        }

        // Enemy collisions
        for (Enemy enemy : enemies) {
            float dx = enemy.x - px, dy = enemy.y - py;
            if (Math.sqrt(dx*dx + dy*dy) < playerRadius + enemy.radius) {
                px = py = 0; vx = vy = 0; // stop player
                gameOver();
                return;
            }
        }

        // All coins collected
        boolean allCollected = true;
        for (Coin coin : coins) if (!coin.collected) { allCollected = false; break; }
        if (allCollected) gameOver();
    }

    private void gameOver() {
        handler.removeCallbacks(runnable);

        post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), ResultActivity2.class);
                intent.putExtra("score", score);
                getContext().startActivity(intent);
                if (getContext() instanceof android.app.Activity)
                    ((android.app.Activity)getContext()).finish();
            }
        });
    }


}


