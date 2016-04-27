package com.xandy.netspeed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NetNotification {

    public static void showNotifacation(Context context, int speed ,String speedFmt ) {
        
        Notification notification = new Notification();
        notification.icon = getNtfIcon(speed);
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        PendingIntent netActivity = PendingIntent.getActivity(
                context,0,new Intent(context,NetSpeed.class),0);

        String title = context.getResources().getString(R.string.working);
        String speedTip = context.getResources().getString(R.string.speed_tip);

//        notification.setLatestEventInfo(context, title, speedTip + speedFmt , netActivity);
        /*
        Bitmap icon = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        Canvas canva = new Canvas(icon);
        Class<?> clazz;
        try {
            clazz = Class.forName("com.android.internal.R$id");
            Field field = clazz.getField("icon");
            field.setAccessible(true);
            int id_icon = field.getInt(null);
            if(notification.contentView != null ){
                notification.contentView.setImageViewBitmap(id_icon, icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
//        NotificationManager nManager = (NotificationManager)
//                context.getSystemService(context.NOTIFICATION_SERVICE);
//        nManager.notify(1, notification);
    }
    
    public static void cancelNtf( Context context ) {
        NotificationManager nManager = (NotificationManager) 
                context.getSystemService(context.NOTIFICATION_SERVICE); 
        nManager.cancelAll();
    }

    private static int getNtfIcon( int speed ) {
        int icon = R.drawable.bkb000;
        if( speed < 1000 ) {
            icon = icon + speed;
        } else if( speed < 1000 * 10 )  {
            icon = R.drawable.bmb010;
            icon = icon + ( speed - 1000 ) / 100;
        } else if(speed < 1000 * 200) {
            icon = R.drawable.bmb100;
            icon = icon + ( speed - 10000 ) / 1000;
        }
        return icon;
    }
    
}
