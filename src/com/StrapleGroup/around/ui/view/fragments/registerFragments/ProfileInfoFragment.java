package com.StrapleGroup.around.ui.view.fragments.registerFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.ui.utils.BCrypt;
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by Robert on 2015-01-21.
 */
public class ProfileInfoFragment extends Fragment implements View.OnClickListener, Constants {
    private EditText loginField;
    private EditText phoneField;
    private ImageView photoView;
    private ImageButton nextButton;
    private Context context;
    private String login;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_PICTURE = 2;
    File photoFile;
    String currentPhotoPath;
    SharedPreferences regPrefs;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.fragment_profile_info, container, false);
        loginField = (EditText) pView.findViewById(R.id.login_field);
        context = getActivity().getApplicationContext();
        regPrefs = context.getSharedPreferences(REGISTRATION_PREFS, Context.MODE_PRIVATE);
        prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        phoneField = (EditText) pView.findViewById(R.id.phone_field);
        photoView = (ImageView) pView.findViewById(R.id.profile_button);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pDialog = new AlertDialog.Builder(getActivity());
                pDialog.setTitle("Choose photo").setItems(new String[]{"Take photo", "From gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            dispatchTakePictureIntent();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                        }
                    }
                });
                pDialog.create().show();

            }
        });
        nextButton = (ImageButton) pView.findViewById(R.id.next_button1);
        nextButton.setOnClickListener(this);

        ImageHelper pImageHelper = new ImageHelper();
        pImageHelper.setImg(getActivity().getApplicationContext(), photoView, BitmapFactory.decodeResource(getResources(), R.drawable.facebook_example));
        return pView;
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
        ImageHelper pImageHelper = new ImageHelper();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                getActivity().getContentResolver().notifyChange(Uri.fromFile(photoFile), null);
                ContentResolver cr = getActivity().getContentResolver();
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
                pImageHelper.setImg(getActivity().getApplicationContext(), photoView, rotatedBitmap);
                SharedPreferences.Editor pPrefsEdit = prefs.edit();
                pPrefsEdit.putString(Constants.KEY_PHOTO, pPhotoString);
                pPrefsEdit.commit();
                photoFile.delete();
            }
            if (requestCode == SELECT_PICTURE) {
                Bitmap pBitmap = null;
                Matrix matrix = new Matrix();
                try {
                    pBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//                    matrix.postRotate(getPhotoRotation(data.getData().getPath()));
                    Bitmap rotated = Bitmap.createBitmap(pBitmap, 0, 0, pBitmap.getWidth(), pBitmap.getHeight(), matrix, true);
                    final String pPhotoString = pImageHelper.compressFromPhoto(rotated);
                    pImageHelper.setImg(getActivity().getApplicationContext(), photoView, rotated);
                    SharedPreferences.Editor pPrefsEdit = prefs.edit();
                    pPrefsEdit.putString(Constants.KEY_PHOTO, pPhotoString);
                    pPrefsEdit.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    private int getPhotoRotation(String path) throws IOException {
        int rotate = 0;
        ExifInterface exif = new ExifInterface(path);
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
        File pStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File pImage = File.createTempFile(pImageFileName, ".jpg", pStorageDir);
        currentPhotoPath = "file:" + pImage.getAbsolutePath();
        return pImage;
    }

    @Override
    public void onClick(View v) {
        boolean done = true;
        loginField.setError(null);
        login = loginField.getText().toString();

        if (TextUtils.isEmpty(login)) {
            done = false;
        }
        if (done) {
            if (ConnectionUtils.hasActiveInternetConnection(context)) {
                new AsyncTask<Void, Void, Boolean>() {
                    ImageHelper pImageHelper = new ImageHelper();
                    String photoString;
                    String email = regPrefs.getString(KEY_EMAIL, "");
                    String pass = regPrefs.getString(KEY_PASS, "");

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        if (prefs.contains(KEY_PHOTO)) {
                            photoString = prefs.getString(KEY_PHOTO, "");
                        } else {
                            photoString = pImageHelper.encodeImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example));
                        }
                        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                        Double pLat = 0.000;
                        Double pLng = 0.000;
                        int pActivity = DetectedActivity.UNKNOWN;
                        if (prefs.contains(KEY_X) && prefs.contains(KEY_Y)) {
                            pLat = Double.parseDouble(prefs.getString(KEY_X, ""));
                            pLng = Double.parseDouble(prefs.getString(KEY_Y, ""));
                        }
                        if (prefs.contains(KEY_ACTIVITY)) pActivity = prefs.getInt(KEY_ACTIVITY, 4);
                        return pConnectionHelper.registerToApp(email, login, pass, pLat, pLng, photoString, pActivity);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean) {
                            SharedPreferences.Editor pPrefsEditor = prefs.edit();
                            pPrefsEditor.putString(KEY_LOGIN, login);
                            pPrefsEditor.putString(KEY_EMAIL, email);
                            pPrefsEditor.putString(KEY_PASS, pass);
                            pPrefsEditor.putString(KEY_STATUS, STATUS_ONLINE);
                            pPrefsEditor.putString(KEY_PHOTO, photoString);
                            pPrefsEditor.commit();
                            getActivity().finish();
                        }
                    }
                }.execute(null, null, null);
            }


        }
    }
}