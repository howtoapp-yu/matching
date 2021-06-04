
import Foundation

class GetChatRequester {
    
    // チャットメッセージ取得APIコール
    class func get(targetId: String,
                   completion: @escaping ((Bool, [ChatData]?) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "getChat"
        
        // 送信者のユーザID
        params["senderId"] = SaveData.shared.userId
        
        // チャット相手のユーザID
        params["targetId"] = targetId
        
        // APIコール
        ApiRequester.post(params: params, completion: { result, data in
            if result, let dic = data as? Dictionary<String, Any> {
                
                // 処理結果: 成功
                if (dic["result"] as? String) == "0" {
                    // チャット情報の配列を取り出し
                    if let chatDics = dic["chats"] as? [Dictionary<String, Any>] {
                        
                        // ChatDataモデルに変換
                        let chats = chatDics.compactMap { ChatData(data: $0) }

                        completion(true, chats)
                        return
                    }
                }
            }
            
            // APIコールに失敗
            completion(false, nil)
        })
    }
}
