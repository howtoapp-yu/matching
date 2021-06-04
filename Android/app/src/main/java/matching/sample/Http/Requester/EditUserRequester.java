package matching.sample.Http.Requester;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import matching.sample.Http.ApiRequester;
import matching.sample.System.Base64Utility;
import matching.sample.System.BitmapUtility;
import matching.sample.System.SaveData;

public class EditUserRequester {

    // プロフィール編集API
    public static void edit(String name,
                            String message,
                            Bitmap image,
                            Callback callback) {

        HashMap<String, String> params = new HashMap<>();

        // API種類
        params.put("command", "editUser");

        // ユーザID
        params.put("userId", SaveData.getInstance().userId);

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
                            callback.didReceiveData(true);
                            return;
                        }
                    } catch (JSONException e) { }
                }

                // 処理結果: 失敗
                callback.didReceiveData(false);
            }
        });
    }

    // コールバック
    public interface Callback {
        void didReceiveData(boolean result);
    }
}
