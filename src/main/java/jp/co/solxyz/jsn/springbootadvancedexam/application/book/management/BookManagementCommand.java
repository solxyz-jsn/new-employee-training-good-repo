package jp.co.solxyz.jsn.springbootadvancedexam.application.book.management;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 書籍管理ユースケース入力
 */
@Data
@NoArgsConstructor
public class BookManagementCommand {

    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 説明 */
    private String description;
    /** 在庫数 */
    private int stock;
}
