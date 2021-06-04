package matching.sample.Fragment;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import matching.sample.Fragment.Splash.SplashFragment;
import matching.sample.Fragment.Tabbar.TabbarFragment;
import matching.sample.R;
import matching.sample.System.DeviceUtility;

public class BaseFragment extends Fragment {

    public enum AnimationType {
        none,
        horizontal,
        vertical
    }

    private AnimationType mAnimationType = AnimationType.none;
    private boolean mAnimating = false;
    private boolean mDidStack = false;

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view == null) {
            return;
        }
        if (mDidStack) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(params);
        }

        if (mAnimationType != AnimationType.none) {
            int fromXDelta = (mAnimationType == AnimationType.horizontal) ? (int)(DeviceUtility.getWindowSize().x) : 0;
            int fromYdelta = (mAnimationType == AnimationType.horizontal) ? 0 : (int)(DeviceUtility.getWindowSize().y);
            TranslateAnimation animation = new TranslateAnimation(fromXDelta, 0, fromYdelta, 0);
            animation.setDuration(200);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
        mDidStack = true;
    }

    public void stackFragment(BaseFragment fragment, AnimationType animationType) {

        if (mAnimating) {
            return;
        }

        fragment.mAnimationType = animationType;
        if (animationType != AnimationType.none) {
            mAnimating = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimating = false;
                }
            }, 500);
        }

        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.rootLayout, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public void popFragment(AnimationType animationType) {

        View view = getView();
        if (view == null) {
            return;
        }

        if (mAnimating) {
            return;
        }
        if (animationType != AnimationType.none) {
            mAnimating = true;
        }

        if (animationType != AnimationType.none) {
            int toXDelta = (animationType == AnimationType.horizontal) ? (int)(DeviceUtility.getWindowSize().x) : 0;
            int toYdelta = (animationType == AnimationType.horizontal) ? 0 : (int)(DeviceUtility.getWindowSize().y);
            TranslateAnimation animation = new TranslateAnimation(0, toXDelta, 0, toYdelta);
            animation.setDuration(200);
            animation.setFillAfter(true);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    mAnimating = false;
                    popFragment();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            view.startAnimation(animation);
        } else {
            popFragment();
        }
    }

    private void popFragment() {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(this);
            transaction.commitAllowingStateLoss();
        }
    }

    public SplashFragment getSplashFragment() {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager manager = activity.getSupportFragmentManager();
            if (manager != null) {
                for (Fragment fragment : manager.getFragments()) {
                    if (fragment instanceof SplashFragment) {
                        return (SplashFragment)fragment;
                    }
                }
            }
        }
        return null;
    }

    public TabbarFragment getTabbarFragment() {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager manager = activity.getSupportFragmentManager();
            if (manager != null) {
                for (Fragment fragment : manager.getFragments()) {
                    if (fragment instanceof TabbarFragment) {
                        return (TabbarFragment)fragment;
                    }
                }
            }
        }
        return null;
    }
}
