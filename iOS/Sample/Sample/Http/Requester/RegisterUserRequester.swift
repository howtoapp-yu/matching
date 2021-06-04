
import UIKit

class RegisterUserRequester {

    // ユーザ登録API
    class func register(name: String,
                        message: String,
                        image: UIImage?,
                        completion: @escaping ((Bool, String?) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "registerUser"
        
        // ユーザ名
        params["name"] = name.base64Encode() ?? ""
        
        // メッセージ
        params["message"] = message.base64Encode() ?? ""
        
        // プロフィール画像
        if let image = image {
            params["image"] = image.pngData()?.base64EncodedString() ?? ""
        } else {
            params["image"] = ""
        }
        
        // APIコール
        ApiRequester.post(params: params, completion: { result, data in
            if result, let dic = data as? Dictionary<String, Any> {
                // 処理結果: 成功
                if (dic["result"] as? String) == "0" {
                    // 発行されたユーザID
                    if let userId = dic["userId"] as? String {
                        completion(true, userId)
                        return
                    }
                }
            }
            // APIコールに失敗
            completion(false, nil)
        })
    }
}
