package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookListUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.BookModel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class BookListController {

    /**
     * 書籍一覧ユースケース
     */
    private final BookListUseCase bookListUseCase;

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     * @throws JsonProcessingException 指定した日付のフォーマットが不適切、または変換対象がJSON形式にシリアライズできない場合
     */
    @GetMapping
    public ModelAndView bookList() throws JsonProcessingException {
        ModelAndView mav = new ModelAndView("user/book-list");
        
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        List<BookModel> displayedBookModels = bookListUseCase.getAllBooks().stream()
                .map(this::toBookModel)
                .toList();
        mav.addObject("books", displayedBookModels);
        mav.addObject("booksJson", mapper.writeValueAsString(displayedBookModels));
        mav.addObject("bookCount", displayedBookModels.size());
        mav.addObject("activeMenu", "bookList");

        return mav;
    }

    /**
     * 書籍概要を画面表示用書籍モデルに変換する
     * @param book 書籍概要
     * @return 画面表示用書籍モデル
     */
    private BookModel toBookModel(BookSummary book) {
        return new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                book.getDescription());
    }
}
