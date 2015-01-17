package com.StrapleGroup.around.ui.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.utils.UpdateHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 2014-09-27.
 */
public class AroundSettingsFragment extends PreferenceFragment implements Constants {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    File photoFile;
    String currentPhotoPath;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.around_settings);
        prefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        ((Preference) findPreference("location_settings")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;
            }
        });
        ((Preference) findPreference("around_range")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        UpdateHelper pUpdateHelper = new UpdateHelper(getActivity().getApplicationContext());
                        pUpdateHelper.getUpdateOnDemand();
                        return null;
                    }
                }.execute(null, null, null);
                return true;
            }
        });
        ((Preference) findPreference("credits")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder pBuilder = new AlertDialog.Builder(getActivity());
                pBuilder.setTitle("Credentials");
                pBuilder.setMessage("StrapleGroup \n \n" +
                        "Robert Sreberski \n" +
                        "Mikołaj Speichert \n" +
                        "Mateusz Rumiński \n \n" +
                        "Special thanks for: \n" +
                        "Michał Hołownia- alpha tester");
                pBuilder.show();
                return true;
            }
        });
        ((Preference) findPreference("log_out")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent pLogOutIntent = new Intent(Constants.LOG_OUT_LOCAL_ACTION);
                getActivity().sendBroadcast(pLogOutIntent);
                return true;
            }
        });
        ((Preference) findPreference("change_photo")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dispatchTakePictureIntent();
                return true;
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            getActivity().getContentResolver().notifyChange(Uri.fromFile(photoFile), null);
            ContentResolver cr = getActivity().getContentResolver();
            ImageHelper pImageHelper = new ImageHelper();
            Bitmap imageBitmap = null;
            Matrix matrix = new Matrix();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(cr, Uri.fromFile(photoFile));
                matrix.postRotate(getPhotoRotation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            final String pPhotoString = pImageHelper.compressFromPhoto(rotatedBitmap);
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    ConnectionHelper pConnectionHelper = new ConnectionHelper(getActivity().getApplicationContext());
                    return pConnectionHelper.updatePhoto(prefs.getString(KEY_LOGIN, ""), prefs.getString(KEY_PASS, ""), pPhotoString);
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (aBoolean) {
                        SharedPreferences.Editor pPrefsEdit = prefs.edit();
                        pPrefsEdit.putString(Constants.KEY_PHOTO, pPhotoString);
                        pPrefsEdit.commit();
                        getActivity().sendBroadcast(new Intent(REFRESH_SETTINGS_LOCAL_ACTION));
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "An error occured while changing photo", Toast.LENGTH_SHORT).show();
                }
            }.execute(null, null, null);
        }

    }

    private int getPhotoRotation() throws IOException {
        int rotate = 0;
        ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    private File createImageFile() throws IOException {
        String pImageFileName = "profile_photo";
        File pStorageDir = Environment.getExternalStorageDirectory();
        File pImage = File.createTempFile(pImageFileName, ".jpg", pStorageDir);
        currentPhotoPath = "file:" + pImage.getAbsolutePath();
        return pImage;
    }

}
