package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カート内の貸出対象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /** ISBN */
    private String isbn;
}
