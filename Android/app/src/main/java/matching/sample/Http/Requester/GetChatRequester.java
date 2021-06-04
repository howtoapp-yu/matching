package matching.sample.Http.Requester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import matching.sample.Http.ApiRequester;
import matching.sample.Http.DataModel.ChatData;
import matching.sample.Http.DataModel.UserData;
import matching.sample.System.SaveData;

public class GetChatRequester {

    // チャットメッセージ取得API
    public static void get(String targetId, Callback callback) {

        HashMap<String, String> params = new HashMap<>();

        // API種類
        params.put("command", "getChat");

        // 送信者のユーザID
        params.put("senderId", SaveData.getInstance().userId);

        // チャット相手のユーザID
        params.put("targetId", targetId);

        // APIコール
        ApiRequester.post(params, new ApiRequester.Callback() {
            @Override
            public void didReceiveData(boolean result, JSONObject json) {
                if (result) {
                    try {
                        String apiResult = json.getString("result");

                        // 処理結果: 成功
                        if (apiResult.equals("0")) {
                            ArrayList<ChatData> chats = new ArrayList<>();

                            // チャット情報を取り出し
                            JSONArray chatArray = json.getJSONArray("chats");
                            for (int i= 0; i < chatArray.length(); i++) {
                                // ChatDataモデルに変換
                                JSONObject obj = chatArray.getJSONObject(i);
                                ChatData chatData = ChatData.create(obj);
                                if (chatData != null) {
                                    chats.add(chatData);
                                }
                            }

                            // 呼び出し元に成功を通知
                            callback.didReceiveData(true, chats);
                            return;
                        }
                    } catch (JSONException e) { }
                }

                // 呼び出し元に失敗を通知
                callback.didReceiveData(false, null);
            }
        });
    }

    // コールバック
    public interface Callback {
        void didReceiveData(boolean result, ArrayList<ChatData> chatDatas);
    }
}
