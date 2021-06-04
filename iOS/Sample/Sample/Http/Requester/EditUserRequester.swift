
import UIKit

class EditUserRequester {
    
    class func edit(name: String,
                    message: String,
                    image: UIImage?,
                    completion: @escaping ((Bool) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "editUser"
        
        // ユーザID
        params["userId"] = SaveData.shared.userId
        
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
                    completion(true)
                    return
                }
            }
            
            // APIコールに失敗
            completion(false)
        })
    }
}
