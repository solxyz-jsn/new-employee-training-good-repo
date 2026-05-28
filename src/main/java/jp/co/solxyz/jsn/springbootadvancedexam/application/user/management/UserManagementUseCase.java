package jp.co.solxyz.jsn.springbootadvancedexam.application.user.management;
import jp.co.solxyz.jsn.springbootadvancedexam.common.util.UUIDGenerator;
import jp.co.solxyz.jsn.springbootadvancedexam.component.user.UserAccountManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.user.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * ユーザ管理ユースケース
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementUseCase {

    /**
     * ユーザアカウントリポジトリ
     */
    private final UserAccountManager userAccountManager;
    /** UUID生成クラス */
    private final UUIDGenerator uuidGenerator;
    /** 現在日時取得用Clock */
    private final Clock clock;

    /**
     * パスワードを除いたユーザ情報を全件取得する
     * @return ユーザ情報リスト
     */
    public List<UserSummary> getAllUsers() {
        List<UserAccount> users = userAccountManager.getAllUsersWithoutPassword();
        return users.stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 新規ユーザを追加する
     * @param newUserModel 追加するユーザ情報
     */
    public void addUser(UserProfileCommand newUserModel) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(uuidGenerator.generateUserId());
        userAccount.setIsAdmin(newUserModel.isAdmin());
        userAccount.setEmail(newUserModel.getEmail());
        userAccount.setUsername(newUserModel.getUserName());
        userAccount.setPassword(newUserModel.getPassword());
        userAccount.setUpdatedAt(Instant.now(clock));

        userAccountManager.addUser(userAccount);
    }

    /**
     * ユーザ情報を更新する
     * @param updatedUserModel 更新するユーザ情報
     */
    public void updateUser(UserProfileCommand updatedUserModel) {
        UserAccount currentUserAccountDto = userAccountManager.findByIdWithoutPassword(updatedUserModel.getUserId());
        UserAccount updatedUserAccount = new UserAccount();
        updatedUserAccount.setUserId(currentUserAccountDto.getUserId());
        updatedUserAccount.setIsAdmin(updatedUserModel.isAdmin());
        updatedUserAccount.setEmail(updatedUserModel.getEmail());
        updatedUserAccount.setUsername(updatedUserModel.getUserName());
        updatedUserAccount.setPassword(updatedUserModel.getPassword());
        updatedUserAccount.setUpdatedAt(Instant.now(clock));

        if (Objects.toString(updatedUserAccount.getPassword(), "").isEmpty()) {
            // パスワードが空の場合はパスワードを含めない
            userAccountManager.updateUserWithoutPassword(updatedUserAccount, currentUserAccountDto.getUpdatedAt());
        } else {
            userAccountManager.updateUserWithPassword(updatedUserAccount, currentUserAccountDto.getUpdatedAt());
        }
    }

    /**
     * ユーザ情報を削除する
     * @param userId 削除するユーザID
     * @throws NoSuchElementException DBに存在しないユーザIDが指定された場合
     * @throws IllegalStateException 管理者ユーザが1件のみの場合
     * @throws DataAccessException ユーザ情報の削除に失敗した場合
     */
    public void deleteUser(String userId) throws NoSuchElementException, IllegalStateException, DataAccessException {
        userAccountManager.deleteUser(userId);
    }

    /**
     * ユーザアカウントをユーザ概要に変換する
     * @param userAccount ユーザアカウント
     * @return ユーザ概要
     */
    private UserSummary toSummary(UserAccount userAccount) {
        UserSummary user = new UserSummary();
        user.setUserId(userAccount.getUserId());
        user.setUserName(userAccount.getUsername());
        user.setEmail(userAccount.getEmail());
        user.setAdmin(userAccount.getIsAdmin());
        return user;
    }
}
