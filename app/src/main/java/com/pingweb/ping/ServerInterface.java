package com.pingweb.ping;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Federico on 15/09/2015.
 */
public class ServerInterface {
    public final static String RESULT = "result";
    public final static String TOKEN = "token";

    public final static int ERROR_GENERAL = 0;
    public final static int RESULT_OK = 1;
    public final static int ERROR_CONNECTION = 2;
    public final static int WRONG_CREDENTIALS = 3;

    // Dummy data
    public final static String DUMMY_TOKEN = "000000000000";
    public final static String DUMMY_USERNAME = "dummy";
    public final static String DUMMY_PASSWORD = "password";

    private static final String TAG = "ServerInterface";

    public final static boolean confirmLogin(String mUsername, String mPassword, String tokenType) {
        return TextUtils.equals(mUsername,DUMMY_USERNAME) && TextUtils.equals(mPassword,DUMMY_PASSWORD) ? true : false;
        // TODO
    }

    public final static boolean confirmLogin(String mToken, String tokenType) {
        return TextUtils.equals(mToken,DUMMY_TOKEN) ? true : false;
        // TODO
    }

    public final static Intent getAuthToken(String mUsername, String mPassword, String tokenType) {
        final Intent intent = new Intent();
        if(TextUtils.equals(mUsername,DUMMY_USERNAME) && TextUtils.equals(mPassword,DUMMY_PASSWORD)) {
            intent.putExtra(RESULT, RESULT_OK);
            intent.putExtra(TOKEN, DUMMY_TOKEN);
        } else {
            intent.putExtra(RESULT, WRONG_CREDENTIALS);
        }
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

    public ServerInterface() {
        setUsername("");
        setPassword("");
        setGoogleSign("");
        setFacebookSign("");
        Log.d(TAG,"Empty ServerInterface created");
    }

    public ServerInterface(String mUsername, String mPassword) {
        setUsername(mUsername);
        setPassword(mPassword);
        Log.d(TAG, "ServerInterface created with username: " + mUsername);
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
