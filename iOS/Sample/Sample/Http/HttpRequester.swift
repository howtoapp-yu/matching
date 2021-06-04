//
//  HttpRequester.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import Foundation

class HttpRequester {
    
    // "GET"メソッドでHTTP通信を行う
    class func get(url: String, completion: @escaping ((Bool, Data?) -> ())) {

        HttpRequester.request(url: url,
                              method: "GET",
                              body: nil,
                              completion: completion)
    }
    
    // "POST"メソッドでHTTP通信を行う
    class func post(url: String,
                    params: [String: String],
                    completion: @escaping ((Bool, Data?) -> ())) {
        
        // POSTパラメータを生成
        let paramsStr = params.map { $0.key + "=" + $0.value }.joined(separator: "&")
        let body = paramsStr.data(using: Constants.StringEncoding)
        
        HttpRequester.request(url: url,
                              method: "POST",
                              body: body,
                              completion: completion)
    }
    
    // HTTP通信を行う
    class func request(url: String,
                       method:String,
                       body: Data?,
                       completion: @escaping ((Bool, Data?) -> ())) {
        
        // 不正なURL
        guard let urlRaw = URL(string: url) else {
            completion(false, nil)
            return
        }
        
        // リクエストを生成
        var request = URLRequest(url: urlRaw,
                                 cachePolicy: .reloadIgnoringLocalAndRemoteCacheData,
                                 timeoutInterval: Constants.HttpTimeOutInterval)
        request.httpMethod = method
        request.httpBody = body
                
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                // 呼び出し元に通知
                if error == nil, let data = data {
                    // 成功
                    completion(true, data)
                } else {
                    // 失敗
                    completion(false, nil)
                }
            }
        }
        task.resume()
    }
}
