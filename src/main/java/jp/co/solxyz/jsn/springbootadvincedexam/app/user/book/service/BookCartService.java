package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * カートサービス
 */
@Service
public class BookCartService {

    /**
     * 書籍サービスビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /**
     * 書籍貸出ビジネスロジック
     */
    private final BookLendingManager bookLendingManager;

    /**
     * コンストラクタ
     * @param bookInventoryManager 書籍サービスビジネスロジック
     * @param bookLendingManager 書籍貸出ビジネスロジック
     */
    public BookCartService(BookInventoryManager bookInventoryManager, BookLendingManager bookLendingManager) {
        this.bookInventoryManager = bookInventoryManager;
        this.bookLendingManager = bookLendingManager;
    }

    /**
     * カート情報取得
     * @param carts カート情報
     * @return カート書籍情報
     */
    public List<CartBookModel> getCartList(List<Cart> carts) {
        List<String> isbnList = new ArrayList<>();
        for (Cart cart : carts) {
            isbnList.add(cart.getIsbn());
        }

        List<Book> books = bookInventoryManager.getBooksByIsbn(isbnList);

        List<CartBookModel> cartBooks = new ArrayList<>();
        for (Book book : books) {
            cartBooks.add(new CartBookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher()));
        }
        return cartBooks;
    }

    /**
     * 書籍の貸出処理
     * @param userId ユーザID
     * @param carts カート情報
     * @return 借りれなかった書籍一覧
     */
    public List<Book> checkout(String userId, List<Cart> carts) {
        List<String> isbnList = new ArrayList<>();

        for (Cart cart : carts) {
            isbnList.add(cart.getIsbn());
        }

        return bookLendingManager.checkout(userId, isbnList);
    }
}
