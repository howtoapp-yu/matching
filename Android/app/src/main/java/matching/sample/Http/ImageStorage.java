package matching.sample.Http;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;

import matching.sample.MainActivity;
import matching.sample.System.Base64Utility;
import matching.sample.System.DateUtility;

public class ImageStorage {

    // リクエスト情報
    private static class RequestData {
        public String url;            // 画像URL
        public ImageView imageView;   // 配置先のImageView
        public int errorImageId;      // 取得失敗した場合に表示する画像

        // リクエスト情報の初期化
        public static RequestData create(String url,
                                         ImageView imageView,
                                         int errorImageId) {

            RequestData requestData = new RequestData();
            requestData.url = url;
            requestData.imageView = imageView;
            requestData.errorImageId = errorImageId;
            return requestData;
        }
    }

    // 取得したデータのキャッシュ情報
    private static class CacheData {
        public String url;          // 画像URL
        public Bitmap bitmap;       // 画像
        public Date datetime;       // 取得日時

        // キャッシュ情報の初期化
        public static CacheData create(String url,
                                       Bitmap bitmap,
                                       Date datetime) {

            CacheData cacheData = new CacheData();
            cacheData.url = url;
            cacheData.bitmap = bitmap;
            cacheData.datetime = datetime;
            return cacheData;
        }
    }

    public static int RequestCodeImageStorage = 1005;

    // 画像キャッシュの有効期限
    private static int expirationInterval = 60 * 60;     // 60分

    private static MainActivity mActivity;
    private static ImageStorage mInstance;

    // リクエスト情報の配列
    private ArrayList<RequestData> mRequests = new ArrayList<>();

    // キャッシュ情報の配列
    private ArrayList<CacheData> mCaches = new ArrayList<>();

    // 初期化
    public static void initialize(MainActivity mainActivity) {

        mActivity = mainActivity;

        // パーミッションのチェック
        getInstance().checkPermission();
    }

    // 外部ストレージの書き込み権限を確認し、付与されていなければリクエストする
    public boolean checkPermission() {

        String permissionStr = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int result = ActivityCompat.checkSelfPermission(mActivity, permissionStr);

        if (result != PackageManager.PERMISSION_GRANTED) {

            // 権限をリクエスト
            String[] permissions = new String[]{ permissionStr };
            ActivityCompat.requestPermissions(mActivity,
                    permissions,
                    RequestCodeImageStorage);

            // 権限の付与なし
            return false;
        }

        // 権限が付与済み
        return true;
    }

    public static ImageStorage getInstance() {

        if (mInstance == null) {
            mInstance = new ImageStorage();
        }
        return mInstance;
    }

    // 画像を取得する
    public void fetch(final String url,
                      final ImageView imageView,
                      int errorImageId) {

        fetch(url, imageView, errorImageId, null);
    }

    // 画像を取得する
    public void fetch(final String url,
                      final ImageView imageView,
                      int errorImageId,
                      final Callback callback) {

        // 同一のImageViewに対するリクエストをキャンセルする
        cancelRequest(imageView);

        // キャッシュに画像があれば通信を行わずに返す
        for (int i = 0; i < mCaches.size(); i++) {
            CacheData cacheData = mCaches.get(i);
            // URLが一致
            if (cacheData.url.equals(url)) {
                // 有効期限内の画像のみ採用する
                Date additionalDate = DateUtility.addSecond(new Date(),
                                -1 * expirationInterval);
                if (additionalDate.before(cacheData.datetime)) {
                    imageView.setImageBitmap(cacheData.bitmap);
                    if (callback != null) {
                        // 呼び出し元に通知
                        callback.didReceiveData(true, cacheData.bitmap);
                    }
                    return;
                }
            }
        }

        // リクエスト情報を設定
        mRequests.add(RequestData.create(url, imageView, errorImageId));

        // ローカルファイルを読み出し
        readLocalFile(url, new DidEndProcedureCallback() {
            @Override
            public void didEnd(Bitmap bitmap) {
                // 画像が存在する
                if (bitmap != null) {
                    // 結果を反映する
                    applyResult(url, imageView, bitmap, callback);
                } else {
                    // 画像を外部から取得する
                    fetchRemoteFile(url, new DidEndProcedureCallback() {
                        @Override
                        public void didEnd(Bitmap bitmap) {
                            // 結果を反映する
                            applyResult(url, imageView, bitmap, callback);
                        }
                    });
                }
            }
        });
    }

    // ローカルに保存した画像を取り出す
    public void query(String url, final Callback callback) {

        readLocalFile(url, new DidEndProcedureCallback() {
            @Override
            public void didEnd(Bitmap bitmap) {
                // 呼び出し元に通知
                callback.didReceiveData((bitmap != null), bitmap);
            }
        });
    }

