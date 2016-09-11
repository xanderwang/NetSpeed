package com.xander.netspeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wangxiaoyang on 15-5-5.
 * 检测开机广播，以启动检测网速service
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    private void startService( Context context ) {
        Intent intent = new Intent();
        intent.setClass(context, NetSpeedService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "intent = " + intent.toString());
        int autoStart = NetSpeedService.getPreferences(
                context ,
                NetSpeedService.KEY_AUTO_START ,
                NetSpeedService.AUTO_START_ON
        );
        if( ACTION_BOOT.equals(intent.getAction()) &&
                NetSpeedService.AUTO_START_ON == autoStart ) {
            startService(context);
        }
    }
}