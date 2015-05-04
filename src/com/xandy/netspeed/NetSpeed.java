package com.xandy.netspeed;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class NetSpeed extends Activity implements OnClickListener {
    
    private static final String TAG = "NetSpeed";
    private boolean flag;  
    
    private View mRowAutoOn;
    private CheckBox mAutoOn;
    private View mRowFrequency;
    private View mRowStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_net_speed);
        
        mRowAutoOn = findViewById(R.id.row_auto_on);
        mRowAutoOn.setOnClickListener(this);
        mAutoOn = (CheckBox)findViewById(R.id.checkbox_auto_on);

        mRowFrequency = findViewById(R.id.row_check_time);
        mRowFrequency.setOnClickListener(this);

        mRowStyle = findViewById(R.id.row_style);
        mRowStyle.setOnClickListener(this);

        startService(this);
    }
    
    private void startService( Context context ) {
        Intent intent = new Intent();
        intent.setClass(context, NetService.class);
        startService(intent);
    }
    
    @Override
    public void onClick(View v) {
    	if( mRowAutoOn == v ) {
    		mAutoOn.toggle();
    		int mode = mAutoOn.isChecked() ? NetService.AUTO_START_ON : NetService.AUTO_START_OFF;
            //NetService.setPreferences(this,NetService.KEY_AUTO_START,mode);
    	} else if( mRowFrequency == v ) {
            changeFrequency();
    	} else if( mRowStyle == v ) {
    	    changeStyle();
    	}
    }

    private void changeFrequency() {

    }

    private void changeStyle() {

    }
    
    
    public class AutoStart extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int autoStart = NetService.getPreferences(context ,NetService.KEY_AUTO_START ,NetService.AUTO_START_ON);
			if( NetService.AUTO_START_ON == autoStart ) {
				startService(context);
			}
		}
    }
    
    
}
