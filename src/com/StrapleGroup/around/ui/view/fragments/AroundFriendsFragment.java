package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.ui.controler.AroundListAdapter;

/**
 * Created by Robert on 2014-09-21.
 */
public class AroundFriendsFragment extends Fragment {
    private AroundListAdapter aroundListAdapter;
    private Context context;
    private DataManagerImpl dataManager;
    private ViewGroup viewGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        dataManager = new DataManagerImpl(context);
        aroundListAdapter = new AroundListAdapter(context, dataManager.getCompleteCursor(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_around_friends, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView) getActivity().findViewById(R.id.listView_around);
        listView.setAdapter(aroundListAdapter);
    }
}