package matching.sample.Fragment.Matching.Chat;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Common.Dialog;
import matching.sample.Fragment.Common.Toast;
import matching.sample.Http.DataModel.ChatData;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.Requester.GetChatRequester;
import matching.sample.Http.Requester.PostChatRequester;
import matching.sample.R;
import matching.sample.System.DeviceUtility;

public class ChatFragment extends BaseFragment {

    private ChatAdapter mAdapter;

    // ユーザ情報
    private UserData mUserData;

    // 定期通信の実行中か
    private boolean mIsActive = true;

    // 初回の更新か
    private boolean mIsInitialReload = true;

    // スクロールが必要か
    private boolean mIsNeedScrollPosition = false;

    // ユーザ情報を設定する
    public void set(UserData userData) {
        mUserData = userData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_chat, null);

        // コンテンツを初期化
        initContents(view);

        // タップイベントを設定
        initAction(view);

        // ListViewを初期化
        initListView(view);

        // 定期通信をスタート
        startTimer();

        return view;
    }

    // コンテンツを初期化する
    private void initContents(View view) {

        // チャット相手の名前を表示
        TextView headerNameTextView = view.findViewById(R.id.headerNameTextView);
        headerNameTextView.setText(mUserData.name);
    }

    // タップイベントを設定する
    private void initAction(View view) {

        // 戻るボタン
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsActive = false;

                // キーボードを閉じる
                DeviceUtility.hideSoftKeyboard();

                // 画面を閉じる
                popFragment(AnimationType.horizontal);
            }
        });

        // メッセージ送信ボタン
        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードを閉じる
                DeviceUtility.hideSoftKeyboard();

                onClickSend();
            }
        });
    }

    // ListViewを初期化
    private void initListView(View view) {

        mAdapter = new ChatAdapter(getActivity());

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        // ListViewがスクロールされた
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // スクロール位置が終端付近であれば、次回更新時に終端までスクロールする
                mIsNeedScrollPosition = (totalItemCount < firstVisibleItem + visibleItemCount + 2);
            }
        });
    }

    // 定期通信を開始する
    private void startTimer() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mIsActive) {
                    // チャットメッセージ取得API
                    fetch();

                    new Handler().postDelayed(this, 5000);
                }
            }
        };

        new Handler().post(runnable);
    }

    // チャットメッセージ取得APIをコールする
    private void fetch() {

        // チャットメッセージ取得API
        GetChatRequester.get(mUserData.id, new GetChatRequester.Callback() {
            @Override
            public void didReceiveData(boolean result, ArrayList<ChatData> chatDatas) {
                // 処理結果: 成功
                if (result) {
                    // 前回から変化があれば画面を更新
                    if (chatDatas.size() != mAdapter.getCount()) {
                        reload(chatDatas);
                    }
                } else {
                    Toast.show("通信に失敗しました");
                }
            }
        });
    }

    // 画面を更新する
    private void reload(ArrayList<ChatData> chatDatas) {

        View view = getView();
        if (view == null) {
            return;
        }

        mAdapter.clear();

        // 全てのチャット情報を表示する
        for (ChatData chatData : chatDatas) {
            mAdapter.add(chatData);
        }

        // 必要があれば終端までスクロール
        boolean needScroll = mIsInitialReload | mIsNeedScrollPosition;
        if (needScroll) {
            ListView listView = view.findViewById(R.id.listView);
            listView.smoothScrollToPosition(listView.getCount() - 1);
        }
        mIsInitialReload = false;
    }

    // メッセージ送信ボタンが押された
    private void onClickSend() {

        View view = getView();

        EditText messageEditText = view.findViewById(R.id.messageEditText);
        String message = messageEditText.getText().toString();
        if (message.length() == 0) {
            return;
        }

        // チャットメッセージ送信API
        PostChatRequester.post(mUserData.id, message, new PostChatRequester.Callback() {
            @Override
            public void didReceiveData(boolean result) {
                // 処理結果: 成功
                if (result) {
                    // 最新情報を取得
                    fetch();
                } else {
                    Dialog.show(Dialog.Style.error, "エラー", "通信に失敗しました");
                }
            }
        });

        messageEditText.setText("");
    }
}
