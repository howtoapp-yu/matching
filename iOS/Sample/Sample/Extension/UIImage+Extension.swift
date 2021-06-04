//
//  UIImage+Extension.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import UIKit

extension UIImage {
        
    // プロフィール画像向けに、1辺が最大500ピクセルの画像にリサイズする
    func toProfileImage() -> UIImage? {

        if self.size.width > self.size.height {
            let width = 500 / UIScreen.main.scale
            let height = width * self.size.height / self.size.width
            return self.resize(size: CGSize(width: width, height: height))
        } else {
            let height = 500 / UIScreen.main.scale
            let width = height * self.size.width / self.size.height
            return self.resize(size: CGSize(width: width, height: height))
        }
    }
        
    // UIImageをリサイズ
    func resize(size: CGSize) -> UIImage? {
        
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        draw(in: CGRect(origin: .zero, size: size))
        let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resizedImage
    }
}
