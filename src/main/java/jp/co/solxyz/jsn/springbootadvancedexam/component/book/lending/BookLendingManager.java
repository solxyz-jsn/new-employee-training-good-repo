package jp.co.solxyz.jsn.springbootadvancedexam.component.book.lending;

import jp.co.solxyz.jsn.springbootadvancedexam.common.util.UUIDGenerator;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.repository.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 書籍の貸出に関するビジネスロジッククラス
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookLendingManager {

    /**
     * 書籍リポジトリ
     */
    private final BookRepository bookRepository;

    /**
     * 書籍貸出履歴リポジトリ
     */
    private final BookCheckoutHistoryRepository bookCheckoutHistoryRepository;
    /** UUID生成クラス */
    private final UUIDGenerator uuidGenerator;
    /** 現在日時取得用Clock */
    private final Clock clock;

    /**
     * チェックアウト
     * @param userId ユーザID
     * @param isbnList ISBNリスト
     * @return 借りられなかった書籍リスト
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Book> checkout(String userId, List<String> isbnList) throws DataAccessException {
        // 借りられなかった本
        List<Book> notCheckoutBooks = new ArrayList<>();

        List<Book> books = bookRepository.findAllById(isbnList);
        Set<String> foundIsbnSet = books.stream().map(Book::getIsbn).collect(Collectors.toSet());
        List<String> missingIsbnList = isbnList.stream()
                .filter(isbn -> !foundIsbnSet.contains(isbn))
                .toList();
        if (!missingIsbnList.isEmpty()) {
            log.info("ISBNの一致する書籍が見つかりません。ISBN: {}", missingIsbnList);
            throw new NoSuchElementException("ISBNの一致する書籍が存在しません。");
        }
        List<BookCheckoutHistory> userBooks = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);
        Set<String> userBookIsbns = userBooks.stream()
                .map(BookCheckoutHistory::getIsbn)
                .collect(Collectors.toSet());

        // 借りる本
        List<Book> checkoutBooks = new ArrayList<>();
        // すでに借りている本がある場合は、借りられない
        for (Book book : books) {
            if (userBookIsbns.contains(book.getIsbn())) {
                log.info("すでに借りている本のため、チェックアウトできません。ISBN: {}", book.getIsbn());
                notCheckoutBooks.add(book);
            } else {
                checkoutBooks.add(book);
            }
        }

        List<BookCheckoutHistory> bookCheckoutHistories = new ArrayList<>();
        for (Book book : checkoutBooks) {
            if (book.getAvailableStock() == 0) {
                log.info("在庫がないため、チェックアウトできません。ISBN: {}", book.getIsbn());
                notCheckoutBooks.add(book);
                continue;
            }
            // 在庫を減らす
            book.setAvailableStock(book.getAvailableStock() - 1);

            // チェックアウト履歴を作成
            BookCheckoutHistory bookCheckoutHistory = new BookCheckoutHistory();
            bookCheckoutHistory.setRentalId(uuidGenerator.generateRentalId());
            bookCheckoutHistory.setUserId(userId);
            bookCheckoutHistory.setIsbn(book.getIsbn());
            bookCheckoutHistory.setRentalAt(LocalDateTime.now(clock));
            bookCheckoutHistories.add(bookCheckoutHistory);

        }

        bookRepository.saveAll(books);
        try {
            bookCheckoutHistoryRepository.saveAll(bookCheckoutHistories);
        } catch (DataAccessException e) {
            log.error("チェックアウト履歴の登録に失敗しました。", e);
            throw e;
        }
        return notCheckoutBooks;
    }

    /**
     * 貸出中の本を取得
     * @param userId ユーザID
     * @return 貸出中の本
     */
    public List<UnreturnedBook> getUnreturnedBooksByUserId(String userId) {
        List<BookCheckoutHistory> histories = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);
        Set<String> isbnList = histories.stream().map(BookCheckoutHistory::getIsbn).collect(Collectors.toSet());
        Map<String, LocalDateTime> rentalAtByIsbn = toRentalAtByIsbn(histories);
        List<Book> result = bookRepository.findAllById(isbnList);
        List<UnreturnedBook> unreturnedBooks = new ArrayList<>();
        for (Book book : result) {
            unreturnedBooks.add(toUnreturnedBook(book, rentalAtByIsbn));
        }

        return unreturnedBooks;
    }

    /**
     * ISBNごとの貸出日時を作成する
     * @param histories 貸出履歴
     * @return ISBNごとの貸出日時
     */
    private Map<String, LocalDateTime> toRentalAtByIsbn(List<BookCheckoutHistory> histories) {
        Map<String, LocalDateTime> rentalAtByIsbn = new HashMap<>();
        for (BookCheckoutHistory history : histories) {
            rentalAtByIsbn.putIfAbsent(history.getIsbn(), history.getRentalAt());
        }
        return rentalAtByIsbn;
    }

    /**
     * 書籍と貸出日時から未返却書籍情報を作成する
     * @param book 書籍
     * @param rentalAtByIsbn ISBNごとの貸出日時
     * @return 未返却書籍情報
     */
    private UnreturnedBook toUnreturnedBook(Book book, Map<String, LocalDateTime> rentalAtByIsbn) {
        UnreturnedBook unreturnedBook = new UnreturnedBook();
        unreturnedBook.setIsbn(book.getIsbn());
        unreturnedBook.setTitle(book.getTitle());
        unreturnedBook.setAuthor(book.getAuthor());
        unreturnedBook.setPublisher(book.getPublisher());
        unreturnedBook.setRentalAt(rentalAtByIsbn.get(book.getIsbn()));
        return unreturnedBook;
    }

    /**
     * 貸し出し中の本を返却
     * @param userId ユーザID
     * @param isbn ISBN
     * @throws NoSuchElementException DBに返却対象が存在しない場合
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(String userId, String isbn) throws NoSuchElementException, DataAccessException {
        try {
            Book book = bookRepository.findById(isbn).orElseThrow(() -> {
                log.info("ISBNの一致する書籍が見つかりません。ISBN: {}", isbn);
                return new NoSuchElementException("ISBNの一致する書籍が存在しません。");
            });
            int updatedHistoryCount = bookCheckoutHistoryRepository.updateReturnAt(userId, isbn, LocalDateTime.now(clock));
            if (updatedHistoryCount != 1) {
                log.info("返却対象の貸し出し履歴が見つかりません。USER_ID: {}, ISBN: {}", userId, isbn);
                throw new NoSuchElementException("返却対象の貸し出し履歴が存在しません。");
            }

            book.setAvailableStock(book.getAvailableStock() + 1);
            bookRepository.save(book);
        } catch (DataAccessException e) {
            log.error("貸し出し履歴の更新に失敗しました。", e);
            throw e;
        }
    }
}
