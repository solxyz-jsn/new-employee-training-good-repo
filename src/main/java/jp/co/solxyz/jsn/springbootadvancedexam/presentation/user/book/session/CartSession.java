package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session;

import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.Cart;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * カートのセッション情報
 */
@Getter
@Component
@SessionScope
public class CartSession implements Serializable {
    /**
     * シリアルバージョンUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * カート情報リスト
     */
    private final List<Cart> cartList = new ArrayList<>();

    /**
     * カートに書籍を追加する
     * @param cart 追加するカート情報
     */
    public void addCart(Cart cart) {
        boolean isExist = cartList.stream()
                .anyMatch(c -> c.getIsbn().equals(cart.getIsbn()));
        if (!isExist) {
            cartList.add(cart);
        }
    }

    /**
     * カートから指定した書籍を削除する
     * @param isbn 削除する書籍のISBN
     */
    public void removeCart(String isbn) {
        cartList.stream()
                .filter(cart -> cart.getIsbn().equals(isbn))
                .findFirst()
                .ifPresent(cartList::remove);
    }

    /**
     * カートを空にする
     */
    public void clearCart() {
        cartList.clear();
    }

}
