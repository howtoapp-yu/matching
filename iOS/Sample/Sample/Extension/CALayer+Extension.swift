//
//  CALayer+Extension.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/28.
//

import UIKit

extension CALayer {
    
    @objc var borderUIColor: UIColor? {
        get {
            return borderColor == nil ? nil : UIColor(cgColor: borderColor!)
        }
        set {
            borderColor = newValue == nil ? nil : newValue!.cgColor
        }
    }
}
