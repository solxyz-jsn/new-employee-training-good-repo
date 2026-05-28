package jp.co.solxyz.jsn.springbootadvancedexam.application.book.management;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookMetadataManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 書籍管理ユースケース
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookManagementUseCase {

    /**
     * 書籍在庫ビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /** 書籍メタデータマネージャー */
    private final BookMetadataManager bookMetadataManager;
    /** 現在日時取得用Clock */
    private final Clock clock;

    /**
     * 全書籍情報の取得
     * @return 全書籍情報
     */
    public List<BookManagementSummary> getAllBooks() {
        return bookInventoryManager.getAllBooks().stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 書籍情報の保存
     * @param addBook 書籍情報
     */
    public void addBook(BookManagementCommand addBook) {
        Book book = new Book();
        book.setIsbn(addBook.getIsbn());
        book.setTitle(addBook.getTitle());
        book.setAuthor(addBook.getAuthor());
        book.setPublisher(addBook.getPublisher());
        book.setDescription(addBook.getDescription());
        book.setStock(addBook.getStock());
        book.setAvailableStock(addBook.getStock());
        LocalDateTime now = LocalDateTime.now(clock);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        bookMetadataManager.registerBook(book);
    }

    /**
     * 書籍情報の更新
     * @param updatedBook 更新する書籍情報
     */
    public void updateBook(BookManagementCommand updatedBook) {
        Book currentBook = bookInventoryManager.getBookByIsbn(updatedBook.getIsbn());
        // 入力された在庫数が現在の在庫数より少ない場合はエラー
        bookInventoryManager.compareInputStockLowerThanCurrent(updatedBook.getStock(), currentBook.getAvailableStock());

        Book book = new Book();
        book.setIsbn(updatedBook.getIsbn());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPublisher(updatedBook.getPublisher());
        book.setDescription(updatedBook.getDescription());
        book.setStock(updatedBook.getStock());
        book.setAvailableStock(currentBook.getAvailableStock() + (updatedBook.getStock() - currentBook.getStock()));
        book.setUpdatedAt(LocalDateTime.now(clock));

        bookMetadataManager.updateBook(book, currentBook.getUpdatedAt());
    }

    /**
     * 書籍情報の削除
     * @param isbn ISBN
     */
    public void deleteBook(String isbn) {
        bookMetadataManager.deleteByIsbn(isbn);
    }

    /**
     * 書籍エンティティを書籍管理概要に変換する
     * @param book 書籍エンティティ
     * @return 書籍管理概要
     */
    private BookManagementSummary toSummary(Book book) {
        return new BookManagementSummary(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDescription(),
                book.getStock(), book.getCreatedAt(), book.getUpdatedAt());
    }
}
