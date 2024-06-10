package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 書籍返却コントローラ
 */
@Controller
@RequestMapping("/book/return")
public class BookReturnController {
    /**
     * 書籍返却サービス
     */
    private final BookLendingService bookReturnService;

    /**
     * コンストラクタ
     * @param bookReturnService 書籍返却サービス
     */
    public BookReturnController(BookLendingService bookReturnService) {
        this.bookReturnService = bookReturnService;
    }

    /**
     * 書籍返却画面表示
     * @param userDetails ログインユーザ情報
     * @return 書籍返却画面
     */
    @GetMapping
    public ModelAndView index(@AuthenticationPrincipal MyUserDetails userDetails) {
        ModelAndView mav = new ModelAndView("user/book-lending");
        List<UnreturnedBookModel> unreturnedBooks = bookReturnService.getCurrentUserBooks(userDetails.getUserId());

        mav.addObject("books", unreturnedBooks);
        return mav;
    }
}
