
import Foundation

struct ChatData {
    
    let datetime: Date          // 送信日時
    let senderId: String        // 送信者のユーザID
    let targetId: String        // チャット相手のユーザID
    let message: String         // メッセージ
    
    // JSONデータからChatDataのインスタンスを生成する
    init?(data: Dictionary<String, Any>) {
        
        let dateFormatter = DateFormatter(dateFormat: "yyyyMMddHHmmss")
        
        // JSONデータに不備があればnilを返す
        guard let datetimeString = data["datetime"] as? String,
              let datetime = dateFormatter.date(from: datetimeString),
              let senderId = data["senderId"] as? String,
              let targetId = data["targetId"] as? String,
              let message = data["message"] as? String else {
            return nil
        }

        // 送信日時
        self.datetime = datetime
        
        // 送信者のユーザID
        self.senderId = senderId
        
        // チャット相手のユーザID
        self.targetId = targetId
        
        // メッセージ
        self.message = message.base64Decode() ?? ""
    }
}
