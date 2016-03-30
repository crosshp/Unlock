package com.learn_thing.animation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;

public class ScreenOnOffService extends Service {
    BroadcastReceiver mReceiver = null;
    BroadcastReceiver volumeReciver = null;
    Intent intentService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        intentService = new Intent(getBaseContext(), PlayerService.class);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mReceiver = new ScreenOnOffReciver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        System.out.println("Start On Off Service");
        boolean screenOff = false;
        try {
            screenOff = intent.getBooleanExtra("screen_state", false);

        } catch (Exception e) {
        }
        if (!screenOff) {
            stopService(intentService);
            if (volumeReciver != null) {
                unregisterReceiver(volumeReciver);
                volumeReciver = null;
            }
        } else {
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
    }
}