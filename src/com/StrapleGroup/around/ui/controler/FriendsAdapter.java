package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.List;

public class FriendsAdapter extends ArrayAdapter<FriendsInfo> {
    public FriendsAdapter(Context context, List<FriendsInfo> friendsList) {
        super(context, R.layout.friend_login, friendsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FriendsInfo friend = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_login, parent, false);
        }
        TextView friendName = (TextView) convertView.findViewById(R.id.friend_name);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
        friendName.setText(friend.getLoginFriend());
        img.setImageResource(R.drawable.nearby_prototyp1);
        return convertView;
    }
}
