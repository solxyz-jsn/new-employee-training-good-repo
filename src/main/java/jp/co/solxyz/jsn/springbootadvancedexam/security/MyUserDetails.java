package jp.co.solxyz.jsn.springbootadvancedexam.security;

import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Spring Securityで扱うログインユーザ情報
 */
@AllArgsConstructor
public class MyUserDetails implements UserDetails, Serializable {
    /**
     * シリアルバージョンUID
     */
    @Serial
    private static final long serialVersionUID = 7985440768523436212L;

    /**
     * ユーザアカウント
     */
    private final UserAccount userAccount;

    /**
     * ユーザ権限を取得する
     * @return ユーザ権限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userAccount.getIsAdmin()
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * ユーザIDを取得する
     * @return ユーザID
     */
    public String getUserId() {
        return this.userAccount.getUserId();
    }

    /**
     * パスワードを取得する
     * @return パスワード
     */
    @Override
    public String getPassword() {
        return this.userAccount.getPassword();
    }

    /**
     * ユーザ名を取得する
     * @return ユーザ名
     */
    @Override
    public String getUsername() {
        return this.userAccount.getUsername();
    }

    /**
     * アカウントの有効期限が切れていないかを返す
     * @return 常にtrue
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントがロックされていないかを返す
     * @return 常にtrue
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 資格情報の有効期限が切れていないかを返す
     * @return 常にtrue
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * アカウントが有効かを返す
     * @return 常にtrue
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
