package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.BookCartUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.CartBook;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.CartItem;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.security.MyUserDetails;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.Cart;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.CartSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending.BookLendingPolicy.RETURN_DUE_DAYS;

/**
 * カートコントローラ
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/book/cart")
public class BookCartController {

    /**
     * カートセッション
     */
    private final CartSession cartSession;
    /**
     * カートユースケース
     */
    private final BookCartUseCase bookCartUseCase;
    /**
     * 現在日時取得用Clock
     */
    private final Clock clock;

    /**
     * カート画面表示
     * @return カート画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("user/book-cart");
        List<CartBookModel> tableData = bookCartUseCase.getCartList(toCartItems(cartSession.getCartList())).stream()
                .map(this::toCartBookModel)
                .toList();
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
        List<BookSummary> unCheckedOutBooks;
        try {
            unCheckedOutBooks = bookCartUseCase.checkout(userDetails.getUserId(), toCartItems(cartSession.getCartList()));
        } catch (DataAccessException | NoSuchElementException e) {
            ModelAndView mav = new ModelAndView("user/book-cart");
            mav.addObject("errorMessage", "処理中にエラーが発生しました。");
            addCartPageAttributes(mav, cartSession.getCartList().size());
            return mav;
        }

        List<BookModel> displayedUnCheckedOutBookModels = unCheckedOutBooks.stream()
                .map(this::toBookModel)
                .toList();

        cartSession.clearCart();

        ModelAndView mav = new ModelAndView("user/book-cart");
        addCartPageAttributes(mav, displayedUnCheckedOutBookModels.size());

        if (!unCheckedOutBooks.isEmpty()) {
            mav.addObject("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
            mav.addObject("cartList", displayedUnCheckedOutBookModels);
        }

        return mav;
    }

    /**
     * カート画面で共通して使用する表示属性を追加する
     * @param mav モデルとビュー
     * @param cartCount カート内の書籍数
     */
    private void addCartPageAttributes(ModelAndView mav, int cartCount) {
        mav.addObject("activeMenu", "cart");
        mav.addObject("cartCount", cartCount);
        mav.addObject("returnDueDays", RETURN_DUE_DAYS);
        mav.addObject("returnDueDateText", formatReturnDueDate(LocalDate.now(clock).plusDays(RETURN_DUE_DAYS)));
    }

    /**
     * 返却期限日を画面表示用の形式に変換する
     * @param returnDueDate 返却期限日
     * @return 画面表示用の返却期限日
     */
    private String formatReturnDueDate(LocalDate returnDueDate) {
        String dayOfWeek = returnDueDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE);
        return returnDueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " (" + dayOfWeek + ")";
    }

    private CartBookModel toCartBookModel(CartBook cartBook) {
        return new CartBookModel(cartBook.getIsbn(), cartBook.getTitle(), cartBook.getAuthor(), cartBook.getPublisher());
    }

    private BookModel toBookModel(BookSummary book) {
        return new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                book.getDescription());
    }

    private List<CartItem> toCartItems(List<Cart> carts) {
        return carts.stream()
                .map(cart -> new CartItem(cart.getIsbn()))
                .toList();
    }
}
