
import UIKit

class ChatViewController: KeyboardRespondableViewController {

    // 画面ヘッダ部のユーザ名
    @IBOutlet private weak var headerNameLabel: UILabel!
    
    // テーブルビュー
    @IBOutlet private weak var tableView: UITableView!
    
    // メッセージ入力
    @IBOutlet private weak var messageTextField: UITextField!
    
    // メッセージ入力欄の下部レイアウト
    @IBOutlet private weak var inputViewBottomConstraint: NSLayoutConstraint!
    
    // チャット相手のユーザ情報
    private var userData: UserData!
    
    // チャット情報の配列
    private var chatDatas = [ChatData]()
    
    // 定期通信用のタイマ
    private var timer: Timer?
    
    // 初回の画面更新か
    private var initialReload = true
    
    // セルの高さ計算用のダミーセル
    private var dummySendTableViewCell: ChatSendTableViewCell?
    private var dummyReceiveTableViewCell: ChatReceiveTableViewCell?
    
    // ユーザ情報を設定する
    func set(userData: UserData) {
        self.userData = userData
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // ユーザ情報を表示する
        self.initHeaderName()

        // ダミーセルを生成する
        self.initDummyCells()
        
        // 定期通信用のタイマ開始
        self.initTimer()
    }
    
    // ユーザ情報を表示する
    private func initHeaderName() {
        
        self.headerNameLabel.text = self.userData.name
    }
    
    // ダミーセルを生成する
    private func initDummyCells() {
        
        // 自身が送信したメッセージのセル
        let sendIdentifier = "ChatSendTableViewCell"
        let sendCell = self.tableView.dequeueReusableCell(withIdentifier: sendIdentifier)
        self.dummySendTableViewCell = sendCell as? ChatSendTableViewCell
        self.dummySendTableViewCell?.frame = CGRect(x: 0,
                                                    y: 0,
                                                    width: UIScreen.main.bounds.size.width,
                                                    height: 1)
        
        // チャット相手が送信したメッセージのセル
        let receiveIdentifier = "ChatReceiveTableViewCell"
        let receiveCell = self.tableView.dequeueReusableCell(withIdentifier: receiveIdentifier)
        self.dummyReceiveTableViewCell = receiveCell as? ChatReceiveTableViewCell
        self.dummyReceiveTableViewCell?.frame = CGRect(x: 0,
                                                       y: 0,
                                                       width: UIScreen.main.bounds.size.width,
                                                       height: 1)
    }
    
    // 定期通信用のタイマ開始
    private func initTimer() {
        
        // 5秒おきに最新のチャット情報を取得する
        self.timer = Timer.scheduledTimer(withTimeInterval: 5.0, repeats: true, block: { [weak self] _ in
            self?.getChat()
        })
        self.getChat()
    }
    
    // チャットメッセージ取得APIをコールする
    private func getChat() {
        
        GetChatRequester.get(targetId: self.userData.id, completion: { result, chatDatas in
            // 成功
            if result, let chatDatas = chatDatas {
                // 前回と変化が無ければ終了
                if self.chatDatas.count == chatDatas.count {
                    return
                }
                
                // 画面を更新
                self.chatDatas = chatDatas
                self.tableView.reloadData()
                
                // 初回の画面更新
                if self.initialReload {
                    // 下端までスクロールする
                    self.scrollToBottom()
                    self.initialReload = false
                } else {
                    // 必要に応じて下端までスクロールする
                    self.scrollToBottomIfNeeded()
                }
            } else {
                Toast.show(text: "通信に失敗しました")
            }
        })
    }
    
    // 必要に応じて下端までスクロールする
    private func scrollToBottomIfNeeded() {
        
        let tableBottom = self.tableView.contentOffset.y
                            + self.tableView.frame.size.height
        
        // 現在スクロール位置が下端付近
        if tableBottom + 100 > self.tableView.contentSize.height {
            self.scrollToBottom()
        }
    }
    
