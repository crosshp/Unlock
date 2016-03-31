package com.learn_thing.animation;

import android.app.KeyguardManager;
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
    int DOUBLE_CLICK_DELAY = 400;
    String PREFS_NAME = "delay";
    private KeyguardManager.KeyguardLock kl;

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("MyKeyguardLock");
        // Сохранение времени клика
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        long last = settings.getLong("last", 0);
        long delta = System.currentTimeMillis() - last;
        // Проверка двойного клика
        if (delta < DOUBLE_CLICK_DELAY) {
            System.out.println("Double Click");
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("last", 0);
            editor.commit();
            // Включение экрана
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire();
            // Вибратор
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long milliseconds = 200;
            v.vibrate(milliseconds);
            kl.disableKeyguard();
            ScreenOnOffService.keyguardLock = kl;
            wakeLock.release();
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("last", System.currentTimeMillis());
            editor.commit();
        }
    }
}
