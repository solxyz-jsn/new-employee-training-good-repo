package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 利用者向け書籍情報
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSummary {

    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 貸出可能在庫数 */
    private int availableStock;
    /** 説明 */
    private String description;
}
