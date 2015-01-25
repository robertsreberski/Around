package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.ui.utils.ImageHelper;

/**
 * Created by Robert on 2014-12-21.
 */
public class NavDrawer extends Fragment implements Constants {
    ImageButton addFriend;
    EditText friendLogin;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.menu_nav_drawer, container, false);
        addFriend = (ImageButton) view.findViewById(R.id.add_friend);
        friendLogin = (EditText) view.findViewById(R.id.friend_field);
        context = getActivity().getApplicationContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendLogin.isShown()) {
                    if (getActivity().getCurrentFocus() != null) {
                        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    DataManagerImpl dataManager = DataManagerImpl.getInstance(context);
                    String friendName = friendLogin.getText().toString();
                    if (TextUtils.isEmpty(friendName) || dataManager.findFriend(friendName) != -1) {
                        Toast.makeText(context, "Can't add typed friend", Toast.LENGTH_SHORT).show();
                    } else {
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                return sendRequest(friendLogin.getText().toString());
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                                if (aBoolean) {
                                    Toast.makeText(getActivity().getApplicationContext(), friendLogin.getText().toString() + " added", Toast.LENGTH_LONG).show();

                                    FriendsInfo pFriend = new FriendsInfo();
                                    ImageHelper pImageHelper = new ImageHelper();
                                    pFriend.setLoginFriend(friendLogin.getText().toString());
                                    pFriend.setProfilePhoto(pImageHelper.encodeImageForDB(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example)));
                                    pFriend.setStatus(Constants.STATUS_INVITATION);
                                    dataManager.saveRequest(pFriend);
                                    context.sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                                    friendLogin.setVisibility(View.INVISIBLE);
                                    friendLogin.setText("");
                                } else
                                    Toast.makeText(context, "Friend cannot be added now", Toast.LENGTH_SHORT).show();
                            }
                        }.execute(null, null, null);
                    }
                } else {
                    friendLogin.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private boolean sendRequest(String aFriendLogin) {
        SharedPreferences pPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String aLogin = pPrefs.getString(KEY_LOGIN, "");
        String aPass = pPrefs.getString(KEY_PASS, "");
        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
        if (pConnectionHelper.sendAddRequest(aLogin, aPass, aFriendLogin)) {
            return true;
        }
        return false;
    }
}