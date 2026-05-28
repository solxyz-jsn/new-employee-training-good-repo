package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

/**
 * カート内書籍
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NullUnmarked
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
