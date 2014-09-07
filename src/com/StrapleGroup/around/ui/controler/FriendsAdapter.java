package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;

import java.util.List;

public class FriendsAdapter extends ArrayAdapter<FriendsInfo> {
    List<FriendsInfo> friendList;

    public FriendsAdapter(Context context, List<FriendsInfo> friendsList) {
        super(context, R.layout.friend_login, friendsList);
        this.friendList = friendsList;
    }

    public FriendsAdapter getFriendsAdapter() {
        return this;
    }

    public void addItem(final FriendsInfo friend) {
        friendList.add(friend);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FriendsInfo friend = friendList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_login, parent, false);
        }
        TextView friendName = (TextView) convertView.findViewById(R.id.friend_name);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
        TextView latView = (TextView) convertView.findViewById(R.id.latView);
        TextView lngView = (TextView) convertView.findViewById(R.id.lngView);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete);
        latView.setText(Double.toString(friend.getXFriend()));
        lngView.setText(Double.toString(friend.getYFriend()));
        friendName.setText(friend.getLoginFriend());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHelper helper = new OpenHelper(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                FriendsInfoDao pFriendDao = new FriendsInfoDao(db);
                pFriendDao.delete(Long.toString(1));
                getFriendsAdapter().notifyDataSetChanged();
            }
        });
        img.setImageResource(R.drawable.nearby_prototyp1);
        return convertView;
    }

    @Override
    public void add(FriendsInfo object) {


        super.add(object);
    }
}
