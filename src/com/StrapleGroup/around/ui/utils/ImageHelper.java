package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.graphics.*;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.StrapleGroup.around.base.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by Robert on 2014-12-14.
 */
public class ImageHelper implements Constants {
    public ImageHelper() {
        super();
    }

    public String compressFromPhoto(Bitmap aBitmap) {
        int width = aBitmap.getWidth();
        int height = aBitmap.getHeight();
        int maxSize = 400;
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        Bitmap pScaledBitmap = Bitmap.createScaledBitmap(aBitmap, width, height, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pScaledBitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String encodedPhoto = Base64.encodeToString(b, 0);
        return encodedPhoto;
    }

    public String encodeImage(Bitmap aBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String encodedPhoto = Base64.encodeToString(b, 0);
        return encodedPhoto;
    }

    public byte[] encodeImageForDB(Bitmap aBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
        return baos.toByteArray();
    }

    public Bitmap decodeImage(String aEncodedImg) {
        byte[] b = Base64.decode(aEncodedImg, 0);
        Bitmap pBitmapImg = BitmapFactory.decodeByteArray(b, 0, b.length);
        return getCroppedBitmap(pBitmapImg);
    }

    public Bitmap decodeImageFromBytes(byte[] aBytes) {
        Bitmap pBitmapImg = BitmapFactory.decodeByteArray(aBytes, 0, aBytes.length);
        return getCroppedBitmap(pBitmapImg);
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

    public void setImg(Context context, ImageView imageView, Bitmap aBitmap) {
        Bitmap sbmp;
        if (aBitmap.getWidth() != 330 || aBitmap.getHeight() != 330)
            sbmp = Bitmap.createScaledBitmap(aBitmap, 330, 330, true);
        else sbmp = aBitmap;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        Log.d("IMG_SIZE", output.getHeight() + "," + output.getWidth());
        imageView.setImageBitmap(output);
    }
}
