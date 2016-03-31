package com.learn_thing.animation;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

public class ScreenOnOffService extends Service {

    BroadcastReceiver screenReceiver = null;
    BroadcastReceiver volumeReciver = null;
    Intent intentService = null;
    String PREFS_NAME = "delay";
    static KeyguardManager.KeyguardLock keyguardLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //Фильтр для ресивера который отвечает за состояния экрана
        intentService = new Intent(getBaseContext(), PlayerService.class);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        screenReceiver = new ScreenOnOffReciver();
        // Регистрация фильтра
        registerReceiver(screenReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        System.out.println("Start On Off Service");
        boolean screenOff = false;
        try {
            screenOff = intent.getBooleanExtra("screen_state", false);

        } catch (Exception e) {
        }
        // Включен экран
        if (!screenOff) {
            stopService(intentService);
            SharedPreferences settings = getBaseContext().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("last", 0);
            editor.putBoolean("firstClick", false);
            editor.commit();
            if (volumeReciver != null) {
                unregisterReceiver(volumeReciver);
                volumeReciver = null;
            }
            // Выключен экран
        } else {
            // Возобновление блокировки
            if(keyguardLock!=null) {
                keyguardLock.reenableKeyguard();
            }
            // Регистрация ресивера для отловки кнопок
            if(volumeReciver==null){
                IntentFilter filterVolume = new IntentFilter();
                filterVolume.addAction(Intent.ACTION_MEDIA_BUTTON);
                filterVolume.addAction("android.media.VOLUME_CHANGED_ACTION");
                volumeReciver = new VolumeDownMediaReciver();
                filterVolume.setPriority(999);
                registerReceiver(volumeReciver, filterVolume);
            }
            startService(intentService);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(volumeReciver);
        volumeReciver = null;
        if(keyguardLock!=null) {
            keyguardLock.reenableKeyguard();
        }
    }
}