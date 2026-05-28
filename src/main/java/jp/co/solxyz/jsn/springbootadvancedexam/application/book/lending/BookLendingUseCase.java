package jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.model.UnreturnedBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 書籍貸出ユースケース
 */
@Service
@RequiredArgsConstructor
public class BookLendingUseCase {

    /**
     * 書籍貸出ビジネスロジック
     */
    private final BookLendingManager bookLendingManager;

    /**
     * ユーザの未返却書籍取得
     * @param userId ユーザID
     * @return 未返却書籍
     */
    public List<UnreturnedBookSummary> getCurrentUserBooks(String userId) {
        return bookLendingManager.getUnreturnedBooksByUserId(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 書籍の返却
     * @param userId ユーザID
     * @param isbn ISBN
     */
    public void returnBook(String userId, String isbn) {
        bookLendingManager.returnBook(userId, isbn);
    }

    /**
     * 未返却書籍を未返却書籍概要に変換する
     * @param book 未返却書籍
     * @return 未返却書籍概要
     */
    private UnreturnedBookSummary toSummary(UnreturnedBook book) {
        UnreturnedBookSummary summary = new UnreturnedBookSummary();
        summary.setIsbn(book.getIsbn());
        summary.setTitle(book.getTitle());
        summary.setAuthor(book.getAuthor());
        summary.setPublisher(book.getPublisher());
        summary.setRentalAt(book.getRentalAt());
        return summary;
    }
}
