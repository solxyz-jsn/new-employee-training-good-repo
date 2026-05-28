package jp.co.solxyz.jsn.springbootadvancedexam.application.user.management;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ管理ユースケース入力
 */
@Data
@NoArgsConstructor
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

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
