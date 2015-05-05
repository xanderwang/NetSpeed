package com.xandy.netspeed;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.xandy.expanddialog.ExpandDialog;

public class NetSpeed extends Activity implements View.OnClickListener {
    
    private static final String TAG = "NetSpeed";
    private boolean flag;  
    
    private View mRowAutoOn;
    private CheckBox mAutoOn;
    private View mRowFrequency;
    private TextView mFrequencyText;
    private View mRowStyle;
    private TextView mStyleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_net_speed);

        initView();

        startService(this);
    }

    public void initView() {

        mRowAutoOn = findViewById(R.id.row_auto_on);
        mRowAutoOn.setOnClickListener(this);
        mAutoOn = (CheckBox)findViewById(R.id.checkbox_auto_on);

        mRowFrequency = findViewById(R.id.row_frequency);
        mRowFrequency.setOnClickListener(this);
        mFrequencyText = (TextView) findViewById(R.id.frequency_value);
        int which = NetService.getPreferences(this,NetService.KEY_FREQUENCY,NetService.FREQUENCY_NORMAL);
        String Str = getResources().getStringArray(R.array.array_frequency)[which];
        mFrequencyText.setText(Str);

        mRowStyle = findViewById(R.id.row_style);
        mRowStyle.setOnClickListener(this);
        mStyleText = (TextView)findViewById(R.id.style_value);
        which = NetService.getPreferences(this,NetService.KEY_STYLE,NetService.STYLE_NTF);
        Str = getResources().getStringArray(R.array.array_style)[which];
        mStyleText.setText(Str);

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

    private DialogInterface.OnClickListener mFrequencyListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String frequencyStr = getResources().getStringArray(R.array.array_frequency)[which];
            mFrequencyText.setText(frequencyStr);
            NetService.setPreferences(getApplicationContext(),NetService.KEY_FREQUENCY,which);
            NetService.instance().updateFrequency(which);
        }
    };

    private void changeFrequency() {
        int frequency = NetService.getPreferences(this,NetService.KEY_FREQUENCY,NetService.FREQUENCY_NORMAL);
        ExpandDialog mFreqrency = new ExpandDialog.Builder(this)
                .setTitle(R.string.frequency)
                .setGravity(Gravity.TOP)
                .setSingleChoiceItems(R.array.array_frequency, frequency, mFrequencyListener)
                .create();
        mFreqrency.show();
    }

    private DialogInterface.OnClickListener mStyleListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String styleStr = getResources().getStringArray(R.array.array_style)[which];
            mStyleText.setText(styleStr);
            NetService.setPreferences(getApplicationContext(),NetService.KEY_STYLE,which);
            NetService.instance().updateStyle(which);
        }
    };

    private void changeStyle() {
        int style = NetService.getPreferences(this,NetService.KEY_STYLE,NetService.STYLE_NTF);
        ExpandDialog mFreqrency = new ExpandDialog.Builder(this)
                .setTitle(R.string.style)
                .setGravity(Gravity.TOP)
                .setSingleChoiceItems(R.array.array_style, style, mStyleListener)
                .create();
        mFreqrency.show();
    }

}
