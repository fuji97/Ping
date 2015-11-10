package com.pingweb.ping;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fuji.ping.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    private static final String TAG = "AuthenticatorActivity";
    private AccountManager mAccountManager;
    private Thread mAuthThread;
    private String mAuthToken;
    private String mAuthTokenType;
    private String mPassword;
    private String mUsername;
    private String mAccountType;
    private Account mAccount;
    private boolean resultOk;
    private Intent mResponse;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    /** for posting authentication attempts back to UI thread */
    private final Handler mHandler = new Handler();

    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(" + savedInstanceState + ")");
        super.onCreate(savedInstanceState);
        resultOk = false;
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Log.d(TAG, "Starting checking intent");
        mAccountManager = AccountManager.get(this);
        final Intent intent = getIntent();

        // Extract need data from intent
        mAuthTokenType = intent.getStringExtra(PingAuthenticator.ARG_AUTHTOKEN_TYPE);
        mAccountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        mAccount = intent.getParcelableExtra(AccountManager.KEY_ACCOUNTS);

        // Checking intent for optional operation
        mUsername = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (!TextUtils.isEmpty(mUsername)) {
            Log.i(TAG, "Email found, setting textbox to " + mUsername);
            mEmailView.setText(mUsername);
        }

        final int errorCode = intent.getIntExtra(AccountManager.KEY_ERROR_CODE, -1);
        if (errorCode != -1) {
            // Error code found, retrieve message and make toast
            final String errorMessage = intent.getStringExtra(AccountManager.KEY_ERROR_MESSAGE);
            Log.i(TAG, "Error during server login, error code: " + errorCode + " - Error message: " + errorMessage);
            final Context context = getApplicationContext();
            final int duration = Toast.LENGTH_LONG;
            String toastText;
            switch (errorCode) {
                case ServerInterface.ERROR_CONNECTION:
                    toastText = getString(R.string.connection_error);
                case ServerInterface.WRONG_CREDENTIALS:
                    toastText = getString(R.string.wrong_credentials);
                default:
                    toastText = getString(R.string.general_error) + errorMessage;
            }
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername= mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mUsername)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(mUsername, mPassword);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 2;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;

    }
    public void finishLogin(Intent response) {
        final int resultCode = response.getIntExtra(ServerInterface.RESULT, -1);
        Log.d(TAG, "finishLogin() with result code: " + resultCode);
        if (resultCode == ServerInterface.RESULT_OK) {
            if (mAccount != null) {
                final String accountName = mAccountManager.getUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME);
                if (!TextUtils.equals(accountName, mUsername)) {
                    final String currentAccountType = mAccountManager.getUserData(mAccount, AccountManager.KEY_ACCOUNT_TYPE);
                    final String currentTokenType = mAccountManager.getUserData(mAccount, PingAuthenticator.ARG_AUTHTOKEN_TYPE);
                    if (Build.VERSION.SDK_INT >= 22) {
                        mAccountManager.removeAccountExplicitly(mAccount);
                    } else {
                        AccountManagerFuture<Boolean> deleteResult = mAccountManager.removeAccount(mAccount,null,null);
                    }
                    mAccount = new Account(mUsername, currentAccountType);
                    Bundle bundle = new Bundle();
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    bundle.putString(PingAuthenticator.ARG_AUTHTOKEN_TYPE, mAuthToken);
                    mAccountManager.addAccountExplicitly(mAccount, mPassword, bundle);
                    //TODO
                } else {
                    mAccountManager.setPassword(mAccount,mPassword);
                }
            } else {
                mAccount = new Account(mUsername, mAccountType);
                Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, mUsername);
                bundle.putString(PingAuthenticator.ARG_AUTHTOKEN_TYPE, mAuthToken);
                mAccountManager.addAccountExplicitly(mAccount, mPassword, bundle);
            }
            setAccountAuthenticatorResult(getIntent().getExtras());
            setResult(RESULT_OK, response);
            finish();
        } else if (resultCode != -1) {
            // Error code found, retrieve message and make toast
            final String errorMessage = response.getStringExtra(ServerInterface.ERROR_MESSAGE);
            Log.i(TAG, "Error during server login, error code: " + resultCode + " - Error message: " + errorMessage);
            final Context context = getApplicationContext();
            final int duration = Toast.LENGTH_LONG;
            String toastText;
            switch (resultCode) {
                case ServerInterface.ERROR_CONNECTION:
                    toastText = getString(R.string.connection_error);
                    break;
                case ServerInterface.WRONG_CREDENTIALS:
                    toastText = getString(R.string.wrong_credentials);
                    break;
                default:
                    toastText = getString(R.string.general_error) + errorMessage;
            }
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            showProgress(false);
        } else {
            Log.wtf(TAG,"Missing result code",new IllegalArgumentException());
            finish();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    @TargetApi(14)
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    //TODO remove useless parameters
    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String email;
        private final String password;

        UserLoginTask(String mEmail, String mPassword) {
            Log.d(TAG, "Started AsyncTask for login operation");
            email = mEmail;
            password = mPassword;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            final Intent serverResponse = ServerInterface.confirmCredentials(email,password);
            return serverResponse;
        }

        @Override
        protected void onPostExecute(final Intent response) {
            Log.d(TAG, "Request completed, return response to the main thread");
            finishLogin(response);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

