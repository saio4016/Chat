package TransChat;

import WebTranslator.WebTranslator;
import java.io.*;
import java.net.Socket;

/**
 * クライアント通信処理スレッド
 *
 * @author 18024115
 */
public class MyClient extends Thread {

    // 親のMyChatへの参照
    MyChat parent;

    // チャットアプリ用のポート番号
    int port = 13579;

    // 状態管理用フラグ
    private boolean status = true;

    // サーバとの通信用のソケット
    Socket socket;

    //===========================================
    // 各種リーダ、ライター、ストリーム
    //===========================================
    // ソケットからの入出力
    public BufferedReader in = null; // リーダー(入力インタフェース)
    public PrintWriter out = null;   // ライター(出力インタフェース)

    //================================================================
    /**
     * 初期化メソッド
     *
     * @param parent メインオブジェクトへの参照
     * @param hostname 接続するサーバのホスト名
     * @param portno 接続するサーバのポート番号
     * @throws Exception
     */
    public MyClient(MyChat parent, String hostname, int portno) throws Exception {
        /*
         * throws Exception を付けることで、このメソッド内で発生した
         * Exceptionはすべて呼出元で処理するように上に投げられる
         */

        // 親のMyChatへの参照を受け取る
        this.parent = parent;

        // ポート番号の取得(設定画面等で変更可能とするための措置)
        port = portno;

        // サーバに接続する
        socket = new Socket(hostname, port);

        //-----------------------------------------------------
        // ソケットから入出力インタフェースを取得する
        //-----------------------------------------------------
        // リーダーは使いやすいようにBufferedReaderにラップする
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // ライターも使いやすいようにPrintWriterにラップする
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    //================================================================
    /**
     * スレッドのメイン処理部
     */
    @Override
    public void run() {
        while (status) {
            try {
//******************************************************************
                // サーバからのメッセージを１行読み込み出力画面に表示する
                String received = in.readLine();
                // ※readLine()は改行(\n)でメッセージを区切って読込み，改行は
                // 破棄されてしまうので、表示のために改めて末尾に追加している

                // [全員課題] ユーザ名とメッセージの分割  
                // splited[0] = 言語番号, splited[1] = ユーザー名, splited[2] = メッセージ
                String[] splited = received.split("\t");

                if (splited[0].equals("-1")) {
                    // 言語番号が-1の時はサーバーからのメッセージ
                    parent.append(splited[2] + "\n");
                } else {
                    // [全員課題] 画面への出力内容を"[ユーザ名] メッセージ"の形式にする
                    String name = "[" + splited[1] + "]";

                    // [全員課題] メッセージを翻訳する
                    String message = splited[2];
                    int befor = Integer.valueOf(splited[0]);
                    int after = parent.getLanguageType();
                    if (befor != after) {
                        WebTranslator wt = new WebTranslator();
                        message = wt.translation(splited[2], befor, after);
                        message += " (原文: " + splited[2] + ")";
                    }

                    // チャット画面への出力(ひな形は受け取ったものをそのまま出力)
                    // appendはprintlnと違い自動で改行を入れないので手作業で挿入
                    parent.append(name + " " + message + "\n");
                }
//******************************************************************
            } catch (IOException e) {
                // 通信エラーなどが発生したらこの処理に入るので、スレッドを停止させる
                status = false;
            }
        }
        // メインループの後処理
        parent.append("サーバとの通信を停止しました.\n");
        // 終了処理
        close();
    }

    //================================================================
    /**
     * 出力用の通信インタフェースを提供するゲッターメソッド
     *
     * @return 出力用のライタ
     */
    public PrintWriter getWriter() {
        // Writerを返す
        return out;
    }

    //================================================================
    /**
     * 終了処理
     */
    public void close() {
        // スレッドを停止
        status = false;
        // null にすることで参照を切り、GCにオブジェクトを廃棄してもらう
        in = null;
        out = null;
        // ソケット停止
        try {
            if (socket != null) {
                socket.close(); // ソケットを閉じる
                socket = null;  // オブジェクトを廃棄する
            }
        } catch (IOException e) {
            // 切断に失敗した場合の対処を書く部分
        }
    }
}
