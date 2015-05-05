package com.xandy.netspeed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class NetService extends Service {
    
    private static String TAG = "NetService";
    
    private OverFlow mOverFlow;
    private static final int UPDATE_NET_DATA = 0;
    
    private static final String LABEL_PREFERENCE   = "Preferences";
    
    public static final String KEY_FREQUENCY   = "frequency";
    public static final String KEY_AUTO_START  = "auto_start";
    public static final String KEY_STYLE       = "style";
    
    public static final int FREQUENCY_FAST    = 0;
    public static final int FREQUENCY_NORMAL  = 1;
    public static final int FREQUENCY_LOW     = 2;
    private static final int TIME_FACTOR      = 1;
    
    public static final int AUTO_START_ON  = 0;
    public static final int AUTO_START_OFF = 1;

    public static final int STYLE_NTF   = 0;
    public static final int STYLE_OVER  = 1;
    public static final int STYLE_BOTH  = 2;


    private static NetService mService;
    private static boolean mHasStartHandel = false;

    public static NetService instance() {
        return mService;
    }
    
    public static void setPreferences( Context context , String key , int value) {
    	SharedPreferences mPreferences = context.getApplicationContext()
    			.getSharedPreferences(LABEL_PREFERENCE, MODE_PRIVATE);
    	Editor mEditor = mPreferences.edit();
    	mEditor.putInt(key, value);
    	mEditor.commit();
    }
    
    public static int getPreferences( Context context , String key , int defaltValue) {
    	SharedPreferences mPreferences = context.getApplicationContext()
    			.getSharedPreferences(LABEL_PREFERENCE, MODE_PRIVATE);
    	return mPreferences.getInt(key, defaltValue);
    }

    private int mFrequency = FREQUENCY_NORMAL + TIME_FACTOR ;
    private int mStyle = STYLE_NTF;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( UPDATE_NET_DATA == msg.what ) {
            	long netData = ((Long) msg.obj );
            	String speedFmt = "";
                float speed = 0.f;
            	if( netData < 1000 * mFrequency ) {
                    speed = netData / mFrequency;
            		speedFmt = String.format("%.2f B/S", speed ) ;
                    speed = 0 ;//   小于1kb/s 默认算作 0kb/s
            	} else if( netData < 1000000 * mFrequency ) {
                    speed = netData/ ( mFrequency * 1000 );
            		speedFmt = String.format("%.2f K/S",  speed ) ;
            	} else {
                    speed = netData / (mFrequency * 1000000 );
            		speedFmt = String.format("%.2f M/S",  speed ) ;
            	}
                //mOverFlow.mSpeed.setText(speedFmt);
                //showNtfSpeed( (int)(speed / (1024 * 1024 * mFrequency)) );
                if( mStyle == STYLE_NTF ) {
                    showNtfSpeed( (int)speed , speedFmt);
                } else {
                    mOverFlow.updateSpeed(speedFmt);
                }
            }
        }
    };

    /**
     *
     * @param speed 当前网速，以kb/s为参数
     * @param speedFmt
     */
    public void showNtfSpeed( int speed ,String speedFmt) {
        NetNotification.showNotifacation(this,speed,speedFmt);
    }
    
    // 系统流量文件
    public final String NET_FILE = "/proc/self/net/dev";

    // 流量数据
    String[] ethData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0" };
    String[] gprsData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0", "0" };
    String[] wifiData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0", "0" };

    // 用来存储前一个时间点的数据
    String[] data = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };

    // 以太网
    final String ETHLINE = "eth0";
    // wifi
    final String WIFILINE = "wlan0";
    // gprs
    final String GPRSLINE = "rmnet0";

    /**
     * 定义线程周期性地获取网速
     */
    private Runnable mRunnable = new Runnable() {
        // 每隔一段时间获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
        @Override
        public void run() {
            Log.d(TAG, "mRunnable run");
            refreshNet();
            mHasStartHandel = true;
            mHandler.postDelayed(mRunnable, mFrequency * 1000 );
        }
    };

    /**
     * 在服务结束时删除消息队列
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
    	Log.d(TAG, "onCreate");
        super.onCreate();
        mService = this;
        initData();
    }

    private void initData() {
        mOverFlow = new OverFlow(this);
        mStyle = getPreferences(this,KEY_STYLE,STYLE_NTF);
        if( STYLE_NTF == mStyle ) {
            mOverFlow.removeFromWindow();
        } else if( STYLE_OVER == mStyle ) {
            mOverFlow.addToWindow();
        }

        mFrequency += TIME_FACTOR;
        mFrequency = getPreferences(this,KEY_FREQUENCY,FREQUENCY_NORMAL);

        readNetFile();
        refreshData();
        if( !mHasStartHandel ) {
            mHandler.postDelayed(mRunnable, 0);
            mHasStartHandel = true;
        }
    }

    public void updateFrequency( int frequency ) {
        mFrequency = frequency + TIME_FACTOR;
    }

    public void updateStyle( int style ) {
        if( mStyle == style ) {
            return;
        }
        mStyle = style;
        if( STYLE_NTF == style ) {
            mOverFlow.removeFromWindow();
        } else if( STYLE_OVER == style ) {
            mOverFlow.addToWindow();
            NetNotification.cancelNtf(this);
        }
    }

    /**
     * 读取系统流量文件
     */
    public synchronized void readNetFile() {
        Log.d(TAG, "readNetFile");
        FileReader netFile = null;
        try {
            netFile = new FileReader(NET_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }

        BufferedReader bufReader = new BufferedReader(netFile, 500);
        String line;
        String[] data_temp;
        String[] netData;
        int k;
        Pattern p = Pattern.compile("\\s+");
        // 读取文件，并对读取到的文件进行操作
        try {
            while ( (line = bufReader.readLine()) != null ) {
                data_temp = line.trim().split(":");
                if ( line.contains(ETHLINE) ) {
                	data_temp[1] = p.matcher(data_temp[1].trim()).replaceAll("-");
                	Log.d( TAG, "line = " + data_temp[1] );
                	netData = data_temp[1].split("-");
                    for (k = 0; k < ethData.length && k < netData.length; k++) {
                    	ethData[k] = netData[k];
                    	Log.d(TAG, "ethData " + ethData[k]);
                    }
                } else if ( line.contains(GPRSLINE) ) {
                	data_temp[1] = p.matcher(data_temp[1].trim()).replaceAll("-");
                	Log.d( TAG, "line = " + data_temp[1] );
                    netData = data_temp[1].split("-");
                    for (k = 0; k < gprsData.length; k++) {
                    	gprsData[k] = netData[k];
                    	Log.d(TAG, "gprsData " + gprsData[k]);
                    }
                } else if ( line.contains(WIFILINE) ) {
                	data_temp[1] = p.matcher(data_temp[1].trim()).replaceAll("-");
                	Log.d( TAG, "line = " + data_temp[1] );
                    netData = data_temp[1].split("-");
                    for (k = 0 ; k < wifiData.length; k++) {
                    	wifiData[k] = netData[k];
                    	Log.d(TAG, "wifiData " + wifiData[k]);
                    }
                }
            }
            netFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 实时读取系统流量文件，更新
     */
    private void refreshNet() {
        Log.d(TAG, "refreshNet");
        // 读取系统流量文件
        readNetFile();
        // 计算增量
        long[] delta = new long[12];
        delta[0]  = Long.parseLong(ethData[0]) - Long.parseLong(data[0]);
        delta[1]  = Long.parseLong(ethData[1]) - Long.parseLong(data[1]);
        delta[2]  = Long.parseLong(ethData[8]) - Long.parseLong(data[2]);
        delta[3]  = Long.parseLong(ethData[9]) - Long.parseLong(data[3]);
        
        delta[4]  = Long.parseLong(gprsData[0]) - Long.parseLong(data[4]);
        delta[5]  = Long.parseLong(gprsData[1]) - Long.parseLong(data[5]);
        delta[6]  = Long.parseLong(gprsData[8]) - Long.parseLong(data[6]);
        delta[7]  = Long.parseLong(gprsData[9]) - Long.parseLong(data[7]);
        
        delta[8]  = Long.parseLong(wifiData[0]) - Long.parseLong(data[8]);
        delta[9]  = Long.parseLong(wifiData[1]) - Long.parseLong(data[9]);
        delta[10] = Long.parseLong(wifiData[8]) - Long.parseLong(data[10]);
        delta[11] = Long.parseLong(wifiData[9]) - Long.parseLong(data[11]);
        
        refreshData();

        // 下载的字节数
        long netData = delta[0] + delta[4] + delta[8];
        Message msg = mHandler.obtainMessage();
        msg.what = UPDATE_NET_DATA;
        msg.obj = netData;
        mHandler.sendMessage(msg);
    }
    
    private void refreshData() {
    	data[0] = ethData[0];
        data[1] = ethData[1];
        data[2] = ethData[8];
        data[3] = ethData[9];
        data[4] = gprsData[0];
        data[5] = gprsData[1];
        data[6] = gprsData[8];
        data[7] = gprsData[9];
        data[8] = wifiData[0];
        data[9] = wifiData[1];
        data[10] = wifiData[8];
        data[11] = wifiData[9];
    }
}
