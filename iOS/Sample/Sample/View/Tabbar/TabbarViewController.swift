
import UIKit

class TabbarViewController: UIViewController {

    // 配下のUIViewControllerを表示するコンテナ
    @IBOutlet private weak var containerView: UIView!
    
    // tab0ボタンの画像
    @IBOutlet private weak var tab0ImageView: UIImageView!
    
    // tab1ボタンの画像
    @IBOutlet private weak var tab1ImageView: UIImageView!
    
    // tab2ボタンの画像
    @IBOutlet private weak var tab2ImageView: UIImageView!
    
    // ユーザ一覧画面
    private var userListViewController: UserListViewController!

    // マッチング画面
    private var matchingViewController: MatchingViewController!

    // マイページ画面
    private var myPageViewController: MyPageViewController!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // 3つの主要コンテンツを配置する
        self.initViewControllers()
        
        // tab0: ユーザ一覧を初期表示とする
        self.changeTab(index: 0)
    }
    
    // 3つの主要コンテンツを配置する
    private func initViewControllers() {
        
        // ユーザ一覧画面
        let vcUserList = self.instantiate(identifier: "UserListViewController")
                                            as! UserListViewController
        self.addViewController(vc: vcUserList)
        self.userListViewController = vcUserList
        
        // マッチング画面
        let vcMatching = self.instantiate(identifier: "MatchingViewController")
                                            as! MatchingViewController
        self.addViewController(vc: vcMatching)
        self.matchingViewController = vcMatching
        
        // マイページ画面
        let vcMyPage = self.instantiate(identifier: "MyPageViewController")
                                            as! MyPageViewController
        self.addViewController(vc: vcMyPage)
        self.myPageViewController = vcMyPage
    }
    
    // UIViewControllerを画面上に表示する
    private func addViewController(vc: UIViewController) {
        
        self.containerView.addSubview(vc.view)
        self.addChild(vc)
        vc.didMove(toParent: self)
        
        vc.view.translatesAutoresizingMaskIntoConstraints = false
        
        let topAnchor = self.containerView.topAnchor
        let top = vc.view.topAnchor.constraint(equalTo: topAnchor)
        top.isActive = true
        
        let leadingAnchor = self.containerView.leadingAnchor
        let leading = vc.view.leadingAnchor.constraint(equalTo: leadingAnchor)
        leading.isActive = true
        
        let trailingAnchor = self.containerView.trailingAnchor
        let trailing = vc.view.trailingAnchor.constraint(equalTo: trailingAnchor)
        trailing.isActive = true

        let bottomAnchor = self.containerView.bottomAnchor
        let bottom = vc.view.bottomAnchor.constraint(equalTo: bottomAnchor)
        bottom.isActive = true
    }
        
    // タブを切り替える
    private func changeTab(index: Int) {
        
        self.userListViewController.view.isHidden = (index != 0)
        self.matchingViewController.view.isHidden = (index != 1)
        self.myPageViewController.view.isHidden = (index != 2)
        
        self.tab0ImageView.image = UIImage(named: (index == 0) ? "tab0_on" : "tab0_off")
        self.tab1ImageView.image = UIImage(named: (index == 1) ? "tab1_on" : "tab1_off")
        self.tab2ImageView.image = UIImage(named: (index == 2) ? "tab2_on" : "tab2_off")
    }
    
    // 定期通信用タイマの開始
    private func startTimer() {
        
        // 60秒おきにユーザ情報取得APIをコール
        Timer.scheduledTimer(withTimeInterval: 60,
                             repeats: true,
                             block: { [weak self] _ in
                                
            GetUserRequester.shared.fetch(completion: { result in
                if result {
                    self?.reload()
                }
            })
        })
    }
    
    // tab0ボタンが押された
    @IBAction func onTapTab0(_ sender: Any) {
        self.changeTab(index: 0)
    }
    
    // tab1ボタンが押された
    @IBAction func onTapTab1(_ sender: Any) {
        self.changeTab(index: 1)
    }
    
    // tab2ボタンが押された
    @IBAction func onTapTab2(_ sender: Any) {
        self.changeTab(index: 2)
    }
    
    // 画面をリロードする
    func reload() {
        
        self.userListViewController.reload()
        self.matchingViewController.reload()
        self.myPageViewController.reload()
    }
}
