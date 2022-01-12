package com.example.ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LeaderBoardActivity extends AppCompatActivity {

    private long bestTime1;
    private long bestTime2;
    private long bestTime3;
    SharedPreferences sharedPref;
    TextView bestTime1Text, bestTime2Text, bestTime3Text;
    Button homeButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        displayBestTime();


        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref = getSharedPreferences("best_time", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                bestTime1Text.setText("Best Time : 00:00:00");
                bestTime2Text.setText("Best Time : 00:00:00");
                bestTime3Text.setText("Best Time : 00:00:00");
            }
        });
    }

    private void displayBestTime() {
        sharedPref = getSharedPreferences("best_time", MODE_PRIVATE);
        bestTime1 = sharedPref.getLong("best_time1",0);
        bestTime2 = sharedPref.getLong("best_time2",0);
        bestTime3 = sharedPref.getLong("best_time3",0);
        int bestTime1Seconds = (int) (bestTime1 / 1000);
        int bestTime1Minutes = bestTime1Seconds / 60;
        int bestTime1Hours = bestTime1Minutes / 60;
        bestTime1Seconds = bestTime1Seconds % 60;
        bestTime1Text = findViewById(R.id.bestTime1);
        bestTime1Text.setText(String.format("Best Time : %02d:%02d:%02d", bestTime1Hours, bestTime1Minutes, bestTime1Seconds));

        int bestTime2Seconds = (int) (bestTime2 / 1000);
        int bestTime2Minutes = bestTime2Seconds / 60;
        int bestTime2Hours = bestTime2Minutes / 60;
        bestTime2Seconds = bestTime2Seconds % 60;
        bestTime2Text = findViewById(R.id.bestTime2);
        bestTime2Text.setText(String.format("Best Time : %02d:%02d:%02d", bestTime2Hours, bestTime2Minutes, bestTime2Seconds));

        int bestTime3Seconds = (int) (bestTime3 / 1000);
        int bestTime3Minutes = bestTime3Seconds / 60;
        int bestTime3Hours = bestTime3Minutes / 60;
        bestTime3Seconds = bestTime3Seconds % 60;
        bestTime3Text = findViewById(R.id.bestTime3);
        bestTime3Text.setText(String.format("Best Time : %02d:%02d:%02d", bestTime3Hours, bestTime3Minutes, bestTime3Seconds));
    }
}