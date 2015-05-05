package com.xandy.netspeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wangxiaoyang on 15-5-5.
 */
public class AutoStart extends BroadcastReceiver {

    private static final String TAG = "AutoStart";

    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    private void startService( Context context ) {
        Intent intent = new Intent();
        intent.setClass(context, NetService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "intent = " + intent.toString());
        int autoStart = NetService.getPreferences(context ,NetService.KEY_AUTO_START ,NetService.AUTO_START_ON);
        if( ACTION_BOOT.equals(intent.getAction()) &&  NetService.AUTO_START_ON == autoStart ) {
            startService(context);
        }
    }
}