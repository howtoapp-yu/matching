
import UIKit

class Toast: UIView {
    
    // メッセージ
    @IBOutlet private weak var textLabel: UILabel!

    // トーストを表示
    class func show(text: String) {
        
        guard let window = UIApplication.shared.keyWindow else {
            return
        }
        if !(window.subviews.compactMap { $0 as? Toast }).isEmpty {
            return
        }
        
        let nib =  UINib(nibName: "Toast", bundle: nil)
        let view = nib.instantiate(withOwner: self, options: nil).first
        let toast = view as! Toast

        // 表示設定
        toast.configure(text: text)
        
        // Window上に表示してframeを調整
        window.addSubview(toast)
        toast.frame = CGRect(origin: .zero, size: window.frame.size)
    }

    // 表示内容を設定する
    private func configure(text: String) {
     
        self.textLabel.text = text
        
        // 透明度のアニメーション
        self.alpha = 0
        
        UIView.animate(withDuration: 0.2, animations: {
            self.alpha = 1.0
        }, completion: { _ in
            UIView.animate(withDuration: 0.2, delay: 2.0, options: .curveEaseInOut, animations: {
                self.alpha = 0.0
            }, completion: { _ in
                self.removeFromSuperview()
            })
        })
    }
}
