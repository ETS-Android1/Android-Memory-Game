package com.example.ca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class    GameCompletionActivity extends AppCompatActivity implements View.OnClickListener{

    TextView time;
    Button homeButton, boardButton;
    SharedPreferences sharedPref;
    private boolean musicFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        sharedPref = getSharedPreferences("music_flag", MODE_PRIVATE);
        musicFlag = sharedPref.getBoolean("music_flag", musicFlag);

        if (!musicFlag) {
            Intent intent = new Intent(GameCompletionActivity.this, MyMusicService.class);
            intent.setAction("pause_bg_music");
            startService(intent);
        }
        else {
            Intent intent = new Intent(GameCompletionActivity.this, MyMusicService.class);
            intent.setAction("resume_bg_music");
            startService(intent);
        }

        homeButton = findViewById(R.id.homeBtn);
        homeButton.setOnClickListener(this);

        boardButton = findViewById(R.id.boardBtn);
        boardButton.setOnClickListener(this);

        Intent intent = getIntent();
        time = findViewById(R.id.time);

        long millis  = intent.getLongExtra("time",0);
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;

        time.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.homeBtn){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(view.getId()== R.id.boardBtn){
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(GameCompletionActivity.this, MyMusicService.class);
        intent.setAction("pause_bg_music");
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        musicFlag = sharedPref.getBoolean("music_flag", musicFlag);
        if (musicFlag) {
            Intent intent = new Intent(GameCompletionActivity.this, MyMusicService.class);
            intent.setAction("resume_bg_music");
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =  new Intent(GameCompletionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
