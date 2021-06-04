package matching.sample.Fragment.User.UserDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Common.Dialog;
import matching.sample.Fragment.Common.Loading;
import matching.sample.Fragment.Common.Toast;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.ImageStorage;
import matching.sample.Http.Requester.PostLikeRequester;
import matching.sample.R;

public class UserDetailFragment extends BaseFragment {

    private UserData mUserData;

    public void set(UserData userData) {
        mUserData = userData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_user_detail, null);

        // 表示コンテンツを作成する
        initContents(view);

        // タップイベントを設定する
        initAction(view);

        return view;
    }

    // 表示コンテンツを作成する
    private void initContents(View view) {

        // ユーザ画像を取得
        ImageView userImageView = view.findViewById(R.id.userImageView);
        ImageStorage.getInstance().fetch(UserData.imageUrl(mUserData.id),
                userImageView,
                R.drawable.no_image);

        // ユーザ名
        ((TextView)view.findViewById(R.id.nameTextView)).setText(mUserData.name);

        // メッセージ
        ((TextView)view.findViewById(R.id.messageTextView)).setText(mUserData.message);
    }

    // タップイベントを設定する
    private void initAction(View view) {

        // 戻るボタン
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 画面を閉じる
                popFragment(AnimationType.horizontal);
            }
        });

        // 「いいね！」ボタン
        view.findViewById(R.id.likeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 「いいね！」送信API
                onClickLike();
            }
        });
    }

    // 「いいね！」送信APIをコールする
    private void onClickLike() {

        // ローディング開始
        Loading.start();

        // 「いいね！」送信API
        PostLikeRequester.post(mUserData.id, new PostLikeRequester.Callback() {
            @Override
            public void didReceiveData(boolean result) {

                // ローディングを停止
                Loading.stop();

                // 処理結果: 成功
                if (result) {
                    Toast.show("いいね！を送信しました");
                } else {
                    Dialog.show(Dialog.Style.error, "エラー", "通信に失敗しました");
                }
            }
        });
    }
}
