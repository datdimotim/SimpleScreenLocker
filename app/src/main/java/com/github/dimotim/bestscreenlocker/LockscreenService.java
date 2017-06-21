package com.github.dimotim.bestscreenlocker;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

//Service for contaning screen off reciever
public class LockscreenService extends Service {
    public static final String TAG = "LockscreenService";
    private final BroadcastReceiver mLockscreenReceiver = new Receiver();
    private SweepFragment sweepFragment=null;
    private KeyguardManager mKeyManager = null;
    private KeyguardManager.KeyguardLock mKeyLock = null;

    public static void startLockscreenService(Context context) {
        context.startService(new Intent(context, LockscreenService.class));
    }

    public static void stopLockscreenService(Context context) {
        context.stopService(new Intent(context, LockscreenService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stateRecever(true);
        showLockScreenFragment();
        setLockGuard();
        return LockscreenService.START_STICKY;
    }

    @Override
    public void onDestroy() {
        hideLockScreenFragment();
        stateRecever(false);
        setStandardKeyguardState(true);
    }

    private void stateRecever(boolean isStartRecever) {
        if (isStartRecever) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mLockscreenReceiver, filter);
        } else unregisterReceiver(mLockscreenReceiver);
    }

    private void setLockGuard() {
        initKeyguardService();
        setStandardKeyguardState(isStandardKeyguardState(this));
    }

    private static boolean isStandardKeyguardState(Context context) {
        boolean isStandardKeyguqrd = false;
        KeyguardManager keyManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (null != keyManager) isStandardKeyguqrd = keyManager.isKeyguardSecure();
        return isStandardKeyguqrd;
    }

    private void initKeyguardService() {
        if (null != mKeyManager) {
            mKeyManager = null;
        }
        mKeyManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (null != mKeyManager) {
            if (null != mKeyLock) {
                mKeyLock = null;
            }
            mKeyLock = mKeyManager.newKeyguardLock(KEYGUARD_SERVICE);
        }
    }

    private void setStandardKeyguardState(boolean isStart) {
        if (isStart) {
            if (null != mKeyLock) mKeyLock.reenableKeyguard();
        } else {
            if (null != mKeyManager) mKeyLock.disableKeyguard();
        }
    }

    private void showLockScreenFragment() {
        if(sweepFragment==null)sweepFragment=new SweepFragment(this);
        sweepFragment.show();
    }
    private void hideLockScreenFragment(){
        if(sweepFragment==null)return;
        sweepFragment.hide();
    }
    private class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) showLockScreenFragment();
        }
    }
}
