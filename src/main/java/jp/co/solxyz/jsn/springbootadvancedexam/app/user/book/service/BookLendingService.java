package jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.model.UnreturnedBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 書籍返却サービス
 */
@Service
@RequiredArgsConstructor
public class BookLendingService {

    /**
     * 書籍貸出ビジネスロジック
     */
    private final BookLendingManager bookLendingManager;

    /**
     * ユーザの未返却書籍取得
     * @param userId ユーザID
     * @return 未返却書籍
     */
    public List<UnreturnedBookModel> getCurrentUserBooks(String userId) {
        return bookLendingManager.getUnreturnedBooksByUserId(userId).stream()
                .map(this::toModel)
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

    private UnreturnedBookModel toModel(UnreturnedBook unreturnedBook) {
        UnreturnedBookModel model = new UnreturnedBookModel();
        model.setIsbn(unreturnedBook.getIsbn());
        model.setTitle(unreturnedBook.getTitle());
        model.setAuthor(unreturnedBook.getAuthor());
        model.setPublisher(unreturnedBook.getPublisher());
        model.setRentalAt(unreturnedBook.getRentalAt());
        return model;
    }
}
