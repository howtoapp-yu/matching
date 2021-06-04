
import UIKit

class ImageStorage {
    
    // 画像キャッシュの有効期限(60分)
    private static let expirationInterval = TimeInterval(60 * 60)

    // リクエスト情報
    private struct RequestData {
        let url: String
        let imageView: UIImageView
        let errorImage: UIImage?
    }
    
    // 取得したデータのキャッシュ情報
    private struct CacheData {
        let url: String
        let image: UIImage
        let datetime: Date
    }

    // シングルトン化
    static let shared = ImageStorage()
    
    // リクエスト情報の配列
    private var requests = [RequestData]()
    
    // キャッシュデータの配列
    private var caches = [CacheData]()
    
    // サーバ上に配置されている画像をHTTP通信で取得し、ローカルに保存する
    func fetch(url: String,
               imageView: UIImageView,
               errorImage: UIImage? = nil,
               completion: ((Bool) -> ())? = nil) {
        
        // 同一のUIImageViewに対するリクエストがあれば、リクエストをキャンセルする
        self.cancelRequest(imageView: imageView)
        
        // キャッシュに画像が存在すれば、キャッシュから取り出す
        let cache = self.caches.first(where: { cache -> Bool in
            if cache.url != url {
                return false
            }
            let timeInterval = Date().timeIntervalSince(cache.datetime)
            return timeInterval <= ImageStorage.expirationInterval
        })
        if let image = cache?.image {
            imageView.image = image
            completion?(true)
            return
        }
        
        // リクエスト情報配列に追加
        self.requests.append(RequestData(url: url,
                                         imageView: imageView,
                                         errorImage: errorImage))
        
        // ローカルから読み込み
        self.readLocalFile(url: url, completion: { image in
            // ローカルに画像が存在する場合
            if let image = image {
                // 画像を呼び出し元に反映
                self.applyResult(url: url,
                                 imageView: imageView,
                                 image: image,
                                 completion: completion)
            } else {
                // サーバから画像を取得
                self.fetchRemoteFile(url: url, completion: { image in
                    // 画像を呼び出し元に反映
                    self.applyResult(url: url,
                                     imageView: imageView,
                                     image: image,
                                     completion: completion)
                })
            }
        })
    }
    
    // 取得した画像を呼び出し元に反映する
    private func applyResult(url: String,
                             imageView: UIImageView,
                             image: UIImage?,
                             completion: ((Bool) -> ())? = nil) {
        
        DispatchQueue.main.async {
            
            // UIImageViewに画像をセット
            self.requests.filter { $0.url == url }.forEach { requestData in
                requestData.imageView.image = image
            }
            self.cancelRequest(imageView: imageView)

            // 呼び出し元に通知
            completion?(image != nil)
            
            if let image = image {
                
                // 古いキャッシュデータを削除
                self.caches.removeAll(where: { cache -> Bool in
                    let timeInterval = Date().timeIntervalSince(cache.datetime)
                    return timeInterval > ImageStorage.expirationInterval
                })
                
                // キャッシュデータに追加
                if self.caches.count > 50 {
                    self.caches.remove(at: 0)
                }
                self.caches.append(CacheData(url: url,
                                             image: image,
                                             datetime: Date()))
            }
        }
    }
    
    // 保存先のディレクトリを返す
    private func getRootDirectory() -> URL? {
        return FileManager.default.urls(for: .documentDirectory,
                                        in: .userDomainMask).first
    }
    
    // 保存先のファイルパスを返す
    private func createLocalPath(url: String) -> URL? {
        
        if let rootUrl = self.getRootDirectory() {
            if var encodedUrl = url.base64Encode() {
                // URLが長い場合は末尾64文字のみ使用する
                if encodedUrl.count > 64 {
                    encodedUrl = encodedUrl.substr(start: encodedUrl.count - 64, length: 64)
                }
                return rootUrl.appendingPathComponent(encodedUrl)
            }
        }
        return nil
    }
    
