//
//  DateFormatter+Extension.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import Foundation

extension DateFormatter {
    
    convenience init(dateFormat: String) {
        self.init()
        
        self.locale = Locale(identifier: "ja_JP")       // 日本に限定
        self.calendar = Calendar(identifier: .gregorian)
        self.dateFormat = dateFormat
    }
}
