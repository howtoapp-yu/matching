//
//  Constants.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import Foundation

class Constants {
    
    // サーバのルートディレクトリ
    static let ServerRootUrl = "https://sample.com/sample/"
    
    // APIの通信先URL
    static let ServerApiUrl = Constants.ServerRootUrl + "server.php"
    
    // APIのPOSTパラメータに使用する文字コード
    static let StringEncoding = String.Encoding.utf8
    
    // 通信のタイムアウト値
    static let HttpTimeOutInterval = TimeInterval(10)
}
