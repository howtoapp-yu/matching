//
//  SaveData.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/28.
//

import Foundation

struct UserDefaultsKey {
    static let userId = "userId"
}

class SaveData {
    
    static let shared = SaveData()
    
    var userId = ""     // ユーザID

    // 初回呼び出し時にUserDefaultsから読み出す
    init() {
        
        let userDefaults = UserDefaults()
        
        self.userId = userDefaults.string(forKey: UserDefaultsKey.userId) ?? ""
    }
    
    // UserDefaultsに保存する
    func save() {
        
        let userDefaults = UserDefaults()
        
        userDefaults.set(self.userId, forKey: UserDefaultsKey.userId)

        userDefaults.synchronize()
    }
}
