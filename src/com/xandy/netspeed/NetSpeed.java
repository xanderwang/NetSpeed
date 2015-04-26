package com.xandy.netspeed;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NetSpeed extends Activity {
    
    private static final String TAG = "NetSpeed";
    
    private static final String KEY_AUTO_START = "auto_start";
    
    private boolean flag;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_net_speed);
        //bindService();
        startService(this);
    }
    
    @Override
    protected void onDestroy() {
        unBindService();
        super.onDestroy();
    }
    
    private void startService( Context context ) {
        Intent intent = new Intent();
        intent.setClass(context, NetService.class);
        startService(intent);
    }
    
    private void bindService() {  
        Log.d(TAG, "bindService");
        Intent intent = new Intent(NetSpeed.this,NetService.class);  
        bindService(intent, conn, Context.BIND_AUTO_CREATE);  
    }  
      
    private void unBindService(){  
        Log.i(TAG, "unBindService() start....");  
        if(flag == true){  
            Log.i(TAG, "unBindService() flag");  
            unbindService(conn);  
            flag = false;  
        }  
    }  
    
    private ServiceConnection conn = new ServiceConnection() {  
        
        @Override  
        public void onServiceDisconnected(ComponentName name) {   
            Log.d(TAG, "onServiceDisconnected()");  
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {   
            Log.d(TAG, "onServiceConnected()");  
            flag = true;  
        }  
    }; 
    
    
    public class AutoStart extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences preferences = context.getSharedPreferences(TAG, MODE_PRIVATE);
			boolean autoStart = preferences.getBoolean(KEY_AUTO_START, true);
			if( autoStart ) {
				startService(context);
			}
		}
    	
    }
}
