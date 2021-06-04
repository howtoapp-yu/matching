<?php

require "./user.php";
require "./chat.php";

/* --------------------------------------------------------------
 * アプリとのインターフェース部
 * -------------------------------------------------------------- */

// APIの種類の判別
$command = getPostParam("command");

// ユーザ登録API
if (strcmp($command, "registerUser") == 0) {
	registerUser();
}
// ユーザのプロフィール編集API
else if (strcmp($command, "editUser") == 0) {
	editUser();
} 
// ユーザ情報取得API
else if (strcmp($command, "getUser") == 0) {
	getUser();
}
// 「いいね！」送信API
else if (strcmp($command, "postLike") == 0) {
	postLike();
} 
// チャットメッセージ送信API
else if (strcmp($command, "postChat") == 0) {
	postChat();
} 
// チャットメッセージ取得API
else if (strcmp($command, "getChat") == 0) {
	getChat();
}

// ユーザ登録API
function registerUser() {

	// POSTパラメータを取り出す
	$name = getPostParam("name");			/* ユーザ名 */
	$message = getPostParam("message");		/* メッセージ */
	$image = getPostParam("image");			/* プロフィール画像 */

	// ユーザを新規作成
	$userId = User::create($name, $message, $image);
	if (!is_null($userId)) {
		// 成功の結果を出力する
		outputResponse(["result" => "0", "userId" => $userId]);
	} else {
		// 失敗の結果を出力する
		outputResponse(["result" => "1"]);
	}
}

// ユーザのプロフィール編集API
function editUser() {

	// POSTパラメータを取り出す
	$userId = getPostParam("userId");		/* ユーザID */
	$name = getPostParam("name");			/* ユーザ名 */
	$message = getPostParam("message");		/* メッセージ */
	$image = getPostParam("image");			/* プロフィール画像 */

	// ユーザ情報を更新
	if (User::update($userId, $name, $message, $image)) {
		// 成功の結果を出力する
		outputResponse(["result" => "0"]);
	} else {
		// 失敗の結果を出力する
		outputResponse(["result" => "1"]);
	}
}

// ユーザ情報取得API
function getUser() {

	$users = [];

	// 全てのユーザ情報を読み出し、配列に格納する
	foreach (User::readAll() as $user) {
		$users[] = $user->toApiResponse();
	}

	// ユーザ情報を出力する
	outputResponse(["result" => "0", "users" => $users]);
}

// 「いいね！」送信API
function postLike() {

	// POSTパラメータを取り出す
	$senderId = getPostParam("senderId");		/* 送信者のユーザID */
	$targetId = getPostParam("targetId");		/* 送信相手のユーザID */

	// 「いいね！」情報を保存する
	if (User::addLikeTargetId($senderId, $targetId)) {
		// 成功の結果を出力する
		outputResponse(["result" => "0"]);
	} else {
		// 失敗の結果を出力する
		outputResponse(["result" => "1"]);
	}
}

// チャットメッセージ送信API
function postChat() {

	// POSTパラメータを取り出す
	$senderId = getPostParam("senderId");		/* 送信者のユーザID */
	$targetId = getPostParam("targetId");		/* 送信相手のユーザID */
	$message = getPostParam("message");			/* チャットメッセージ */

	// メッセージを保存する
	if (Chat::addMessage($senderId, $targetId, $message)) {
		// 成功の結果を出力する
		outputResponse(["result" => "0"]);
	} else {
		// 失敗の結果を出力する
		outputResponse(["result" => "1"]);
	}
}

// チャットメッセージ取得API
function getChat() {

	// POSTパラメータを取り出す
	$senderId = getPostParam("senderId");		/* 送信者のユーザID */
	$targetId = getPostParam("targetId");		/* チャット相手のユーザID */

	$chats = [];

	// チャットのメッセージを読み出し、配列に格納する
	foreach (Chat::readAll($senderId, $targetId) as $chat) {
		$chats[] = $chat->toApiResponse();
	}

	// チャット情報を出力する
	outputResponse(["result" => "0", "chats" => $chats]);
}

// POSTパラメータを取り出し、" "(スペース)があれば"+"に変換する
function getPostParam($key) {

	$value = $_POST[$key];
	return str_replace(" ", "+", $value);
}

// 配列をJSONに変換して、アプリ向けに出力する
function outputResponse($array) {

	$json = json_encode($array);
	echo($json);
}

?>
