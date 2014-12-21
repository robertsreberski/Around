package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
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

    public String encodeImage(Bitmap aBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedPhoto = Base64.encodeToString(b, 0);
        return encodedPhoto;
    }

    public byte[] encodeImageForDB(Bitmap aBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
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

    public void setImg(Context context, ImageView imageView, Bitmap aBitmap) {
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(new ImageLoaderConfiguration.Builder(context).build());
//        DisplayImageOptions pOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(330)).cacheInMemory(true).cacheOnDisk(true).build();
//        imageLoader.displayImage(getImageUri(context, getCroppedBitmap(aBitmap)).toString(), imageView, pOptions);
//        imageLoader.destroy();
//        BitmapShader shader;
//        Canvas canvas = new Canvas();
//        shader = new BitmapShader(aBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setShader(shader);
//        RectF rect = new RectF(0.0f, 0.0f, aBitmap.getWidth(), aBitmap.getHeight());
//// rect contains the bounds of the shape
//// radius is the radius in pixels of the rounded corners
//// paint contains the shader that will texture the shape
//        canvas.drawRoundRect(rect, 330, 330, paint);
        Bitmap sbmp;
        if (aBitmap.getWidth() != 330 || aBitmap.getHeight() != 330)
            sbmp = Bitmap.createScaledBitmap(aBitmap, 330, 330, false);
        else
            sbmp = aBitmap;
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
        imageView.setImageBitmap(output);
    }
}
