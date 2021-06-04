
import UIKit

// ダイアログに表示するアクションボタン情報
struct DialogAction {
    
    // ボタンタイトル
    let title: String
    
    // タップイベント
    let action: (() -> ())?
    
    // 背景色
    let color: Dialog.ActionColor?
    
    init(title: String,
         action: (() -> ())?,
         color: Dialog.ActionColor? = nil) {

        self.title = title
        self.action = action
        self.color = color
    }
}

class Dialog: UIView {
    
    // ダイアログスタイル
    enum Style {
        case success    // 成功
        case error      // エラー
    }
    
    // ダイアログボタンの背景色
    enum ActionColor {
        
        case success        // 成功
        case error          // エラー
        case cancel         // キャンセル
        
        // UIColorを返す
        func toColor() -> UIColor {
            switch self {
            case .success:
                return UIColor(red: 123 / 255,
                               green: 209 / 255,
                               blue: 249 / 255,
                               alpha: 1)
            case .error:
                return UIColor(red: 230 / 255,
                               green: 73 / 255,
                               blue: 66 / 255,
                               alpha: 1)
            case .cancel:
                return UIColor(red: 200 / 255,
                               green: 200 / 255,
                               blue: 200 / 255,
                               alpha: 1)
            }
        }
    }
    
    // ダイアログタイトル
    @IBOutlet private weak var titleLabel: UILabel!
    
    // ダイアログメッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    // アクションボタン表示エリア
    @IBOutlet private weak var buttonsStackView: UIStackView!
    
    // ダイアログを表示する
    class func show(style: Style,
                    title: String,
                    message: String,
                    actions: [DialogAction]) {
        
        guard let window = UIApplication.shared.keyWindow else {
            return
        }
        
        let nib = UINib(nibName: "Dialog", bundle: nil)
        
        let dialog = nib.instantiate(withOwner: nil, options: nil).first as! Dialog
        window.addSubview(dialog)
        dialog.translatesAutoresizingMaskIntoConstraints = false
        dialog.topAnchor.constraint(equalTo: window.topAnchor).isActive = true
        dialog.leadingAnchor.constraint(equalTo: window.leadingAnchor).isActive = true
        dialog.trailingAnchor.constraint(equalTo: window.trailingAnchor).isActive = true
        dialog.bottomAnchor.constraint(equalTo: window.bottomAnchor).isActive = true
        
        // ダイアログの表示設定
        dialog.configure(style: style,
                         title: title,
                         message: message,
                         actions: actions)
        
        // 透明度のアニメーション
        dialog.alpha = 0
        UIView.animate(withDuration: 0.15) {
            dialog.alpha = 1.0
        }
    }
    
    // ダイアログの表示設定
    private func configure(style: Style,
                           title: String,
                           message: String,
                           actions: [DialogAction]) {
        
        // タイトル
        self.titleLabel.text = title
        
        // メッセージ
        self.messageLabel.text = message
        
        // ダイアログボタンを配置する
        actions.forEach { action in
            let nib = UINib(nibName: "DialogButton", bundle: nil)
            let view = nib.instantiate(withOwner: nil, options: nil).first
            let button = view as! DialogButton
            
            let color: UIColor
            if let actionColor = action.color {
                color = actionColor.toColor()
            } else {
                color = (style == .success)
                        ? ActionColor.success.toColor()
                        : ActionColor.error.toColor()
            }
            
            button.configure(title: action.title,
                             color: color,
                             didTap: { [weak self] in

                // タップイベントを実装
                action.action?()

                // 透明度のアニメーション
                UIView.animate(withDuration: 0.15, animations: {
                    self?.alpha = 0
                }, completion: { [weak self] _ in
                    self?.removeFromSuperview()
                })
            })
            self.buttonsStackView.addArrangedSubview(button)
        }
    }
}
