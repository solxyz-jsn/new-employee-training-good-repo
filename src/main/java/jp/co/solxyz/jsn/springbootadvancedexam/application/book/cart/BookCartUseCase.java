package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.lending.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * カートユースケース
 */
@Service
@RequiredArgsConstructor
public class BookCartUseCase {

    /**
     * 書籍在庫ビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /**
     * 書籍貸出ビジネスロジック
     */
    private final BookLendingManager bookLendingManager;

    /**
     * カート情報取得
     * @param cartItems カート情報
     * @return カート書籍情報
     */
    public List<CartBook> getCartList(List<CartItem> cartItems) {
        List<String> isbnList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            isbnList.add(cartItem.getIsbn());
        }

        List<Book> books = bookInventoryManager.getBooksByIsbn(isbnList);

        List<CartBook> cartBooks = new ArrayList<>();
        for (Book book : books) {
            cartBooks.add(new CartBook(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher()));
        }
        return cartBooks;
    }

    /**
     * 書籍の貸出処理
     * @param userId ユーザID
     * @param cartItems カート情報
     * @return 借りれなかった書籍一覧
     */
    public List<BookSummary> checkout(String userId, List<CartItem> cartItems) {
        List<String> isbnList = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            isbnList.add(cartItem.getIsbn());
        }

        return bookLendingManager.checkout(userId, isbnList).stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 書籍エンティティを書籍概要に変換する
     * @param book 書籍エンティティ
     * @return 書籍概要
     */
    private BookSummary toSummary(Book book) {
        return new BookSummary(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                book.getDescription());
    }
}
