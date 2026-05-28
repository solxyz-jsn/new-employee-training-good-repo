package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.model.BookManagementModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookManagementControllerTest {

    @InjectMocks
    private BookManagementController controller;

    @Mock
    private BookManagementUseCase service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が存在しない場合、空のリストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBookIsAvailable() {
        when(service.getAllBooks()).thenReturn(List.of());

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/book-management");
        assertThat(actual.getModel().get("activeMenu")).isEqualTo("bookManagement");
        List<BookManagementModel> result = getBooks(actual);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBookAreAvailable() {
        BookManagementModel expected = new BookManagementModel("1234567890123", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0), LocalDateTime.of(2021, 1, 1, 0, 0, 0));

        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        BookManagementSummary book = new BookManagementSummary("1234567890123", "Test Book", "Test Author", "Test Publisher",
                "Test Description", 10, testTime, testTime);

        when(service.getAllBooks()).thenReturn(List.of(book));

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/book-management");
        assertThat(actual.getModel().get("activeMenu")).isEqualTo("bookManagement");
        List<BookManagementModel> result = getBooks(actual);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenMultipleBooksAreAvailable() {
        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime expectedTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

        BookManagementModel expected1 = new BookManagementModel("1234567890123", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                expectedTime, expectedTime);
        BookManagementModel expected2 = new BookManagementModel("0987654321098", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                expectedTime, expectedTime);

        BookManagementSummary book1 = new BookManagementSummary("1234567890123", "Test Book", "Test Author", "Test Publisher",
                "Test Description", 10, testTime, testTime);
        BookManagementSummary book2 = new BookManagementSummary("0987654321098", "Test Book", "Test Author", "Test Publisher",
                "Test Description", 10, testTime, testTime);

        when(service.getAllBooks()).thenReturn(List.of(book1, book2));

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/book-management");
        assertThat(actual.getModel().get("activeMenu")).isEqualTo("bookManagement");
        List<BookManagementModel> result = getBooks(actual);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(expected1);
        assertThat(result.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("ISBNが13桁の数字ではない場合、IllegalArgumentExceptionが発生する")
    void shouldThrowIllegalArgumentExceptionWhenIsbnIsInvalid() {
        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        BookManagementSummary book = new BookManagementSummary("test_isbn", "Test Book", "Test Author", "Test Publisher",
                "Test Description", 10, testTime, testTime);

        when(service.getAllBooks()).thenReturn(List.of(book));

        assertThatThrownBy(() -> controller.index())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("引数には13桁の数字のみが使用できます");

        verify(service).getAllBooks();
    }

    private List<BookManagementModel> getBooks(ModelAndView modelAndView) {
        Object books = modelAndView.getModel().get("books");
        assertThat(books).isInstanceOf(List.class);
        return ((List<?>) books).stream()
                .map(BookManagementModel.class::cast)
                .toList();
    }
}
