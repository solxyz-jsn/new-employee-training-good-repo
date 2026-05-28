package jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未返却書籍のユースケース出力
 */
@Data
public class UnreturnedBookSummary {

    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 貸出日時 */
    private LocalDateTime rentalAt;
}
