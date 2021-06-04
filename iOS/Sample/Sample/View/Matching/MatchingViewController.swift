
import UIKit

class MatchingViewController: UIViewController {

    // テーブルビュー
    @IBOutlet private weak var tableView: UITableView!
    
    // ユーザ情報の配列
    private var userDatas = [UserData]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // 画面を更新する
        self.reload()
    }
    
    // 画面を更新する
    func reload() {
        
        // 一旦全て削除する
        self.userDatas.removeAll()
        
        // 相互に「いいね！」を送信し合ったユーザをリストアップする
        if let myUserData = GetUserRequester.shared.query(userId: SaveData.shared.userId) {
            myUserData.likeTargetIds.forEach { likeTargetId in
                if let userData = GetUserRequester.shared.query(userId: likeTargetId) {
                    if userData.likeTargetIds.contains(SaveData.shared.userId) {
                        self.userDatas.append(userData)
                    }
                }
            }
        }
        
        // テーブルビューを更新する
        self.tableView.reloadData()
    }
}

extension MatchingViewController: UITableViewDelegate,
                                  UITableViewDataSource {
    
    func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        
        // マッチング中のユーザ全てを表示する
        return self.userDatas.count
    }
    
    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let identifier = "MatchingTableViewCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: identifier,
                                                 for: indexPath)
        let matchingTableViewCell = cell as! MatchingTableViewCell

        // ユーザ情報を表示
        matchingTableViewCell.configure(userData: self.userDatas[indexPath.row])

        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        // チャット画面に遷移
        let chatViewController = self.instantiate(identifier: "ChatViewController")
            as! ChatViewController
        
        // ユーザ情報を表示
        chatViewController.set(userData: self.userDatas[indexPath.row])

        self.getTabbarViewController()?.stack(viewController: chatViewController,
                                              animationType: .horizontal)
    }
}
