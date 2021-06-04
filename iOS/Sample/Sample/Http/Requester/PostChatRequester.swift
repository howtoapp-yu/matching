
import Foundation

class PostChatRequester {
    
    // チャットメッセージ送信APIをコール
    class func post(targetId: String,
                    message: String,
                    completion: @escaping ((Bool) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "postChat"
        
        // 送信者ID
        params["senderId"] = SaveData.shared.userId
        
        // 送信相手ID
        params["targetId"] = targetId
        
        // チャットメッセージ
        params["message"] = message.base64Encode() ?? ""
        
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
