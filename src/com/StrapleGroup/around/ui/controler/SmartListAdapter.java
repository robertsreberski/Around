package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.utils.ImageHelper;

import java.util.List;

/**
 * Created by Robert on 2014-09-20.
 */
public class SmartListAdapter extends CursorAdapter implements Constants {


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
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder pViewHolder = new ViewHolder();
        pViewHolder.login = (TextView) view.findViewById(R.id.friend_name);
        final String pFriendName = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
        pViewHolder.login.setText(pFriendName);
        pViewHolder.photo = (ImageView) view.findViewById(R.id.photo);
        new AsyncTask<ViewHolder, Void, Bitmap>() {
            private ViewHolder viewHolder;
            private ImageHelper imageHelper;

            @Override
            protected Bitmap doInBackground(ViewHolder... params) {
                viewHolder = params[0];
                imageHelper = new ImageHelper();
                Bitmap fBitmap = imageHelper.decodeImageFromBytes(cursor.getBlob(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO)));
                return fBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageHelper.setImg(context, viewHolder.photo, result);
            }
        }.execute(pViewHolder);
        view.setTag(pViewHolder);
    }


    static class ViewHolder {
        ImageView photo;
        TextView login;
    }
}
