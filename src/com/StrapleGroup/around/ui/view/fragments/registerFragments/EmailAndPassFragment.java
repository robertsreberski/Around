package com.StrapleGroup.around.ui.view.fragments.registerFragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

/**
 * Created by Robert on 2015-01-21.
 */
public class EmailAndPassFragment extends Fragment implements View.OnClickListener, Constants {
    private ImageButton nextButton;
    private EditText emailField;
    private EditText passField;
    private EditText doublePassField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.fragment_email_pass, container, false);
        nextButton = (ImageButton) pView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        emailField = (EditText) pView.findViewById(R.id.email_field);
        passField = (EditText) pView.findViewById(R.id.password_field);
        doublePassField = (EditText) pView.findViewById(R.id.double_password_field);
        return pView;
    }

    @Override
    public void onClick(View v) {
        boolean pDone = true;
        String emailText = emailField.getText().toString();
        String passText = passField.getText().toString();
        String doublePassText = doublePassField.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            pDone = false;
            emailField.setError("Type email!");
        }
        if (!emailText.contains("@")) {
            pDone = false;
            emailField.setError("This is not e-mail format!");
        }
        if (!TextUtils.isEmpty(passText)) {
            if (!passText.equals(doublePassText)) {
                pDone = false;
                doublePassField.setError("Passwords not match!");
            }
        } else {
            pDone = false;
            passField.setError("Type password!");
        }
        if (pDone) {
            SharedPreferences pRegPrefs = getActivity().getSharedPreferences(REGISTRATION_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor pEditor = pRegPrefs.edit();
            pEditor.putString(KEY_EMAIL, emailText);
            pEditor.putString(KEY_PASS, passText);
            pEditor.commit();
            getActivity().sendBroadcast(new Intent(CHANGE_PAGE_LOCAL_ACTION).putExtra("PAGE", 1));
        }
    }
}