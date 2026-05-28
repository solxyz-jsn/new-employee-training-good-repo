package jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.lending.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.lending.UnreturnedBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookLendingUseCaseTest {

    @InjectMocks
    private BookLendingUseCase bookLendingUseCase;

    @Mock
    private BookLendingManager bookLendingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザに未返却書籍がない場合、空のリストが返される")
    void shouldReturnEmptyListWhenCurrentUserBooksIsEmpty() {
        String userId = "user1";
        String expectedUserId = "user1";

        List<UnreturnedBookSummary> expectedUnreturnedBookList = List.of();

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(List.of());

        List<UnreturnedBookSummary> result = bookLendingUseCase.getCurrentUserBooks(expectedUserId);

        assertThat(result).isEqualTo(expectedUnreturnedBookList);
    }

    @Test
    @DisplayName("ユーザに1件の未返却書籍がある場合、1件の未返却書籍含んだリストが返される")
    void shouldReturnCurrentUserBooksSuccessfully() {
        String userId = "user1";
        String expectedUserId = "user1";

        UnreturnedBook unreturnedBook = new UnreturnedBook();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");

        UnreturnedBookSummary expectedUnreturnedBook = new UnreturnedBookSummary();
        expectedUnreturnedBook.setIsbn("isbn1");
        expectedUnreturnedBook.setTitle("title1");
        expectedUnreturnedBook.setAuthor("author1");
        expectedUnreturnedBook.setPublisher("publisher1");

        List<UnreturnedBook> unreturnedBookList = List.of(unreturnedBook);
        List<UnreturnedBookSummary> expectedunreturnedBookList = List.of(expectedUnreturnedBook);

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(unreturnedBookList);

        List<UnreturnedBookSummary> result = bookLendingUseCase.getCurrentUserBooks(expectedUserId);

        assertThat(result).isEqualTo(expectedunreturnedBookList);
    }

    @Test
    @DisplayName("ユーザに複数の未返却書籍がある場合、複数の未返却書籍含んだリストが返される")
    void shouldReturnCurrentUserBooksSuccessfullyWhenMultipleBooks() {
        String userId = "user1";
        String expectedUserId = "user1";

        UnreturnedBook unreturnedBook1 = new UnreturnedBook();
        unreturnedBook1.setIsbn("isbn1");
        unreturnedBook1.setTitle("title1");
        unreturnedBook1.setAuthor("author1");
        unreturnedBook1.setPublisher("publisher1");

        UnreturnedBook unreturnedBook2 = new UnreturnedBook();
        unreturnedBook2.setIsbn("isbn2");
        unreturnedBook2.setTitle("title2");
        unreturnedBook2.setAuthor("author2");
        unreturnedBook2.setPublisher("publisher2");

        UnreturnedBookSummary expectedUnreturnedBook1 = new UnreturnedBookSummary();
        expectedUnreturnedBook1.setIsbn("isbn1");
        expectedUnreturnedBook1.setTitle("title1");
        expectedUnreturnedBook1.setAuthor("author1");
        expectedUnreturnedBook1.setPublisher("publisher1");

        UnreturnedBookSummary expectedUnreturnedBook2 = new UnreturnedBookSummary();
        expectedUnreturnedBook2.setIsbn("isbn2");
        expectedUnreturnedBook2.setTitle("title2");
        expectedUnreturnedBook2.setAuthor("author2");
        expectedUnreturnedBook2.setPublisher("publisher2");

        List<UnreturnedBook> unreturnedBookList = List.of(unreturnedBook1, unreturnedBook2);
        List<UnreturnedBookSummary> expectedunreturnedBookList = List.of(expectedUnreturnedBook1, expectedUnreturnedBook2);

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(unreturnedBookList);

        List<UnreturnedBookSummary> result = bookLendingUseCase.getCurrentUserBooks(expectedUserId);

        assertThat(result).isEqualTo(expectedunreturnedBookList);
    }

    @Test
    @DisplayName("書籍の返却が成功し、BookManagerのreturnBookが呼ばれる")
    void shouldReturnBookSuccessfully() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        try {
            bookLendingUseCase.returnBook(userId, isbn);
        } catch (Exception e) {
            fail();
        }
        verify(bookLendingManager, times(1)).returnBook(expectedUserId, expectedIsbn);
    }

    @Test
    @DisplayName("書籍の返却でDataAccessExceptionのサブクラスがスローされる")
    void shouldThrowDataAccessExceptionWhenReturnBook() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        doThrow(DataAccessResourceFailureException.class).when(bookLendingManager).returnBook(userId, isbn);

        assertThrows(DataAccessException.class, () -> bookLendingUseCase.returnBook(expectedUserId, expectedIsbn));
    }

    @Test
    @DisplayName("書籍の返却でNoSuchElementExceptionがスローされる")
    void shouldThrowNoSuchElementExceptionWhenReturnBook() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        doThrow(NoSuchElementException.class).when(bookLendingManager).returnBook(userId, isbn);

        assertThrows(NoSuchElementException.class, () -> bookLendingUseCase.returnBook(expectedUserId, expectedIsbn));
    }
}
