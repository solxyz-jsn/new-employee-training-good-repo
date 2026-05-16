package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookReturnControllerTest {
    /**
     * テストで使用する固定の現在日
     */
    private static final LocalDate TODAY = LocalDate.of(2026, 5, 17);

    /**
     * テストで使用する固定のタイムゾーン
     */
    private static final ZoneId TEST_ZONE = ZoneId.of("Asia/Tokyo");

    /**
     * 書籍貸出サービス
     */
    @MockitoBean
    private BookLendingService bookLendingService;

    /**
     * 現在日時取得用Clock
     */
    @MockitoBean
    private Clock clock;

    /**
     * ログインユーザ情報
     */
    @Mock
    private MyUserDetails userDetails;

    /**
     * Webアプリケーションコンテキスト
     */
    private final WebApplicationContext context;

    /**
     * Spring MVCテスト用モック
     */
    private MockMvc mockMvc;

    /**
     * テスト用ユーザID
     */
    private final String USER_ID = "user1";

    BookReturnControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(userDetails.getUserId()).thenReturn(USER_ID);
        when(clock.instant()).thenReturn(TODAY.atStartOfDay(TEST_ZONE).toInstant());
        when(clock.getZone()).thenReturn(TEST_ZONE);
    }

    @Test
    @DisplayName("1件の借りている書籍がある時にindexが呼び出された場合、1件の書籍を含んだリストを返す")
    void shouldReturnBookListWhenIndexIsCalled() throws Exception {
        String expectedUserId = "user1";
        LocalDateTime rentalAt = TODAY.atTime(10, 30);

        UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");
        unreturnedBook.setRentalAt(rentalAt);
        List<UnreturnedBookModel> unreturnedBooks = List.of(unreturnedBook);

        UnreturnedBookModel expectedUnreturnedBook = new UnreturnedBookModel();
        expectedUnreturnedBook.setIsbn("isbn1");
        expectedUnreturnedBook.setTitle("title1");
        expectedUnreturnedBook.setAuthor("author1");
        expectedUnreturnedBook.setPublisher("publisher1");
        expectedUnreturnedBook.setRentalAt(rentalAt);
        List<UnreturnedBookModel> expectedUnreturnedBooks = List.of(expectedUnreturnedBook);

        when(bookLendingService.getCurrentUserBooks(any())).thenReturn(unreturnedBooks);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("dueSoonCount", 0L))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("displayBooks"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("data-due-soon=\"false\"")))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<UnreturnedBookModel> displayBooks = (List<UnreturnedBookModel>) result.getModelAndView()
                .getModel()
                .get("displayBooks");
        assertThat(displayBooks).hasSize(1);
        UnreturnedBookModel displayBook = displayBooks.get(0);
        assertThat(displayBook.getDueAt()).isEqualTo(rentalAt.plusDays(14));
        assertThat(displayBook.getRemainingDays()).isEqualTo(14);
        assertThat(displayBook.isDueSoon()).isFalse();
        assertThat(displayBook.isOverdue()).isFalse();
        assertThat(displayBook.getStatusLabel()).isEqualTo("貸出中");

        verify(bookLendingService, times(1)).getCurrentUserBooks(expectedUserId);
    }

    @Test
    @DisplayName("返却期限が近い書籍がある場合、返却期限が近い書籍数に含める")
    void shouldCountDueSoonBooks() throws Exception {
        String userId = "user1";
        LocalDateTime rentalAt = TODAY.minusDays(12).atTime(10, 30);

        UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");
        unreturnedBook.setRentalAt(rentalAt);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getCurrentUserBooks(userId)).thenReturn(List.of(unreturnedBook));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("dueSoonCount", 1L))
                .andExpect(MockMvcResultMatchers.content().string(containsString("data-due-soon=\"true\"")))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<UnreturnedBookModel> displayBooks = (List<UnreturnedBookModel>) result.getModelAndView()
                .getModel()
                .get("displayBooks");
        assertThat(displayBooks).hasSize(1);
        UnreturnedBookModel displayBook = displayBooks.get(0);
        assertThat(displayBook.getDueAt()).isEqualTo(rentalAt.plusDays(14));
        assertThat(displayBook.getRemainingDays()).isEqualTo(2);
        assertThat(displayBook.isDueSoon()).isTrue();
        assertThat(displayBook.isOverdue()).isFalse();
        assertThat(displayBook.getStatusLabel()).isEqualTo("返却間近");
    }

    @Test
    @DisplayName("返却期限を過ぎた書籍がある場合、返却期限が近い書籍数には含めない")
    void shouldExcludeOverdueBooksFromDueSoonCount() throws Exception {
        String userId = "user1";
        LocalDateTime rentalAt = TODAY.minusDays(15).atTime(10, 30);

        UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");
        unreturnedBook.setRentalAt(rentalAt);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getCurrentUserBooks(userId)).thenReturn(List.of(unreturnedBook));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("dueSoonCount", 0L))
                .andExpect(MockMvcResultMatchers.content().string(containsString("data-due-soon=\"false\"")))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<UnreturnedBookModel> displayBooks = (List<UnreturnedBookModel>) result.getModelAndView()
                .getModel()
                .get("displayBooks");
        assertThat(displayBooks).hasSize(1);
        UnreturnedBookModel displayBook = displayBooks.get(0);
        assertThat(displayBook.getDueAt()).isEqualTo(rentalAt.plusDays(14));
        assertThat(displayBook.getRemainingDays()).isZero();
        assertThat(displayBook.isDueSoon()).isFalse();
        assertThat(displayBook.isOverdue()).isTrue();
        assertThat(displayBook.getStatusLabel()).isEqualTo("期限超過");
    }

    @Test
    @DisplayName("複数件の借りている書籍があるときにindexが呼び出された場合、複数件の書籍を含んだリストを返す")
    void shouldReturnMultipleBookListWhenIndexIsCalled() throws Exception {
        String userId = "user1";
        String expectedUserId = "user1";

        UnreturnedBookModel unreturnedBook1 = new UnreturnedBookModel();
        unreturnedBook1.setIsbn("isbn1");
        unreturnedBook1.setTitle("title1");
        unreturnedBook1.setAuthor("author1");
        unreturnedBook1.setPublisher("publisher1");
        UnreturnedBookModel unreturnedBook2 = new UnreturnedBookModel();
        unreturnedBook2.setIsbn("isbn2");
        unreturnedBook2.setTitle("title2");
        unreturnedBook2.setAuthor("author2");
        unreturnedBook2.setPublisher("publisher2");
        List<UnreturnedBookModel> unreturnedBooks = List.of(unreturnedBook1, unreturnedBook2);

        UnreturnedBookModel expectedUnreturnedBook1 = new UnreturnedBookModel();
        expectedUnreturnedBook1.setIsbn("isbn1");
        expectedUnreturnedBook1.setTitle("title1");
        expectedUnreturnedBook1.setAuthor("author1");
        expectedUnreturnedBook1.setPublisher("publisher1");
        UnreturnedBookModel expectedUnreturnedBook2 = new UnreturnedBookModel();
        expectedUnreturnedBook2.setIsbn("isbn2");
        expectedUnreturnedBook2.setTitle("title2");
        expectedUnreturnedBook2.setAuthor("author2");
        expectedUnreturnedBook2.setPublisher("publisher2");
        List<UnreturnedBookModel> expectedUnreturnedBooks = List.of(expectedUnreturnedBook1, expectedUnreturnedBook2);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getCurrentUserBooks(userId)).thenReturn(unreturnedBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 2))
                .andExpect(MockMvcResultMatchers.model().attribute("dueSoonCount", 0L))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attributeExists("displayBooks"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks));

        verify(bookLendingService, times(1)).getCurrentUserBooks(expectedUserId);
    }

    @Test
    @DisplayName("借りている書籍がないときにindexが呼び出された場合、空の書籍リストを返す")
    void shouldReturnEmptyBookListWhenIndexIsCalled() throws Exception {
        String userId = "user1";
        String expectedUserId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getCurrentUserBooks(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 0))
                .andExpect(MockMvcResultMatchers.model().attribute("dueSoonCount", 0L))
                .andExpect(MockMvcResultMatchers.model().attribute("returnDueDays", 14))
                .andExpect(MockMvcResultMatchers.model().attribute("displayBooks", Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.model().attribute("books", Collections.emptyList()));

        verify(bookLendingService, times(1)).getCurrentUserBooks(expectedUserId);
    }

    @Test
    @DisplayName("getCurrentUserBooksがExceptionをスローした場合、例外をキャッチしない")
    void shouldCatchExceptionWhenGetCurrentUserBooksThrowsException() {
        String userId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getCurrentUserBooks(userId)).thenThrow(new RuntimeException());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                            .with(user(userDetails)))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError());
            fail();
        } catch (Exception e) {
            verify(bookLendingService, times(1)).getCurrentUserBooks(userId);
        }
    }
}
