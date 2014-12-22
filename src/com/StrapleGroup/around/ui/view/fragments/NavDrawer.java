package com.StrapleGroup.around.ui.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.StrapleGroup.around.R;

/**
 * Created by Robert on 2014-12-21.
 */
public class NavDrawer extends Fragment {
    ImageButton addFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.menu_nav_drawer, container, false);
        addFriend = (ImageButton) view.findViewById(R.id.add_friend);
        final EditText pFriendLogin = (EditText) view.findViewById(R.id.friend_field);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pFriendLogin.isShown()) {
                    Toast.makeText(getActivity().getApplicationContext(), pFriendLogin.getText().toString(), Toast.LENGTH_LONG).show();
                    pFriendLogin.setVisibility(View.INVISIBLE);
                    pFriendLogin.setText("");
                } else {
                    pFriendLogin.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

}