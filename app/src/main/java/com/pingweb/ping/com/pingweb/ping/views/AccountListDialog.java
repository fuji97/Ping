package com.pingweb.ping.com.pingweb.ping.views;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import fuji.ping.R;

/**
 * Created by Federico on 10/11/2015.
 */
public class AccountListDialog extends DialogFragment {
    public interface AccountListListner {
        void onClick(DialogFragment dialog, int which);
    }

    static public AccountListDialog newInstance(Account[] accounts) {
        AccountListDialog f = new AccountListDialog();

        Bundle args = new Bundle();
        args.putParcelableArray("accounts", accounts);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.account_picker_dialog_title));

        return builder.create();
    }

    // Use this instance of the interface to deliver action events
    AccountListListner mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AccountListListner) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
