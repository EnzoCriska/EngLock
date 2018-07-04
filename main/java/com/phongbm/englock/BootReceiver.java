package com.phongbm.englock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (pref.getBoolean("status", false)) {
            Intent i = new Intent(context, EnglockService.class);
            context.startService(i);
        }
    }

}