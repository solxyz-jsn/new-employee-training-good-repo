package jp.co.solxyz.jsn.springbootadvancedexam.application.user.management;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ管理ユースケース出力
 */
@Data
@NoArgsConstructor
public class UserSummary {

    /** ユーザID */
    private String userId;
    /** ユーザ名 */
    private String userName;
    /** メールアドレス */
    private String email;
    /** 管理者フラグ */
    private boolean admin;
}
