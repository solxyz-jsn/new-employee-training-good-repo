package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending.BookLendingUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending.UnreturnedBookSummary;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending.BookLendingPolicy.RETURN_DUE_DAYS;

/**
 * 書籍返却コントローラ
 */
@Controller
@RequestMapping("/book/return")
@RequiredArgsConstructor
public class BookReturnController {
    /**
     * 返却期限が近いとみなす残日数
     */
    private static final int DUE_SOON_DAYS = 3;

    /**
     * 書籍返却ユースケース
     */
    private final BookLendingUseCase bookLendingUseCase;
    /**
     * 現在日時取得用Clock
     */
    private final Clock clock;

    /**
     * 書籍返却画面表示
     * @param userDetails ログインユーザ情報
     * @return 書籍返却画面
     */
    @GetMapping
    public ModelAndView index(@AuthenticationPrincipal MyUserDetails userDetails) {
        ModelAndView mav = new ModelAndView("user/book-lending");
        List<UnreturnedBookSummary> unreturnedBooks = bookLendingUseCase.getCurrentUserBooks(userDetails.getUserId());
        List<UnreturnedBookModel> displayBooks = unreturnedBooks.stream()
                .map(this::createDisplayBook)
                .toList();

        mav.addObject("books", unreturnedBooks);
        mav.addObject("displayBooks", displayBooks);
        addLendingPageAttributes(mav, displayBooks);
        return mav;
    }

    /**
     * 借りている書籍画面で共通して使用する表示属性を追加する
     * @param mav モデルとビュー
     * @param displayBooks 表示用の未返却書籍
     */
    private void addLendingPageAttributes(ModelAndView mav, List<UnreturnedBookModel> displayBooks) {
        long dueSoonCount = displayBooks.stream()
                .filter(UnreturnedBookModel::isDueSoon)
                .count();

        mav.addObject("activeMenu", "lending");
        mav.addObject("bookCount", displayBooks.size());
        mav.addObject("dueSoonCount", dueSoonCount);
        mav.addObject("returnDueDays", RETURN_DUE_DAYS);
    }

    /**
     * 画面表示用の未返却書籍情報を作成する
     * @param book 未返却書籍
     * @return 画面表示用の未返却書籍
     */
    private UnreturnedBookModel createDisplayBook(UnreturnedBookSummary book) {
        UnreturnedBookModel displayBook = new UnreturnedBookModel();
        displayBook.setIsbn(book.getIsbn());
        displayBook.setTitle(book.getTitle());
        displayBook.setAuthor(book.getAuthor());
        displayBook.setPublisher(book.getPublisher());
        displayBook.setRentalAt(book.getRentalAt());

        LocalDateTime dueAt = calculateDueAt(book.getRentalAt());
        long remainingDays = calculateRemainingDays(dueAt);
        boolean overdue = dueAt != null && remainingDays < 0;
        boolean dueSoon = dueAt != null && !overdue && remainingDays <= DUE_SOON_DAYS;

        displayBook.setDueAt(dueAt);
        displayBook.setRemainingDays(Math.max(remainingDays, 0));
        displayBook.setDueSoon(dueSoon);
        displayBook.setOverdue(overdue);
        displayBook.setStatusLabel(createStatusLabel(overdue, dueSoon));
        return displayBook;
    }

    /**
     * 貸出日時から返却期限日時を計算する
     * @param rentalAt 貸出日時
     * @return 返却期限日時
     */
    private @Nullable LocalDateTime calculateDueAt(@Nullable LocalDateTime rentalAt) {
        if (rentalAt == null) {
            return null;
        }
        return rentalAt.plusDays(RETURN_DUE_DAYS);
    }

    /**
     * 返却期限日までの残日数を計算する
     * @param dueAt 返却期限日時
     * @return 返却期限日までの残日数
     */
    private long calculateRemainingDays(@Nullable LocalDateTime dueAt) {
        if (dueAt == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(clock), dueAt.toLocalDate());
    }

    /**
     * 貸出ステータスの表示名を作成する
     * @param overdue 返却期限を過ぎているか
     * @param dueSoon 返却期限が近いか
     * @return 貸出ステータスの表示名
     */
    private String createStatusLabel(boolean overdue, boolean dueSoon) {
        if (overdue) {
            return "期限超過";
        }
        if (dueSoon) {
            return "返却間近";
        }
        return "貸出中";
    }
}
