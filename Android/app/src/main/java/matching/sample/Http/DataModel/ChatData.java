package matching.sample.Http.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import matching.sample.System.Base64Utility;
import matching.sample.System.DateUtility;

public class ChatData {

    // 送信日時
    public Date datetime;

    // 送信者のユーザID
    public String senderId;

    // チャット相手のユーザID
    public String targetId;

    // メッセージ
    public String message;

    // JSONオブジェクトからChatDataインスタンスを生成する
    public static ChatData create(JSONObject json) {

        try {
            ChatData chatData = new ChatData();

            // 送信日時
            String datetime = json.getString("datetime");
            chatData.datetime = DateUtility.stringToDate(datetime, "yyyyMMddkkmmss");

            // 送信者のユーザID
            String senderId = json.getString("senderId");
            chatData.senderId = senderId;

            // チャット相手のユーザID
            String targetId = json.getString("targetId");
            chatData.targetId = targetId;

            // メッセージ
            String message = json.getString("message");
            chatData.message = Base64Utility.decode(message);

            return chatData;

        } catch (JSONException e) {
            return null;
        }
    }
}
