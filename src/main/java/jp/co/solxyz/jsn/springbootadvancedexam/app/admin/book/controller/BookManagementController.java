package jp.co.solxyz.jsn.springbootadvancedexam.app.admin.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.model.BookManagementModel;
import jp.co.solxyz.jsn.springbootadvancedexam.app.admin.book.service.BookManagementService;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 書籍管理コントローラ
 */
@Controller
@RequestMapping("/admin/management/book")
@RequiredArgsConstructor
public class BookManagementController {

    /**
     * 書籍管理サービス
     */
    private final BookManagementService bookManagementService;

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("admin/book-management");
        mav.addObject("activeMenu", "bookManagement");
        List<Book> books = bookManagementService.getAllBooks();
        List<BookManagementModel> displayedBookModels = new ArrayList<>();
        for (Book book : books) {
            displayedBookModels.add(
                    new BookManagementModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDescription(),
                            book.getStock(), book.getCreatedAt(), book.getUpdatedAt()));
        }

        mav.addObject("books", displayedBookModels);
        return mav;
    }
}
