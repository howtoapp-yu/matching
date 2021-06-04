
import UIKit

class ProfileViewController: UIViewController {

    // 戻るボタン
    @IBOutlet private weak var backButton: UIButton!
    
    // ユーザ画像
    @IBOutlet private weak var profileImageView: UIImageView!
    
    // ユーザ名
    @IBOutlet private weak var nameTextField: UITextField!
    
    // メッセージ
    @IBOutlet private weak var messageTextField: UITextField!
    
    // 初期登録時か
    private var isNewRegister = false
    
    // 選択されたユーザ画像
    private var selectedProfileImage: UIImage?
    
    // イメージピッカー
    private let imagePicker = ImagePickerManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // 既に自身の情報が登録済みの場合
        if let myUserData = GetUserRequester.shared.query(userId: SaveData.shared.userId) {
            // ユーザ画像
            ImageStorage.shared.fetch(url: UserData.imageUrl(userId: myUserData.id), imageView: self.profileImageView, errorImage: UIImage(named: "image_guide"), completion: nil)
            // ユーザ名
            self.nameTextField.text = myUserData.name
            // メッセージ
            self.messageTextField.text = myUserData.message
        }
        // 新規ユーザ登録
        else {
            // 戻るボタンを非表示
            self.backButton.isHidden = true
            
            // 新規登録フラグをオン
            self.isNewRegister = true
        }
    }
    
    // ユーザ登録APIをコールする
    private func registerUser(name: String, message: String) {
        
        // ローディング開始
        Loading.start()
        
        // ユーザ登録API
        RegisterUserRequester.register(name: name, message: message, image: self.selectedProfileImage, completion: { result, userId in
            // 成功
            if result, let userId = userId {
                // ユーザIDを保存
                let saveData = SaveData.shared
                saveData.userId = userId
                saveData.save()
                
                // ユーザ情報を再取得
                self.fetchUser()
            }
            // 失敗
            else {
                Loading.stop()
                Dialog.show(style: .error, title: "エラー", message: "ユーザー登録に失敗しました", actions: [DialogAction(title: "OK", action: nil)])
            }
        })
    }
    
    // プロフィール編集APIをコールする
    private func updateUser(name: String, message: String) {
        
        // ローディング開始
        Loading.start()
        
        // プロフィール編集API
        EditUserRequester.edit(name: name, message: message, image: self.selectedProfileImage, completion: { result in
            // 成功
            if result {
                // ローカルに保存した画像キャッシュを削除
                ImageStorage.shared.removeAll()
                
                // ユーザ情報を再取得
                self.fetchUser()
            }
            // 失敗
            else {
                Loading.stop()
                Dialog.show(style: .error, title: "エラー", message: "プロフィール設定に失敗しました", actions: [DialogAction(title: "OK", action: nil)])
            }
        })
    }
    
    // ユーザ情報を再取得する
    private func fetchUser() {
        
        // ユーザ情報取得API
        GetUserRequester.shared.fetch(completion: { resultFetch in
            
            // ローディング停止
            Loading.stop()
            
            // 成功
            if resultFetch {
                // 新規登録の場合
                if self.isNewRegister {
                    // タブバーを表示する
                    self.stackTabbar()
                } else {
                    // 画面を更新
                    self.getTabbarViewController()?.reload()
                    
                    // 完了メッセージを表示
                    self.showSuccess()
                }
            }
            // 失敗
            else {
                let action = DialogAction(title: "リトライ", action: { [weak self] in
                    // リトライ
                    self?.fetchUser()
                })
                Dialog.show(style: .error, title: "エラー", message: "ユーザー情報の取得に失敗しました", actions: [action])
            }
        })
    }
    
    // タブバーを表示する
    private func stackTabbar() {
        
        if let splash = self.parent as? SplashViewController {

            // スプラッシュ画面の上にタブバーを表示
            let tabbarViewController = self.instantiate(identifier: "TabbarViewController") as! TabbarViewController
            splash.stack(viewController: tabbarViewController, animationType: .none)

            // 本画面を閉じる
            self.pop(animationType: .none)
        }
    }
    
    // 完了メッセージを表示する
    private func showSuccess() {
        
        let action = DialogAction(title: "OK", action: { [weak self] in
            // 画面を閉じる
            self?.pop(animationType: .horizontal)
        })
        Dialog.show(style: .success, title: "確認", message: "プロフィールを更新しました", actions: [action])
    }
    
    // 確定キーが押された
    @IBAction func didEndOnExitTextField(_ sender: Any) {
        self.view.endEditing(true)
    }
    
    // 戻るボタンが押された
    @IBAction func onTapBack(_ sender: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
        
        // 画面を閉じる
        self.pop(animationType: .horizontal)
    }
    
    // プロフィール画像が押された
    @IBAction func onTapProfileImage(_ sedner: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
        
        // 画像選択ピッカーを表示
        self.imagePicker.showPicker(on: self,
                                    type: .photoLibrary,
                                    completion: { [weak self] image in
                                        
            // 画像を縮小
            if let profileImage = image.toProfileImage() {
                // 画面に表示する
                self?.profileImageView.image = profileImage
                self?.selectedProfileImage = profileImage
            }
        })
    }
    
    // 設定ボタンが押された
    @IBAction func onTapSave(_ sender: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
        
        // ユーザ名
        let name = self.nameTextField.text ?? ""
        
        // メッセージ
        let message = self.messageTextField.text ?? ""
        
        // ユーザ名が未入力
        if name.count == 0 {
            let action = DialogAction(title: "OK", action: nil)
            
            Dialog.show(style: .error,
                        title: "エラー",
                        message: "名前を入力してください",
                        actions: [action])
            return
        }
        
        // メッセージが未入力
        if message.count == 0 {
            let action = DialogAction(title: "OK", action: nil)
            Dialog.show(style: .error,
                        title: "エラー",
                        message: "メッセージを入力してください",
                        actions: [action])
            return
        }
        
        // 新規登録の場合
        if self.isNewRegister {
            self.registerUser(name: name, message: message)
        }
        // プロフィール編集の場合
        else {
            self.updateUser(name: name, message: message)
        }
    }
}
