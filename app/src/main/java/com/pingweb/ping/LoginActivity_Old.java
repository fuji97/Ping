package com.pingweb.ping;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import fuji.ping.R;

public class LoginActivity_Old extends Activity {
    public static final String PARAM_CONFIRMCREDENTIALS = "com.pingweb.ping.confirmCredentials";
    public static final String PARAM_PASSWORD = "com.pingweb.ping.password";
    public static final String PARAM_USERNAME = "com.pingweb.ping.username";
    public static final String PARAM_AUTHTOKEN_TYPE = "com.pingweb.ping.authtokenType";

    private static final String TAG = "LoginActivity_Old";

    private AccountManager mAccountManager;
    private Thread mAuthThread;
    private String mAuthtoken;
    private String mAuthtokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    /** for posting authentication attempts back to UI thread */
    private final Handler mHandler = new Handler();
    private TextView mMessage;
    private String mPassword;
    private EditText mPasswordEdit;

    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;

    private String mUsername;
    private EditText mUsernameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(" + savedInstanceState + ")");
        super.onCreate(savedInstanceState);
        mAccountManager = AccountManager.get(this);


        setContentView(R.layout.activity_login_old);

        // Loading things
        Log.d(TAG, "Loading resource");
        //TODO

        // Trying login
        //TODO

        // If login failed, show login field
        Log.d(TAG, "Showing login fields");
        //TODO

        // Finally, try to login
        //TODO
    }

}
