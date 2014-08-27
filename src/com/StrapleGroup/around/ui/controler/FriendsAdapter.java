package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends ArrayAdapter<FriendsInfo>{

	private List<FriendsInfo> friendsArray = new ArrayList<FriendsInfo>();
	private Context context;
	public FriendsAdapter(Context context, FriendsInfo aTest) {
		super(context, 0);
		this.context = context;
		friendsArray.add(aTest);
		System.out.println(friendsArray.toString());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View pFriend = inflater.inflate(R.layout.friend_login, parent, false);
		TextView loginView = (TextView) pFriend.findViewById(R.id.friend_name);
		
		loginView.setText(friendsArray.get(position).getLoginFriend());
//		
		return pFriend;
	}
}
