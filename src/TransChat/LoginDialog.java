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
 * メッセージ表示用ダイアログが共通ダイアログに無いので，Stageを改良して自作
 * @author Kazuhiko Sato
 */
public class LoginDialog extends Stage{
    // 入力情報を記憶しておく内部変数
    String id_val = null;
    String pass_val = null;
    
    // 参照するために入力欄をフィールド変数として宣言
    TextField txtId;
    TextField txtPass;
    //PasswordField txtPass;
    //課題１：passについて，26行目のTextFieldの宣言をコメント化し，
    //       27行目のPasswordFieldのほうを有効化しなさい。（52行目につづく）

    
    //--------------------------------------------------------------------
    /**
     * コンストラクタ
     * @param wnd  親ウィンドウ
     */
    public LoginDialog(Window wnd){
        setTitle("ログイン画面風ダイアログ");
        initStyle(StageStyle.UTILITY); // ウィンドウのスタイルを選択
        initOwner(wnd);                // 親となるウィンドウを設定 
        initModality(Modality.APPLICATION_MODAL); // 生き死にをアプリに連動させる
        
        //=========================================================
        // 上部入力欄のコントロール生成
        //=========================================================
        // パスワード入力欄っぽい画面のコントロール群
        Label lblId = new Label("Ｉ　Ｄ　：");
        Label lblPass = new Label("パ ス　：");
        txtId = new TextField();
        txtPass = new TextField();
        //txtPass = new PasswordField();
        //課題１(28行目の続き)さらに、上の50行目をコメント化し、51行目を有効にして、
        //      変更後，実行して動作の違いを確認しなさい。
        
        // GridPaneを利用して
        // 縦2横2のグリッドにラベルとテキストエリアを配置
        GridPane upPane = new GridPane();
        upPane.add(lblId, 0, 0);
        upPane.add(txtId, 1, 0);
        upPane.add(lblPass, 0, 1);
        upPane.add(txtPass, 1, 1);
        
        //=========================================================
        // 下部のボタンのコントロール生成
        //=========================================================
        Button btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(80);
        // Cencel押下時は何もせずにダイアログを閉じる
        btnCancel.setOnAction(event->this.close());  

        Button btnOK = new Button("OK");
        btnOK.setPrefWidth(80);
        // OK押下時は入力内容をセット後にダイアログを閉じる
        btnOK.setOnAction(event->{this.setInfo();this.close();});
        
        // 画面下部にボタンを2個配置
        HBox btnPane = new HBox();
        btnPane.getChildren().addAll(btnCancel,btnOK);
        
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
        id_val = txtId.getText();
        pass_val = txtPass.getText();
    }

    //--------------------------------------------------------------------
    /**
     * IDについてOKボタンで確定した入力内容を取得する
     * @return 入力されたＩＤ
     */
    public String getId_val() {
        return id_val;
    }

    //--------------------------------------------------------------------
    /**
     * PassについてOKボタンで確定した入力内容を取得する
     * @return 入力されたパス 
     */
    public String getPass_val() {
       return pass_val;
    }
}
