package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 書籍一覧サービス
 */
@Service
public class BookListService {

    /**
     * 書籍サービスビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /**
     * コンストラクタ
     * @param bookInventoryManager 書籍サービスビジネスロジック
     */
    public BookListService(BookInventoryManager bookInventoryManager) {
        this.bookInventoryManager = bookInventoryManager;
    }

    /**
     * 全書籍取得
     * @return 全書籍
     */
    public List<Book> getAllBooks() {
        return bookInventoryManager.getAllBooks();
    }
}
