package matching.sample.Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.StringJoiner;

import matching.sample.System.Constants;

public class ApiRequester {

    // APIをコール
    public static void post(HashMap<String, String> params,
                            final Callback callback) {

        HttpRequester.RequestType requestType = HttpRequester.RequestType.string;

        HttpRequester http = new HttpRequester(requestType,
                new HttpRequester.Callback() {

            @Override
            public void didReceiveData(boolean result, Object data) {
                // 成功
                if (result) {
                    try {
                        // JSON変換
                        JSONObject json = new JSONObject((String)data);

                        // 呼び出し元に成功を通知
                        callback.didReceiveData(true, json);
                        return;

                    } catch (JSONException e) { }
                }

                // 呼び出し元に失敗を通知
                callback.didReceiveData(false, null);
            }
        });

        // POSTパラメータを格納する
        StringJoiner paramJoiner = new StringJoiner("&");
        for (String key : params.keySet()) {
            paramJoiner.add(key + "=" + params.get(key));
        }

        // HTTP通信を実行
        http.execute(Constants.ServerApiUrl, "POST", paramJoiner.toString());
    }

    // コールバック
    public interface Callback {
        void didReceiveData(boolean result, JSONObject json);
    }
}