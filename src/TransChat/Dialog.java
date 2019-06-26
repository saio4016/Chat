package TransChat;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import static javafx.application.Application.launch;

/**
 * ダイアログ
 * @author 
 */
public class Dialog extends Application {
    // ラベル表示用のフィールド変数
    String host;
    String server;
    Label lblHost;
    Label lblServer;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("仮");
        stage.setWidth(240);
        stage.setHeight(200);
       
        Button btnShowLoginDialog = new Button("ダイアログ１を開く");
        Button btnShowMsgDialog = new Button("ダイアログ２を開く");
        
        // ボタンへのイベント割り当て
        btnShowLoginDialog.setOnAction(event->showLoginDialog(stage));
        btnShowMsgDialog.setOnAction(event->showMsgDialog(stage));
        
        // 入力結果出力用ラベル
        lblHost = new Label();
        lblServer = new Label();
        // ラベル初期化
        reset();
        
        // 最後にupPaneとdownPaneを縦に並べる
        VBox mainPane = new VBox();
        mainPane.setSpacing(10.0);
        mainPane.setAlignment(Pos.CENTER);               // 配置位置を中央寄せに設定
        mainPane.setPadding(new Insets(10, 10, 10, 10)); // ウィンドウ外枠からの上下左右の余白の設定
        mainPane.getChildren().addAll(btnShowLoginDialog,btnShowMsgDialog,lblId,lblPass);

        // 画面生成
        stage.setScene(new Scene(mainPane));
        stage.show();                     // ステージ(ウィンドウ)を表示開始
    }

    /**
     * ラベルのリセット
     */
    void reset(){
        host="";
        server="";
        setLabel();
    }
    
    /**
     * ラベルに情報をセット
     */
    void setLabel(){
        //埋め込む形の条件分岐代入 「条件式?真の値:偽の値」
        lblHost.setText("HOST="+(host.equals("")?"未入力":id));
        lblServer.setText("SERVER="+(server.equals("")?"未入力":pass));
    }
    
    /**
     * ログインダイアログの表示
     * @param stage
     */
    void showLoginDialog(Stage stage){
        // 親ウィンドウと表示するメッセージを引数として渡し，ダイアログを生成
        LoginDialog login = new LoginDialog(stage);

        // ダイアログの表示 ※閉じられるまで処理を止める
        login.showAndWait();
        //課題２：↑をコメント化し，↓を有効にした時の処理の違いを体験しなさい。
        //login.show();
        
        // IDをダイアログから取得
        host = login.getHost_val();
        // キャンセルが押された場合、nullが返るのでその場合は空文字にする。
        if(host == null){
            host = "";
        }
        // PASSをダイアログから取得
        server = login.getSerVer_val();
        // キャンセルが押された場合、nullが返るのでその場合は空文字にする。
        if(server == null){
            server = "";
        }
        //課題３： 91行目と97行目の空文字を代入している箇所を 
        //        id = "キャンセル"; のようにに変更して、実行し、
        //        (1)ダイアログでキャンセルボタンが押された場合と、
        //        (2)ダイアログで空欄の状態でOKが押された場合の
        //        挙動の違いを比べて見なさい。
        
        // 取得したIDとパスをラベルに表示する
        setLabel();
    }

    /**
     * メッセージダイアログの表示
     * @param stage
     */
    void showMsgDialog(Stage stage){
        Window wnd = stage;
        // 親ウィンドウと表示するメッセージを引数として渡し，ダイアログを生成
        MsgBox msgBox = new MsgBox(wnd, "HOST："+host+"\nSERVER："+server);
        // ダイアログの表示
        msgBox.show();
    }

    /**
     * JavaFXアプリケーションを起動するだけのメインメソッド
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);  // JavaFXアプリケーションを起動する
    }
}
