package com.learn_thing.animation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Andrew on 30.03.2016.
 */
public class ScreenOnOffReciver extends BroadcastReceiver {
    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            System.out.println("Screen off");

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
            System.out.println("Screen on");
        }
        Intent i = new Intent(context, ScreenOnOffService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
    }
}