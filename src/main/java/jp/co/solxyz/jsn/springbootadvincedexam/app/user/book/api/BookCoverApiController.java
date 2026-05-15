package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookCoverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 書影取得APIコントローラ
 */
@RestController
@RequestMapping("/api/book/covers")
@Slf4j
public class BookCoverApiController {

    /**
     * 書影取得サービス
     */
    private final BookCoverService bookCoverService;

    /**
     * コンストラクタ
     * @param bookCoverService 書影取得サービス
     */
    public BookCoverApiController(BookCoverService bookCoverService) {
        this.bookCoverService = bookCoverService;
    }

    /**
     * ISBNに対応する書影URLを取得
     * @param isbn カンマ区切りのISBN
     * @return ISBNと書影URLのマップ
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getCoverUrls(@RequestParam("isbn") String isbn) {
        List<String> isbnList = Arrays.stream(isbn.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
        if (isbnList.isEmpty()) {
            log.info("ISBN is empty");
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(bookCoverService.getCoverUrls(isbnList));
    }
}
