package jp.co.solxyz.jsn.springbootadvancedexam.application.book.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 書籍管理ユースケース出力
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookManagementSummary {

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
    /** 作成日時 */
    private LocalDateTime createdAt;
    /** 更新日時 */
    private LocalDateTime updatedAt;
}
