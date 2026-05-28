package jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookCartUseCaseTest {

    @InjectMocks
    private BookCartUseCase bookCartUseCase;

    @Mock
    private BookInventoryManager bookInventoryManager;

    @Mock
    private BookLendingManager bookLendingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("カートに複数の書籍がある場合、複数の書籍を含んだリストが返される")
    void shouldReturnCartBooksWhenCartHasBooks() {
        CartBook expected1 = new CartBook();
        expected1.setIsbn("1234567890123");
        expected1.setTitle("Test Book1");
        expected1.setAuthor("Test Author1");
        expected1.setPublisher("Test Publisher1");

        CartBook expected2 = new CartBook();
        expected2.setIsbn("0987654321123");
        expected2.setTitle("Test Book2");
        expected2.setAuthor("Test Author2");
        expected2.setPublisher("Test Publisher2");

        CartItem cart1 = new CartItem();
        cart1.setIsbn("1234567890123");
        CartItem cart2 = new CartItem();
        cart2.setIsbn("0987654321123");

        List<CartItem> carts = List.of(cart1, cart2);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book1");
        book1.setAuthor("Test Author1");
        book1.setPublisher("Test Publisher1");

        Book book2 = new Book();
        book2.setIsbn("0987654321123");
        book2.setTitle("Test Book2");
        book2.setAuthor("Test Author2");
        book2.setPublisher("Test Publisher2");

        List<String> isbnList = List.of(book1.getIsbn(), book2.getIsbn());

        when(bookInventoryManager.getBooksByIsbn(isbnList)).thenReturn(List.of(book1, book2));

        List<CartBook> cartBooks = bookCartUseCase.getCartList(carts);

        verify(bookInventoryManager, times(1)).getBooksByIsbn(isbnList);
        assertThat(cartBooks.size()).isEqualTo(2);
        assertThat(cartBooks.get(0)).isEqualTo(expected1);
        assertThat(cartBooks.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("カートに1件書籍がある場合、1件の書籍を含んだリストが返される")
    void shouldReturnCartBookWhenCartHasBook() {
        CartBook expected = new CartBook();
        expected.setIsbn("1234567890");
        expected.setTitle("Test Book");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");

        CartItem cart = new CartItem();
        cart.setIsbn("1234567890");
        List<CartItem> carts = List.of(cart);

        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");

        List<String> isbnList = List.of(book.getIsbn());

        when(bookInventoryManager.getBooksByIsbn(isbnList)).thenReturn(List.of(book));

        List<CartBook> cartBooks = bookCartUseCase.getCartList(carts);

        verify(bookInventoryManager, times(1)).getBooksByIsbn(isbnList);
        assertThat(cartBooks.size()).isEqualTo(1);
        assertThat(cartBooks.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("カートが空の場合、空のリストが返される")
    void shouldReturnEmptyListWhenCartIsEmpty() {
        List<CartItem> carts = List.of();
        when(bookInventoryManager.getBooksByIsbn(List.of())).thenReturn(List.of());

        List<CartBook> cartBooks = bookCartUseCase.getCartList(carts);

        verify(bookInventoryManager, times(1)).getBooksByIsbn(List.of());
        assertThat(cartBooks).isEmpty();
    }

    @Test
    @DisplayName("ユーザーがカートに1件書籍を持っており、借りている書籍が含まれていない場合、空のリストが返る")
    void shouldCheckoutBooksWhenUserHasBooksInCart() {
        BookSummary expected = new BookSummary();
        expected.setIsbn("1234567890");

        Book book = new Book();
        book.setIsbn("1234567890");

        CartItem cart = new CartItem();
        cart.setIsbn(book.getIsbn());

        List<CartItem> carts = List.of(cart);

        when(bookLendingManager.checkout("user1", List.of(cart.getIsbn()))).thenReturn(List.of());

        List<BookSummary> unCheckoutBooks = bookCartUseCase.checkout("user1", carts);

        verify(bookLendingManager, times(1)).checkout("user1", List.of(expected.getIsbn()));
        assertThat(unCheckoutBooks).isEmpty();
    }

    @Test
    @DisplayName("ユーザーがカートに複数の書籍を持っており、借りている書籍が含まれていない場合、空のリストが返る")
    void shouldCheckoutBooksWhenUserHasMultipleBooksInCart() {
        BookSummary expected1 = new BookSummary();
        expected1.setIsbn("1234567890123");

        BookSummary expected2 = new BookSummary();
        expected2.setIsbn("0987654321123");

        Book book1 = new Book();
        book1.setIsbn("1234567890123");

        Book book2 = new Book();
        book2.setIsbn("0987654321123");

        CartItem cart1 = new CartItem();
        cart1.setIsbn(book1.getIsbn());

        CartItem cart2 = new CartItem();
        cart2.setIsbn(book2.getIsbn());

        List<CartItem> carts = List.of(cart1, cart2);

        when(bookLendingManager.checkout("user1", List.of(cart1.getIsbn(), cart2.getIsbn()))).thenReturn(List.of());

        List<BookSummary> unCheckoutBooks = bookCartUseCase.checkout("user1", carts);

        verify(bookLendingManager, times(1)).checkout("user1", List.of(expected1.getIsbn(), expected2.getIsbn()));
        assertThat(unCheckoutBooks).isEmpty();
    }

    @Test
    @DisplayName("ユーザーがカートに書籍を持っているおり、既に借りている書籍が含まれている場合、借りている書籍は借りれなかった書籍として返される")
    void shouldReturnUnCheckoutBooksWhenUserHasBooksInCartButAlreadyCheckedOut() {
        BookSummary expected = new BookSummary();
        expected.setIsbn("1234567890");

        Book book = new Book();
        book.setIsbn("1234567890");

        CartItem cart = new CartItem();
        cart.setIsbn(book.getIsbn());

        List<CartItem> carts = List.of(cart);

        when(bookLendingManager.checkout("user1", List.of(cart.getIsbn()))).thenReturn(List.of(book));

        List<BookSummary> unCheckoutBooks = bookCartUseCase.checkout("user1", carts);

        verify(bookLendingManager, times(1)).checkout("user1", List.of(expected.getIsbn()));
        assertThat(unCheckoutBooks.size()).isEqualTo(1);
        assertThat(unCheckoutBooks.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("ユーザーがカートに複数書籍を持っているが、全て借りている書籍であった場合、全て借りれなかった書籍として返される")
    void shouldReturnUnCheckoutBooksWhenUserHasMultipleBooksInCartButAlreadyCheckedOut() {
        BookSummary expected1 = new BookSummary();
        expected1.setIsbn("1234567890");

        BookSummary expected2 = new BookSummary();
        expected2.setIsbn("0987654321");

        Book book1 = new Book();
        book1.setIsbn("1234567890");

        Book book2 = new Book();
        book2.setIsbn("0987654321");

        CartItem cart1 = new CartItem();
        cart1.setIsbn(book1.getIsbn());

        CartItem cart2 = new CartItem();
        cart2.setIsbn(book2.getIsbn());

        List<CartItem> carts = List.of(cart1, cart2);

        when(bookLendingManager.checkout("user1", List.of(cart1.getIsbn(), cart2.getIsbn()))).thenReturn(List.of(book1, book2));

        List<BookSummary> unCheckoutBooks = bookCartUseCase.checkout("user1", carts);

        verify(bookLendingManager, times(1)).checkout("user1", List.of(expected1.getIsbn(), expected2.getIsbn()));
        assertThat(unCheckoutBooks.size()).isEqualTo(2);
        assertThat(unCheckoutBooks.get(0)).isEqualTo(expected1);
        assertThat(unCheckoutBooks.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("ユーザーがカートに書籍を持っていない場合、空のリストが返される")
    void shouldReturnEmptyListWhenUserHasNoBooksInCart() {
        List<CartItem> carts = List.of();
        when(bookLendingManager.checkout("user1", List.of())).thenReturn(List.of());

        List<BookSummary> checkedOutBooks = bookCartUseCase.checkout("user1", carts);

        verify(bookLendingManager, times(1)).checkout("user1", List.of());
        assertThat(checkedOutBooks).isEmpty();
    }
}
