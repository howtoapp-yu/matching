package matching.sample.Fragment.Matching;

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

public class MatchingAdapter extends ArrayAdapter<UserData> {

    LayoutInflater mInflater;

    MatchingAdapter(Context context) {
        super(context, 0);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        UserData userData = getItem(position);

        convertView = mInflater.inflate(R.layout.adapter_matching,
                parent,
                false);

        ImageView userImageView = convertView.findViewById(R.id.userImageView);

        // ユーザ画像に角丸を設定
        int radius = (int)(10 * DeviceUtility.getDeviceDensity());
        RoundOutlineProvider.setOutline(userImageView, radius);

        // ユーザ画像を取得
        ImageStorage.getInstance().fetch(UserData.imageUrl(userData.id),
                userImageView,
                R.drawable.no_image);

        // ユーザ名
        ((TextView)convertView.findViewById(R.id.nameTextView)).setText(userData.name);

        // メッセージ
        ((TextView)convertView.findViewById(R.id.messageTextView)).setText(userData.message);

        return convertView;
    }
}
