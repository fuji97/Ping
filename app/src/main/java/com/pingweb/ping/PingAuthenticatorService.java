package com.pingweb.ping;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PingAuthenticatorService extends Service {
    private static final String TAG = "PingAuthService";
    private PingAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Ping Authentication Service started.");
        }
        mAuthenticator = new PingAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Ping Authentication Service stopped.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG,
                    "getBinder()...  returning the PingAccountAuthenticator binder for intent "
                            + intent);
        }
        return mAuthenticator.getIBinder();
    }
}
