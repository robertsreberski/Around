package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 2014-09-27.
 */
public class AroundSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.around_settings);
        ((Preference) findPreference("log_out")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent pLogOutIntent = new Intent(Constants.LOG_OUT_LOCAL_ACTION);
                getActivity().sendBroadcast(pLogOutIntent);
                return true;
            }
        });
        ((Preference) findPreference("update_photo")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(), "Photo updated", Toast.LENGTH_SHORT).show();
                try {
                    updatePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void updatePhoto() throws IOException {
        Context context = getActivity().getApplicationContext();
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedPhoto = Base64.encodeToString(b, 0);
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost("http://89.71.164.245/straple/image");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(Constants.KEY_LOGIN, "robert"));
        nameValuePairs.add(new BasicNameValuePair("photo", encodedPhoto));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpResponse pResponse = null;
                try {
                    Log.e("Start", "HttpResponse");
                    pResponse = httpclient.execute(httppost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("HTTPRESPONSE", "not working yet");
                return null;
            }
        }.execute(null, null, null);

    }
}
