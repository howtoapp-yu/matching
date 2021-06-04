package matching.sample.Http.Requester;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import matching.sample.Http.ApiRequester;
import matching.sample.System.Base64Utility;
import matching.sample.System.BitmapUtility;

public class RegisterUserRequester {

    // ユーザ登録API
    public static void register(String name,
                                String message,
                                Bitmap image,
                                Callback callback) {

        HashMap<String, String> params = new HashMap<>();

        // API種類
        params.put("command", "registerUser");

        // ユーザ名
        params.put("name", Base64Utility.encode(name));

        // メッセージ
        params.put("message", Base64Utility.encode(message));

        // プロフィール画像
        if (image != null) {
            params.put("image", BitmapUtility.getBase64EncodedString(image));
        } else {
            params.put("image", "");
        }

        // APIコール
        ApiRequester.post(params, new ApiRequester.Callback() {
            @Override
            public void didReceiveData(boolean result, JSONObject json) {
                if (result) {
                    try {
                        String apiResult = json.getString("result");

                        // 処理結果: 成功
                        if (apiResult.equals("0")) {
                            // 発行されたユーザID
                            String userId = json.getString("userId");
                            callback.didReceiveData(true, userId);
                            return;
                        }
                    } catch (JSONException e) { }
                }

                // APIコールに失敗
                callback.didReceiveData(false, null);
            }
        });
    }

    public interface Callback {
        void didReceiveData(boolean result, String userId);
    }
}
