package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookLendingManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 書籍返却サービス
 */
@Service
public class BookLendingService {

    /**
     * 書籍貸出ビジネスロジック
     */
    private final BookLendingManager bookLendingManager;

    /**
     * コンストラクタ
     * @param bookLendingManager 書籍貸出ビジネスロジック
     */
    public BookLendingService(BookLendingManager bookLendingManager) {
        this.bookLendingManager = bookLendingManager;
    }

    /**
     * ユーザの未返却書籍取得
     * @param userId ユーザID
     * @return 未返却書籍
     */
    public List<UnreturnedBookModel> getCurrentUserBooks(String userId) {
        return bookLendingManager.getUnreturnedBooksByUserId(userId);
    }

    /**
     * 書籍の返却
     * @param userId ユーザID
     * @param isbn ISBN
     */
    public void returnBook(String userId, String isbn) {
        bookLendingManager.returnBook(userId, isbn);
    }
}
