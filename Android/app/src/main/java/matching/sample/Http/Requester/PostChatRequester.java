package matching.sample.Http.Requester;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import matching.sample.Http.ApiRequester;
import matching.sample.System.Base64Utility;
import matching.sample.System.SaveData;

public class PostChatRequester {

    // チャットメッセージ送信API
    public static void post(String targetId, String message, Callback callback) {

        HashMap<String, String> params = new HashMap<>();

        // API種類
        params.put("command", "postChat");

        // 送信者のユーザID
        params.put("senderId", SaveData.getInstance().userId);

        // チャット相手のユーザID
        params.put("targetId", targetId);

        // チャットメッセージ
        params.put("message", Base64Utility.encode(message));

        // APIコール
        ApiRequester.post(params, new ApiRequester.Callback() {
            @Override
            public void didReceiveData(boolean result, JSONObject json) {
                if (result) {
                    try {
                        String apiResult = json.getString("result");

                        // 処理結果: 成功
                        if (apiResult.equals("0")) {
                            // 呼び出し元に成功を通知
                            callback.didReceiveData(true);
                            return;
                        }
                    } catch (JSONException e) { }
                }

                // 呼び出し元に失敗を通知
                callback.didReceiveData(false);
            }
        });
    }

    // コールバック
    public interface Callback {
        void didReceiveData(boolean result);
    }
}
