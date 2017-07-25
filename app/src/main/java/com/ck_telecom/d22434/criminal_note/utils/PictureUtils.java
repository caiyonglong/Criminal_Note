package com.ck_telecom.d22434.criminal_note.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by D22434 on 2017/7/25.
 */

public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);

    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float width = options.outWidth;
        float height = options.outHeight;

        int inSampleSize = 2;

        if (width > destWidth || height > destHeight) {
            float heightScale = height / destHeight;
            float widthScale = width / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);

        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);

    }
}
