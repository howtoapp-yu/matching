package matching.sample.Fragment.Common;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import matching.sample.Fragment.BaseFragment;
import matching.sample.MainActivity;
import matching.sample.R;

public class Toast extends BaseFragment {

    private static MainActivity mActivity;
    private static Toast mToast;

    // 表示メッセージ
    private String mMessage = "";

    // 初期化
    public static void initialize(MainActivity activity) {
        mActivity = activity;
    }

    // トーストを表示する
    public static void show(String message) {

        // 既に表示中の場合は終了
        if ((mToast != null) || (mActivity == null)) {
            return;
        }

        Toast toast = new Toast();
        toast.mMessage = message;

        FragmentTransaction transaction = mActivity
                                        .getSupportFragmentManager()
                                        .beginTransaction();
        transaction.add(mActivity.getSubContainerId(), toast);
        transaction.commitAllowingStateLoss();

        mToast = toast;
    }

    // トーストを停止する
    public static void stop() {

        // 表示中でない場合は終了
        if (mToast == null) {
            return;
        }

        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.remove(mToast);
        transaction.commitAllowingStateLoss();

        mToast = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_toast, null);

        // メッセージを表示
        ((TextView)view.findViewById(R.id.messageTextView)).setText(mMessage);

        // 表示アニメーションを開始
        startAnimation(view);

        return view;
    }

    // 表示アニメーションを開始する
    private void startAnimation(View view) {

        // 透過度を0から1に変更する
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAnimation();
                    }
                }, 2000);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(animation);
    }

    // 非表示アニメーションを開始する
    private void stopAnimation() {

        // 透過度を1から0に変更する
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                stop();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        View view = getView();
        if (view != null) {
            view.startAnimation(animation);
        }
    }
}
