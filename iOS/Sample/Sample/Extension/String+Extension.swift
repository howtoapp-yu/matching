//
//  String+Extension.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import Foundation

extension String {
    
    // Base64エンコード
    func base64Encode() -> String? {
        let data = self.data(using: .utf8)
        return data?.base64EncodedString()
    }
    
    // Base64でコード
    func base64Decode() -> String? {
        let replaced = self.replacingOccurrences(of: " ", with: "+")
        if let data = Data(base64Encoded: replaced) {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
    
    // 文字列の切り出し
    func substr(start: Int, length: Int) -> String {
        
        let startIndex = self.index(self.startIndex, offsetBy: start)
        let endIndex = self.index(self.startIndex, offsetBy: start + length - 1)
        return String(self[startIndex...endIndex])
    }
}
