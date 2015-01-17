package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.utils.UpdateHelper;

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


    private int getItemViewType(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS));
        int t = 0;
        switch (type) {
            case STATUS_INVITATION:
                t = 1;
                break;
            case STATUS_REQUEST:
                t = 2;
                break;
        }
        return t;
    }


    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v;
        if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_REQUEST)) {
            ViewInvitationHolder pViewInvitationHolder = new ViewInvitationHolder();
            v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.invitation_item, viewGroup, false);
            pViewInvitationHolder.login = (TextView) v.findViewById(R.id.login_label);
            pViewInvitationHolder.setTrue = (ImageButton) v.findViewById(R.id.set_true);
            pViewInvitationHolder.setFalse = (ImageButton) v.findViewById(R.id.set_false);
            v.setTag(pViewInvitationHolder);
        } else if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_INVITATION)) {
            ViewRequestHolder pViewRequestHolder = new ViewRequestHolder();
            v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_item, viewGroup, false);
            pViewRequestHolder.login = (TextView) v.findViewById(R.id.login_view);
            v.setTag(pViewRequestHolder);
        } else {
            v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friend_item, viewGroup, false);
            ViewFriendHolder pViewFriendHolder = new ViewFriendHolder();
            pViewFriendHolder.login = (TextView) v.findViewById(R.id.friend_name);
            pViewFriendHolder.photo = (ImageView) v.findViewById(R.id.photo);
            v.setTag(pViewFriendHolder);
        }
        return v;
    }
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_REQUEST)) {
            ViewInvitationHolder pViewInvitationHolder = (ViewInvitationHolder) view.getTag();
            final String aFriendLogin = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
            pViewInvitationHolder.login.setText(aFriendLogin);
            ProgressBar pProgressBar = (ProgressBar) view.findViewById(R.id.progress);

            pViewInvitationHolder.setTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pViewInvitationHolder.setTrue.setVisibility(View.INVISIBLE);
                    pViewInvitationHolder.setFalse.setVisibility(View.INVISIBLE);
                    pProgressBar.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                            SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                            if (pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), aFriendLogin, true)) {
                                UpdateHelper updateHelper = new UpdateHelper(context);
                                updateHelper.getUpdateOnDemand();
                            }
                            return null;
                        }
                    }.execute(null, null, null);
                }
            });
            pViewInvitationHolder.setFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pViewInvitationHolder.setTrue.setVisibility(View.INVISIBLE);
                    pViewInvitationHolder.setFalse.setVisibility(View.INVISIBLE);
                    pProgressBar.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                            SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                            if (pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), aFriendLogin, false)) {
                                DataManagerImpl pDataManager = new DataManagerImpl(context);
                                pDataManager.deleteFriend(pDataManager.findFriend(aFriendLogin));
                                context.sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                            }
                            return null;
                        }
                    }.execute(null, null, null);
                }
            });
        } else if (cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.STATUS)).equals(STATUS_INVITATION)) {
            ViewRequestHolder pViewRequestHolder = (ViewRequestHolder) view.getTag();
            pViewRequestHolder.login.setText(cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND)));
        } else {
            ViewFriendHolder pViewFriendHolder = (ViewFriendHolder) view.getTag();
            final String pFriendName = cursor.getString(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND));
            pViewFriendHolder.login.setText(pFriendName);
            ImageHelper imageHelper = new ImageHelper();
            Bitmap fBitmap = imageHelper.decodeImageFromBytes(cursor.getBlob(cursor.getColumnIndex(FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO)));
            imageHelper.setImg(context, pViewFriendHolder.photo, fBitmap);
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        return super.swapCursor(c);
    }


    static class ViewRequestHolder {
        TextView login;
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
