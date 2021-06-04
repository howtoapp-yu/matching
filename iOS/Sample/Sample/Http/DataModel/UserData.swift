
import Foundation

struct UserData {
    
    let id: String                  // ユーザID
    let name: String                // ユーザ名
    let message: String             // メッセージ
    let likeTargetIds: [String]     // 「いいね！」送信対象のユーザIDリスト
    
    // JSONデータからUserDataのインスタンスを生成する
    init?(data: Dictionary<String, Any>) {
        
        // JSONデータに不備があればnilを返す
        guard let id = data["id"] as? String,
              let name = data["name"] as? String,
              let message = data["message"] as? String,
              let likeTargetIds = data["likeTargetIds"] as? [String] else {
            return nil
        }

        // ユーザID
        self.id = id
        
        // ユーザ名
        self.name = name.base64Decode() ?? ""
    
        // メッセージ
        self.message = message.base64Decode() ?? ""
        
        // 「いいね！」送信対象のユーザIDリスト
        self.likeTargetIds = likeTargetIds
    }
    
    // ユーザのプロフィール画像のURLを返す
    static func imageUrl(userId: String) -> String {
        return Constants.ServerRootUrl + "data/user/" + userId + "/image"
    }
}
