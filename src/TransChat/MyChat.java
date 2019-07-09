package TransChat;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * オブジェクト指向応用演習　後半課題
 * Excite翻訳を利用した多言語対応チャット
 * @author 学籍番号　氏名　// 自分の氏名・番号を記入して下さい
 */
public class MyChat extends Application {
    // アプリ固有に設定した通信ポート番号(定数)
    final int port = 13579; // チャットアプリ用のポート番号

    //=======================================================
    // メソッドをまたいで参照されるGUIコントロール
    //=======================================================
    TextArea output;            // 出力エリア
    TextField input;            // メッセージ入力欄
    Button enter;               // 発言ボタン
    ChoiceBox<String> choice;   // 言語選択欄
    
    //=======================================================
    // 他の処理で参照する変数
    //=======================================================
    static MyClient client;    // クライアントスレッド
    static PrintWriter netout; // 送信用ライタ

    // ユーザ名
    String username = "Unknown";
    
    // 状態管理用変数
    int language_type = 0;  // 使用言語
   
    // 言語リスト(選択肢に利用する)
    List<String> lang_list = Arrays.asList(
                "Japanese","English","Chinese"
            );
   
    //------------------------------------------------------------------
    /**
     * 処理部メイン
     * @param stage 
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("多言語チャット");
        stage.setWidth(800);              
        stage.setHeight(600);

        //=============================================================
        // 出力欄となるテキストエリアの設定
        output = new TextArea();
        // 出力欄は直接書き込み不可
        output.setEditable(false);
        // 領域のはじで文字列を折り返すように設定
        output.setWrapText(true);
        // フォントを見やすく変更
        output.setStyle("-fx-font:14pt Meiryo;");
        
        //=============================================================
        // メッセージ入力欄
        input = new TextField();
        // サイズの調整
        input.setMinWidth(520);
        input.setPrefWidth(520);
        input.setMaxWidth(520);
        // キーが押された際のイベントを追加
        input.setOnKeyPressed(event->onKeyPressedForInput(event));
        
        // フォントを見やすく変更
        input.setStyle("-fx-font:14pt Meiryo;");

        //=============================================================
        // 送信ボタン
        enter = new Button(" 送信 ");
        // 押下時，メッセージ入力欄の内容を送信する
        enter.setOnAction(event->sendAction());
        // フォントを見やすく変更
        enter.setStyle("-fx-font:14pt Meiryo;");
        
        //=============================================================
        // 言語選択欄
        choice = new ChoiceBox<>();
        choice.getItems().addAll(lang_list); // 言語リストから項目を作成する
        choice.setStyle("-fx-font:14pt Meiryo;");
        // 選択時の動作を設定
        // オブジェクト指向言語の演習で示したサンプルを参考に自分で設定する
        // 言語番号の設定はこのプログラムにあるsetLanguageTypeを使うこと
        choice.setOnAction(
                e->setLanguageType(lang_list.indexOf(((ChoiceBox)e.getSource()).getValue()))
        );
        
        // 最初の選択肢が選択された状態を初期状態にする
        choice.getSelectionModel().selectFirst();
        
        //=============================================================
        // メニューバーを使用する
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        // メニューの設定
        Menu ctrlMenu = new Menu("操作");
        ctrlMenu.setStyle("-fx-font:14pt Meiryo;");
        
        // サブメニューの設定と割り当て
        MenuItem mnuConnect = new MenuItem("通信開始");
        mnuConnect.setOnAction(event -> connectAction(stage));

        MenuItem mnuDisconnect = new MenuItem("切断");
        mnuDisconnect.setOnAction(event -> disconnectAction());

        MenuItem mnuExit = new MenuItem("終了");
        mnuExit.setOnAction(event -> exit());

        // サブメニューに個別メニューを登録
        ctrlMenu.getItems().addAll(mnuConnect,mnuDisconnect,mnuExit);
        // メインメニューにサブメニューを登録
        menuBar.getMenus().add(ctrlMenu);
        
        //=============================================================
        
        // 入力欄を横一列に配置
        HBox textPane = new HBox();
        textPane.setPadding(new Insets(10,10,10,10));
        textPane.setSpacing(10);
        textPane.getChildren().addAll(input,enter,choice);

        // メニューバーとテキストエリアを配置する
        BorderPane root = new BorderPane();
        root.setTop(menuBar);       // 上部にメニュー
        root.setCenter(output);     // 中央はメッセージ表示画面
        root.setBottom(textPane);   // 下部はメッセージ入力欄
        
        // シーンの割当てと表示
        stage.setScene(new Scene(root));
        stage.show();                     
    }

    //------------------------------------------------------------------
    /**
     * 「通信開始」メニュー選択時の処理
     */
    void connectAction(Stage stage) {
        // すでに接続中の場合にはエラー表示
        if (netout != null) {
            append("すでにサーバと接続中です.");
            return;
        } 

//******************************************************************
        //--------------------------------------------
        // ダイアログからの接続先情報の取得
        //--------------------------------------------
        // ここにダイアログを表示して情報を取得する処理を入れる
        // (1) ダイアログのオブジェクトの生成
        ConnectDialog dialog = new ConnectDialog(stage);
        
        // (2) ダイアログの表示 ※閉じられるまで処理を止める
        dialog.showAndWait();
        
        // (3) キャンセルボタン押下時の処理
        // ※「キャンセルされました」と画面表示してメソッドを終了(return)する
        // ダイアログから入力欄の内容を取得したときにnullであるならばキャンセルが
        // 押された場合である。接続ボタンが押された場合はnull以外の値を持っている。
        // ※ 150-153行目を参考にして作成してください。
        if(dialog.getHost_val() == null && dialog.getUser_val() == null) {
            append("キャンセルされました");
            return;
        }

        // (4) ホスト名をダイアログから取得
        // ※必要なゲッターをダイアログ側で用意すること
        //--------------------------------------------
        // ディフォルトのホスト名を設定
        // このプログラムを実行したホスト(localhost)を指定
        String hostname = "localhost";
        // ダイアログからの取得が可能な場合は，取得した情報で
        // hostnameを上書きする，取得できない場合はlocalhostを使う
        if(!dialog.getUser_val().isEmpty()) {
            hostname = dialog.getHost_val();
        }
        //--------------------------------------------

        // (5) ユーザ名をダイアログから取得
        // ※必要なゲッターをダイアログ側で用意すること
        //--------------------------------------------
        // ディフォルトのユーザ名を設定
        // 実行したコンピュータのホスト名をユーザ名とする
        try {
            // このプログラムを実行したホストの名前を取得
            username = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            username = "Unknown";
        }
        // ダイアログからの取得が可能な場合は，取得した情報で
        // usernameを上書きする，取得できない場合はホスト名とする
        if(!dialog.getUser_val().isEmpty()) {
            username = dialog.getUser_val();
        }
        //--------------------------------------------
        
//******************************************************************

        //--------------------------------------------
        // サーバへの接続
        //--------------------------------------------
        try {
            // クライアントスレッドの生成と開始
            client = new MyClient(this, hostname, port);
            client.start();
            // 送信用パイプの取得
            netout = client.getWriter();
        } catch (Exception ex) {
            // 接続時に例外が発生(接続失敗)
            append("サーバとの通信が開始できませんでした.");
        }

        if (netout != null) {
            append("サーバに接続しました.");
            append("Server:" + hostname + " Port:" + port);
        } else {
            // 接続処理は終了したがインタフェースが生成されていない(接続失敗)
            append("サーバとの接続に失敗しました.");
        }
    }
    
