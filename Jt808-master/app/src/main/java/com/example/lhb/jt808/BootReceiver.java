package com.example.lhb.jt808;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lhb on 2017/5/7.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
            Intent intent2 = new Intent(context, MainActivity.class);

            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }
}