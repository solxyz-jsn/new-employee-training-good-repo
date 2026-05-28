package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.api;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementCommand;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.management.BookManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.book.json.BookDetail;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation.Isbn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 書籍管理APIコントローラ
 */
@RestController
@RequestMapping("/api/admin/management/book")
@RequiredArgsConstructor
@Slf4j
public class BookManagementApiController {

    /**
     * 書籍管理ユースケース
     */
    private final BookManagementUseCase bookManagementUseCase;

    /**
     * 書籍登録
     * @param addedBook 書籍情報
     * @param bindingResult バインディング結果
     * @return レスポンス
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> addBook(@Validated @RequestBody BookDetail addedBook, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            bookManagementUseCase.addBook(toCommand(addedBook));
        } catch (IllegalArgumentException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "書籍情報の登録に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 書籍更新
     * @param updatedBook 書籍情報
     * @param bindingResult バインディング結果
     * @return レスポンス
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> updateBook(@Validated @RequestBody BookDetail updatedBook, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            bookManagementUseCase.updateBook(toCommand(updatedBook));
        } catch (OptimisticLockingFailureException | NoSuchElementException | IllegalArgumentException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "書籍情報の更新に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 書籍削除
     * @param isbn ISBN
     * @return レスポンス
     */
    @DeleteMapping(path = "/{isbn}")
    public ResponseEntity<List<ObjectError>> deleteBook(@PathVariable @Isbn String isbn) {
        log.warn("書籍情報の削除を行います。ISBN: {}", isbn);

        try {
            bookManagementUseCase.deleteBook(isbn);
        } catch (NoSuchElementException | IllegalStateException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "書籍情報の削除に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 書籍詳細リクエストを書籍管理コマンドに変換する
     * @param bookDetail 書籍詳細リクエスト
     * @return 書籍管理コマンド
     */
    private BookManagementCommand toCommand(BookDetail bookDetail) {
        BookManagementCommand command = new BookManagementCommand();
        command.setIsbn(bookDetail.getIsbn());
        command.setTitle(bookDetail.getTitle());
        command.setAuthor(bookDetail.getAuthor());
        command.setPublisher(bookDetail.getPublisher());
        command.setDescription(bookDetail.getDescription());
        command.setStock(bookDetail.getStock());
        return command;
    }
}
