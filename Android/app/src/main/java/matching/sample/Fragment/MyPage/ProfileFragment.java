package matching.sample.Fragment.MyPage;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Common.Dialog;
import matching.sample.Fragment.Common.Loading;
import matching.sample.Fragment.Splash.SplashFragment;
import matching.sample.Fragment.Tabbar.TabbarFragment;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.ImageStorage;
import matching.sample.Http.Requester.EditUserRequester;
import matching.sample.Http.Requester.GetUserRequester;
import matching.sample.Http.Requester.RegisterUserRequester;
import matching.sample.R;
import matching.sample.System.BitmapUtility;
import matching.sample.System.DeviceUtility;
import matching.sample.System.GalleryManager;
import matching.sample.System.RoundOutlineProvider;
import matching.sample.System.SaveData;

public class ProfileFragment extends BaseFragment {

    private boolean mIsNewRegister = false;
    private Bitmap mSelectedImage = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_profile, null);

        // コンテンツを初期化
        initContents(view);

        // タップイベントを設定
        initAction(view);

        return view;
    }

    // コンテンツを初期化する
    private void initContents(View view) {

        ImageView userImageView = view.findViewById(R.id.userImageView);

        // ユーザ画像に角丸を付ける
        int radius = (int)(80 * DeviceUtility.getDeviceDensity());
        RoundOutlineProvider.setOutline(userImageView, radius);

        UserData myUserData = GetUserRequester.getInstance().query(SaveData.getInstance().userId);

        // プロフィール編集時はユーザ情報を表示する
        if (myUserData != null) {
            // 画像を表示
            ImageStorage.getInstance().fetch(UserData.imageUrl(myUserData.id), userImageView, R.drawable.image_guide);
            // ユーザ名
            ((TextView)view.findViewById(R.id.nameEditText)).setText(myUserData.name);
            // メッセージ
            ((TextView)view.findViewById(R.id.messageEditText)).setText(myUserData.message);
        }
        // 新規登録時
        else {
            view.findViewById(R.id.backButton).setVisibility(View.GONE);
            mIsNewRegister = true;
        }
    }

    // タップイベントを設定する
    private void initAction(View view) {

        // 戻るボタン
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードを隠す
                DeviceUtility.hideSoftKeyboard();

                // 画面を閉じる
                popFragment(AnimationType.horizontal);
            }
        });

        // ユーザ画像
        view.findViewById(R.id.userImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードを隠す
                DeviceUtility.hideSoftKeyboard();

                // 画像ピッカーを開く
                GalleryManager.getInstance().open(GalleryManager.SourceType.Gallery,
                                                    new GalleryManager.Callback() {
                    @Override
                    public void didSelectImage(Bitmap bitmap) {
                        // 画像を縮小
                        Bitmap profileImage = BitmapUtility.createProfileBitmap(bitmap);
                        View view = getView();
                        if ((profileImage != null) && (view != null)) {
                            // 選択結果を反映
                            ((ImageView)view.findViewById(R.id.userImageView)).setImageBitmap(profileImage);
                            mSelectedImage = profileImage;
                        }
                    }
                });
            }
        });

        // 設定ボタン
        view.findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードを隠す
                DeviceUtility.hideSoftKeyboard();

                // プロフィール編集API
                onClickSend();
            }
        });
    }

    // 設定ボタンが押された
    private void onClickSend() {

        View view = getView();

        // ユーザ名
        String name = ((EditText)view.findViewById(R.id.nameEditText)).getText().toString();

        // メッセージ
        String message = ((EditText)view.findViewById(R.id.messageEditText)).getText().toString();

        // ユーザ名が未入力
        if (name.length() == 0) {
            Dialog.show(Dialog.Style.error, "エラー", "名前を入力してください");
            return;
        }

        // メッセージが未入力
        if (message.length() == 0) {
            Dialog.show(Dialog.Style.error, "エラー", "メッセージを入力してください");
            return;
        }

        // 新規登録
        if (mIsNewRegister) {
            registerUser(name, message);
        }
        // プロフィール編集
        else {
            editUser(name, message);
        }
    }

    // ユーザ登録APIをコールする
    private void registerUser(String name, String message) {

        // ローディング開始
        Loading.start();

        // ユーザ登録APIをコール
        RegisterUserRequester.register(name,
                message,
                mSelectedImage,
                new RegisterUserRequester.Callback() {

            @Override
            public void didReceiveData(boolean result, String userId) {
                // 処理結果: 成功
                if (result) {
                    // ユーザIDを保存
                    SaveData saveData = SaveData.getInstance();
                    saveData.userId = userId;
                    saveData.save();

                    // 最新情報を取得
                    fetchUser();

                } else {
                    Loading.stop();
                    Dialog.show(Dialog.Style.error,
                            "エラー",
                            "ユーザー登録に失敗しました");
                }
            }
        });
    }

    // プロフィール編集APIをコールする
    private void editUser(String name, String message) {

        // ローディング開始
        Loading.start();

        // プロフィール編集APIをコール
        EditUserRequester.edit(name,
                message,
                mSelectedImage,
                new EditUserRequester.Callback() {

            @Override
            public void didReceiveData(boolean result) {
                // 処理結果: 成功
                if (result) {
                    // 画像キャッシュを削除
                    ImageStorage.getInstance().removeAll();

                    // 最新情報を取得
                    fetchUser();
                } else {
                    Loading.stop();
                    Dialog.show(Dialog.Style.error, "エラー", "プロフィール設定に失敗しました");
                }
            }
        });
    }

    // 最新のユーザ情報を取得する
    private void fetchUser() {

        // ユーザ情報取得API
        GetUserRequester.getInstance().get(new GetUserRequester.Callback() {
            @Override
            public void didReceiveData(boolean result) {

                // ローディング停止
                Loading.stop();

                // 処理結果: 成功
                if (result) {
                    // 新規登録時
                    if (mIsNewRegister) {
                        // タブバーを表示
                        stackTabbar();
                    }
                    // プロフィール編集時
                    else {
                        // 全ての画面を更新
                        TabbarFragment tabbarFragment = getTabbarFragment();
                        if (tabbarFragment != null) {
                            tabbarFragment.reload();
                        }

                        // 成功を表示
                        showSuccess();
                    }
                } else {
                    Dialog.Action action = new Dialog.Action();
                    action.title = "リトライ";
                    action.callback = new Dialog.Callback() {
                        @Override
                        public void didClose() {
                            // リトライ
                            fetchUser();
                        }
                    };
                    Dialog.show(Dialog.Style.error,
                            "エラー",
                            "ユーザー情報の取得に失敗しました",
                            action);
                }
            }
        });
    }

    // タブバー画面を表示する
    private void stackTabbar() {

        TabbarFragment tabbarFragment = new TabbarFragment();
        getSplashFragment().stackFragment(tabbarFragment, AnimationType.none);

        popFragment(AnimationType.none);
    }

    // 処理が成功した事を表示する
    private void showSuccess() {

        Dialog.Action action = new Dialog.Action();
        action.title = "OK";
        action.callback = new Dialog.Callback() {
            @Override
            public void didClose() {
                // 画面を閉じる
                popFragment(AnimationType.horizontal);
            }
        };
        Dialog.show(Dialog.Style.success,
                "確認",
                "プロフィールを更新しました",
                action);
    }
}
