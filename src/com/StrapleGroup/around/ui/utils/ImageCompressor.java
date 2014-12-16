package com.StrapleGroup.around.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Robert on 2014-12-14.
 */
public class ImageCompressor {
    public ImageCompressor() {
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
}
