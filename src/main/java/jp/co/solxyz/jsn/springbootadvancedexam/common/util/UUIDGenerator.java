package jp.co.solxyz.jsn.springbootadvancedexam.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * UUID生成クラス
 */
@Component
public class UUIDGenerator {

    /**
     * ユーザIDを生成する
     * @return UUID
     */
    public String generateUserId() {
        return generate();
    }

    /**
     * 貸出IDを生成する
     * @return UUID
     */
    public String generateRentalId() {
        return generate();
    }

    private String generate() {
        return UUID.randomUUID().toString();
    }

}
