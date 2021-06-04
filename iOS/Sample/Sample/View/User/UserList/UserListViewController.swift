
import UIKit

class UserListViewController: UIViewController {

    @IBOutlet private weak var collectionView: UICollectionView!
    
    private var userDatas = [UserData]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.reload()
    }
    
    // UICollectionViewの内容を更新する
    func reload() {

        // 自身以外の全ユーザを保持する
        let userDatas = GetUserRequester.shared.dataList
        self.userDatas = userDatas.filter { $0.id != SaveData.shared.userId }
        
        self.collectionView.reloadData()
    }
}

extension UserListViewController: UICollectionViewDelegate,
                                  UICollectionViewDataSource,
                                  UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAt indexPath: IndexPath) -> CGSize {

        // セルの横幅
        let width = (UIScreen.main.bounds.size.width - 20) / 2
        
        // 画像の高さ
        let imageHeight = (width - 10) * 2 / 3
        
        // セルの高さ
        let height = imageHeight + 110
        
        // セルのサイズ
        return CGSize(width: width, height: height)
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }

    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        numberOfItemsInSection section: Int) -> Int {
        
        // 自身を除く全てのユーザを表示する
        return self.userDatas.count
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        let identifier = "UserListCollectionViewCell"
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: identifier,
                                                      for: indexPath)
        let userListCollectionViewCell = cell as! UserListCollectionViewCell
        
        // ユーザ情報を設定する
        userListCollectionViewCell.configure(userData: self.userDatas[indexPath.row])

        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        didSelectItemAt indexPath: IndexPath) {

        // ユーザ詳細画面に遷移する
        let vcIdentifier = "UserDetailViewController"
        let userDetailViewController = self.instantiate(identifier: vcIdentifier)
                                            as! UserDetailViewController
        
        // ユーザ情報を設定する
        userDetailViewController.set(userData: self.userDatas[indexPath.row])
        
        self.getTabbarViewController()?.stack(viewController: userDetailViewController,
                                              animationType: .horizontal)
    }
}
