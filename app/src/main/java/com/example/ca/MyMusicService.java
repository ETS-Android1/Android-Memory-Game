package com.example.ca;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyMusicService extends Service {
    MediaPlayer player;

    public MyMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
            player = MediaPlayer.create(this, R.raw.music);
            player.setLooping(true);
            player.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equalsIgnoreCase("play_bg_music")) {
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.music);
                    player.setLooping(true);
                    player.start();
                }
                else {
                    onDestroy();
                }
            }

            else if (action.equalsIgnoreCase("pause_bg_music")) {
                if (player != null) {
                    player.pause();
                }
            }

            else if (action.equalsIgnoreCase("resume_bg_music")) {
                if (player != null) {
                    player.start();
                }
            }

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}