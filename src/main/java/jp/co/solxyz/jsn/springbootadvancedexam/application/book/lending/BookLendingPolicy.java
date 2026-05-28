package jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 書籍貸出ポリシー
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookLendingPolicy {

    /**
     * 返却期限までの日数
     */
    public static final int RETURN_DUE_DAYS = 14;
}