    //------------------------------------------------------------------
    /**
     * 「切断」メニュー選択時の処理
     */
    void disconnectAction() {
        close();
    }
    
    //------------------------------------------------------------------
    /**
     * 「送信ボタン」押下時の処理
     */
    void sendAction() {
        // 未接続の場合にはエラー表示
        if (netout == null) {
            append("サーバに接続されていません.");
            return;
        }
        // メッセージ文の取得
        String str = input.getText();
        if (str.length() <= 0) {
            return;
        }
        // 取得したメッセージを送信する
        sendMessage(str);
        // 入力欄を初期化する
        input.setText("");
    }
    
    /**
     * キーが押された場合の処理
     */
    void onKeyPressedForInput(KeyEvent event) {
        if(event.getCode() == ENTER) sendAction();
    }
    
    //------------------------------------------------------------------
    /**
     * 選択中の言語種別を返す(翻訳機能と連動させる際に用いる)
     * @return 選択された言語
     */
    public int getLanguageType(){
        return language_type;
    }

    //------------------------------------------------------------------
    /**
     * 引数で指定された言語種別に設定(ChoiceBox選択時に呼ばれる)
     * @param type 
     */
    void setLanguageType(int type){
        language_type = type;
    }

    //------------------------------------------------------------------
    /**
     * メッセージ送信処理
     * @param str 送信されるメッセージ
     */
    public void sendMessage(String str) {
        // 末尾改行の追加
        if (!str.endsWith("\n")) {
            str += "\n";
        }

        // 送信メッセージの生成
        str = getLanguageType() + "\t" + username + "\t" + str; // 言語番号 ユーザ名 メッセージ

        // メッセージの送信
        if (netout != null) {  // サーバと接続済みかを確認
            netout.print(str); // サーバにメッセージを送信する
            netout.flush();
        }
    }
    
    /**
     * 切断メッセージ送信処理
     */
    public void sendClosedMessage() {
        // メッセージの送信
        if (netout != null) {  // サーバと接続済みかを確認
            netout.print(username+"との接続が切断されました."); // サーバにメッセージを送信する
            netout.flush();
        }
    }
    //------------------------------------------------------------------
    /**
     * 出力欄にメッセージを追記する
     * @param str メッセージ
     */
    public void append(String str) {
        // 末尾改行の追加
        if (!str.endsWith("\n")) {
            str += "\n";
        }
        // 出力欄の末尾に文字列を追記
        output.appendText(str);
    }

    //------------------------------------------------------------------
    /**
     * 切断処理
     */
    public void close() {
        if (netout != null) {
            // 切断メッセージの送信
            sendClosedMessage();
            // 出力インタフェースを閉じる
            netout = null;
            // クライアント接続を切断
            if (client != null) {
                client.close(); // 接続停止処理
                client = null;  // クライアントオブジェクトを削除
            }
        }
    }

    //------------------------------------------------------------------
    /**
     * 終了処理
     */
    public void exit() {
        // ネットワーク接続を停止
        close();
        // プログラム終了
        System.exit(0);
    }

    //------------------------------------------------------------------
    /**
     * JavaFXアプリケーションを起動するだけのメインメソッド
     * ※基本的にはこの内容で固定と考えてよい
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);  // JavaFXアプリケーションを起動する
    } 
}
