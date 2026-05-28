package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

/**
 * カート内の貸出対象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NullUnmarked
public class CartItem {

    /** ISBN */
    private String isbn;
}
