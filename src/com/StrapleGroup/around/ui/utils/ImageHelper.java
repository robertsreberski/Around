package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Robert on 2014-12-14.
 */
public class ImageHelper {
    public ImageHelper() {
        super();
    }

    public String encodeImage(Bitmap aBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedPhoto = Base64.encodeToString(b, 0);
        return encodedPhoto;
    }

    public Bitmap decodeImage(String aEncodedImg) {
        byte[] b = Base64.decode(aEncodedImg, 0);
        Bitmap pBitmapImg = BitmapFactory.decodeByteArray(b, 0, b.length);
        return pBitmapImg;
    }

    public Bitmap decodeImageFromBytes(byte[] aBytes) {
        Bitmap pBitmapImg = BitmapFactory.decodeByteArray(aBytes, 0, aBytes.length);
        return pBitmapImg;
    }

    public Uri getImageUri(Context aContext, Bitmap aImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        aImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(aContext.getContentResolver(), aImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap dstBmp;
        if (bitmap.getWidth() >= bitmap.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return dstBmp;
    }
}
