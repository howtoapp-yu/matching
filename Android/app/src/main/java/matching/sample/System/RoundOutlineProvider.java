package matching.sample.System;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

public class RoundOutlineProvider extends ViewOutlineProvider {

    // 角丸の半径
    private int mRadius = 0;

    @Override
    public void getOutline(View view, Outline outline) {

        outline.setRoundRect(0,
                0,
                view.getWidth(),
                view.getHeight(), mRadius);

        view.setClipToOutline(true);
    }

    // 角丸を設定する
    public static void setOutline(View view, int radius) {

        RoundOutlineProvider provider = new RoundOutlineProvider();
        provider.mRadius = radius;
        view.setOutlineProvider(provider);
    }
}