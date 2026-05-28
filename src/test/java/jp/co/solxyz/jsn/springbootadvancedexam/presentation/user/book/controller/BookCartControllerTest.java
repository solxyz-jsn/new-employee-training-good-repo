package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.BookCartUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.CartBook;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.CartItem;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.Cart;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.CartSession;
import jp.co.solxyz.jsn.springbootadvancedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookCartControllerTest {

    @MockitoBean
    private CartSession cartSession;

    @MockitoBean
    private BookCartUseCase bookCartUseCase;

    @Mock
    private MyUserDetails userDetails;

    private MockMvc mockMvc;

    private final WebApplicationContext context;

    private final String USER_ID = "user1";

    BookCartControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .with(user(userDetails)))
                .defaultRequest(MockMvcRequestBuilders.post("/")
                        .with(user(userDetails))
                        .with(csrf()))
                .build();

        when(userDetails.getUserId()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("カートに書籍が1冊ある状態でindexが呼び出された場合、cartListに1件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalled() throws Exception {
        Cart cart = new Cart();
        cart.setIsbn("1234567890");

        List<Cart> carts = List.of(cart);
        List<CartItem> expectedCartItems = List.of(new CartItem("1234567890"));

        CartBook cartBook = new CartBook("1234567890", "Test Book", "Test Author", "Test Publisher");

        List<CartBook> cartBooks = List.of(cartBook);
        List<CartBookModel> expectedCartBooks = List.of(new CartBookModel("1234567890", "Test Book", "Test Author", "Test Publisher"));

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartUseCase.getCartList(expectedCartItems)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
        verify(bookCartUseCase, times(1)).getCartList(expectedCartItems);
    }

    @Test
    @DisplayName("カートに書籍が複数ある状態でindexが呼び出された場合、cartListに複数件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalledWithMultipleBooks() throws Exception {
        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321");

        List<Cart> carts = List.of(cart1, cart2);
        List<CartItem> cartItems = List.of(new CartItem("1234567890"), new CartItem("0987654321"));

        CartBook cartBook1 = new CartBook("1234567890", "Test Book1", "Test Author1", "Test Publisher1");
        CartBook cartBook2 = new CartBook("0987654321", "Test Book2", "Test Author2", "Test Publisher2");

        List<CartBook> cartBooks = List.of(cartBook1, cartBook2);
        List<CartBookModel> expectedCartBooks = List.of(
                new CartBookModel("1234567890", "Test Book1", "Test Author1", "Test Publisher1"),
                new CartBookModel("0987654321", "Test Book2", "Test Author2", "Test Publisher2"));

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartUseCase.getCartList(cartItems)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 2))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
    }

    @Test
    @DisplayName("カートに何もない状態でindexが呼び出された場合、空のカートが返される")
    void shouldEmptyDisplayCartWhenIndexIsCalled() throws Exception {
        when(cartSession.getCartList()).thenReturn(Collections.emptyList());
        when(bookCartUseCase.getCartList(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 0))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", Collections.emptyList()));

        verify(bookCartUseCase, times(1)).getCartList(Collections.emptyList());
    }

    @Test
    @DisplayName("有効なユーザー情報がある場合、カート内の書籍の貸し出し処理をする")
    void shouldCheckedOutBookToCartWhenValidUserDetails() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        List<CartItem> cartItems = List.of(new CartItem("1234567890123"));

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartUseCase.checkout(USER_ID, cartItems)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 0))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("cartList"));

        verify(bookCartUseCase, times(1)).checkout(expectedUserId, cartItems);
    }

    @Test
    @DisplayName("チェックアウト時にDataAccessExceptionのサブクラスが発生した場合、エラーが返される")
    void shouldReturnErrorWhenExceptionIsThrownOnCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        List<CartItem> cartItems = List.of(new CartItem("1234567890123"));

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        doThrow(DataAccessResourceFailureException.class).when(bookCartUseCase).checkout(USER_ID, cartItems);

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "処理中にエラーが発生しました。"));

        verify(bookCartUseCase, times(1)).checkout(expectedUserId, cartItems);
    }

    @Test
    @DisplayName("チェックアウト時に存在しないISBNが含まれる場合、エラーが返される")
    void shouldReturnErrorWhenCheckoutContainsNonexistentIsbn() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        List<CartItem> cartItems = List.of(new CartItem("1234567890123"));

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        doThrow(NoSuchElementException.class).when(bookCartUseCase).checkout(USER_ID, cartItems);

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "処理中にエラーが発生しました。"));

        verify(bookCartUseCase, times(1)).checkout(expectedUserId, cartItems);
    }

    @Test
    @DisplayName("すでに借りている書籍を借りる場合、借りれなかった書籍として返される")
    void shouldReturnErrorWhenSomeBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        List<CartItem> cartItems = List.of(new CartItem("1234567890123"));

        BookModel expectedBook = new BookModel();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Book");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        List<BookModel> expectedCartBooks = List.of(expectedBook);

        BookSummary book = new BookSummary("1234567890123", "Test Book", "Test Author", "Test Publisher", 0, null);

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartUseCase.checkout(USER_ID, cartItems)).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartUseCase, times(1)).checkout(expectedUserId, cartItems);
    }

    @Test
    @DisplayName("すでに借りている書籍を複数件借りる場合、複数件の借りれなかった書籍が返される")
    void shouldReturnErrorWhenMultipleBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890123");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321098");
        List<CartItem> cartItems = List.of(new CartItem("1234567890123"), new CartItem("0987654321098"));

        BookModel expectedBook1 = new BookModel();
        expectedBook1.setIsbn("1234567890123");
        expectedBook1.setTitle("Test Book1");
        expectedBook1.setAuthor("Test Author1");
        expectedBook1.setPublisher("Test Publisher1");
        BookModel expectedBook2 = new BookModel();
        expectedBook2.setIsbn("0987654321098");
        expectedBook2.setTitle("Test Book2");
        expectedBook2.setAuthor("Test Author2");
        expectedBook2.setPublisher("Test Publisher2");
        List<BookModel> expectedCartBooks = List.of(expectedBook1, expectedBook2);

        BookSummary book1 = new BookSummary("1234567890123", "Test Book1", "Test Author1", "Test Publisher1", 0, null);
        BookSummary book2 = new BookSummary("0987654321098", "Test Book2", "Test Author2", "Test Publisher2", 0, null);

        when(cartSession.getCartList()).thenReturn(List.of(cart1, cart2));
        when(bookCartUseCase.checkout(USER_ID, cartItems)).thenReturn(List.of(book1, book2));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartCount", 2))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("returnDueDateText"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartUseCase, times(1)).checkout(expectedUserId, cartItems);
    }
}
