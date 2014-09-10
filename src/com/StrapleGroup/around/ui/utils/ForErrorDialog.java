package com.StrapleGroup.around.ui.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ForErrorDialog {
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
