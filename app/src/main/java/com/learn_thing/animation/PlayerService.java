package com.learn_thing.animation;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

/**
 * Created by Andrew on 27.03.2016.
 */
public class PlayerService extends Service {
    static volatile MediaPlayer player = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("Stop music!");
        player.release();
        player = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Play Music");
        player = MediaPlayer.create(this, R.raw.foneground);
        player.start();
        player.setLooping(true);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.release();
                player = null;

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
