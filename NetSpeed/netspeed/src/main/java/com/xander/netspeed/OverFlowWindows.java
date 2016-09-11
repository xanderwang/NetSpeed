package com.xander.netspeed;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class OverFlowWindows {
	private static final String TAG = "OverFlowWindows";
	
    WindowManager windowManager;
    LayoutParams windowsParams;
    public View mOverFlowView;
    public float mStateBarHeight = 20;
    
    //坐标
    private float rawX;
    private float rawY;
    
    private float mTouchStartX;
    private float mTouchStartY;
    
    //组件
    public ImageView mIcon;
    public TextView mSpeed;

	private boolean mHasAdd = false;
    
    Context mContext;
    
    public OverFlowWindows(Context context) {
        this.mContext = context;
        initView();
    }
    
    /**
     * 初始化mWManger,windowsParams
     */
    private void initView() {
    	windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    	windowsParams = new LayoutParams();
    	//设置LayoutParams的参数
    	//设置系统级窗口
    	windowsParams.type = LayoutParams.TYPE_TOAST ;
    	windowsParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE ;
    	windowsParams.type = LayoutParams.TYPE_SYSTEM_ERROR ;
    	windowsParams.flags |= LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_IN_SCREEN  ;
    	//调整悬浮窗到左上角
    	windowsParams.gravity = Gravity.TOP | Gravity.LEFT;
    	//以屏幕左上角为源点，设置x，y
    	windowsParams.x = 0;
    	windowsParams.y = 0;
    	//悬浮窗的长宽数据
    	windowsParams.width = LayoutParams.WRAP_CONTENT;
    	windowsParams.height = LayoutParams.WRAP_CONTENT;
    	windowsParams.format = PixelFormat.RGBA_8888;//透明
    	//加载悬浮窗布局文件
    	mOverFlowView = LayoutInflater.from(mContext).inflate(com.xander.netspeed.R.layout.overflow, null);
    	mOverFlowView.setOnTouchListener(new OnTouchListener() {
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    			//获取相对屏幕的位置，即以屏幕左上角为原点
    			rawX = event.getRawX();
    			rawY = event.getRawY();
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
    	mSpeed = (TextView) mOverFlowView.findViewById(com.xander.netspeed.R.id.tv_show);
    }
    
    private void updateStateBarHeight() {
    	Rect frame = new Rect();
    	mOverFlowView.getWindowVisibleDisplayFrame(frame);
    	// 状态栏高度
    	mStateBarHeight = frame.top ;
    }
    
    public void addToWindow() {
    	windowManager.addView(mOverFlowView, windowsParams);
		mHasAdd = true;
    }

	public void removeFromWindow() {
		if( mHasAdd ) {
			windowManager.removeView(mOverFlowView);
			mHasAdd = false;
		}
	}
    
    public void show( ) {
    	updateStateBarHeight();
    	Log.d(TAG, "mStateBarHeight = " + mStateBarHeight);
    	boolean show = (0.f != mStateBarHeight);
    	mOverFlowView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    public void updateSpeed(String speed) {
    	mSpeed.setText(speed);
    }
    
    /**
     * 更新悬浮窗的位置
     */
    private void updatePosition() {
        windowsParams.x = (int) ( rawX - mTouchStartX );
        windowsParams.y = (int) ( rawY - mTouchStartY  );
        windowManager.updateViewLayout(mOverFlowView, windowsParams);
    }
}
