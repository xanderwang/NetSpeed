package com.xandy.netspeed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetService extends Service {
    
    private static String TAG = "NetService";
    
    private OverFlow mOverFlow;
    private static final int UPDATE_NET_DATA = 0;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( UPDATE_NET_DATA == msg.what ) {
                mOverFlow.mShow.setText((float) (msg.arg1 / (1024 * 3)) + "k/s");
            }
        }
    };
    
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
        // 每3秒钟获取一次数据，求平均，以减少读取系统文件次数，减少资源消耗
        @Override
        public void run() {
            Log.d(TAG, "mRunnable run");
            refreshNet();
            mHandler.postDelayed(mRunnable, 4000);
        }
    };

    /**
     * 启动服务时就开始启动线程获取网速
     */
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart");
        mHandler.postDelayed(mRunnable, 0);
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
        super.onCreate();
        Log.d(TAG, "onCreate");
        mOverFlow = new OverFlow(this);
        mOverFlow.show();
    }

    /**
     * 读取系统流量文件
     */
    public void readNetFile() {
        Log.d(TAG, "readNetFile");
        FileReader netFile = null;
        try {
            netFile = new FileReader(NET_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "系统流量文件读取失败", Toast.LENGTH_SHORT).show();
        }

        BufferedReader bufReader = new BufferedReader(netFile, 500);
        String line;
        String[] data_temp;
        String[] netData;
        int k;
        int j;
        // 读取文件，并对读取到的文件进行操作
        try {
            while ( (line = bufReader.readLine()) != null ) {
                data_temp = line.trim().split(":");
                if ( line.contains(ETHLINE) ) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < ethData.length; k++) {
                        if (netData[k].length() > 0) {
                            ethData[j] = netData[k];
                            j++;
                        }
                    }
                } else if ( line.contains(GPRSLINE) ) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < gprsData.length; k++) {
                        if (netData[k].length() > 0) {
                            gprsData[j] = netData[k];
                            j++;
                        }
                    }
                } else if ( line.contains(WIFILINE) ) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < wifiData.length; k++) {
                        wifiData[j] = netData[k];
                        j++;
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
    public void refreshNet() {
        Log.d(TAG, "refreshNet");
        // 读取系统流量文件
        readNetFile();
        // 计算增量
        int[] delta = new int[12];
        delta[0] = Integer.parseInt(ethData[0]) - Integer.parseInt(data[0]);
        delta[1] = Integer.parseInt(ethData[1]) - Integer.parseInt(data[1]);
        delta[2] = Integer.parseInt(ethData[8]) - Integer.parseInt(data[2]);
        delta[3] = Integer.parseInt(ethData[9]) - Integer.parseInt(data[3]);
        delta[4] = Integer.parseInt(gprsData[0]) - Integer.parseInt(data[4]);
        delta[5] = Integer.parseInt(gprsData[1]) - Integer.parseInt(data[5]);
        delta[6] = Integer.parseInt(gprsData[8]) - Integer.parseInt(data[6]);
        delta[7] = Integer.parseInt(gprsData[9]) - Integer.parseInt(data[7]);
        delta[8] = Integer.parseInt(wifiData[0]) - Integer.parseInt(data[8]);
        delta[9] = Integer.parseInt(wifiData[1]) - Integer.parseInt(data[9]);
        delta[10] = Integer.parseInt(wifiData[8]) - Integer.parseInt(data[10]);
        delta[11] = Integer.parseInt(wifiData[9]) - Integer.parseInt(data[11]);

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

        // 每秒下载的字节数
        int netData = delta[0] + delta[4] + delta[8];
        Message msg = mHandler.obtainMessage();
        msg.what = UPDATE_NET_DATA;
        msg.arg1 = netData;
        mHandler.sendMessage(msg);
    }
}
