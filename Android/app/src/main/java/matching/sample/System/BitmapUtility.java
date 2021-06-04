package matching.sample.System;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapUtility {

    // ユーザのプロフィール画像向けにBitmapを縮小する
    public static Bitmap createProfileBitmap(Bitmap bitmap) {

        int width, height;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // 横長の画像
        if (bitmapWidth > bitmapHeight) {
            width = 500;
            height = 500 * bitmapHeight / bitmapWidth;
        }
        // 縦長の画像
        else {
            width = 500 * bitmapWidth / bitmapHeight;
            height = 500;
        }
        // 縮小
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // BitmapをBase64エンコード
    public static String getBase64EncodedString(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
