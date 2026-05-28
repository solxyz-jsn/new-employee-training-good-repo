package jp.co.solxyz.jsn.springbootadvancedexam.application.user.management;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

/**
 * ユーザ管理ユースケース入力
 */
@Data
@NoArgsConstructor
@NullUnmarked
public class UserProfileCommand {

    /** ユーザID */
    private String userId;
    /** ユーザ名 */
    private String userName;
    /** メールアドレス */
    private String email;
    /** 管理者フラグ */
    private boolean isAdmin;
    /** パスワード */
    private String password;

    /**
     * 管理者フラグを設定する
     * @param isAdmin 管理者フラグ
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
