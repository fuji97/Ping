package com.pingweb.ping;

import android.content.Intent;
import android.util.Log;

/**
 * Created by Federico on 15/09/2015.
 */
public class LoginClass {
    public final static String RESULT = "result";
    public final static String TOKEN = "token";

    public final static String RESULT_OK = "result_ok";
    public final static String ERROR_CONNECTION = "error_connection";
    public final static String WRONG_CREDENTIALS = "wrong_credentials";

    private static final String TAG = "LoginClass";

    public final static boolean confirmLogin(String mUsername, String mPassword) {
        // TODO
        return true;
    }

    public final static boolean confirmLogin(String mToken) {
        // TODO
        return true;
    }

    public final static Intent getAuthToken(String mUsername, String mPassword) {
        final Intent intent = new Intent();
        // TODO
        return intent;
    }
}

/*
    *** OLD VERSION ***
    private String username;

    private String password;
    private String googleSign;
    private String facebookSign;
    private PingUser loggedUser;

    public LoginClass() {
        setUsername("");
        setPassword("");
        setGoogleSign("");
        setFacebookSign("");
        Log.d(TAG,"Empty LoginClass created");
    }

    public LoginClass(String mUsername, String mPassword) {
        setUsername(mUsername);
        setPassword(mPassword);
        Log.d(TAG, "LoginClass created with username: " + mUsername);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoogleSign() {
        return googleSign;
    }

    public void setGoogleSign(String googleSign) {
        this.googleSign = googleSign;
    }

    public String getFacebookSign() {
        return facebookSign;
    }

    public void setFacebookSign(String facebookSign) {
        this.facebookSign = facebookSign;
    }

    public String tryPingAuthenticate() {
        Log.d(TAG,"Authenticate via Ping user started");
        loggedUser = new PingUser();
        return RESULT_OK;
    }

    public PingUser getUser() {
        if (loggedUser == null) {
            Log.e(TAG, "User not logged");
            return null;
        } else {
            return loggedUser;
        }
    }
}
*/