package jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.api;

import jakarta.validation.groups.Default;
import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserProfileCommand;
import jp.co.solxyz.jsn.springbootadvancedexam.application.user.management.UserManagementUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.json.AccountProfile;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.model.validation.OnUserCreation;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.admin.user.model.validation.OnUserUpdate;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
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
 * ユーザ管理APIコントローラー
 */
@RestController
@RequestMapping("/api/admin/management/user")
@RequiredArgsConstructor
@Slf4j
public class UserManagementApiController {

    /**
     * ユーザ管理ユースケース
     */
    private final UserManagementUseCase userManagementUseCase;

    /**
     * ユーザ情報を登録する
     * @param userManagementForm ユーザ情報
     * @param bindingResult バインディング結果
     * @return レスポンスエンティティ
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> addUser(
            @Validated({ OnUserCreation.class, Default.class }) @RequestBody AccountProfile userManagementForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            userManagementUseCase.addUser(toCommand(userManagementForm));
        } catch (JpaSystemException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "既に登録されているメールアドレスです。"));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の登録に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * ユーザ情報を更新する
     * @param userManagementForm ユーザ情報
     * @param bindingResult バインディング結果
     * @return レスポンスエンティティ
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> updateUser(
            @Validated({ OnUserUpdate.class, Default.class }) @RequestBody AccountProfile userManagementForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            userManagementUseCase.updateUser(toCommand(userManagementForm));
        } catch (NoSuchElementException | OptimisticLockingFailureException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (JpaSystemException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "既に登録されているメールアドレスです。"));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の更新に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * ユーザ情報を削除する
     * @param  userId ユーザID情報
     * @return レスポンスエンティティ
     */
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<List<ObjectError>> deleteUser(
            @UserId @PathVariable String userId) {

        try {
            userManagementUseCase.deleteUser(userId);
        } catch (NoSuchElementException | IllegalStateException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の削除に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    private UserProfileCommand toCommand(AccountProfile accountProfile) {
        UserProfileCommand command = new UserProfileCommand();
        command.setUserId(accountProfile.getUserId());
        command.setUserName(accountProfile.getUserName());
        command.setEmail(accountProfile.getEmail());
        command.setPassword(accountProfile.getPassword());
        command.setIsAdmin(accountProfile.isAdmin());
        return command;
    }
}
