package matching.sample.Fragment.Common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

import matching.sample.Fragment.BaseFragment;
import matching.sample.MainActivity;
import matching.sample.R;

public class Loading extends BaseFragment {

    private static MainActivity mActivity;
    private static Loading mLoading;

    // 初期化
    public static void initialize(MainActivity activity) {
        mActivity = activity;
    }

    // ローディングの開始
    public static void start() {

        // 既に表示中の場合は終了
        if ((mLoading != null) || (mActivity == null)) {
            return;
        }

        Loading loading = new Loading();
        FragmentTransaction transaction = mActivity
                                        .getSupportFragmentManager()
                                        .beginTransaction();
        transaction.add(mActivity.getSubContainerId(), loading);
        transaction.commitAllowingStateLoss();

        mLoading = loading;
    }

    // ローディングの終了
    public static void stop() {

        // 表示されていない場合は終了
        if (mLoading == null) {
            return;
        }

        FragmentTransaction transaction = mActivity
                                            .getSupportFragmentManager()
                                            .beginTransaction();
        transaction.remove(mLoading);
        transaction.commitAllowingStateLoss();

        mLoading = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_loading, null);

        // 1.2秒でローディング画像を1回転させる
        ImageView imageView = (ImageView)view.findViewById(R.id.loadingImageView);
        RotateAnimation animation = new RotateAnimation(0.0f,
                360.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        animation.setDuration(1200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(animation);

        return view;
    }
}