    // 下端までスクロールする
    private func scrollToBottom() {
        
        let contentHeight = self.tableView.contentSize.height
        let tableHeight = self.tableView.frame.size.height
        
        if contentHeight > tableHeight {
            let offsetY = self.tableView.contentSize.height
                            - self.tableView.frame.size.height
            let offset = CGPoint(x: 0, y: offsetY)
            
            self.tableView.setContentOffset(offset,
                                            animated: true)
        }
    }
    
    // 確定キーが押された
    @IBAction func didEndOnExitTextField(_ sender: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
    }
    
    // 戻るボタンが押された
    @IBAction func onTapBack(_ sender: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
        
        // タイマを停止
        self.timer?.invalidate()
        
        // 画面を閉じる
        self.pop(animationType: .horizontal)
    }
    
    // メッセージ送信ボタンが押された
    @IBAction func onTapSend(_ sender: Any) {
        
        // キーボードを隠す
        self.view.endEditing(true)
        
        // 入力されたメッセージ
        let message = self.messageTextField.text ?? ""
        if message.count == 0 {
            return
        }
        
        // チャットメッセージ送信APIをコール
        PostChatRequester.post(targetId: self.userData.id,
                               message: message,
                               completion: { result in
                                
            // 成功
            if result {
                // 最新データを取得
                self.getChat()
            }
            // 失敗
            else {
                let action = DialogAction(title: "OK", action: nil)
                Dialog.show(style: .error,
                            title: "エラー",
                            message: "通信に失敗しました",
                            actions: [action])
            }
        })
        
        // 入力メッセージをクリア
        self.messageTextField.text = ""
    }
    
    // キーボードの表示状態の変更を検知した
    override func didChangeKeyboard(option: KeyboardAnimationOption) {
        
        // メッセージ入力欄をキーボードのサイズに合わせて移動する
        var height = option.height
        if height > 0 {
            height -= self.view.safeAreaInsets.bottom
        }
        
        self.inputViewBottomConstraint.constant = height
        
        UIView.animate(withDuration: option.duration, delay: 0, options: option.curve, animations: {
            self.view.layoutIfNeeded()
        })
    }
}

extension ChatViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        
        // チャットメッセージの数だけ表示
        return self.chatDatas.count
    }
    
    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        // 該当位置のチャットデータを取り出し
        let chatData = self.chatDatas[indexPath.row]
        
        // 自身が送信したメッセージ
        if chatData.senderId == SaveData.shared.userId {
            let identifier = "ChatSendTableViewCell"
            let cell = tableView.dequeueReusableCell(withIdentifier: identifier,
                                                     for: indexPath)
            let chatSendCell = cell as! ChatSendTableViewCell

            // チャット情報を表示
            chatSendCell.configure(chatData: chatData)

            return cell
        }
        // 相手が送信したメッセージ
        else {
            let identifier = "ChatReceiveTableViewCell"
            let cell = tableView.dequeueReusableCell(withIdentifier: identifier,
                                                     for: indexPath)
            let chatReceiveCell = cell as! ChatReceiveTableViewCell
            
            // チャット情報を表示
            chatReceiveCell.configure(chatData: chatData)

            return cell
        }
    }
    
    func tableView(_ tableView: UITableView,
                   heightForRowAt indexPath: IndexPath) -> CGFloat {
        
        // 該当位置のチャットデータを取り出し
        let chatData = self.chatDatas[indexPath.row]
    
        // 自身が送信したメッセージ
        if chatData.senderId == SaveData.shared.userId {
            // 高さ計算用のダミーセルを利用して高さを計算
            return self.dummySendTableViewCell?.height(chatData: chatData) ?? 0
        }
        // 相手が送信したメッセージ
        else {
            // 高さ計算用のダミーセルを利用して高さを計算
            return self.dummyReceiveTableViewCell?.height(chatData: chatData) ?? 0
        }
    }
}
