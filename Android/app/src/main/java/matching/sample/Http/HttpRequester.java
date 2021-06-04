package matching.sample.Http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import matching.sample.System.Constants;

public class HttpRequester extends AsyncTask<String, Integer, Object> {

    // リクエスト種類
    public enum RequestType {
        string,     // API
        image       // 画像
    }

    // リクエスト種類
    private RequestType mRequestType;

    // コールバック
    private Callback mCallback = null;

    public HttpRequester(RequestType requestType, Callback callback) {
        mRequestType = requestType;
        mCallback = callback;
    }

    @Override
    protected Object doInBackground(String... params) {

        HttpURLConnection conn = null;
        Object object = null;

        try {
            // URLを引数から取り出し
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();

            // タイムアウトの設定
            conn.setReadTimeout(Constants.HttpReadTimeout);
            conn.setConnectTimeout(Constants.HttpConnectTimeout);

            // キャッシュ:OFF
            conn.setUseCaches(false);

            // METHODを指定
            conn.setRequestMethod(params[1]);

            // POSTパラメータを設定
            conn.setDoInput(true);
            conn.setDoOutput(true);
            if (params.length >= 3) {
                String paramStr = params[2];
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
                printWriter.print(paramStr);
                printWriter.close();
            }

            // 接続
            conn.connect();

            // ステータスコード: 2XXを成功として扱う
            int resp = conn.getResponseCode();
            if ((int) (resp / 100) == 2) {
                InputStream stream = conn.getInputStream();

                if (mRequestType == RequestType.string) {
                    // 文字列として扱う
                    object = streamToString(stream);
                } else {
                    // 画像として扱う
                    object = streamToBitmap(stream);
                }
                stream.close();
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return object;
    }

    @Override
    protected void onPostExecute(Object data) {
        super.onPostExecute(data);

        // 受信処理後、呼び出し元に通知
        if (data != null) {
            mCallback.didReceiveData(true, data);
        } else {
            mCallback.didReceiveData(false, null);
        }
    }

    // InputStreamから文字列へ変換する
    private String streamToString(InputStream stream) {

        try {
            StringBuffer buffer = new StringBuffer();
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader bufReder = new BufferedReader(reader);
            String line = "";
            while ((line = bufReder.readLine()) != null) {
                if (buffer.length() > 0) {
                    buffer.append("\n");
                }
                buffer.append(line);
            }
            return buffer.toString();

        } catch (Exception e) {
            return null;
        }
    }

    // InputStreamから画像へ変換する
    private Bitmap streamToBitmap(InputStream stream) {

        try {
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
            return null;
        }
    }

    // コールバック
    public interface Callback {
        void didReceiveData(boolean result, Object data);
    }
}