    // ローカルファイルを削除する
    public void deleteLocalFile(String url) {

        File file = createLocalPath(url);
        file.delete();
    }

    // 画像の取得結果を反映する
    private void applyResult(final String url,
                             final ImageView imageView,
                             final Bitmap bitmap,
                             final Callback callback) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // ImageViewに画像をセットする
                for (RequestData requestData : mRequests) {
                    if (requestData.url.equals(url)) {
                        if (bitmap != null) {
                            requestData.imageView.setImageBitmap(bitmap);
                        } else {
                            requestData.imageView.setImageResource(requestData.errorImageId);
                        }
                    }
                }

                // 同一のImageViewのリクエストをキャンセルする
                cancelRequest(imageView);

                // 呼び出し元に通知
                if (callback != null) {
                    callback.didReceiveData((bitmap != null), bitmap);
                }

                if (bitmap != null) {

                    // キャッシュに追加
                    int cacheCount = mCaches.size();
                    for (int i = cacheCount - 1; i >= 0; i--) {
                        CacheData cacheData = mCaches.get(i);
                        if (DateUtility.addSecond(new Date(), -1 * expirationInterval).after(cacheData.datetime)) {
                            mCaches.remove(i);
                        }
                    }
                    if (mCaches.size() > 50) {
                        mCaches.remove(0);
                    }
                    mCaches.add(CacheData.create(url, bitmap, new Date()));
                }
            }
        });
    }

    // 保存先のルートディレクトリを返す
    private File getRootDirectory() {
        return mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    // 保存先のファイルパスを返す
    private File createLocalPath(String url) {
        return new File(getRootDirectory(), Base64Utility.encode(url));
    }

    // ローカルに保存されたファイルを読み出す
    private void readLocalFile(final String url,
                               final DidEndProcedureCallback callback) {

        // 権限の付与なし
        if (!checkPermission()) {
            callback.didEnd(null);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = null;
                try {
                    // ファイルパス
                    File localPath = createLocalPath(url);

                    // 有効期限が切れたファイルを削除する
                    BasicFileAttributes attrs = Files.readAttributes(localPath.toPath(), BasicFileAttributes.class);
                    long creationTime = attrs.creationTime().toMillis();
                    long diff = (new Date().getTime() - creationTime) / 1000;
                    if (diff > ImageStorage.RequestCodeImageStorage) {
                        deleteLocalFile(url);
                        callback.didEnd(null);
                        return;
                    }

                    // ファイルから画像に変換
                    InputStream inputStream = new FileInputStream(localPath);
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (IOException e) {}

                // 呼び出し元に通知
                callback.didEnd(bitmap);
            }
        }).start();
    }

    // 画像データをファイルに保存する
    private void saveLocalFile(final String url,
                               final Bitmap bitmap,
                               final DidEndProcedureCallback callback) {

        // 権限の付与なし
        if (!checkPermission()) {
            callback.didEnd(null);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // ファイルに書き込み
                    FileOutputStream outputStream = new FileOutputStream(createLocalPath(url));
                    boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    if (result) {
                        callback.didEnd(bitmap);
                    } else {
                        callback.didEnd(null);
                    }
                } catch (Exception e) {
                    callback.didEnd(null);
                }
            }
        }).start();
    }

    // 外部から画像を取得する
    private void fetchRemoteFile(final String url,
                                 final DidEndProcedureCallback callback) {

        // HTTP通信を実行
        HttpRequester.RequestType requestType = HttpRequester.RequestType.image;
        HttpRequester http = new HttpRequester(requestType,
                new HttpRequester.Callback() {

            @Override
            public void didReceiveData(boolean result, Object data) {
                // 取得成功
                if (result && (data != null) && (data instanceof Bitmap)) {
                    // ファイルに保存
                    saveLocalFile(url, (Bitmap)data, callback);
                } else {
                    callback.didEnd(null);
                }
            }
        });
        http.execute(url, "GET");
    }

    // 通信をキャンセルする
    public void cancelRequest(ImageView imageView) {

        for (int i = mRequests.size() - 1; i >= 0; i--) {
            if (mRequests.get(i).imageView == imageView) {
                mRequests.remove(i);
            }
        }
    }

    // 取得済みの画像を削除する
    public void removeAll() {

        File rootDir = getRootDirectory();
        for (File file : rootDir.listFiles()) {
            file.delete();
        }

        mCaches.clear();
    }

    // ファイル処理のコールバック
    private interface DidEndProcedureCallback {
        void didEnd(Bitmap bitmap);
    }

    // 画像取得のコールバック
    public interface Callback {
        void didReceiveData(boolean result, Bitmap bitmap);
    }
}