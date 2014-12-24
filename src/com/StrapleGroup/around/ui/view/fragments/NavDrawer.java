package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class NavDrawer extends Fragment implements View.OnClickListener, Constants {
    ImageButton addFriend;
    EditText friendLogin;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.menu_nav_drawer, container, false);
        addFriend = (ImageButton) view.findViewById(R.id.add_friend);
        friendLogin = (EditText) view.findViewById(R.id.friend_field);
        context = getActivity().getApplicationContext();
        addFriend.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (friendLogin.isShown()) {
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
                        friendLogin.setVisibility(View.INVISIBLE);
                        friendLogin.setText("");
                    } else
                        Toast.makeText(context, "Friend cannot be added now", Toast.LENGTH_SHORT);
                }
            };
        } else {
            friendLogin.setVisibility(View.VISIBLE);
        }
    }

    private boolean sendRequest(String aFriendLogin) {
        SharedPreferences pPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String aLogin = pPrefs.getString(KEY_LOGIN, "");
        String aPass = pPrefs.getString(KEY_PASS, "");
        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
        if (pConnectionHelper.sendAddRequest(aLogin, aPass, aFriendLogin)) {
            DataManagerImpl pDataManager = new DataManagerImpl(context);
            FriendsInfo pFriend = new FriendsInfo();
            ImageHelper pImageHelper = new ImageHelper();
            pFriend.setLoginFriend(aFriendLogin);
            pFriend.setProfilePhoto(pImageHelper.encodeImageForDB(BitmapFactory.decodeResource(getResources(), R.drawable.facebook_example)));
            pFriend.setStatus(STATUS_REQUEST);
            pDataManager.saveRequest(pFriend);
            return true;
        }
        return false;
    }
}