package com.xandy.netspeed;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class OverFlow {
    WindowManager mWManger;
    WindowManager.LayoutParams mWManParams;
    public View mOverFlowView;
    public float mStateBarHeight = 20;
    
    //坐标
    private float x;
    private float y;
    
    private float mTouchStartX;
    private float mTouchStartY;
    
    //组件
    public ImageView mIcon;
    public TextView mShow;
    
    Context mContext;
    
    public OverFlow(Context context) {
        this.mContext = context;
        initView();
    }
    
    /**
     * 初始化mWManger,mWManParams
     */
    private void initView() {
    	mWManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    	mWManParams = new WindowManager.LayoutParams();
    	//设置LayoutParams的参数
    	//设置系统级窗口
    	mWManParams.type = LayoutParams.TYPE_PHONE ; 
    	mWManParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE ;
    	//调整悬浮窗到左上角
    	mWManParams.gravity = Gravity.TOP | Gravity.LEFT;
    	//以屏幕左上角为源点，设置x，y
    	mWManParams.x = 0;
    	mWManParams.y = 0;
    	//悬浮窗的长宽数据
    	mWManParams.width = LayoutParams.WRAP_CONTENT;
    	mWManParams.height = LayoutParams.WRAP_CONTENT;
    	mWManParams.format = PixelFormat.RGBA_8888;//透明
    	//加载悬浮窗布局文件
    	mOverFlowView = LayoutInflater.from(mContext).inflate(R.layout.overflow, null);
    	mOverFlowView.setOnTouchListener(new OnTouchListener() {
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    			//获取相对屏幕的位置，即以屏幕左上角为原点
    			x = event.getRawX();
    			y = event.getRawY();
    			switch(event.getAction()){
    			case MotionEvent.ACTION_DOWN :
    				//获取相对View的坐标,以view的左上角为原点
    				mTouchStartX = event.getX();
    				mTouchStartY = event.getY();
    				break;
    			case MotionEvent.ACTION_MOVE :
    				break;
    			case MotionEvent.ACTION_UP :
    				break;
    			}
    			updatePosition();
    			return true;
    		}
    	});
    	mShow = (TextView) mOverFlowView.findViewById(R.id.tv_show);
    	Rect frame = new Rect();
        mOverFlowView.getWindowVisibleDisplayFrame(frame);
        // 状态栏高度
        mStateBarHeight = frame.top;
    }
    public void show() {
    	mWManger.addView(mOverFlowView, mWManParams);
    }
    
    /**
     * 更新悬浮窗的位置
     */
    public void updatePosition(){
        mWManParams.x = (int) ( x - mTouchStartX );
        mWManParams.y = (int) ( y - mTouchStartY - mStateBarHeight );
        mWManger.updateViewLayout(mOverFlowView, mWManParams);
    }
}
