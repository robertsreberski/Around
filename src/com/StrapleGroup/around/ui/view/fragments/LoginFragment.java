package com.StrapleGroup.around.ui.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.ui.utils.BCrypt;
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.StrapleGroup.around.ui.utils.FastLocationObtainer;
import com.StrapleGroup.around.ui.view.LoginActivity;
import com.StrapleGroup.around.ui.view.MainActivity;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Robert on 2015-01-17.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, Constants, View.OnKeyListener {

    private EditText loginField;
    private EditText passField;
    private SharedPreferences prefs;
    private String email = null;
    private String pass = null;
    private Button loginButton = null;
    private ProgressBar loginProgress = null;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        context = getActivity().getApplicationContext();
        loginField = (EditText) view.findViewById(R.id.loginField);
        passField = (EditText) view.findViewById(R.id.passField);
        passField.setOnKeyListener(this);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        loginProgress = (ProgressBar) view.findViewById(R.id.loginProgress);
        loginProgress.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onClick(View v) {
        boolean done = true;
        loginField.setError(null);
        passField.setError(null);
        email = loginField.getText().toString();
        pass = passField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            loginField.setError("Field required");
            done = false;
        } else if (TextUtils.isEmpty(pass)) {
            passField.setError("Field required");
            done = false;
        }
        if (done) {
            if (ConnectionUtils.hasActiveInternetConnection(context)) {
                loginButton.setText("");
                loginProgress.setVisibility(View.VISIBLE);
                prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);

                new AsyncTask<Void, Void, Boolean>() {
                    Double pLat;
                    Double pLng;

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                        FastLocationObtainer pFastLocationObtainer = new FastLocationObtainer(context);
                        pLat = pFastLocationObtainer.getLatitude();
                        pLng = pFastLocationObtainer.getLongtitude();
                        int pActivity = DetectedActivity.UNKNOWN;
                        if (prefs.contains(KEY_X) && prefs.contains(KEY_Y)) {
                            pLat = Double.parseDouble(prefs.getString(KEY_X, ""));
                            pLng = Double.parseDouble(prefs.getString(KEY_Y, ""));
                        }
                        if (prefs.contains(KEY_ACTIVITY)) pActivity = prefs.getInt(KEY_ACTIVITY, 4);
                        return pConnectionHelper.loginToApp(email, "xxx", pass, pLat, pLng, pActivity);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBool) {
                        super.onPostExecute(aBool);
                        if (aBool) {
                            SharedPreferences.Editor pEditor = prefs.edit();
                            pEditor.putString(KEY_EMAIL, email);
                            pEditor.putString(KEY_PASS, pass);
                            pEditor.putString(KEY_STATUS, STATUS_ONLINE);
                            pEditor.putString(KEY_X, Double.toString(pLat));
                            pEditor.putString(KEY_Y, Double.toString(pLng));
                            pEditor.commit();
                            nextActivity();
                        } else badRequest();
                    }
                }.execute(null, null, null);
            } else noConnection();
        }
    }

    protected void nextActivity() {
        Intent pNextActivityIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(pNextActivityIntent);
        getActivity().finish();
    }

    private void noConnection() {
        loginProgress.setVisibility(View.INVISIBLE);
        loginButton.setText("Log in");
        Toast.makeText(context, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

    protected void badRequest() {
        loginProgress.setVisibility(View.INVISIBLE);
        loginButton.setText("Log in");
        Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
        return true;
    }
}