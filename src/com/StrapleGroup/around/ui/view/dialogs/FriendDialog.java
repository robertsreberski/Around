package com.StrapleGroup.around.ui.view.dialogs;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

/**
 * Created by Robert on 2014-12-24.
 */
public class FriendDialog extends Activity implements Constants, OnMapReadyCallback {
    TextView loginView;
    private GoogleMap mapPane;
    private MapFragment mapFragment;
    LatLng loc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_friend);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        getWindow().setLayout(width - 100, height - 550);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setMap();
        Intent intent = getIntent();
        friendCustomizer(intent);
        ImageButton pClose = (ImageButton) findViewById(R.id.close);
        pClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button pDeleteAction = (Button) findViewById(R.id.delete_button);
        pDeleteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        SharedPreferences pPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                        ConnectionHelper pConnectionHelper = new ConnectionHelper(getApplicationContext());
                        return pConnectionHelper.sendDeleteRequest(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), loginView.getText().toString());
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean == true) {
                            DataManagerImpl dataManager = new DataManagerImpl(getApplicationContext());
                            dataManager.deleteFriend(dataManager.findFriend(loginView.getText().toString()));
                            dataManager.deleteAround(dataManager.findAround(loginView.getText().toString()));
                            sendBroadcast(new Intent(MARKER_LOCAL_ACTION));
                            sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                            finish();
                        }
                    }
                }.execute(null, null, null);
            }
        });
    }

    private void setMap() {

    }

    public void friendCustomizer(Intent aIntent) {
        Bundle pResult = aIntent.getBundleExtra("friend_bundle");
        loginView = (TextView) findViewById(R.id.login);
        TextView pDistance = (TextView) findViewById(R.id.distance);
        ImageView pPhotoView = (ImageView) findViewById(R.id.profilePhoto);
        ImageView pStatusView = (ImageView) findViewById(R.id.status_view);
        String pLogin = pResult.getString(KEY_LOGIN);
        loginView.setText(pLogin);
        Double x = pResult.getDouble(KEY_X);
        Double y = pResult.getDouble(KEY_Y);
        loc = new LatLng(x, y);

        ImageHelper pImageHelper = new ImageHelper();
        pImageHelper.setImg(getApplicationContext(), pPhotoView, pImageHelper.decodeImageFromBytes(pResult.getByteArray(KEY_PHOTO)));
        GoogleMapOptions pGoogleMapOptions = new GoogleMapOptions().liteMode(true).mapToolbarEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).camera(new CameraPosition.Builder()
                .target(loc).zoom(7).build());
        mapFragment = MapFragment.newInstance(pGoogleMapOptions);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_small, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
        SharedPreferences prefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        Location pLocation = new Location("MyLoc");
        pLocation.setLatitude(Double.parseDouble(prefs.getString(Constants.KEY_X, "")));
        pLocation.setLongitude(Double.parseDouble(prefs.getString(Constants.KEY_Y, "")));
        Location pFriendLocation = new Location("FriendLoc");
        pFriendLocation.setLatitude(x);
        pFriendLocation.setLongitude(y);
        DecimalFormat formatter = new DecimalFormat("###m");
        pDistance.setText(formatter.format(pLocation.distanceTo(pFriendLocation)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(loc));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //do nothing
            }
        });
    }
}