package com.xandy.netspeed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NetSpeed extends Activity {
    
    private static final String TAG = "NetSpeed";
    private boolean flag;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_net_speed);
        //bindService();
        startService();
    }
    
    @Override
    protected void onDestroy() {
        unBindService();
        super.onDestroy();
    }
    
    private void startService() {
        Intent intent = new Intent(NetSpeed.this,NetService.class);
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
            Log.i(TAG, "onServiceDisconnected()");  
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {   
            Log.d(TAG, "onServiceConnected()");  
            flag = true;  
        }  
    };  
}
