package matching.sample.Fragment.Matching.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import matching.sample.Http.DataModel.ChatData;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.ImageStorage;
import matching.sample.R;
import matching.sample.System.DeviceUtility;
import matching.sample.System.RoundOutlineProvider;
import matching.sample.System.SaveData;

public class ChatAdapter extends ArrayAdapter<ChatData> {

    LayoutInflater mInflater;

    ChatAdapter(Context context) {
        super(context, 0);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        ChatData chatData = getItem(position);

        // 自身が送信したメッセージ
        if (chatData.senderId.equals(SaveData.getInstance().userId)) {
            convertView = mInflater.inflate(R.layout.adapter_chat_send,
                    parent,
                    false);

            // チャットメッセージ
            ((TextView)convertView.findViewById(R.id.messageTextView)).setText(chatData.message);

        }
        // チャット相手が送信したメッセージ
        else {
            convertView = mInflater.inflate(R.layout.adapter_chat_send,
                    parent,
                    false);

            // チャット相手のユーザ画像
            ImageView userImageView = convertView.findViewById(R.id.userImageView);

            // 画像に角丸を付ける
            int radius = (int)(10 * DeviceUtility.getDeviceDensity());
            RoundOutlineProvider.setOutline(userImageView, radius);

            // 画像を取得
            String imageUrl = UserData.imageUrl(chatData.senderId);
            ImageStorage.getInstance().fetch(imageUrl, userImageView, R.drawable.no_image);

            // チャットメッセージ
            TextView messageTextView = convertView.findViewById(R.id.messageTextView);
            messageTextView.setText(chatData.message);
        }

        return convertView;
    }
}
