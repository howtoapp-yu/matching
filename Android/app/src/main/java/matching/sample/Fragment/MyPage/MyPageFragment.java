package matching.sample.Fragment.MyPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.ImageStorage;
import matching.sample.Http.Requester.GetUserRequester;
import matching.sample.R;
import matching.sample.System.DeviceUtility;
import matching.sample.System.RoundOutlineProvider;
import matching.sample.System.SaveData;

public class MyPageFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_my_page, null);

        // タップイベントの設定
        initAction(view);

        // ユーザ情報を表示
        showUserData(view);

        return view;
    }

    // タップイベントを設定する
    private void initAction(View view) {

        // 編集ボタン
        view.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // プロフィール編集画面へ遷移
                ProfileFragment profileFragment = new ProfileFragment();
                getTabbarFragment().stackFragment(profileFragment, AnimationType.horizontal);
            }
        });
    }

    // ユーザ情報を表示する
    private void showUserData(View view) {

        UserData myUserData = GetUserRequester.getInstance()
                                .query(SaveData.getInstance().userId);

        ImageView userImageView = view.findViewById(R.id.userImageView);

        // ユーザ画像に角丸を設定
        int radius = (int)(10 * DeviceUtility.getDeviceDensity());
        RoundOutlineProvider.setOutline(userImageView, radius);

        // 画像を取得
        ImageStorage.getInstance().fetch(UserData.imageUrl(myUserData.id), userImageView, R.drawable.no_image);

        // ユーザ名
        ((TextView)view.findViewById(R.id.nameTextView)).setText(myUserData.name);

        // メッセージ
        ((TextView)view.findViewById(R.id.messageTextView)).setText(myUserData.message);
    }

    // 画面を更新する
    public void reload() {

        View view = getView();
        if (view == null) {
            return;
        }

        showUserData(view);
    }
}
