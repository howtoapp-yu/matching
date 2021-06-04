<?php

/* --------------------------------------------------------------
 * Chat: チャットのメッセージデータの管理を行う
 * -------------------------------------------------------------- */

class Chat {

	public $datetime;			/* 送信日時 */
	public $senderId;			/* 送信者のユーザID */
	public $targetId;			/* 受信者のユーザID */
	public $message;			/* チャットメッセージ */

	/* ----------------------------------------------------------
	 * 概要: ファイルからチャットの情報を読み出し、
	 *       Chatクラスのオブジェクトを作成する
	 * 引数: ファイルパス(./data/chat/{ユーザID1}-{ユーザID2}.txt)
	 * 返値: Chatクラスのオブジェクト
	 *       ファイルデータに不備がある場合はnullを返す
	 * ---------------------------------------------------------- */
	static function initFromString($string) {

		$datas = explode(",", $string);
		if (count($datas) == 4) {
			$data = new Chat();
			$data->datetime = $datas[0];
			$data->senderId = $datas[1];
			$data->targetId = $datas[2];
			$data->message = $datas[3];
			return $data;
		}
		return null;
	}

	/* ----------------------------------------------------------
	 * 概要: Chatオブジェクトをファイルに保存するために文字列に変換する
	 * 引数: なし
	 * 返値: チャット情報の文字列
	 * ---------------------------------------------------------- */
	function toFileString() {

		return $this->datetime . ","
				. $this->senderId . ","
				. $this->targetId . ","
				. $this->message . "\n";
	}

	/* ----------------------------------------------------------
	 * 概要: ChatオブジェクトをAPIのレスポンスに格納するための配列に
	 *       変換する
	 * 引数: なし
	 * 返値: チャット情報の配列
	 * ---------------------------------------------------------- */
	function toApiResponse() {

		return Array("datetime" => $this->datetime,
					 "senderId" => $this->senderId,
					 "targetId" => $this->targetId,
					 "message" => $this->message);
	}

	/* ----------------------------------------------------------
	 * 概要: チャット情報を保存するファイルパスを返す
	 * 引数: userId1 -> チャット中の1人目のユーザ
	        userId2 -> チャット中の2人目のユーザ
	 * 返値: ユーザのプロフィール情報のファイルパス
	 * ---------------------------------------------------------- */
	function getFilePath($userId1, $userId2) {

		if (strcmp($userId1, $userId2) > 0) {
			return "./data/chat/" . $userId1 . "-" . $userId2 . ".txt";
		} else {
			return "./data/chat/" . $userId2 . "-" . $userId1 . ".txt";
		}
	}

	/* ----------------------------------------------------------
	 * 概要: 全てのチャット情報を読み出す
	 * 引数: userId1 -> チャット中の1人目のユーザ
	        userId2 -> チャット中の2人目のユーザ
	 * 返値: チャット情報の配列
	 * ---------------------------------------------------------- */
	function readAll($userId1, $userId2) {

		$datas = [];

		$filePath = Chat::getFilePath($userId1, $userId2);				
		if (file_exists($filePath)) {
			$fileData = file_get_contents($filePath);
			if ($fileData !== false) {
				foreach (explode("\n", $fileData) as $line) {
					$data = Chat::initFromString($line);
					if (!is_null($data)) {
						$datas[] = $data;
					}
				}
			}
		}

		return $datas;
	}

	/* ----------------------------------------------------------
	 * 概要: チャットメッセージをファイルに書き込む
	 * 引数: senderId -> メッセージ送信者のユーザID
	        targetId -> チャット相手のユーザID
	        message -> チャットメッセージ
	 * 返値: なし
	 * ---------------------------------------------------------- */
	function addMessage($senderId, $targetId, $message) {

		$chat = new Chat();
		$chat->datetime = date("YmdHis");
		$chat->senderId = $senderId;
		$chat->targetId = $targetId;
		$chat->message = $message;

		$filePath = Chat::getFilePath($senderId, $targetId);
		return file_put_contents($filePath, $chat->toFileString(), FILE_APPEND) !== false;
	}
}

?>
