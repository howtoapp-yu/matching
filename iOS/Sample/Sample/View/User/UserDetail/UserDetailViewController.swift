
import UIKit

class UserDetailViewController: UIViewController {

    // ユーザ画像
    @IBOutlet private weak var userImageView: UIImageView!
    
    // ユーザ名
    @IBOutlet private weak var nameLabel: UILabel!
    
    // メッセージ
    @IBOutlet private weak var messageLabel: UILabel!
    
    // ユーザ情報
    private var userData: UserData!
    
    // ユーザ情報を設定する
    func set(userData: UserData) {
        self.userData = userData
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // ユーザ情報を表示する
        self.showUserData()
    }
    
    // ユーザ情報を表示する
    private func showUserData() {

        // ユーザ画像
        let imageUrl = UserData.imageUrl(userId: self.userData.id)
        ImageStorage.shared.fetch(url: imageUrl,
                                  imageView: self.userImageView)
        
        // ユーザ名
        self.nameLabel.text = self.userData.name
        
        // メッセージ
        self.messageLabel.text = self.userData.message
    }

    // 戻るボタンが押された
    @IBAction func onTapBack(_ sender: Any) {
        
        // 画面を閉じる
        self.pop(animationType: .horizontal)
    }
    
    // 「いいね！」が押された
    @IBAction func onTapLike(_ sender: Any) {

        // ローディング開始
        Loading.start()
        
        // 「いいね！」送信APIをコール
        PostLikeRequester.post(targetId: self.userData.id,
                               completion: { result in
                                
            // ローディング停止
            Loading.stop()
            
            // 成功
            if result {
                Toast.show(text: "いいね！を送信しました")
            }
            // 失敗
            else {
                Dialog.show(style: .error, title: "エラー", message: "通信に失敗しました", actions: [DialogAction(title: "OK", action: nil)])
            }
        })
    }
}
