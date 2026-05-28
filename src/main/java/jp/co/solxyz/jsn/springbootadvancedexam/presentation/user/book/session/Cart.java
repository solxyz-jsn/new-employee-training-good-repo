package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * セッションに保存するカート情報
 */
@Data
public class Cart implements Serializable {
    /**
     * シリアルバージョンUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ISBN
     */
    private String isbn;
}
