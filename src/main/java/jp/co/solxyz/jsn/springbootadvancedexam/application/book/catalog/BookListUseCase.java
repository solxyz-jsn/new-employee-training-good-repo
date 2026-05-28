package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 書籍一覧ユースケース
 */
@Service
@RequiredArgsConstructor
public class BookListUseCase {

    /**
     * 書籍在庫ビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /**
     * 全書籍取得
     * @return 全書籍
     */
    public List<BookSummary> getAllBooks() {
        return bookInventoryManager.getAllBooks().stream()
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
