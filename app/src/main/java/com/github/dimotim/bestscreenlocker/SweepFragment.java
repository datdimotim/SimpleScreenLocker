package com.github.dimotim.bestscreenlocker;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

class SweepFragment{
    private static final String TAG="ScreenLocker";
    private final View stub;
    private final View root;
    private final WindowManager.LayoutParams stubParams;
    private final WindowManager.LayoutParams rootParams;
    private final WindowManager windowManager;
    private final int yTreshold;
    private boolean isShow=false;

    SweepFragment(Context context){
        rootParams = initRootLayout();
        stubParams= initStubLayout(context);
        root= LayoutInflater.from(context).inflate(R.layout.main,null);
        stub = new CustomViewGroup(context);
        windowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        yTreshold=initYTteshold();
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        root.findViewById(R.id.apple).setOnTouchListener(new OnTouchListener());
    }

    void show() {
        if(isShow)return;
        isShow=true;
        Log.i(TAG, "SweepFragment: show");
        windowManager.addView(root, rootParams);
        windowManager.addView(stub, stubParams);
    }

    void hide(){
        if(!isShow)return;
        isShow=false;
        Log.i(TAG, "SweepFragment: hide");
        windowManager.removeView(root);
        windowManager.removeView(stub);
    }

    private static class CustomViewGroup extends ViewGroup {
        public CustomViewGroup(Context context) {
            super(context);
        }
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    private class OnTouchListener implements View.OnTouchListener{
        float dX,dY;
        float xs,ys;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                xs=v.getX();
                ys=v.getY();
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(0).start();
                return true;
            }
            if(event.getAction()== MotionEvent.ACTION_MOVE) {
                v.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                return true;
            }

            if(event.getAction()== MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL){
                int y_cord = (int) event.getRawY();

                if(y_cord>yTreshold){
                    v.animate().x(xs).y(ys).scaleX(1).scaleY(1).setDuration(0).start();
                    hide();
                    return true;
                }
                v.animate().x(xs).y(ys).scaleX(1).scaleY(1).setDuration(100).start();
                return true;
            }
            return true;
        }
    }

    private int initYTteshold(){
        Point size=new Point();
        windowManager.getDefaultDisplay().getSize(size);
        Log.i(TAG,"treshold="+size.y*9/10);
        return size.y*9/10;
    }

    private static WindowManager.LayoutParams initStubLayout(Context context){
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * context.getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        return localLayoutParams;
    }

    private static WindowManager.LayoutParams initRootLayout(){
        WindowManager.LayoutParams rootParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        rootParams.gravity = Gravity.TOP;
        rootParams.screenOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        return rootParams;
    }
}

