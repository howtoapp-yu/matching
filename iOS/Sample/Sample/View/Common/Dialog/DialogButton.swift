
import UIKit

class DialogButton: UIView {

    @IBOutlet private weak var titleLabel: UILabel!     // ボタンタイトル
    @IBOutlet private weak var button: UIButton!        // ボタン
    
    private var didTap: (() -> ())?
    
    // ボタンのタイトルと背景色を設定する
    func configure(title: String, color: UIColor, didTap: @escaping (() -> ())) {
        
        // タイトル
        self.titleLabel.text = title
        
        // 背景色
        self.button.backgroundColor = color
        
        self.didTap = didTap
    }

    // ボタンがタップされた
    @IBAction func onTap(_ sender: Any) {
        self.didTap?()
    }
}
