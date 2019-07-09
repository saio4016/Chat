package TransChat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *  接続用ダイアログが共通ダイアログに無いので，Stageを改良して自作
 * @author 
 */
public class ConnectDialog extends Stage
{
    // 入力情報を記憶しておく内部変数
    String host_val = null;
    String user_val = null;
    
    // 参照するために入力欄をフィールド変数として宣言
    TextField txtHost;
    TextField txtUser;
    
    //--------------------------------------------------------------------
    /**
     * コンストラクタ
     * @param wnd  親ウィンドウ
     */
    public ConnectDialog(Window wnd){
        setTitle("接続設定");
        initStyle(StageStyle.UTILITY); // ウィンドウのスタイルを選択
        initOwner(wnd);                // 親となるウィンドウを設定 
        initModality(Modality.APPLICATION_MODAL); // 生き死にをアプリに連動させる
        
        //=========================================================
        // 上部入力欄のコントロール生成
        //=========================================================
        // パスワード入力欄っぽい画面のコントロール群
        Label lblHost   = new Label("ホスト名　");
        Label lblUser = new Label("ユーザー名　");
        txtHost   = new TextField();
        txtUser = new TextField();
        
        // GridPaneを利用して縦2横2のグリッドにラベルとテキストエリアを配置
        GridPane upPane = new GridPane();
        upPane.add(lblHost, 0, 0);
        upPane.add(txtHost, 1, 0);
        upPane.add(lblUser, 0, 1);
        upPane.add(txtUser, 1, 1);
        
        //=========================================================
        // 下部のボタンのコントロール生成
        //=========================================================
        Button btnConnect = new Button("接続");
        btnConnect.setPrefWidth(80);
        // [接続]押下時は入力内容をセット後にダイアログを閉じる
        btnConnect.setOnAction(event->{this.setInfo();this.close();});
        
        Button btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(80);
        // [Cencel]押下時は何もせずにダイアログを閉じる
        btnCancel.setOnAction(event->this.close());  
        
        // 画面下部にボタンを2個配置
        HBox btnPane = new HBox();
        btnPane.getChildren().addAll(btnConnect,btnCancel);
        
        //=========================================================
        // 最後にupPaneとdownPaneを縦に並べる
        //=========================================================
        VBox mainPane = new VBox();
        mainPane.setSpacing(10.0);
        mainPane.setAlignment(Pos.CENTER);               // 配置位置を中央寄せに設定
        mainPane.setPadding(new Insets(10, 10, 10, 10)); // ウィンドウ外枠からの上下左右の余白の設定
        mainPane.getChildren().addAll(upPane,btnPane);

        // 最後に画面を生成
        setScene(new Scene(mainPane));
    }

    //--------------------------------------------------------------------
    /**
     * テキスト入力欄の内容をフィールド変数に確保しておく
     */
    private void setInfo() {
        host_val = txtHost.getText();
        user_val = txtUser.getText();
    }

    //--------------------------------------------------------------------
    /**
     * ホストについてOKボタンで確定した入力内容を取得する
     * @return 入力されたホスト
     */
    public String getHost_val() {
        return host_val;
    }

    //--------------------------------------------------------------------
    /**
     * サーバーについてOKボタンで確定した入力内容を取得する
     * @return 入力されたサーバー
     */
    public String getUser_val() {
       return user_val;
    }
}
