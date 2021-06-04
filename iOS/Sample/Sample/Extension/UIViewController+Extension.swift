//
//  UIViewController+Extension.swift
//  Sample
//
//  Created by Leapfrog-Inc. on 2021/05/27.
//

import UIKit

// 画面遷移アニメーションの種類
enum SceneAnimationType {
    case none           // アニメーションなし
    case horizontal     // 横方向
    case vertical       // 縦方向
}

extension UIViewController {

    // "Main.storyboard"からUIViewControllerのインスタンスを取り出す
    func instantiate(identifier: String) -> UIViewController {
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        return storyboard.instantiateViewController(withIdentifier: identifier)
    }
    
    // 自身の上に新しいUIViewControllerを配置する
    func stack(viewController: UIViewController,
               animationType: SceneAnimationType,
               completion: (() -> ())? = nil) {
        
        self.view.addSubview(viewController.view)
        self.addChild(viewController)
        viewController.didMove(toParent: self)
        
        // アニメーション無し
        if animationType == .none {
            let frame = CGRect(origin: .zero, size: self.view.frame.size)
            viewController.view.frame = frame
            completion?()
        } else {
            // 横方向アニメーション
            if animationType == .horizontal {
                viewController.view.frame = CGRect(x: self.view.frame.size.width,
                                                   y: 0,
                                                   width: self.view.frame.size.width,
                                                   height: self.view.frame.size.height)
            }
            // 縦方向アニメーション
            else {
                viewController.view.frame = CGRect(x: 0,
                                                   y: self.view.frame.size.height,
                                                   width: self.view.frame.size.width,
                                                   height: self.view.frame.size.height)
            }
            
            // 連打防止
            UIApplication.shared.keyWindow?.isUserInteractionEnabled = false
            
            UIView.animate(withDuration: 0.2,
                           delay: 0,
                           options: .curveEaseInOut,
                           animations: { [weak self] in
                let frame = CGRect(origin: .zero, size: self?.view.frame.size ?? .zero)
                viewController.view.frame = frame
                            
            }, completion: { _ in
                // 連打防止を解除
                UIApplication.shared.keyWindow?.isUserInteractionEnabled = true
                
                // 呼び出し元に画面遷移完了を通知
                completion?()
            })
        }
    }
    
    // 自身を画面上から取り除く
    func pop(animationType: SceneAnimationType) {
        
        // アニメーション無し
        if animationType == .none {
            self.pop()
        } else {
            var frame: CGRect
            
            // 横方向アニメーション
            if animationType == .horizontal {
                frame = CGRect(x: self.view.frame.size.width,
                               y: 0,
                               width: self.view.frame.size.width,
                               height: self.view.frame.size.height)
            }
            // 縦方向アニメーション
            else {
                frame = CGRect(x: 0,
                               y: self.view.frame.size.height,
                               width: self.view.frame.size.width,
                               height: self.view.frame.size.height)
            }
            
            // 連打防止
            UIApplication.shared.keyWindow?.isUserInteractionEnabled = false
            UIView.animate(withDuration: 0.25,
                           delay: 0,
                           options: .curveEaseInOut,
                           animations: { [weak self] in
                self?.view.frame = frame
            }, completion: { [weak self] _ in
                self?.pop()
                
                // 連打防止を解除
                UIApplication.shared.keyWindow?.isUserInteractionEnabled = true
            })
        }
    }
    
    // 自身を画面上から取り除く
    private func pop() {
        
        self.willMove(toParent: nil)
        self.view.removeFromSuperview()
        self.removeFromParent()
    }
    
    // アプリで表示中のタブバーを返す
    func getTabbarViewController() -> TabbarViewController? {
        
        if let rootViewController = UIApplication.shared.keyWindow?.rootViewController {
            if let splashViewController = rootViewController as? SplashViewController {
                let children = splashViewController.children
                return children.compactMap { $0 as? TabbarViewController }.first
            }
        }
        return nil
    }
}
