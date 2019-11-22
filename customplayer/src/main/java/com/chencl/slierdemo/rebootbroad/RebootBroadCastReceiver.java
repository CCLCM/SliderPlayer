package com.chencl.slierdemo.rebootbroad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chencl.slierdemo.MainActivity;


public class RebootBroadCastReceiver extends BroadcastReceiver {

    static final String action_boot="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(action_boot)){

            Intent bootMainIntent = new Intent(context, MainActivity.class);
            bootMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootMainIntent);
        }

    }
}
