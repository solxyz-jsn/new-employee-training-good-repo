package jp.co.solxyz.jsn.springbootadvancedexam.security;

import jp.co.solxyz.jsn.springbootadvancedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvancedexam.infra.repository.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ユーザアカウントサービス
 */
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    /**
     * ユーザアカウントリポジトリ
     */
    private final UserAccountRepository userAccountRepository;

    /**
     *  ユーザ名を指定してユーザ情報を取得する
     * @param username ユーザ名
     * @return ユーザ情報
     * @throws UsernameNotFoundException ユーザが見つからない場合
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(userAccount);
    }
}
