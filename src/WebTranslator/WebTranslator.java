package WebTranslator;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 翻訳サイト(Excite)のサービスを使って文字列を翻訳する
 * 役割：チャットの入力を指定した言語に自動的に翻訳する
 * @author 学籍番号　氏名　// 自分の氏名・番号を記入して下さい
 */
public class WebTranslator {
    //================================================================
    // Excite翻訳と通信するための各種情報
    //================================================================
    int before = 0; // 翻訳前の文字列(初期値 0..日本語)
    int after  = 1;  // 翻訳後の文字列(初期値 1..英語)

    // エキサイト翻訳のサーバホスト情報
    String sysHost = "www.excite.co.jp";
    String sysPort = "80";

    // エキサイト翻訳のアクセスパス
    String urlje = "/english/";   // 日英翻訳のパス(日、英)
    String urljc = "/chinese/"; // 日中(日、簡、繁)
    String urljk = "/korean/";  // 日韓(日、韓)
    String urljf = "/french/";  // 日仏(日、英、仏)
    String urljg = "/german/";  // 日独(日、英、独)

    //wb_lp　※Excite翻訳で使用
    final String type[] = {"JA", "EN", "CH", "CH", "KO", "FR", "DE"};  // JA..日　EN..英
    
    //=========================================================================
    /**
     * メインメソッド 
     * このクラス単体での動作確認用。完成版では使用しない
     * @param args 
     */
    public static void main(String args[]) {
        // 翻訳機能を利用するためにWebTranslatorの実体を生成
        WebTranslator gwc = new WebTranslator();
        
        // 翻訳を実行（例：こんにちはを英語に翻訳）した結果をresultに代入
        String str = gwc.translation("こんにちは", 0, 6);
        
        // こんにちはを英訳した結果(Webコンテンツ(HTML))を表示
        System.out.println(str);
    }

    //=========================================================================
    /**
     * コンストラクタ
     */
    public WebTranslator(){
        // コンストラクタの処理をここに書く
    }
    
    //=========================================================================
    /**
     * 翻訳処理本体メソッド
     * 文章(word)をbeforeからafterに翻訳する
     * @param word   翻訳対象の文章
     * @param before 翻訳前の言語種別
     * @param after  翻訳後の言語種別
     * @return 
     */
    public String translation(String word,int before,int after){
        // 翻訳結果のWebコンテンツ全体が入る文字列変数
        String result = null;
        
        // アクセス先のパス情報
        String url; 
        
        // 翻訳サイトへのURLを設定
        if (before == after){
            // 指定された言語がいずれも同じ場合は翻訳しない
            return word;        
        }else if (before != 0 && after != 0) {
            // いずれかに日本語が含まれていない場合は対応できない
            System.err.println("Translation is failed: langage setting error : 001");
            return word;
        } else if (before == 1 || after == 1) { // 日英or英日の場合
            url = "https://" + sysHost + "/world" + urlje;    // 日英変換
        } else if (before == 2 || after == 2 || before == 3 || after == 3) { 
            // 日中or中日の場合 ※チャレンジ用
            url = "https://" + sysHost + "/world" + urljc;    // 日中変換
        } else if (before == 4 || after == 4) { // 日韓or韓日
            url = "https://" + sysHost + "/world" + urljk;    // 日韓
        } else if (before == 5 || after == 5) { // 日仏or仏日
            url = "https://" + sysHost + "/world" + urljf;    // 日仏変換
        } else if (before == 6 || after == 6) { // 日独or独日
            url = "https://" + sysHost + "/world" + urljg;    // 日独変換
        }
        else {
            // それ以外のケースはありえないはずだが念のため
            System.err.println("Translation is failed: langage setting error : 002");
            // チャットに組み込んで「翻訳しないで普通にチャットする」を許可する場合は上をコメント化
            return word;
        }

        try {
            // オプション情報をハッシュに挿入
            Properties props = new Properties();
            props.put("before", word);
            props.put("wb_lp", type[before] + type[after]);
            if(before == 2 || after == 2) props.put("big5", "no");  // チャレンジ用(中国語の簡の選択)
            if(before == 3 || after == 3) props.put("big5", "yes"); // チャレンジ用(中国語の繁の選択)

            // URLクラスにアクセス先情報を設定
            URL target = new URL(url);
            // 通信処理部に情報を渡し、コンテンツを取得する
            result = GetWebContents.doPost(target, props);
        } catch (IOException e) {
            // HTTP通信のどこかでIO処理例外が発生した場合のエラー出力
            System.err.println("Translation is failed: IO error : 003");
        }

        //===========================================================
        // 翻訳機能を利用するためにWebTranslatorの実体を生成
        //WebTranslator gwc = new WebTranslator();
        
        // 翻訳を実行
        String reg = "<textarea id=\"after\" .*>.*</textarea>";
        Pattern p = Pattern.compile (reg);
        Matcher m = p.matcher(result);
        while(m.find()) {
            String g = m.group();
            String[] split1 = g.split("<textarea id=\"after\" .*?>");
            String[] split2 = split1[1].split("</textarea>");
            result = split2[0];
        }
        
        // こんにちはを英訳した結果(Webコンテンツ(HTML))を表示
        //===========================================================
        
        // 最後にresultを返す
        return result;
    }
}
