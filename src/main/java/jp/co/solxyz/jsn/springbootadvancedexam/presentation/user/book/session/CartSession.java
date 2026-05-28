package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session;
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
        if (!containsIsbn(cart.getIsbn())) {
            cartList.add(cart);
        }
    }

    /**
     * カートから指定した書籍を削除する
     * @param isbn 削除する書籍のISBN
     */
    public void removeCart(String isbn) {
        cartList.removeIf(cart -> hasIsbn(cart, isbn));
    }

    /**
     * カートに指定したISBNの書籍が存在するか判定する
     * @param isbn ISBN
     * @return 指定したISBNの書籍が存在する場合true
     */
    private boolean containsIsbn(String isbn) {
        return cartList.stream()
                .anyMatch(cart -> hasIsbn(cart, isbn));
    }

    /**
     * カート情報が指定したISBNを持つか判定する
     * @param cart カート情報
     * @param isbn ISBN
     * @return ISBNが一致する場合true
     */
    private boolean hasIsbn(Cart cart, String isbn) {
        return isbn.equals(cart.getIsbn());
    }

    /**
     * カートを空にする
     */
    public void clearCart() {
        cartList.clear();
    }

}
