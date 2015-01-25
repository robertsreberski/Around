package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.AroundInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.google.android.gms.location.DetectedActivity;

import java.text.DecimalFormat;

/**
 * Created by Robert on 2014-12-14.
 */
public class AroundListAdapter extends CursorAdapter implements Constants {

    DataManagerImpl dataManager;

    public AroundListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        dataManager = DataManagerImpl.getInstance(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder pViewHolder = new ViewHolder();
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.around_friends_card, parent, false);
        pViewHolder.login = (TextView) view.findViewById(R.id.friend_name);
        pViewHolder.photo = (ImageView) view.findViewById(R.id.photo);
        pViewHolder.distance = (TextView) view.findViewById(R.id.distance_view);
        pViewHolder.activity = (ImageView) view.findViewById(R.id.activity_view);
        view.setTag(pViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageHelper pImageHelper = new ImageHelper();
        ViewHolder pViewHolder = (ViewHolder) view.getTag();
        final String pLogin = cursor.getString(cursor.getColumnIndex(AroundInfoTable.AroundColumns.LOGIN_FRIEND));
        FriendsInfo pFriend = dataManager.getFriendInfo(pLogin);
        final float pDistance = cursor.getFloat(cursor.getColumnIndex(AroundInfoTable.AroundColumns.DISTANCE));
        final int pActivityType = pFriend.getActivities();
        pViewHolder.login.setText(pLogin);
        DecimalFormat formatter = new DecimalFormat("###m");
        pViewHolder.distance.setText(formatter.format(pDistance));
        pImageHelper.setImg(context, pViewHolder.photo, pImageHelper.decodeImageFromBytes(pFriend.getProfilePhoto()));
        switch (pActivityType) {
            case DetectedActivity.IN_VEHICLE:
                pViewHolder.activity.setImageResource(R.drawable.icon_car);
                break;
            case DetectedActivity.ON_BICYCLE:
                pViewHolder.activity.setImageResource(R.drawable.icon_bike);
                break;
            case DetectedActivity.ON_FOOT:
                pViewHolder.activity.setImageResource(R.drawable.icon_walk);
                break;
            case DetectedActivity.STILL:
                pViewHolder.activity.setImageResource(R.drawable.icon_still);
                break;
            case DetectedActivity.UNKNOWN:
                pViewHolder.activity.setImageResource(R.drawable.icon_unknown);
                break;
        }
    }

    static class ViewHolder {
        ImageView photo;
        TextView login;
        TextView distance;
        ImageView activity;
    }
}
