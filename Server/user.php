<?php

/* --------------------------------------------------------------
 * User: ユーザのプロフィールデータの管理を行う
 * -------------------------------------------------------------- */

class User {

	public $id;					/* ユーザID */
	public $name;				/* ユーザ名 */
	public $message;			/* メッセージ */
	public $likeTargetIds;		/* 「いいね！」送信先のユーザID */

	/* ----------------------------------------------------------
	 * 概要: ファイルからユーザのプロフィール情報を読み出し、
	 *       Userクラスのオブジェクトを作成する
	 * 引数: ファイルパス(./data/user/{ユーザID}/profile.txt)
	 * 返値: Userクラスのオブジェクト
	 *       ファイルデータに不備がある場合はnullを返す
	 * ---------------------------------------------------------- */
	static function initFromFilePath($filePath) {

		if (!file_exists($filePath)) {
			return null;
		}
		$fileData = file_get_contents($filePath);
		if ($fileData === false) {
			return null;
		}

		$datas = explode("\n", $fileData);
		if (count($datas) == 4) {
			$data = new User();
			$data->id = $datas[0];
			$data->name = $datas[1];
			$data->message = $datas[2];
			$data->likeTargetIds = array_filter(explode(",", $datas[3]), "strlen");
			return $data;
		}
		return null;
	}

	/* ----------------------------------------------------------
	 * 概要: Userオブジェクトをファイルに保存するために文字列に変換する
	 * 引数: なし
	 * 返値: ユーザのプロフィール情報の文字列
	 * ---------------------------------------------------------- */
	function toFileString() {

		return $this->id . "\n"
				. $this->name . "\n"
				. $this->message. "\n"
				. implode(",", $this->likeTargetIds);
	}

	/* ----------------------------------------------------------
	 * 概要: UserオブジェクトをAPIのレスポンスに格納するための配列に
	 *       変換する
	 * 引数: なし
	 * 返値: ユーザのプロフィール情報の配列
	 * ---------------------------------------------------------- */
	function toApiResponse() {

		return Array("id" => $this->id,
					 "name" => $this->name,
					 "message" => $this->message,
					 "likeTargetIds" => $this->likeTargetIds);
	}

	/* ----------------------------------------------------------
	 * 概要: ユーザのプロフィール情報を保存するファイルパスを返す
	 * 引数: なし
	 * 返値: ユーザのプロフィール情報のファイルパス
	 * ---------------------------------------------------------- */
	function getFilePath() {
		return "./data/user/" . $this->id . "/profile.txt";
	}

	/* ----------------------------------------------------------
	 * 概要: ユーザのプロフィール情報を保存する
	 * 引数: なし
	 * 返値: 保存結果
	 * ---------------------------------------------------------- */
	function save() {
		return file_put_contents($this->getFilePath(), $this->toFileString()) !== false;
	}

	/* ----------------------------------------------------------
	 * 概要: 全ユーザの情報を取得する
	 * 引数: なし
	 * 返値: 全ユーザのプロフィール情報の配列
	 * ---------------------------------------------------------- */
	static function readAll() {

		$datas = [];

		foreach (glob("./data/user/*", GLOB_BRACE) as $dir) {
			$data = User::initFromFilePath($dir . "/profile.txt");
			if (!is_null($data)) {
				$datas[] = $data;
			}
		}
		return $datas;
	}

	/* ----------------------------------------------------------
	 * 概要: ユーザの情報を取得する。
	 * 引数: $id -> 情報を取得するユーザのユーザID
	 * 返値: 該当ユーザのUserオブジェクト
	 * ---------------------------------------------------------- */
	static function read($id) {

		$filePath = "./data/user/" . $id . "/profile.txt";
		return User::initFromFilePath($filePath);
	}

	/* ----------------------------------------------------------
	 * 概要: ユーザを新規作成する
	 * 引数: $name -> ユーザ名
	 *      $message -> メッセージ
	 *      $image -> プロフィール画像
	 * 返値: 作成したユーザのユーザID
	        作成に失敗した場合はnullを返す
	 * ---------------------------------------------------------- */
	static function create($name, $message, $image) {

		// ユーザIDを「日時 + 8桁のランダム文字列」のルールで作成する
		$userId = date("YmdHis") . "-" . substr(str_shuffle("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"), 0, 8);

		$user = new User();
		$user->id = $userId;
		$user->name = $name;
		$user->message = $message;
		$user->likeTargetIds = [];

		// ユーザ情報を格納するディレクトリを作成
		$dir = "./data/user/" . $userId;
		$mask = umask();
		umask(000);
		mkdir($dir, 0777, true);
		umask($mask);

		// プロフィール画像を保存する
		if (strlen($image) > 0) {
			$imagePath = $dir . "/image";
			if (file_put_contents($imagePath, base64_decode($image)) === false) {
				// 画像の保存に失敗
				return null;
			}
		}

		// 保存
		if ($user->save()) {
			return $userId;
		}

		// ユーザ情報の保存に失敗
		return null;
	}

	/* ----------------------------------------------------------
	 * 概要: ユーザ情報を更新する
	 * 引数: $id -> ユーザID
	 *      $name -> ユーザ名
	 *      $message -> メッセージ
	 *      $image -> プロフィール画像
	 * 返値: 処理結果
	 * ---------------------------------------------------------- */
	static function update($id, $name, $message, $image) {

		// ユーザ情報を読み込み
		$user = User::read($id);
		if (is_null($user)) {
			// 読み込みに失敗
			return false;
		}

		$user->name = $name;
		$user->message = $message;

		// プロフィール画像を保存する
		if (strlen($image) > 0) {
			if (file_put_contents("./data/user/" . $id . "/image", base64_decode($image)) === false) {
				// 画像の保存に失敗
				return false;
			}
		}

		// 保存
		return $user->save();
	}

	/* ----------------------------------------------------------
	 * 概要: 「いいね！」送信対象ユーザを追加する
	 * 引数: $id -> ユーザID
	 *      $targetId -> 送信対象のユーザID
	 * 返値: 処理結果
	 * ---------------------------------------------------------- */
	static function addLikeTargetId($id, $targetId) {

		// ユーザ情報を読み込み
		$user = User::read($id);
		if (is_null($user)) {
			// 読み込みに失敗
			return false;
		}

		if (!in_array($targetId, $user->likeTargetIds)) {
			$user->likeTargetIds[] = $targetId;

			// 保存
			return $user->save();
		}

		// 既に追加済みのため成功として扱う
		return true;
	}
}

?>
