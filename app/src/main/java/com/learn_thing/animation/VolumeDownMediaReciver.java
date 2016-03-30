package com.learn_thing.animation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.Vibrator;

/**
 * Created by Andrew on 29.03.2016.
 */
public class VolumeDownMediaReciver extends BroadcastReceiver {
    int DOUBLE_CLICK_DELAY = 500;
    String PREFS_NAME = "delay";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        long last = settings.getLong("last", 0);
        long delta = System.currentTimeMillis() - last;
        if (delta < DOUBLE_CLICK_DELAY) {
            System.out.println("Double Click");
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("last", 0);
            editor.commit();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long milliseconds = 700;
            v.vibrate(milliseconds);
            wakeLock.release();
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("last", System.currentTimeMillis());
            editor.commit();
        }

    }
}
