package matching.sample.Fragment.User.UserList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.ImageStorage;
import matching.sample.R;
import matching.sample.System.DeviceUtility;
import matching.sample.System.RoundOutlineProvider;

public class UserListAdapter extends ArrayAdapter<UserData> {

    LayoutInflater mInflater;

    UserListAdapter(Context context) {
        super(context, 0);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserData userData = getItem(position);

        convertView = mInflater.inflate(R.layout.adapter_user_list,
                parent,
                false);

        float density = DeviceUtility.getDeviceDensity();

        // GridViewの横幅
        int gridViewWidth = DeviceUtility.getWindowSize().x - (int)(20 * density);

        // ImageViewの横幅
        int imageViewWidth = gridViewWidth / 2 - (int)(10 * density);

        // ImageViewの高さを設定
        ImageView userImageView = convertView.findViewById(R.id.userImageView);
        userImageView.getLayoutParams().height = imageViewWidth * 2 / 3;

        // ImageViewに角丸を付ける
        RoundOutlineProvider.setOutline(userImageView, (int)(10 * density));

        // 画像を取得
        ImageStorage.getInstance().fetch(UserData.imageUrl(userData.id), userImageView, R.drawable.no_image);

        // ユーザ名
        ((TextView)convertView.findViewById(R.id.nameTextView)).setText(userData.name);

        // メッセージ
        ((TextView)convertView.findViewById(R.id.messageTextView)).setText(userData.message);

        return convertView;
    }
}
