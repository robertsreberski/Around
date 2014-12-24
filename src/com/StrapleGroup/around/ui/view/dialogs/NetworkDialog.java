package com.StrapleGroup.around.ui.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Robert on 2014-12-17.
 */
public class NetworkDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener noticeDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder pAlertBuilder = new AlertDialog.Builder(getActivity());
        pAlertBuilder.setMessage("Network connection missing").setPositiveButton("Try Again!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noticeDialogListener.onDialogPositiveClick(NetworkDialog.this);
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noticeDialogListener.onDialogNegativeClick(NetworkDialog.this);
            }
        });
        return pAlertBuilder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        noticeDialogListener.onDialogPositiveClick(NetworkDialog.this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            noticeDialogListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}

