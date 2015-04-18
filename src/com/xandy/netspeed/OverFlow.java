package com.xandy.netspeed;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class OverFlow {
    WindowManager mWManger;
    WindowManager.LayoutParams mWManParams;
    
    public View mOverFlowView;
    
    //初始位置
    private float startX;
    private float startY;
    
    //坐标
    private float x;
    private float y;
    
    private float mTouchSatrtX;
    private float mTouchStartY;
    
    //组件
    public ImageView mIcon,mClose;
    public TextView mShow;
    
    Context mContext;
    
    public OverFlow(Context context) {
        //super(context);
        this.mContext = context;
    }
    /**
     * 初始化mWManger,mWManParams
     */
    public void show(){
        mWManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWManParams = new WindowManager.LayoutParams();
        
        //设置LayoutParams的参数
        mWManParams.type = 2002;//设置系统级窗口
        mWManParams.flags |= 8;
        //调整悬浮窗到左上角
        mWManParams.gravity = Gravity.TOP|Gravity.LEFT;
        
        //以屏幕左上角为源点，设置x，y
        mWManParams.x = 0;
        mWManParams.y = 0;
        
        //悬浮窗的长宽数据
        mWManParams.width = 80;
        mWManParams.height = 40;
        mWManParams.format = -3;//透明
        
        //加载悬浮窗布局文件
        mOverFlowView = LayoutInflater.from(mContext).inflate(R.layout.overflow, null);
        mWManger.addView(mOverFlowView, mWManParams);
        mOverFlowView.setOnTouchListener(new OnTouchListener() {
            /**
             * 改变悬浮窗位置
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取相对屏幕的位置，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY();
                
                switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = x;
                    startY = y;
                    //获取相对View的坐标,以view的左上角为原点
                    mTouchSatrtX = event.getX();
                    mTouchStartY = event.getY();
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    updatePosition();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    updatePosition();
                    showCloseView();
                    mTouchSatrtX = mTouchStartY = 0;
                    break;
                }
                return true;
            }
        });
        
        mClose = (ImageView) mOverFlowView.findViewById(R.id.img_close);
        /**
         * 关闭悬浮窗图标点击事件
         */
        /*
        mClose.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,NetService.class);
                mContext.stopService(intent);
                mOverFlowView.setVisibility(View.GONE);
            }
        });
        */
        mShow = (TextView) mOverFlowView.findViewById(R.id.tv_show);
    }
    
    /**
     * 更新悬浮窗的位置
     */
    public void updatePosition(){
        mWManParams.x = (int) (x - mTouchSatrtX);
        mWManParams.y = (int) (y - mTouchStartY);
        mWManger.updateViewLayout(mOverFlowView, mWManParams);
    }
    
    /**
     * 控制关闭悬浮窗按钮的显示与隐藏
     */
    public void showCloseView() {
        if (Math.abs(x - startX) < 1.5 && Math.abs(y - startY) < 1.5
                && !mClose.isShown()) {
            mClose.setVisibility(View.VISIBLE);
        } else if (mClose.isShown()) {
            mClose.setVisibility(View.GONE);
        }
    }
}
