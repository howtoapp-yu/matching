package matching.sample.System;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Date;

import matching.sample.MainActivity;

import static android.app.Activity.RESULT_OK;

public class GalleryManager {

    // 画像の選択方法
    public enum SourceType {
        Gallery
    }

    public static int RequestCodeGallery = 1001;

    private static GalleryManager mManager = new GalleryManager();
    private static MainActivity mActivity;

    // 画像の選択方法
    private SourceType mSourceType;

    // 画像選択完了時のコールバック
    private Callback mCallback;

    // 初期化
    public static void initialize(MainActivity activity) {
        mActivity = activity;
    }

    public static GalleryManager getInstance() {
        return mManager;
    }

    // 画像ピッカーを開く
    public void open(SourceType sourceType, Callback callback) {

        // 画像の選択方法
        mSourceType = sourceType;

        // コールバック
        mCallback = callback;

        // ギャラリー
        if (sourceType == SourceType.Gallery) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            mActivity.startActivityForResult(intent,
                    GalleryManager.RequestCodeGallery);
        }
    }

    // 画像データがシステムから渡された
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        // 成功
        if (resultCode == RESULT_OK) {
            // 画像の選択結果の場合
            if (requestCode == RequestCodeGallery) {
                // 呼び出し元に画像を反映
                didSelectImage(data);
            }
        }
    }

    // 呼び出し元に画像を反映する
    public void didSelectImage(Intent data) {

        Uri uri = null;
        Bitmap bitmap = null;

        // ギャラリー
        if (mSourceType == SourceType.Gallery) {
            uri = data.getData();
            if (uri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                } catch (IOException e) {
                    mSourceType = null;
                    mCallback = null;
                    return;
                }
            }
        }

        if ((uri != null) && (bitmap != null)) {
            // 画像を正しい向きに回転させる
            Bitmap rotatedBitmap = rotateIfNeeded(uri, bitmap);
            if (rotatedBitmap != null) {
                mCallback.didSelectImage(rotatedBitmap);
            }
        }

        mSourceType = null;
        mCallback = null;
    }

    // 画像を正しい向きに回転させる
    private Bitmap rotateIfNeeded(Uri uri, Bitmap bitmap) {

        try {
            ParcelFileDescriptor pfd = mActivity.getContentResolver()
                                        .openFileDescriptor(uri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();

            ExifInterface ei = new ExifInterface(fd);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                ExifInterface.ORIENTATION_NORMAL);

            pfd.close();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 190);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            return bitmap;
        }
    }

    // 画像を回転させる
    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true);

        bitmap.recycle();

        return rotatedImg;
    }

    // コールバック
    public interface Callback {
        // 画像の選択が完了した
        void didSelectImage(Bitmap bitmap);
    }
}
