
import UIKit

class ChatSendTableViewCell: UITableViewCell {

    // メッセージ土台ビュー
    @IBOutlet private weak var baseView: UIView!
    
    // メッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    // チャット情報を表示する
    func configure(chatData: ChatData) {
        
        self.messageLabel.text = chatData.message
    }
    
    // 必要な高さを計算する
    func height(chatData: ChatData) -> CGFloat {
        
        self.configure(chatData: chatData)
        
        self.setNeedsLayout()
        self.layoutIfNeeded()
        
        return self.baseView.frame.origin.y
                + self.baseView.frame.size.height
                + 10
    }
}
