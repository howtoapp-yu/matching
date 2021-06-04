package matching.sample.System;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.HashSet;

public class SaveData {

    // 保存キー
    static class SharedPreferenceKey {
        public static String Key = "Sample";
        public static String UserId = "UserId";
    }

    private static SaveData mInstance = null;

    public Context mContext;

    // ユーザID
    public String userId = "";

    public static SaveData getInstance() {

        if(mInstance == null){
            mInstance = new SaveData();
        }
        return mInstance;
    }

    // 初期化
    public void initialize(Context context) {

        mContext = context;

        SharedPreferences prefs = context.getSharedPreferences(
                                    SharedPreferenceKey.Key,
                                    Context.MODE_PRIVATE);

        // ユーザID
        this.userId = prefs.getString(SharedPreferenceKey.UserId, "");
    }

    public void save() {

        SharedPreferences data = mContext.getSharedPreferences(SharedPreferenceKey.Key,
                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        // ユーザID
        editor.putString(SharedPreferenceKey.UserId, this.userId);

        editor.apply();
    }
}
