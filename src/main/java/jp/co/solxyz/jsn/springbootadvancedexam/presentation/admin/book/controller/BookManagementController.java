package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.model.BookManagementModel;
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
     * 書籍管理ユースケース
     */
    private final BookManagementUseCase bookManagementUseCase;

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("admin/book-management");
        mav.addObject("activeMenu", "bookManagement");
        List<BookManagementSummary> books = bookManagementUseCase.getAllBooks();
        List<BookManagementModel> displayedBookModels = new ArrayList<>();
        for (BookManagementSummary book : books) {
            displayedBookModels.add(
                    new BookManagementModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDescription(),
                            book.getStock(), book.getCreatedAt(), book.getUpdatedAt()));
        }

        mav.addObject("books", displayedBookModels);
        return mav;
    }
}
