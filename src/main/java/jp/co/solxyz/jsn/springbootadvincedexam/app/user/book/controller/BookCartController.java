package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookCartService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import jp.co.solxyz.jsn.springbootadvincedexam.session.CartSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * カートコントローラ
 */
@Controller
@Slf4j
@RequestMapping("/book/cart")
public class BookCartController {

    /**
     * 返却期限までの日数
     */
    private static final int RETURN_DUE_DAYS = 14;

    /**
     * カートセッション
     */
    private final CartSession cartSession;
    /**
     * カートサービス
     */
    private final BookCartService bookCartService;

    /**
     * コンストラクタ
     * @param cartSession カートセッション
     * @param bookCartService カートサービス
     */
    public BookCartController(CartSession cartSession, BookCartService bookCartService) {
        this.cartSession = cartSession;
        this.bookCartService = bookCartService;
    }

    /**
     * カート画面表示
     * @return カート画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("user/book-cart");
        List<CartBookModel> tableData = bookCartService.getCartList(cartSession.getCartList());
        mav.addObject("cartList", tableData);
        addCartPageAttributes(mav, tableData.size());
        return mav;
    }

    /**
     * カート内の書籍を貸出
     * @param userDetails ユーザ情報
     * @return カート画面
     */
    @PostMapping
    public ModelAndView checkout(@AuthenticationPrincipal MyUserDetails userDetails) {
        List<Book> unCheckedOutBooks;
        try {
            unCheckedOutBooks = bookCartService.checkout(userDetails.getUserId(), cartSession.getCartList());
        } catch (DataAccessException e) {
            ModelAndView mav = new ModelAndView("user/book-cart");
            mav.addObject("errorMessage", "処理中にエラーが発生しました。");
            addCartPageAttributes(mav, cartSession.getCartList().size());
            return mav;
        }

        List<BookModel> displayedUnCheckedOutBookModels = unCheckedOutBooks.stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();

        cartSession.clearCart();

        ModelAndView mav = new ModelAndView("user/book-cart");
        addCartPageAttributes(mav, displayedUnCheckedOutBookModels.size());

        if (!unCheckedOutBooks.isEmpty()) {
            mav.addObject("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
            mav.addObject("cartList", displayedUnCheckedOutBookModels);
        }

        return mav;
    }

    private void addCartPageAttributes(ModelAndView mav, int cartCount) {
        mav.addObject("activeMenu", "cart");
        mav.addObject("cartCount", cartCount);
        mav.addObject("returnDueDays", RETURN_DUE_DAYS);
        mav.addObject("returnDueDateText", formatReturnDueDate(LocalDate.now().plusDays(RETURN_DUE_DAYS)));
    }

    private String formatReturnDueDate(LocalDate returnDueDate) {
        String dayOfWeek = returnDueDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE);
        return returnDueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " (" + dayOfWeek + ")";
    }

}
