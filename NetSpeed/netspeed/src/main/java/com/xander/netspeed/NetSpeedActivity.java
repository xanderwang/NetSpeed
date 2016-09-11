package com.xander.netspeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xander.panel.PanelInterface;
import com.xander.panel.XanderPanel;

public class NetSpeedActivity extends Activity
        implements View.OnClickListener, PanelInterface.PanelMenuListener {

    private static final String TAG = "NetSpeedActivity";

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
        setContentView(com.xander.netspeed.R.layout.activity_net_speed);

        initView();

        startService(this);
    }

    public void initView() {

        mRowAutoOn = findViewById(com.xander.netspeed.R.id.row_auto_on);
        mRowAutoOn.setOnClickListener(this);
        mAutoOn = (CheckBox) findViewById(com.xander.netspeed.R.id.checkbox_auto_on);

        mRowFrequency = findViewById(com.xander.netspeed.R.id.row_frequency);
        mRowFrequency.setOnClickListener(this);
        mFrequencyText = (TextView) findViewById(com.xander.netspeed.R.id.frequency_value);
        int which = NetSpeedService.getPreferences(
                this,
                NetSpeedService.KEY_FREQUENCY,
                NetSpeedService.FREQUENCY_NORMAL
        );
        String Str = getResources().getStringArray(com.xander.netspeed.R.array.array_frequency)[which];
        mFrequencyText.setText(Str);

        mRowStyle = findViewById(com.xander.netspeed.R.id.row_style);
        mRowStyle.setOnClickListener(this);
        mStyleText = (TextView) findViewById(com.xander.netspeed.R.id.style_value);
        which = NetSpeedService.getPreferences(
                this,
                NetSpeedService.KEY_STYLE,
                NetSpeedService.STYLE_NTF
        );
        Str = getResources().getStringArray(com.xander.netspeed.R.array.array_style)[which];
        mStyleText.setText(Str);

    }

    private void startService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, NetSpeedService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        if (mRowAutoOn == v) {
            mAutoOn.toggle();
            int mode = mAutoOn.isChecked() ? NetSpeedService.AUTO_START_ON : NetSpeedService.AUTO_START_OFF;
            NetSpeedService.setPreferences(this, NetSpeedService.KEY_AUTO_START, mode);
        } else if (mRowFrequency == v) {
            changeFrequency();
        } else if (mRowStyle == v) {
            changeStyle();
        }
    }


    private void changeFrequency() {
        new XanderPanel.Builder(this)
                .list()
                .setGravity(Gravity.TOP)
                .setMenu(com.xander.netspeed.R.menu.frequency, this)
                .show();
    }


    private void changeStyle() {
        new XanderPanel.Builder(this)
                .list()
                .setGravity(Gravity.TOP)
                .setMenu(com.xander.netspeed.R.menu.show_style, this)
                .show();
    }

    @Override
    public void onMenuClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case com.xander.netspeed.R.id.action_frequency_fast:
                applyFrequency(0);
                break;
            case com.xander.netspeed.R.id.action_frequency_normal:
                applyFrequency(1);
                break;
            case com.xander.netspeed.R.id.action_frequency_low:
                applyFrequency(2);
                break;
            case com.xander.netspeed.R.id.action_show_ntf:
                applyShowStyle(0);
                break;
            case com.xander.netspeed.R.id.action_show_over:
                applyShowStyle(1);
                break;
        }
    }

    private void applyFrequency(int which) {
        String frequencyStr = getResources().getStringArray(com.xander.netspeed.R.array.array_frequency)[which];
        mFrequencyText.setText(frequencyStr);
        NetSpeedService.setPreferences(getApplicationContext(), NetSpeedService.KEY_FREQUENCY, which);
        NetSpeedService.instance().updateFrequency(which);
    }

    private void applyShowStyle(int which) {
        String styleStr = getResources().getStringArray(com.xander.netspeed.R.array.array_style)[which];
        mStyleText.setText(styleStr);
        NetSpeedService.setPreferences(getApplicationContext(), NetSpeedService.KEY_STYLE, which);
        NetSpeedService.instance().updateStyle(which);
    }
}
