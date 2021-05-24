package me.keepalive.demo.rs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.keepalive.demo.uts.Ml;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Ml.i(intent.toString());
    }
}