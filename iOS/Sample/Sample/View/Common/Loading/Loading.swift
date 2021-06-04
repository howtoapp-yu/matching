
import UIKit

class Loading: UIView {

    // 通信中に回転させるUIImageView
    @IBOutlet private weak var loadingImageView: UIImageView!
    
    private var timerCount = 0
    
    override func awakeFromNib() {
        super.awakeFromNib()

        // 0.1秒毎のタイマーをスタート
        Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true, block: { _ in
            self.timerProc()
        })
    }
    
    // 0.1秒毎に45°ずつ画像を回転させる
    private func timerProc() {
        
        self.timerCount += 1
        if self.timerCount >= 8 {
            self.timerCount = 0
        }
        
        let angle = 45 * CGFloat(self.timerCount) * CGFloat.pi / 180
        self.loadingImageView.transform = CGAffineTransform(rotationAngle: angle)
    }
    
    // ローディング開始
    class func start() {
        
        guard let window = UIApplication.shared.keyWindow else {
            return
        }
        if !(window.subviews.compactMap { $0 as? Loading }).isEmpty {
            return
        }
        
        let nib = UINib(nibName: "Loading", bundle: nil)
        let loading = nib.instantiate(withOwner: self, options: nil).first as! Loading
        
        // Window上に表示してframeを調整する
        window.addSubview(loading)
        loading.frame = CGRect(origin: .zero, size: window.frame.size)
    }
    
    // ローディング停止
    class func stop() {
        
        guard let window = UIApplication.shared.keyWindow else {
            return
        }
        let loadingViews = window.subviews.compactMap { $0 as? Loading }
        loadingViews.forEach { $0.removeFromSuperview() }
    }
}
