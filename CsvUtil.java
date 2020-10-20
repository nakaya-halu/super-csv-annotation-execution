//package ;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;

import jp.co.ajs.common.properties.Constants;
import jp.co.ajs.common.properties.CsvUtilResourceBundle;

/**
* csvに関するメソッド
* @author Nakaya
* @version 1.00
* @param <E> The element type of this list.
*/
public class CsvUtil<E> {
    /**
     * コンストラクタで受け取ったジェネリクスのクラス
     */
    private Class<E>type;
    /**
     *引用符　ダブルクオテーション
     */
    final char QUOTE[] = "\"".toCharArray();
    /**
     *区切り文字　コンマ
     */
    final int DELIMITER = 44;
    /**
     * 文字コード UTF-8
     */
    final String ENCODE = "UTF-8";
    /**
     * 改行コード LF
     */
    final String END_OF_LINE_SYMBOL = "\n";
    /**
     * CsvPreference<br>
     * CsvPreferenceをカスタマイズする(書き込みの際は必ずダブルクオテーションをつける設定)
     */
    final CsvPreference LOCAL_PREFERENCE = new CsvPreference.Builder(QUOTE[0], DELIMITER, END_OF_LINE_SYMBOL)
            .useQuoteMode(new AlwaysQuoteMode()).build();
    /**
     * コンストラクタ<br>
     *
     * @param e ジェネリクス
     */
    @SuppressWarnings("unchecked")
    public CsvUtil(E... e) {
        //E.classを取得
        Class<E> type = (Class<E>) e.getClass().getComponentType();
        this.type = type;
    }

    /**
     * ジェネリクスクラスを取得します
     * @return Class<E>
     */
    public Class<E>getType(){
        return type;
    }

    /**
     * csvファイルを読み込み、csv用のbeanに詰めてreturnする。
     * @param path
     * @return List<E>
     * @throws Exception
     */
    public List<E> readCsvAll(String path) throws Exception {
        return readCsvAll(path,LOCAL_PREFERENCE);
    }

    /**
     * csvファイルを読み込み、csv用のbeanに詰めてreturnする。
     * @param path
     * @param CsvPreference not {@code null}
     * @return List<E>
     * @throws Exception
     */
    public List<E> readCsvAll(String path, CsvPreference Preference) throws Exception {
        return readCsvAll(Files.newBufferedReader(new File(path).toPath(), Charset.forName(ENCODE)),Preference);
    }

    /**
     * csvファイルを読み込み、csv用のbeanに詰めてreturnする。
     * @param BufferedReader
     * @return List<E>
     * @throws Exception
     */
    public List<E> readCsvAll(BufferedReader br) throws Exception {
        return readCsvAll(br, LOCAL_PREFERENCE);
    }

    /**
     * csvファイルを読み込み、csv用のbeanに詰めてreturnする。
     * @param BufferedReader
     * @param CsvPreference not {@code null}
     * @return List<E>
     * @throws Exception
     */
    public List<E> readCsvAll(BufferedReader br, CsvPreference Preference) throws Exception {
        //対象bean , csvファイルオブジェクト , 文字コードを引数に取る
        CsvAnnotationBeanReader<E> csvReader = new CsvAnnotationBeanReader<>(this.type,
                br,Preference);
        //CsvAnnotationBeanReaderに展開したcsvデータをListに詰め替える
        List<E> csvList = csvReader.readAll();
        csvReader.close();
        if(csvList.isEmpty()) {
            throw new IOException();
        }
        return csvList;
    }

    /**
     * csvファイルを作成する
     * @param List<E>
     * @param String
     * @throws Exception
     */
    public void writeCsvAll(List<E> list,String path) throws Exception {
        writeCsvAll(list,path,LOCAL_PREFERENCE);
    }

    /**
     * csvファイルを作成する
     * @param List<E>
     * @param String
     * @param CsvPreference not {@code null}
     * @throws Exception
     */
    public void writeCsvAll(List<E> list,String path,CsvPreference Preference) throws Exception {
        CsvAnnotationBeanWriter<E> csvWriter = new CsvAnnotationBeanWriter<>(this.type,
                Files.newBufferedWriter(new File(path).toPath(),
                        Charset.forName(ENCODE)),
                Preference);
        csvWriter.writeAll(list);
        csvWriter.close();
    }
}
