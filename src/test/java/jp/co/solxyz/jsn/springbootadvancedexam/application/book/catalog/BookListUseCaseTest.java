package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BookListUseCaseTest {

    @InjectMocks
    private BookListUseCase bookListUseCase;

    @Mock
    private BookInventoryManager bookInventoryManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が存在しない場合、空のリストが返される")
    void shouldReturnEmptyBookListWhenNoBookIsAvailable() {
        when(bookInventoryManager.getAllBooks()).thenReturn(List.of());

        List<BookSummary> actual = bookListUseCase.getAllBooks();

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストが返される")
    void shouldReturnBookListWhenBookIsAvailable() {
        BookSummary expected = new BookSummary("1234567890123", "Test Book", "Test Author", "Test Publisher", 10,
                "Test Description");

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setAvailableStock(10);

        when(bookInventoryManager.getAllBooks()).thenReturn(List.of(book));

        List<BookSummary> actual = bookListUseCase.getAllBooks();

        assertThat(actual).hasSize(1);
        assertThat(actual).isEqualTo(List.of(expected));

    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数件の書籍を含んだリストが返される")
    void shouldReturnBookListWhenMultipleBooksAreAvailable() {
        BookSummary expected1 = new BookSummary("1234567890123", "Test Book1", "Test Author1", "Test Publisher1", 10,
                "Test Description1");
        BookSummary expected2 = new BookSummary("1234567890124", "Test Book2", "Test Author2", "Test Publisher2", 20,
                "Test Description2");

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book1");
        book1.setAuthor("Test Author1");
        book1.setPublisher("Test Publisher1");
        book1.setDescription("Test Description1");
        book1.setAvailableStock(10);
        Book book2 = new Book();
        book2.setIsbn("1234567890124");
        book2.setTitle("Test Book2");
        book2.setAuthor("Test Author2");
        book2.setPublisher("Test Publisher2");
        book2.setDescription("Test Description2");
        book2.setAvailableStock(20);

        when(bookInventoryManager.getAllBooks()).thenReturn(List.of(book1, book2));

        List<BookSummary> actual = bookListUseCase.getAllBooks();

        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(List.of(expected1, expected2));
    }
}
