package com.pingweb.ping;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

import fuji.ping.R;

/**
 * Created by Federico on 17/09/2015.
 */
public class PingAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;

    public static final String ACCOUNT_TYPE = "com.pingweb.ping.account";
    public static final String TAG = "PingAuthenticator";
    public static final String ARG_IS_ADDING_ACCOUNT = "com.pingweb.ping.adding";
    public static final String ARG_AUTHTOKEN_TYPE = "com.pingweb.ping.authtoken_type";

    public PingAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(TAG, "editProperties()");
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(ARG_IS_ADDING_ACCOUNT, false);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "addAccount()");
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (authTokenType == null) {
            authTokenType = AuthTypeParser.FULL_ACCESS;
        }
        intent.putExtra(ARG_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(ARG_IS_ADDING_ACCOUNT, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials()");
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNTS, account);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken()");
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        final String accountName = am.getUserData(account, AccountManager.KEY_ACCOUNT_NAME);
        String authToken = am.peekAuthToken(account, authTokenType);
        final Bundle bundle = new Bundle();

        if (authToken == null) {
            Log.d(TAG, "AuthToken not valid, requesting new token");
            final String password = am.getPassword(account);
            if (password == null) {
                Log.d(TAG,"No password saved in the account, starting LoginActivity for request it");
                final Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                intent.putExtra(ARG_AUTHTOKEN_TYPE, authTokenType);
                bundle.putParcelable(AccountManager.KEY_INTENT, intent);
                return bundle;
            }
            final Intent serverResponse = ServerInterface.getAuthToken(accountName,password,authTokenType);
            final int serverResult = serverResponse.getIntExtra(ServerInterface.RESULT, ServerInterface.ERROR_GENERAL);
            if (serverResult == ServerInterface.RESULT_OK) {
                authToken = serverResponse.getStringExtra(ServerInterface.TOKEN);
                Log.d(TAG, "Token retrieved: " + authToken);
            } else {
                final String errorMessage = serverResponse.getStringExtra(ServerInterface.ERROR_MESSAGE);
                Log.d(TAG, "Error while retrieving token - Error code: " + serverResult + " - Error message: " + errorMessage);
                final Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                intent.putExtra(ARG_AUTHTOKEN_TYPE, authTokenType);
                intent.putExtra(AccountManager.KEY_ACCOUNTS, account);
                intent.putExtra(AccountManager.KEY_ERROR_CODE, serverResult);
                intent.putExtra(AccountManager.KEY_ERROR_MESSAGE, errorMessage);
                intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                bundle.putParcelable(AccountManager.KEY_INTENT, intent);
                return bundle;
            }
        }
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        String[] permissions = AuthTypeParser.parse(authTokenType);
        if(Arrays.asList(permissions).contains(AuthTypeParser.FULL_ACCESS)) {
            return "Full data access";
        } else {
            return "Specific data access (under developement)";
        }
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(mContext);
        final String accountName = am.getUserData(account, AccountManager.KEY_ACCOUNT_NAME);
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNTS, account);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        intent.putExtra(ARG_AUTHTOKEN_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        /*
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
        */
        throw new UnsupportedOperationException();
    }
}
