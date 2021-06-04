
import UIKit

class MyPageViewController: UIViewController {

    // ユーザ画像
    @IBOutlet private weak var userImageView: UIImageView!
    
    // 名前
    @IBOutlet private weak var nameLabel: UILabel!
    
    // メッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // 表示内容を更新する
        self.reload()
    }

    // 表示内容を更新する
    func reload() {
        
        // ユーザ情報取得APIの結果から自身のユーザ情報を取り出す
        if let myUserData = GetUserRequester.shared.query(userId: SaveData.shared.userId) {
            // ユーザ画像
            ImageStorage.shared.fetch(url: UserData.imageUrl(userId: myUserData.id), imageView: self.userImageView)
            
            // ユーザ名
            self.nameLabel.text = myUserData.name
            
            // メッセージ
            self.messageLabel.text = myUserData.message
        }
    }
    
    // 「編集する」ボタンが押された
    @IBAction func onTapEdit(_ sender: Any) {
        
        // プロフィール画面に遷移
        let identifier = "ProfileViewController"
        let vcProfile = self.instantiate(identifier: identifier)
                                        as! ProfileViewController
        self.getTabbarViewController()?.stack(viewController: vcProfile,
                                              animationType: .horizontal)
    }
}
