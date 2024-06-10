package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 書籍一覧コントローラ
 */
@Controller
@RequestMapping("/book/list")
@Slf4j
public class BookListController {

    /**
     * 書籍一覧サービス
     */
    private final BookListService bookListService;

    /**
     * コンストラクタ
     * @param bookListService 書籍一覧サービス
     */
    public BookListController(BookListService bookListService) {
        this.bookListService = bookListService;
    }

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     * @throws JsonProcessingException JSON変換例外
     */
    @GetMapping
    public ModelAndView bookList() throws JsonProcessingException {
        ModelAndView mav = new ModelAndView("user/book-list");

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        List<BookModel> displayedBookModels = bookListService.getAllBooks().stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();
        mav.addObject("books", displayedBookModels);
        mav.addObject("booksJson", mapper.writeValueAsString(displayedBookModels));

        return mav;
    }
}
