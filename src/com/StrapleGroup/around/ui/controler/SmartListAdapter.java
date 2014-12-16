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
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

import java.util.List;

/**
 * Created by Robert on 2014-09-20.
 */
public class SmartListAdapter extends CursorAdapter {


    private List<FriendsInfo> friendsInfoList;
    private Context context;

    public SmartListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friend_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder pViewHolder = new ViewHolder();
        pViewHolder.login = (TextView) view.findViewById(R.id.friend_name);
        final String pFriendName = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
        pViewHolder.login.setText(pFriendName);
        pViewHolder.photo = (ImageView) view.findViewById(R.id.photo);

//        pViewHolder.distance.setText("323");
//        pViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        DataManagerImpl dataManager = new DataManagerImpl(context);
//                        dataManager.deleteFriend(dataManager.findFriend(pFriendName));
//                        OpenHelper pOpenHelper = new OpenHelper(context);
//                        SQLiteDatabase db = pOpenHelper.getReadableDatabase();
//                        Cursor pNewCursor = db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
//                                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND,
//                                        FriendsInfoTable.FriendsInfoColumns.X_FRIEND, FriendsInfoTable.FriendsInfoColumns.Y_FRIEND},
//                                null, null, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, null);
//                        swapCursor(pNewCursor);
//                        return null;
//                    }
//                }.execute(null, null, null);
//            }
//        });
    }


    static class ViewHolder {
        ImageView photo;
        TextView login;
    }
}
