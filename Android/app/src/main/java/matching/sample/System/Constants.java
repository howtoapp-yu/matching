package matching.sample.System;

public class Constants {

    // サーバのルートディレクトリ
    public static String ServerRootUrl = "http://10.0.2.2/sample/";

    // APIの通信先URL
    public static String ServerApiUrl = Constants.ServerRootUrl + "server.php";

    // 通信のタイムアウト値
    public static int HttpConnectTimeout = 10000;
    public static int HttpReadTimeout = 10000;
}
