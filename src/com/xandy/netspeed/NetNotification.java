package com.xandy.netspeed;

import java.lang.reflect.Field;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class NetNotification {

    public static void showNotifacation(Context context) {
        
        Notification notification = new Notification();
        notification.icon = android.R.drawable.presence_busy;
//        notification.largeIcon = context.getResources().getBoolean(id);
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        PendingIntent p = null ;
        notification.setLatestEventInfo(context, "title", "content", p);
        
        Bitmap icon = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        Canvas canva = new Canvas(icon);
        canva.drawRGB(25, 86, 95);
        
        Class<?> clazz;
        try {
            clazz = Class.forName("com.android.internal.R$id");
            Field field = clazz.getField("icon");
            field.setAccessible(true);
            int id_icon = field.getInt(null);
            if(notification.contentView != null ){  
                notification.contentView.setImageViewBitmap(id_icon, icon);  
            } 
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        NotificationManager nManager = (NotificationManager) 
                context.getSystemService(context.NOTIFICATION_SERVICE); 
        nManager.notify(3, notification);
    }
    
    public static void cancelNtf( Context context ) {
        NotificationManager nManager = (NotificationManager) 
                context.getSystemService(context.NOTIFICATION_SERVICE); 
        nManager.cancelAll();
    }
    
}
