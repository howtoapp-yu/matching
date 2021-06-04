package matching.sample.System;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import matching.sample.MainActivity;

import static android.content.Context.WINDOW_SERVICE;

public class DeviceUtility {

    private static MainActivity mActivity;

    // ルートウィンドウのサイズ
    private static Point mWindowSize = null;

    // 端末の画面解像度
    private static float mDensity = -1;

    // 初期化
    public static void initialize(MainActivity activity) {

        mActivity = activity;

        // ルートウィンドウのサイズを取得
        WindowManager wm = (WindowManager)activity
                            .getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        mWindowSize = size;

        // 画面解像度を取得
        mDensity = activity.getResources().getDisplayMetrics().density;
    }

    // ルートウィンドウのサイズを返す
    public static Point getWindowSize() {
        return mWindowSize;
    }

    // 端末の画面解像度を返す
    public static float getDeviceDensity(){
        return mDensity;
    }

    // キーボードを隠す
    public static void hideSoftKeyboard() {

        InputMethodManager imm = (InputMethodManager)mActivity
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = mActivity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