    // ローカルに保存されている画像を読み出す
    private func readLocalFile(url: String,
                               completion: @escaping ((UIImage?) -> ())) {
        
        DispatchQueue.global().async {
            guard let imagePath = self.createLocalPath(url: url) else {
                completion(nil)
                return
            }
            
            let manager = FileManager()
            
            do {
                // ファイル保存期間が規定値より大きい場合は破棄する
                let attrs = try manager.attributesOfItem(atPath: imagePath.path)
                if let modificationDate = attrs[.modificationDate] as? Date {
                    let timeInterval = Date().timeIntervalSince(modificationDate)
                    if timeInterval > ImageStorage.expirationInterval {
                        completion(nil)
                        return
                    }
                }
            } catch {}

            // ファイルパス先のデータをUIImageに変換
            let image = UIImage(contentsOfFile: imagePath.path)
            
            // 呼び出し元に通知
            completion(image)
        }
    }
    
    // 取得した画像をローカルファイルに保存する
    private func saveLocalFile(url: String,
                               image: UIImage,
                               completion: @escaping ((UIImage?) -> ())) {

        // UIImageからData型への変換、ファイルパスの生成
        guard let imageData = image.pngData(),
              let imagePath = self.createLocalPath(url: url) else {
            
            completion(nil)
            return
        }
        
        do {
            // ファイルに書き込み
            try imageData.write(to: imagePath, options: .atomic)
        } catch {
            completion(nil)
            return
        }
        
        // 呼び出し元に通知
        completion(image)
    }
    
    // サーバ上の画像をHTTP通信で取得する
    private func fetchRemoteFile(url: String,
                                 completion: @escaping ((UIImage?) -> ())) {
        
        // HTTP通信
        HttpRequester.get(url: url) { result, data in
            DispatchQueue.global().async {
                
                // 取得したデータをUIImage変換
                if result, let data = data, let image = UIImage(data: data) {
                    // ローカルファイルに保存
                    self.saveLocalFile(url: url, image: image, completion: completion)
                } else {
                    // 呼び出し元で指定したNoImage画像を採用
                    if let request = (self.requests.filter { $0.url == url }).first,
                       let errorImage = request.errorImage {

                        // ローカルファイルに保存
                        self.saveLocalFile(url: url, image: errorImage, completion: completion)
                        completion(errorImage)
                    }
                    // デフォルトのNoImage画像を採用
                    else if let noImage = UIImage(named: "no_image") {

                        // ローカルファイルに保存s
                        self.saveLocalFile(url: url, image: noImage, completion: completion)
                        completion(noImage)
                    } else {
                        completion(nil)
                    }
                }
            }
        }
    }
    
    // ローカルに保存されたファイルを削除する
    func deleteLocalFile(url: String) -> Bool {
        
        if let imagePath = self.createLocalPath(url: url) {
            do {
                // ファイル削除
                try FileManager.default.removeItem(at: imagePath)
                return true
            } catch {
                return false
            }
        }
        return false
    }
    
    // 画像の取得リクエストを取り下げる
    func cancelRequest(imageView: UIImageView) {
        self.requests.removeAll(where: { $0.imageView == imageView })
    }
    
    // 指定した画像をローカルから読み出す
    func query(url: String, completion: @escaping ((UIImage?) -> ())) {
        
        // ファイルパスの生成
        guard let imagePath = self.createLocalPath(url: url) else {
            completion(nil)
            return
        }
        
        DispatchQueue.global().async {
            // データのUIImage変換
            let image = UIImage(contentsOfFile: imagePath.path)

            // 呼び出し元に通知
            DispatchQueue.main.async {
                completion(image)
            }
        }
    }
    
    // 取得済みの画像データを全て削除する
    func removeAll() {

        guard var rootUrl = self.getRootDirectory()?.absoluteString else {
            return
        }
        if rootUrl.substr(start: 0, length: 7) == "file://" {
            rootUrl = rootUrl.substr(start: 7, length: rootUrl.count - 7)
        }
        
        let fileManager = FileManager.default
        
        if let contents = try? fileManager.contentsOfDirectory(atPath: rootUrl) {
            contents.forEach { content in
                let filePath = rootUrl + "/" + content
                try? FileManager.default.removeItem(atPath: filePath)
            }
        }
        
        // キャッシュデータも同時に削除
        self.caches.removeAll()
    }
}
