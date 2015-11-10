package com.pingweb.ping;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.pingweb.ping.com.pingweb.ping.views.*;

import java.util.ArrayList;

import fuji.ping.R;

public class SplashActivity extends AppCompatActivity implements AccountListDialog.AccountListListner {

    private static final String TAG = "SplashActivity";
    private String authToken;

    private ImageView logoPing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoPing = (ImageView) findViewById(R.id.logoPing);
        // Check if an authToken is saved
        //TODO

        // if an authToken do not exist, request a new token
        if (authToken == null) {
            final AccountManager mAccountManager = AccountManager.get(this);

            // Check if a Ping account exist on this device
            final Account[] accounts = mAccountManager.getAccountsByType(PingAuthenticator.ACCOUNT_TYPE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.account_picker_dialog_title));
            builder.setItems(null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }
}
