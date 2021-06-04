
import Foundation

class PostLikeRequester {
    
    // 「いいね！」送信APIコール
    class func post(targetId: String,
                    completion: @escaping ((Bool) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "postLike"
        
        // 送信者ID
        params["senderId"] = SaveData.shared.userId
        
        // 送信相手ID
        params["targetId"] = targetId
        
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
