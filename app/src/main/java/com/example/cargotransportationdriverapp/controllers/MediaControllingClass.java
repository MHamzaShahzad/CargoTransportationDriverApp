package com.example.cargotransportationdriverapp.controllers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.example.cargotransportationdriverapp.R;

import java.io.IOException;

public class MediaControllingClass {

    private Context context;
    private static MediaPlayer mp;
    private static Vibrator vibrator;
    ;

    public static MediaControllingClass getInstance(Context context) {
        return new MediaControllingClass(context);
    }

    private MediaControllingClass(Context context) {
        this.context = context;
    }

    public void startPlaying(){
        startTune();
        startVibration();
    }

    public void stopPlaying(){
        stopTune();
        stopVibration();
    }

    private void startVibration() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(20000);
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    private void startTune() {
        mp = MediaPlayer.create(context, R.raw.tune);
        mp.start();
        mp.setLooping(true);
    }

    private void stopTune() {

        if (mp != null) {
            try {
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
