package TransChat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * メッセージ表示用ダイアログが共通ダイアログに無いので，Stageを改良して自作
 * @author Kazuhiko Sato
 */
public class MsgBox extends Stage{
    // コンストラクタ
    public MsgBox(Window wnd, String msg){
        setTitle("MsgBox");
        initStyle(StageStyle.UTILITY); // ウィンドウのスタイルを選択
        initOwner(wnd);                // 親となるウィンドウを設定 
        initModality(Modality.APPLICATION_MODAL); // 生き死にをアプリに連動させる
        
        Label lbl = new Label();
        lbl.setPrefWidth(160);
        lbl.setText(msg);
        
        Button btnOK = new Button("OK");
        btnOK.setPrefWidth(80);
        btnOK.setOnAction(event->this.close());  // OK押下でダイアログを閉じる
        
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10,10,10,10));
        root.setSpacing(20.0);
        root.getChildren().addAll(lbl,btnOK);
        
        setScene(new Scene(root));
    }
}
