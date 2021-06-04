
import UIKit

class MatchingTableViewCell: UITableViewCell {

    // ユーザ画像
    @IBOutlet private weak var userImageView: UIImageView!
    
    // ユーザ名
    @IBOutlet private weak var nameLabel: UILabel!
    
    // メッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        // セルが再利用されるタイミングでユーザ画像をリセットする
        ImageStorage.shared.cancelRequest(imageView: self.userImageView)
        self.userImageView.image = nil
    }
    
    func configure(userData: UserData) {
        
        // ユーザ画像
        let imageUrl = UserData.imageUrl(userId: userData.id)
        ImageStorage.shared.fetch(url: imageUrl,
                                  imageView: self.userImageView)
        
        // ユーザ名
        self.nameLabel.text = userData.name
        
        // メッセージ
        self.messageLabel.text = userData.message
    }
}
