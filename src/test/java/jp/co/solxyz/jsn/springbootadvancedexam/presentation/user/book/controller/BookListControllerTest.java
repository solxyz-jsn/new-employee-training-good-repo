package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookListUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.BookModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookListControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private BookListUseCase bookListUseCase;

    private final WebApplicationContext context;

    BookListControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .with(user("user1").roles("USER")))
                .build();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBookAreAvailable() throws Exception {
        BookModel expected = new BookModel();
        expected.setIsbn("1234567890123");
        expected.setTitle("Test Book");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setAvailableStock(10);
        List<BookModel> expectedList = List.of(expected);

        BookSummary book = new BookSummary("1234567890123", "Test Book", "Test Author", "Test Publisher", 10,
                "Test Description");

        // JSON文字列をBookModelのリストに変換
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(expectedList);

        when(bookListUseCase.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "bookList"));

        verify(bookListUseCase, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBooksAreAvailable() throws Exception {
        BookModel expected1 = new BookModel();
        expected1.setIsbn("1234567890123");
        expected1.setTitle("Test Book");
        expected1.setAuthor("Test Author");
        expected1.setPublisher("Test Publisher");
        expected1.setDescription("Test Description");
        expected1.setAvailableStock(10);
        BookModel expected2 = new BookModel();
        expected2.setIsbn("0987654321098");
        expected2.setTitle("Test Book");
        expected2.setAuthor("Test Author");
        expected2.setPublisher("Test Publisher");
        expected2.setDescription("Test Description");
        expected2.setAvailableStock(5);
        List<BookModel> expectedList = List.of(expected1, expected2);

        BookSummary book1 = new BookSummary("1234567890123", "Test Book", "Test Author", "Test Publisher", 10,
                "Test Description");
        BookSummary book2 = new BookSummary("0987654321098", "Test Book", "Test Author", "Test Publisher", 5,
                "Test Description");
        List<BookSummary> books = List.of(book1, book2);

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(expectedList);

        when(bookListUseCase.getAllBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 2))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "bookList"));

        verify(bookListUseCase, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("書籍がない場合、空の書籍リストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBooksAreAvailable() throws Exception {
        when(bookListUseCase.getAllBooks()).thenReturn(Collections.emptyList());

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", equalTo(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", equalTo(bookJson)))
                .andExpect(MockMvcResultMatchers.model().attribute("bookCount", 0))
                .andExpect(MockMvcResultMatchers.model().attribute("activeMenu", "bookList"));

        verify(bookListUseCase, times(1)).getAllBooks();
    }
}
