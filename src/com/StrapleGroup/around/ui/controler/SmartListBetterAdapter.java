package com.StrapleGroup.around.ui.controler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.utils.UpdateHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Robert on 2015-01-10.
 */
public class SmartListBetterAdapter extends BaseAdapter implements Constants {

    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_INVITATION = 1;
    private static final int TYPE_REQUEST = 2;
    private static final int TYPE_FRIEND = 3;

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Object> data = new ArrayList<Object>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    public SmartListBetterAdapter(Context aContext) {
        this.context = aContext;
        inflater = (LayoutInflater) aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    public void addItem(FriendsInfo aFriend) {
        data.add(aFriend);
    }


    public void addHeader(String aHeader) {
        data.add(aHeader);
        sectionHeader.add(data.size() - 1);
    }


    @Override
    public int getItemViewType(int position) {
        int type = TYPE_SEPARATOR;
        if (!sectionHeader.contains(position)) {
            FriendsInfo pFriend = (FriendsInfo) data.get(position);
            switch (pFriend.getStatus()) {
                case STATUS_INVITATION:
                    type = TYPE_INVITATION;
                    break;
                case STATUS_REQUEST:
                    type = TYPE_REQUEST;
                    break;
                default:
                    type = TYPE_FRIEND;
                    break;
            }
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    public void clearList() {
        data.clear();
        sectionHeader.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_SEPARATOR:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.section_header, null);
                }
                TextView pHeaderText = (TextView) convertView.findViewById(R.id.header_text);
                pHeaderText.setText((String) data.get(position));
                break;


            case TYPE_INVITATION:
                ViewRequestHolder pViewRequestHolder = null;
                if (convertView == null) {
                    pViewRequestHolder = new ViewRequestHolder();
                    convertView = inflater.inflate(R.layout.request_item, null);
                    pViewRequestHolder.login = (TextView) convertView.findViewById(R.id.login_view);
                    convertView.setTag(pViewRequestHolder);
                }
                ViewRequestHolder pViewRequestHolder2 = (ViewRequestHolder) convertView.getTag();
                pViewRequestHolder2.login.setText(((FriendsInfo) data.get(position)).getLoginFriend());
                break;


            case TYPE_REQUEST:
                ViewInvitationHolder pViewInvitationHolder = null;
                if (convertView == null) {
                    pViewInvitationHolder = new ViewInvitationHolder();
                    convertView = inflater.inflate(R.layout.invitation_item, null);
                    pViewInvitationHolder.login = (TextView) convertView.findViewById(R.id.login_label);
                    pViewInvitationHolder.setTrue = (ImageButton) convertView.findViewById(R.id.set_true);
                    pViewInvitationHolder.setFalse = (ImageButton) convertView.findViewById(R.id.set_false);
                    convertView.setTag(pViewInvitationHolder);
                }
                FriendsInfo pFriendRequest = (FriendsInfo) data.get(position);

                final String pFriendLogin = pFriendRequest.getLoginFriend();
                ProgressBar pProgressBar = (ProgressBar) convertView.findViewById(R.id.progress);
                final ViewInvitationHolder pViewInvitationHolder2 = (ViewInvitationHolder) convertView.getTag();
                pViewInvitationHolder2.login.setText(pFriendLogin);

                pViewInvitationHolder2.setTrue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pViewInvitationHolder2.setTrue.setVisibility(View.INVISIBLE);
                        pViewInvitationHolder2.setFalse.setVisibility(View.INVISIBLE);
                        pProgressBar.setVisibility(View.VISIBLE);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                                SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                                if (pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), pFriendLogin, true)) {
                                    UpdateHelper updateHelper = new UpdateHelper(context);
                                    updateHelper.getUpdateOnDemand();
                                }
                                return null;
                            }
                        }.execute(null, null, null);
                    }
                });
                pViewInvitationHolder2.setFalse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pViewInvitationHolder2.setTrue.setVisibility(View.INVISIBLE);
                        pViewInvitationHolder2.setFalse.setVisibility(View.INVISIBLE);
                        pProgressBar.setVisibility(View.VISIBLE);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                                SharedPreferences pPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                                if (pConnectionHelper.sendAddResponse(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), pFriendLogin, false)) {
                                    DataManagerImpl pDataManager = new DataManagerImpl(context);
                                    pDataManager.deleteFriend(pDataManager.findFriend(pFriendLogin));
                                    context.sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                                }
                                return null;
                            }
                        }.execute(null, null, null);
                    }
                });
                break;


            case TYPE_FRIEND:
                ViewFriendHolder pViewFriendHolder = null;
                if (convertView == null) {
                    pViewFriendHolder = new ViewFriendHolder();
                    convertView = inflater.inflate(R.layout.friend_item, null);
                    pViewFriendHolder.login = (TextView) convertView.findViewById(R.id.friend_name);
                    pViewFriendHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
                    pViewFriendHolder.alpha = (TextView) convertView.findViewById(R.id.alpha_label);
                    convertView.setTag(pViewFriendHolder);
                }

                ViewFriendHolder pViewFriendHolder2 = (ViewFriendHolder) convertView.getTag();
                FriendsInfo pFriend = (FriendsInfo) getItem(position);
                final String pFriendName = pFriend.getLoginFriend();
                pViewFriendHolder2.login.setText(pFriendName);
                ImageHelper imageHelper = new ImageHelper();
                Bitmap fBitmap = imageHelper.decodeImageFromBytes(pFriend.getProfilePhoto());
                imageHelper.setImg(context, pViewFriendHolder2.photo, fBitmap);
                String alphaCurrent = pFriendName.substring(0, 1).toUpperCase();
                if (!sectionHeader.contains(position - 1)) {
                    FriendsInfo prevFriend = (FriendsInfo) data.get(position - 1);
                    if (!alphaCurrent.equals(prevFriend.getLoginFriend().substring(0, 1).toUpperCase())) {
                        pViewFriendHolder2.alpha.setText(alphaCurrent);
                    } else {
                        pViewFriendHolder2.alpha.setText("");
                    }
                } else {
                    pViewFriendHolder2.alpha.setText(alphaCurrent);
                }
                break;
        }


        return convertView;
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
        TextView alpha;
    }
}
