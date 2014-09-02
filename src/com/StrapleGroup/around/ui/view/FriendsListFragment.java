package com.StrapleGroup.around.ui.view;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.ui.controler.FriendsAdapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsListFragment extends ListFragment {
    private Context context;
    private DataManager dataManager;
    private AtomicInteger atomicInteger = new AtomicInteger();
    private List<FriendsInfo> friendsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        friendsList = dataManager.getAllFriendsInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        FriendsAdapter adapter = new FriendsAdapter(context, friendsList);
        this.setListAdapter(adapter);
    }
}