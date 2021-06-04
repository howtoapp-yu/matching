package matching.sample.Fragment.Common;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;

import matching.sample.Fragment.BaseFragment;
import matching.sample.MainActivity;
import matching.sample.R;

public class Dialog extends BaseFragment {

    // ボタンアクション情報
    public static class Action {

        // ボタンタイトル
        public String title;

        // タップ時のコールバック
        public Callback callback;

        // 背景色
        public ActionColor color;
    }

    // ボタンの背景色
    public enum ActionColor {
        success,
        error,
        cancel;
    }

    // ダイアログ表示スタイル
    public enum Style {
        success,        // 成功
        error           // エラー、警告
    }

    private static MainActivity mActivity;

    // ダイアログ表示スタイル
    private Style mStyle;

    // ダイアログタイトル
    private String mTitle;

    // ダイアログメッセージ
    private String mMessage;

    // ボタンアクションの配列
    private ArrayList<Action> mActions;

    // 初期化
    public static void initialize(MainActivity activity) {
        mActivity = activity;
    }

    // ダイアログを表示する
    public static void show(Style style,
                            String title,
                            String message) {

        Action action = new Action();
        action.title = "OK";

        ArrayList<Action> actions = new ArrayList<>(Arrays.asList(action));

        Dialog.show(style, title, message, actions);
    }

    // ダイアログを表示する
    public static void show(Style style,
                            String title,
                            String message,
                            Action action) {

        Dialog.show(style,
                title,
                message,
                new ArrayList<Action>(Arrays.asList(action)));
    }

    // ダイアログを表示する
    public static void show(Style style,
                            String title,
                            String message,
                            ArrayList<Action> actions) {

        if (mActivity == null) {
            return;
        }

        Dialog dialog = new Dialog();
        FragmentTransaction transaction = mActivity
                                        .getSupportFragmentManager()
                                        .beginTransaction();
        transaction.add(mActivity.getSubContainerId(), dialog);
        transaction.commitAllowingStateLoss();

        dialog.mStyle = style;
        dialog.mTitle = title;
        dialog.mMessage = message;
        dialog.mActions = actions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_dialog, null);

        // コンテンツを表示する
        initContents(view);

        return view;
    }

    // コンテンツを表示する
    private void initContents(View view) {

        // タイトル
        ((TextView)view.findViewById(R.id.titleTextView)).setText(mTitle);

        // メッセージ
        ((TextView)view.findViewById(R.id.messageTextView)).setText(mMessage);

        // ボタンアクション
        for (int i = 0; i < mActions.size(); i++) {
            final int fi = i;
            final Action action = mActions.get(i);

            // アクションボタンを生成する
            Button button = new Button(mActivity);
            button.setText(action.title);
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            button.setTypeface(Typeface.DEFAULT_BOLD);
            button.setTextColor(Color.WHITE);

            // アクションボタンの表示色設定
            if (action.color == null) {
                if (mStyle == Style.success) {
                    button.setBackgroundResource(R.drawable.shape_dialog_success_button);
                } else {
                    button.setBackgroundResource(R.drawable.shape_dialog_error_button);
                }
            } else {
                if (action.color == ActionColor.success) {
                    button.setBackgroundResource(R.drawable.shape_dialog_success_button);
                } else if (action.color == ActionColor.error) {
                    button.setBackgroundResource(R.drawable.shape_dialog_error_button);
                } else {
                    button.setBackgroundResource(R.drawable.shape_dialog_cancel_button);
                }
            }

            float density = mActivity.getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams params = new ViewGroup
                                                .MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                    (int)(40 * density));
            params.topMargin = (int)(12 * density);
            button.setLayoutParams(params);

            // タップイベントを設定
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (action.callback != null) {
                        // 呼び出し元に通知
                        action.callback.didClose();
                    }

                    // ダイアログを非表示
                    FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                    transaction.remove(Dialog.this);
                    transaction.commitAllowingStateLoss();
                }
            });
            ((LinearLayout)view.findViewById(R.id.dialogActionBaseLayout)).addView(button);
        }
    }

    // kオールバック
    public interface Callback {
        void didClose();
    }
}
