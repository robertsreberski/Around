package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Robert on 2014-12-14.
 */
public class AroundListAdapter extends CursorAdapter implements Constants {

    public AroundListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private int getItemViewType(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS));
        if (type.equals(STATUS_INVITATION)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        final double pLat = cursor.getDouble(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.X_FRIEND));
        final double pLng = cursor.getDouble(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.Y_FRIEND));
        Location pMyLocation = new Location("Me");
        SharedPreferences pPrefs = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        pMyLocation.setLatitude(Double.parseDouble(pPrefs.getString(Constants.KEY_X, "")));
        pMyLocation.setLongitude(Double.parseDouble(pPrefs.getString(Constants.KEY_Y, "")));
        Location pFriendLocation = new Location("Friend");
        pFriendLocation.setLatitude(pLat);
        pFriendLocation.setLongitude(pLng);
        if (!cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_INVITATION) && pMyLocation.distanceTo(pFriendLocation) <= 5000) {
            ViewHolder pViewHolder = (ViewHolder) view.getTag();
            final String pLogin = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
            pViewHolder.login.setText(pLogin);
            pViewHolder.photo.setImageBitmap(pImageHelper.decodeImageFromBytes(cursor.getBlob(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO))));
            pViewHolder.distance.setText(Float.toString(pMyLocation.distanceTo(pFriendLocation)));
            final int pActivityType = cursor.getInt(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.ACTIVITY));
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

    }

    static class ViewHolder {
        ImageView photo;
        TextView login;
        TextView distance;
        ImageView activity;
    }
}
