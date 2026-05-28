package jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

/**
 * 書籍貸出履歴エンティティ
 */
@Entity
@Table(name = "book_checkout_history")
@Data
@DynamicInsert
public class BookCheckoutHistory {
    /**
     * 貸出ID
     */
    @Id
    @Column(name = "rental_id")
    private String rentalId;

    /**
     * ISBN
     */
    @Column(name = "isbn")
    private String isbn;

    /**
     * ユーザID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 貸出日時
     */
    @Column(name = "rental_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime rentalAt;

    /**
     * 返却日時
     */
    @Column(name = "return_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime returnAt;

    /**
     * バージョン
     */
    @Version
    private long version;
}
