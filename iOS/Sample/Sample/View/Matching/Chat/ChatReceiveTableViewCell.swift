
import UIKit

class ChatReceiveTableViewCell: UITableViewCell {

    // ユーザ画像
    @IBOutlet private weak var userImageView: UIImageView!
    
    // メッセージ土台ビュー
    @IBOutlet private weak var baseView: UIView!
    
    // メッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        // セルが再利用されるタイミングでユーザ画像をリセットする
        ImageStorage.shared.cancelRequest(imageView: self.userImageView)
        self.userImageView.image = nil
    }
    
    // チャット情報を表示する
    func configure(chatData: ChatData) {
      
        // ユーザ画像
        ImageStorage.shared.fetch(url: UserData.imageUrl(userId: chatData.senderId), imageView: self.userImageView)
        
        // メッセージ
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
