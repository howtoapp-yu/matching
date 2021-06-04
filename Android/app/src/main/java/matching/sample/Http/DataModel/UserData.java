package matching.sample.Http.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import matching.sample.System.Base64Utility;
import matching.sample.System.Constants;

public class UserData {

    // ユーザID
    public String id;

    // ユーザ名
    public String name;

    // メッセージ　
    public String message;

    // 「いいね！」送信対象のユーザIDリスト
    public ArrayList<String> likeTargetIds;

    // JSONオブジェクトからUserDataインスタンスを生成する
    public static UserData create(JSONObject json) {

        try {
            UserData userData = new UserData();

            // ユーザID
            userData.id = json.getString("id");

            // ユーザ名
            String name = json.getString("name");
            userData.name = Base64Utility.decode(name);

            // メッセージ
            String message = json.getString("message");
            userData.message = Base64Utility.decode(message);

            // 「いいね！」送信対象のユーザIDリスト
            userData.likeTargetIds = new ArrayList<>();
            JSONArray likeTargetIdArray = json.getJSONArray("likeTargetIds");
            for (int i = 0; i < likeTargetIdArray.length(); i++) {
                userData.likeTargetIds.add(likeTargetIdArray.getString(i));
            }

            return userData;

        } catch (JSONException e) {
            return null;
        }
    }

    // ユーザのプロフィール画像のURLを返す
    public static String imageUrl(String userId) {
        return Constants.ServerRootUrl + "data/user/" + userId + "/image";
    }
}
