package jp.co.solxyz.jsn.springbootadvancedexam.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 文字列ユーティリティ
 */
@Component
@RequiredArgsConstructor
public class PasswordUtility {

    /**
     * パスワードエンコーダ
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * パスワードをハッシュ化する
     * @param password パスワード
     * @return ハッシュ化されたパスワード
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
