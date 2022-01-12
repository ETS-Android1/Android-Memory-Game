package com.example.ca;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameActivity extends AppCompatActivity  {
    private TextView score, timerText,textInfo;
    private int firstCard, secondCard;
    private int firstClick, secondClick;
    private int cardNumber = 1;
    private int match = 0;
    private Handler timerHandler;
    private boolean musicFlag;
    private ImageButton btnMusic;
    private int timerSeconds;

    private ImageView pic11,pic12,pic13,pic21,pic22,pic23,pic31,pic32,pic33,pic41,pic42,pic43;

    private Integer [] cardArray = {11,12,13,14,15,16,11,12,13,14,15,16};

    private Bitmap image11,image12,image13,image14,image15,image16,image21,image22,image23,image24,image25,image26;
    private SoundPool soundpool;
    private int correct, fail, won;
    private TextView pauseForeground;
    private boolean isPaused;
    private Button pauseButton;
    private Button resumeButton;
    private Button backButton;
    Runnable timerRunnable;
    long millis;
    long currentSystemTime;
    long elapsedTimeOnPause = 0;
    long bestTime1;
    long bestTime2;
    long bestTime3;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        score = findViewById(R.id.matchProgress);
        pauseForeground = findViewById(R.id.pauseForeground);
        pauseButton = findViewById(R.id.pauseButton);
        resumeButton = findViewById(R.id.resumeButton);
        backButton = findViewById(R.id.backButton);
        textInfo = findViewById(R.id.textInfo);

        startTimer();

        btnMusic = findViewById(R.id.btnMusic);

        sharedPref = getSharedPreferences("music_flag", MODE_PRIVATE);
        editor = sharedPref.edit();

        musicFlag = sharedPref.getBoolean("music_flag", musicFlag);
        if (musicFlag) {
            btnMusic.setBackgroundResource(R.drawable.music_play);
        }
        else {
            btnMusic.setBackgroundResource(R.drawable.music_stop);
        }

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicFlag) {
                    btnMusic.setBackgroundResource(R.drawable.music_stop);
                    musicFlag = false;
                    Intent intent = new Intent(GameActivity.this, MyMusicService.class);
                    intent.setAction("pause_bg_music");
                    startService(intent);
                }
                else  {
                    btnMusic.setBackgroundResource(R.drawable.music_play);
                    musicFlag = true;
                    Intent intent = new Intent(GameActivity.this, MyMusicService.class);
                    intent.setAction("resume_bg_music");
                    startService(intent);
                }

                editor.putBoolean("music_flag", musicFlag);
                editor.commit();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isPaused = true;
                pauseForeground.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(GameActivity.this, MyMusicService.class);
                intent.setAction("pause_bg_music");
                startService(intent);
                stopTimer();
                removeListener();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isPaused = false;
                pauseForeground.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.INVISIBLE);
                if (musicFlag) {
                    Intent intent = new Intent(GameActivity.this, MyMusicService.class);
                    intent.setAction("resume_bg_music");
                    startService(intent);
                }
                startTimer();
                addListener();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        pic11 = findViewById(R.id.imgs1);
        pic12 = findViewById(R.id.imgs2);
        pic13 = findViewById(R.id.imgs3);
        pic21 = findViewById(R.id.imgs4);
        pic22 = findViewById(R.id.imgs5);
        pic23 = findViewById(R.id.imgs6);
        pic31 = findViewById(R.id.imgs7);
        pic32 = findViewById(R.id.imgs8);
        pic33 = findViewById(R.id.imgs9);
        pic41 = findViewById(R.id.imgs10);
        pic42 = findViewById(R.id.imgs11);
        pic43 = findViewById(R.id.imgs12);

        pic11.setTag("0");
        pic12.setTag("1");
        pic13.setTag("2");
        pic21.setTag("3");
        pic22.setTag("4");
        pic23.setTag("5");
        pic31.setTag("6");
        pic32.setTag("7");
        pic33.setTag("8");
        pic41.setTag("9");
        pic42.setTag("10");
        pic43.setTag("11");

        cardFront();
        Collections.shuffle(Arrays.asList(cardArray));
        addListener();
        AudioAttributes audioAttributes = new AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
            soundpool = new SoundPool
                .Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();
        correct = soundpool.load(this, R.raw.correct, 1);
        fail = soundpool.load(this, R.raw.fail, 1);
        won = soundpool.load(this, R.raw.won, 1);

    }

    public void removeListener() {
        pic11.setOnClickListener(null);
        pic12.setOnClickListener(null);
        pic13.setOnClickListener(null);
        pic21.setOnClickListener(null);
        pic22.setOnClickListener(null);
        pic23.setOnClickListener(null);
        pic31.setOnClickListener(null);
        pic32.setOnClickListener(null);
        pic33.setOnClickListener(null);
        pic41.setOnClickListener(null);
        pic42.setOnClickListener(null);
        pic43.setOnClickListener(null);
    }
    public void addListener(){
        pic11.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic11, card);
        });

        pic12.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic12, card);
        });

        pic13.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic13, card);
        });

        pic21.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic21, card);
        });

        pic22.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic22, card);
        });

        pic23.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic23, card);
        });

        pic31.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic31, card);
        });

        pic32.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic32, card);
        });

        pic33.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic33, card);
        });

        pic41.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic41, card);
        });

        pic42.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic42, card);
        });

        pic43.setOnClickListener(view -> {
            int card = Integer.parseInt((String) view.getTag());
            setImage(pic43, card);
        });
    }

    private void setImage(ImageView imageView, int card){
        switch (cardArray[card]) {
            case 11:
                imageView.setImageBitmap(image11);
                break;

            case 12:
                imageView.setImageBitmap(image12);
                break;

            case 13:
                imageView.setImageBitmap(image13);
                break;

            case 14:
                imageView.setImageBitmap(image14);
                break;

            case 15:
                imageView.setImageBitmap(image15);
                break;

            case 16:
                imageView.setImageBitmap(image16);
                break;

            case 21:
                imageView.setImageBitmap(image21);
                break;

            case 22:
                imageView.setImageBitmap(image22);
                break;

            case 23:
                imageView.setImageBitmap(image23);
                break;

            case 24:
                imageView.setImageBitmap(image24);
                break;

            case 25:
                imageView.setImageBitmap(image25);
                break;

            case 26:
                imageView.setImageBitmap(image26);
                break;
        }
        //card 0~11
        if (cardNumber == 1){
            firstCard = cardArray[card];
            cardNumber = 2;
            firstClick = card;

            imageView.setEnabled(false);
        }

        else if (cardNumber == 2){
            secondCard = cardArray[card];
            cardNumber = 1;
            secondClick = card;

            pic11.setEnabled(false);
            pic12.setEnabled(false);
            pic13.setEnabled(false);
            pic21.setEnabled(false);
            pic22.setEnabled(false);
            pic23.setEnabled(false);
            pic31.setEnabled(false);
            pic32.setEnabled(false);
            pic33.setEnabled(false);
            pic41.setEnabled(false);
            pic42.setEnabled(false);
            pic43.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(this::calculate, 1000);
        }
    }
    @SuppressLint("SetTextI18n")
    private void calculate(){
        ArrayList<ImageView> imageArray = new ArrayList<>(Arrays.asList(pic11,pic12,pic13,pic21,pic22,pic23,pic31,pic32,pic33,pic41,pic42,pic43));
        if(firstCard == secondCard){
            textInfo.setText(R.string.correct);
            soundpool.play(correct, 1, 1, 1, 0, 1);
            switch (firstClick){
                case 0:
                    pic11.setSelected(true);
                    break;
                case 1:
                    pic12.setSelected(true);
                    break;
                case 2:
                    pic13.setSelected(true);
                    break;
                case 3:
                    pic21.setSelected(true);
                    break;
                case 4:
                    pic22.setSelected(true);
                    break;
                case 5:
                    pic23.setSelected(true);
                    break;
                case 6:
                    pic31.setSelected(true);
                    break;
                case 7:
                    pic32.setSelected(true);
                    break;
                case 8:
                    pic33.setSelected(true);
                    break;
                case 9:
                    pic41.setSelected(true);
                    break;
                case 10:
                    pic42.setSelected(true);
                    break;
                case 11:
                    pic43.setSelected(true);
                    break;
            }

            switch (secondClick){
                case 0:
                    pic11.setSelected(true);
                    break;
                case 1:
                    pic12.setSelected(true);
                    break;
                case 2:
                    pic13.setSelected(true);
                    break;
                case 3:
                    pic21.setSelected(true);
                    break;
                case 4:
                    pic22.setSelected(true);
                    break;
                case 5:
                    pic23.setSelected(true);
                    break;
                case 6:
                    pic31.setSelected(true);
                    break;
                case 7:
                    pic32.setSelected(true);
                    break;
                case 8:
                    pic33.setSelected(true);
                    break;
                case 9:
                    pic41.setSelected(true);
                    break;
                case 10:
                    pic42.setSelected(true);
                    break;
                case 11:
                    pic43.setSelected(true);
                    break;
            }
            match++;
            score.setText(match + " of 6 matches");

        }

        else {
            soundpool.play(fail, 1, 1, 1, 0, 1);
            textInfo.setText(R.string.fail);
            for (int i = 0; i < imageArray.size(); i++){
                if(!imageArray.get(i).isSelected()){
                    imageArray.get(i).setImageResource(R.drawable.cardback);
                }
            }

        }

        for (int i = 0; i < imageArray.size(); i++) {
            if (!imageArray.get(i).isSelected()) {
                imageArray.get(i).setEnabled(true);
            }
        }
        checkGameEnd();
    }

    private void checkGameEnd(){
        if (match == 6) {
            timerHandler.removeCallbacks(timerRunnable);
            sharedPref = getSharedPreferences("best_time", MODE_PRIVATE);
            bestTime1 = sharedPref.getLong("best_time1", 0);
            bestTime2 = sharedPref.getLong("best_time2", 0);
            bestTime3 = sharedPref.getLong("best_time3", 0);

            if (bestTime1 == 0) {
                bestTime1 = millis;
            }

            else if (bestTime1 != 0 && bestTime2 == 0 && millis <= bestTime1)  {
                bestTime2 = bestTime1;
                bestTime1 = millis;
            }

            else if (bestTime1 != 0 &&  bestTime2 == 0 && millis > bestTime1) {
                bestTime2 = millis;
            }

            else if (bestTime1 != 0 && bestTime2 != 0 && millis <= bestTime1) {
                bestTime3 = bestTime2;
                bestTime2 = bestTime1;
                bestTime1 = millis;
            }

            else if (bestTime1 != 0 && bestTime2 != 0 && millis > bestTime1 && millis <= bestTime2) {
                bestTime3 = bestTime2;
                bestTime2 = millis;
            }

            else if (bestTime1 != 0 && bestTime2 != 0 && millis > bestTime1 && millis > bestTime2 && bestTime3 == 0) {
                bestTime3 = millis;
            }

            else if (bestTime1 != 0 && bestTime2 != 0 && bestTime3 != 0 && millis <= bestTime3) {
                bestTime3 = millis;
            }

            editor = sharedPref.edit();
            editor.putLong("best_time1", bestTime1);
            editor.putLong("best_time2", bestTime2);
            editor.putLong("best_time3", bestTime3);
            editor.commit();

            soundpool.play(won, 1, 1, 1, 0, 1);
            Intent intent = new Intent(getApplicationContext(), GameCompletionActivity.class);
            intent.putExtra("time",millis);
            startActivity(intent);
        }
    }



    private void cardFront() {
        ArrayList<Bitmap> images = MainActivity.selected;
        ArrayList<Bitmap> newBit = new ArrayList<Bitmap>();

        for (Bitmap img : images) {
            newBit.add(img);
        }
        images.addAll(newBit);

        image11 = images.get(0);
        image12 = images.get(1);
        image13 = images.get(2);
        image14 = images.get(3);
        image15 = images.get(4);
        image16 = images.get(5);
        image21 = images.get(6);
        image22 = images.get(7);
        image23 = images.get(8);
        image24 = images.get(9);
        image25 = images.get(10);
        image26 = images.get(11);

    }

    private void startTimer() {
        timerText = findViewById(R.id.timer);
        timerHandler = new Handler();
        currentSystemTime = System.currentTimeMillis();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                millis = System.currentTimeMillis() - currentSystemTime + elapsedTimeOnPause;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;

                timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.post(timerRunnable);

    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        elapsedTimeOnPause = elapsedTimeOnPause + (System.currentTimeMillis() - currentSystemTime);
    }


    public void pauseGame() {
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isPaused = true;
                pauseForeground.setVisibility(View.VISIBLE);
                pauseButton.setText("Resume");
                stopTimer();

            }
        });
    }

    public void resumeGame() {
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isPaused = false;
                pauseForeground.setVisibility(View.INVISIBLE);
                pauseButton.setText("Pause");
                currentSystemTime = System.currentTimeMillis();
                timerHandler.post(timerRunnable);

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(GameActivity.this, MyMusicService.class);
        intent.setAction("pause_bg_music");
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        musicFlag = sharedPref.getBoolean("music_flag", musicFlag);
        if (musicFlag) {
            btnMusic.setBackgroundResource(R.drawable.music_play);
        } else {
            btnMusic.setBackgroundResource(R.drawable.music_stop);
        }
        if (musicFlag && !isPaused) {
            Intent intent = new Intent(GameActivity.this, MyMusicService.class);
            intent.setAction("resume_bg_music");
            startService(intent);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}
