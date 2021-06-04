
import UIKit

// キーボードのアニメーションに関する情報
struct KeyboardAnimationOption {
    
    // キーボードの高さ
    let height: CGFloat
    
    // アニメーション時間
    let duration: TimeInterval
    
    // アニメーションオプション
    let curve: UIView.AnimationOptions
}

class KeyboardRespondableViewController: UIViewController {
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let notificationCenter = NotificationCenter.default
        
        // キーボード表示を監視するオブザーバを追加
        notificationCenter.addObserver(self,
                                       selector: #selector(keyboardWillShow(notification:)),
                                       name: UIResponder.keyboardWillShowNotification,
                                       object: nil)
        
        // キーボード非表示を監視するオブザーバを追加
        notificationCenter.addObserver(self,
                                       selector: #selector(keyboardWillHide(notification:)),
                                       name: UIResponder.keyboardWillHideNotification,
                                       object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        let notificationCenter = NotificationCenter.default

        // キーボード表示を監視するオブザーバを削除
        notificationCenter.removeObserver(self,
                                          name: UIResponder.keyboardWillShowNotification,
                                          object: nil)
        
        // キーボード非表示を監視するオブザーバを削除
        notificationCenter.removeObserver(self,
                                          name: UIResponder.keyboardWillHideNotification,
                                          object: nil)
    }
    
    // キーボードが出現した
    @objc func keyboardWillShow(notification: NSNotification) {
        
        if let option = self.convertOption(from: notification) {
            self.didChangeKeyboard(option: option)
        }
    }
    
    // キーボードが非表示となった
    @objc func keyboardWillHide(notification: NSNotification) {
        
        if let option = self.convertOption(from: notification) {
            self.didChangeKeyboard(option: option)
        }
    }
    
    // NSNotificationからアニメーション情報を取り出す
    private func convertOption(from: NSNotification) -> KeyboardAnimationOption? {
        
        let userInfo = from.userInfo
        let frameEndKey = UIResponder.keyboardFrameEndUserInfoKey
        let durationKey = UIResponder.keyboardAnimationDurationUserInfoKey
        let curveKey = UIResponder.keyboardAnimationCurveUserInfoKey
        
        guard let frameEndValue = userInfo?[frameEndKey],
              let frameEnd = (frameEndValue as? NSValue)?.cgRectValue,
              let durationValue = userInfo?[durationKey],
              let duration = durationValue as? TimeInterval,
              let curveValue = userInfo?[curveKey],
              let curve = curveValue as? UIView.AnimationOptions else {
            return nil
        }
        
        // キーボードの高さ
        let height = UIScreen.main.bounds.size.height - frameEnd.origin.y
        
        return KeyboardAnimationOption(height: height, duration: duration, curve: curve)
    }
    
    func didChangeKeyboard(option: KeyboardAnimationOption) {
        // Override必須
    }
}
