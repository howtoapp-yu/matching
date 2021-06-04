//
//  ImagePickerManager.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/28.
//

import UIKit

class ImagePickerManager: NSObject {
    
    private var completion: ((UIImage) -> ())?
    
    // 画像ピッカーを表示
    func showPicker(on viewController: UIViewController,
                    type: UIImagePickerController.SourceType,
                    completion: @escaping ((UIImage) -> ())) {
        
        if UIImagePickerController.isSourceTypeAvailable(type) {
            let picker = UIImagePickerController()
            picker.sourceType = type
            picker.delegate = self
            viewController.present(picker,
                                   animated: true,
                                   completion: nil)
            
            self.completion = completion
        }
    }
}

extension ImagePickerManager: UIImagePickerControllerDelegate,
                              UINavigationControllerDelegate {
    
    // 画像が選択された時に呼ばれる
    func imagePickerController(_ picker: UIImagePickerController,
                               didFinishPickingMediaWithInfo
                                info: [UIImagePickerController.InfoKey : Any]) {
        
        // 選択画像を取り出す
        if let rawImage = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            // 呼び出し元に通知
            self.completion?(rawImage)
        }
        picker.dismiss(animated: true, completion: nil)
    }
}
