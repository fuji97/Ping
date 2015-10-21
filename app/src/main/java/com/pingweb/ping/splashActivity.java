package com.pingweb.ping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import fuji.ping.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView logoPing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoPing = (ImageView) findViewById(R.id.logoPing);
        final Intent intent = new Intent(this, LoginActivity.class);
        logoPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Starting LoginActivity");
                startActivity(intent);
            }
        });
    }
}
