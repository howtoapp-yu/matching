
import Foundation

class ApiRequester {
    
    // APIをコールする
    class func post(params: [String: String],
                    completion: @escaping ((Bool, Any?) -> ())) {
        
        // HTTP通信を行う
        HttpRequester.post(url: Constants.ServerApiUrl, params: params) { result, data in
            
            // HTTP通信に失敗
            guard result, let data = data else {
                completion(false, nil)
                return
            }
            do {
                // JSONに変換
                let options = JSONSerialization.ReadingOptions.allowFragments
                let json = try JSONSerialization.jsonObject(with: data,
                                                            options: options)
                let dic = json as! Dictionary<String, Any>
                
                // 呼び出し元に通知
                completion(true, dic)
                
            } catch {
                // JSON変換に失敗
                completion(false, nil)
            }
        }
    }
}
