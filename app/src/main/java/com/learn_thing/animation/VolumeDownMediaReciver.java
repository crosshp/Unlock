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
    int DOUBLE_CLICK_DELAY = 700;
    String PREFS_NAME = "delay";
    private KeyguardManager.KeyguardLock kl;
    private boolean isVolumeUp = false;
    private boolean isFirstClick = false;
    private boolean isSecondClick = false;
    private boolean isThirdClick = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("MyKeyguardLock");

        int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
        int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
        System.out.println(oldVolume + "  " + newVolume);
        if (newVolume > oldVolume) {
            isVolumeUp = true;
            System.out.println("Up volume");
        } else {
            if (newVolume < oldVolume) {
                isVolumeUp = false;
                System.out.println("Down volume");
            } else {
                if (newVolume == oldVolume && newVolume == 0) {
                    isVolumeUp = false;
                    System.out.println("Down volume");
                } else {
                    isVolumeUp = true;
                    System.out.println("Up volume");
                }
            }
        }

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        isFirstClick = settings.getBoolean("firstClick", false);
        isSecondClick = settings.getBoolean("doubleClick", false);
        System.out.println("Ololo " + isFirstClick);
        //Первый клик
        if (!isFirstClick) {
            // Первый клик Вниз
            if (!isVolumeUp) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong("last", System.currentTimeMillis());
                editor.putBoolean("firstClick", true);
                editor.commit();
                // Если клик - вверх
            } else {
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong("last", System.currentTimeMillis());
                editor.putBoolean("firstClick", false);
                editor.commit();
            }
        } else {
            if (!isSecondClick && isFirstClick) {
                // Если клик вверх
                if (isVolumeUp) {
                    long last = settings.getLong("last", 0);
                    long delta = System.currentTimeMillis() - last;
                    // Проверка двойного клика
                    if (delta < DOUBLE_CLICK_DELAY) {
                        System.out.println("Double Click");
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("doubleClick", true);
                        editor.putBoolean("firstClick", true);
                        editor.putLong("last", System.currentTimeMillis());
                        editor.commit();
                        // Если клик вверх но время клика больше
                    } else {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("firstClick", false);
                        editor.putBoolean("doubleClick", false);
                        editor.putLong("last", System.currentTimeMillis());
                        editor.commit();
                    }
                    // если клик вниз
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong("last", System.currentTimeMillis());
                    editor.putBoolean("firstClick", true);
                    editor.putBoolean("doubleClick", false);
                    editor.commit();
                }


            } else {
                if (!isThirdClick && isSecondClick && isFirstClick) {
                    if (!isVolumeUp) {
                        long last = settings.getLong("last", 0);
                        long delta = System.currentTimeMillis() - last;
                        // Проверка двойного клика
                        if (delta < DOUBLE_CLICK_DELAY) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putLong("last", 0);
                            editor.commit();
                            //  Включение экрана
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
                        }
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("firstClick", false);
                        editor.putBoolean("doubleClick", false);
                        editor.commit();
                    }

                }
            }

        }
        System.out.println(isFirstClick);
        System.out.println(isSecondClick);
        System.out.println(isThirdClick);
    }
}
