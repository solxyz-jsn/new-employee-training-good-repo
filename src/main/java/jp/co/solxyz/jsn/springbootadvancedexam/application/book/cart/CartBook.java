package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カート内書籍
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartBook {

    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
}
