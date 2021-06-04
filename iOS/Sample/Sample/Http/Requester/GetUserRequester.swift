
import Foundation

class GetUserRequester {
    
    // データ保持のためシングルトン化
    static let shared = GetUserRequester()
    
    // ユーザ情報の配列
    var dataList = [UserData]()
    
    // APIコール
    func fetch(completion: @escaping ((Bool) -> ())) {
        
        var params = [String: String]()
        
        // API種類
        params["command"] = "getUser"
        
        // APIコール
        ApiRequester.post(params: params, completion: { result, data in
            if result, let dic = data as? Dictionary<String, Any> {
                // 処理結果: 成功
                if (dic["result"] as? String == "0") {

                    // ユーザ情報の配列を取り出し
                    if let users = dic["users"] as? [Dictionary<String, Any>] {
                        // UserDataモデルに変換
                        self.dataList = users.compactMap { UserData(data: $0) }
                        
                        // 呼び出し元に成功を通知
                        completion(true)
                        return
                    }
                }
            }
            
            // APIコールに失敗
            completion(false)
        })
    }
    
    // 特定ユーザの情報を取り出す
    func query(userId: String) -> UserData? {
        return self.dataList.first(where: { $0.id == userId })
    }
}
