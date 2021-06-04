package matching.sample.Fragment.Splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Common.Dialog;
import matching.sample.Fragment.MyPage.ProfileFragment;
import matching.sample.Fragment.Tabbar.TabbarFragment;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.Requester.GetUserRequester;
import matching.sample.R;
import matching.sample.System.SaveData;

public class SplashFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_splash, null);

        // 3秒間待機
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // ユーザ情報取得APIをコール
                fetch();
            }
        }, 3000);

        return view;
    }

    // ユーザ情報取得APIをコールする
    private void fetch() {

        // ユーザ情報取得API
        GetUserRequester.getInstance().get(new GetUserRequester.Callback() {
            @Override
            public void didReceiveData(boolean result) {
                // 処理結果: 成功
                if (result) {
                    // 次の画面へ遷移する
                    stackNextFragment();
                } else {
                    // エラー表示
                    Dialog.Action action = new Dialog.Action();
                    action.title = "リトライ";
                    action.callback = new Dialog.Callback() {
                        @Override
                        public void didClose() {
                            // リトライ
                            fetch();
                        }
                    };
                    Dialog.show(Dialog.Style.error, "エラー", "通信に失敗しました", action);
                }
            }
        });
    }

    // 次の画面へ遷移する
    private void stackNextFragment() {

        UserData myUserData = GetUserRequester.getInstance().query(SaveData.getInstance().userId);

        // 自身のユーザ情報がサーバに保存されている場合
        if (myUserData != null) {
            // タブバー画面を表示する
            TabbarFragment tabbarFragment = new TabbarFragment();
            stackFragment(tabbarFragment, AnimationType.none);
        } else {
            // プロフィール設定画面に遷移
            ProfileFragment profileFragment = new ProfileFragment();
            stackFragment(profileFragment, AnimationType.none);
        }
    }
}
