package matching.sample.System;

import android.util.Base64;

public class Base64Utility {

    // Base64エンコード
    public static String encode(String string) {

        String encoded = new String(Base64.encode(string.getBytes(),
                Base64.DEFAULT | Base64.NO_WRAP));

        return (encoded == null) ? "" : encoded;
    }

    // Base64デコード
    public static String decode(String string) {

        String decoded = new String(Base64.decode(string,
                Base64.DEFAULT | Base64.NO_WRAP));

        return (decoded == null) ? "" : decoded;
    }
}