
import UIKit

// アプリ画面1 スプラッシュ
class SplashViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // 3秒間待機した後、ユーザ情報取得APIをコール
        DispatchQueue.main.asyncAfter(deadline: .now() + 3, execute: {
            self.fetch()
        })
    }
    
    // ユーザ情報取得APIをコールする
    private func fetch() {
        
        GetUserRequester.shared.fetch(completion: { result in
            
            // APIコールに成功
            if result {
                self.stackNextViewController()
            } else {
                // エラーダイアログを表示
                let action = DialogAction(title: "リトライ", action: { [weak self] in
                    // リトライを実装
                    self?.fetch()
                })
                Dialog.show(style: .error,
                            title: "エラー",
                            message: "通信に失敗しました",
                            actions: [action])
            }
        })
    }
    
    // 次の画面に遷移する
    private func stackNextViewController() {

        // 自身のユーザ情報がサーバに保存されている場合
        if let _ = GetUserRequester.shared.query(userId: SaveData.shared.userId) {
            // タブバー画面を表示する
            let tabbar = self.instantiate(identifier: "TabbarViewController")
            self.stack(viewController: tabbar, animationType: .none)
        } else {
            // プロフィール設定画面に遷移
            let profile = self.instantiate(identifier: "ProfileViewController")
            self.stack(viewController: profile, animationType: .none)
        }
    }
}
