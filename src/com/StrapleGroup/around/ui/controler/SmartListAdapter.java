package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
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
        if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_INVITATION)) {
            return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.invitation_item, viewGroup, false);
        }
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friend_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_INVITATION)) {
            ViewInvitationHolder pViewInvitationHolder = new ViewInvitationHolder();
            pViewInvitationHolder.login = (TextView) view.findViewById(R.id.login_label);
            final String aFriendLogin = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
            pViewInvitationHolder.login.setText(aFriendLogin);
            pViewInvitationHolder.setTrue = (ImageButton) view.findViewById(R.id.set_true);
            pViewInvitationHolder.setFalse = (ImageButton) view.findViewById(R.id.set_false);
            pViewInvitationHolder.setTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                            SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                            if (!pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), aFriendLogin, true)) {
//                                Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            } else notifyChange();
                            return null;
                        }
                    }.execute(null, null, null);
                }
            });
            pViewInvitationHolder.setFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                            SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                            if (pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), aFriendLogin, false)) {
                                DataManagerImpl pDataManager = new DataManagerImpl(context);
                                pDataManager.deleteFriend(pDataManager.findFriend(aFriendLogin));
                                notifyChange();
                            } else
                                Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    }.execute(null, null, null);
                }
            });
            view.setTag(pViewInvitationHolder);
        } else {
            ViewFriendHolder pViewFriendHolder = new ViewFriendHolder();
            pViewFriendHolder.login = (TextView) view.findViewById(R.id.friend_name);
            final String pFriendName = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
            pViewFriendHolder.login.setText(pFriendName);
            pViewFriendHolder.photo = (ImageView) view.findViewById(R.id.photo);
            new AsyncTask<ViewFriendHolder, Void, Bitmap>() {
                private ViewFriendHolder viewFriendHolder;
                private ImageHelper imageHelper;

                @Override
                protected Bitmap doInBackground(ViewFriendHolder... params) {
                    viewFriendHolder = params[0];
                    imageHelper = new ImageHelper();
                    Bitmap fBitmap = imageHelper.decodeImageFromBytes(cursor.getBlob(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO)));
                    return fBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    imageHelper.setImg(context, viewFriendHolder.photo, result);
                }
            }.execute(pViewFriendHolder);
            view.setTag(pViewFriendHolder);
        }
    }

    public void notifyChange() {
        notifyDataSetChanged();
    }


    static class ViewInvitationHolder {
        TextView login;
        ImageButton setTrue;
        ImageButton setFalse;
    }

    static class ViewFriendHolder {
        ImageView photo;
        TextView login;
    }
}
