package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

/**
 * Created by Robert on 2014-09-09.
 */
public class FriendsCursorAdapter extends CursorAdapter {
    public FriendsCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }


    @Override
    protected void onContentChanged() {

        super.onContentChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final View view = LayoutInflater.from(context).inflate(R.layout.friend_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View convertView, final Context context, Cursor cursor) {
        final String pLogin = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
        final String pLat = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.X_FRIEND));
        final String pLng = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.Y_FRIEND));
        TextView friendName = (TextView) convertView.findViewById(R.id.friend_name);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
        TextView latView = (TextView) convertView.findViewById(R.id.latView);
        TextView lngView = (TextView) convertView.findViewById(R.id.lngView);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete);
        latView.setText(pLat);
        lngView.setText(pLng);
        friendName.setText(pLogin);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHelper helper = new OpenHelper(context);
                SQLiteDatabase db = helper.getWritableDatabase();
                FriendsInfoDao pFriendDao = new FriendsInfoDao(db);
                pFriendDao.delete(Long.toString(1));
                notifyDataSetChanged();
            }
        });
        img.setImageResource(R.drawable.nearby_prototyp1);
    }
}
