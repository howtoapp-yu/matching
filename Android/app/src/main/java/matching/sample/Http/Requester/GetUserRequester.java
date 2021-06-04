package matching.sample.Http.Requester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import matching.sample.Http.ApiRequester;
import matching.sample.Http.DataModel.UserData;

public class GetUserRequester {

    // データ保持のためシングルトン化
    private static GetUserRequester mInstance = new GetUserRequester();

    public static GetUserRequester getInstance() {
        return mInstance;
    }

    // ユーザ情報の配列
    public ArrayList<UserData> dataList = new ArrayList<>();

    // ユーザ情報取得API
    public void get(Callback callback) {

        HashMap<String, String> params = new HashMap<>();

        // API種類
        params.put("command", "getUser");

        // APIコール
        ApiRequester.post(params, new ApiRequester.Callback() {
            @Override
            public void didReceiveData(boolean result, JSONObject json) {
                if (result) {
                    try {
                        String apiResult = json.getString("result");

                        // 処理結果: 成功
                        if (apiResult.equals("0")) {
                            ArrayList<UserData> users = new ArrayList<>();

                            // ユーザ情報の配列を取り出し
                            JSONArray userArray = json.getJSONArray("users");
                            for (int i= 0; i < userArray.length(); i++) {
                                // UserDataモデルに変換
                                JSONObject obj = userArray.getJSONObject(i);
                                UserData userData = UserData.create(obj);
                                if (userData != null) {
                                    users.add(userData);
                                }
                            }
                            GetUserRequester.this.dataList = users;

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

    // 特定ユーザの情報を取り出す
    public UserData query(String userId) {

        for (UserData userData : dataList) {
            if (userData.id.equals(userId)) {
                return userData;
            }
        }
        return null;
    }
}
