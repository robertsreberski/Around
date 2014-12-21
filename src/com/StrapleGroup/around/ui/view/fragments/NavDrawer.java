package com.StrapleGroup.around.ui.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.StrapleGroup.around.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

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
                Log.d("CONN", "CONNECTION_STARTED");
                URL pUrl = null;
                try {
                    pUrl = new URL("http://10.0.2.2:8080/Servletkurfa/Test");
                    URLConnection pConnection = pUrl.openConnection();
                    String pTest = pFriendLogin.getText().toString();
                    pConnection.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(pConnection.getOutputStream());
                    out.write(pTest);
                    out.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(pConnection.getInputStream()));
                    final String pResponse = in.readLine();
                    in.close();
                    Toast.makeText(getActivity().getApplicationContext(), pResponse, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Log.d("CONN", "CONNECTION_STARTED");
//                            URL pUrl = new URL("http://10.0.2.2:8080/Servletkurfa/Test");
//                            URLConnection pConnection = pUrl.openConnection();
//                            String pTest = pFriendLogin.getText().toString();
//                            pConnection.setDoOutput(true);
//                            OutputStreamWriter out = new OutputStreamWriter(pConnection.getOutputStream());
//                            out.write(pTest);
//                            out.close();
//                            BufferedReader in = new BufferedReader(new InputStreamReader(pConnection.getInputStream()));
//                            final String pResponse = in.readLine();
//                            in.close();
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getActivity().getApplicationContext(),pResponse,Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
            }
        });
        return view;
    }

}