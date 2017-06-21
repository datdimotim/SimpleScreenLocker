package com.github.dimotim.bestscreenlocker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

// Simple UI Launcher-Stopper, presents launcher icon
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonStartLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockscreenService.startLockscreenService(MainActivity.this);
                finish();
            }
        });
        findViewById(R.id.buttonStopLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LockscreenService.stopLockscreenService(MainActivity.this);
                finish();
            }
        });
    }
}