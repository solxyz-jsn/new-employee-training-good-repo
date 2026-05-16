package jp.co.solxyz.jsn.springbootadvincedexam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * 時刻取得設定クラス
 */
@Configuration
public class ClockConfig {

    /**
     * システム標準タイムゾーンのClockを生成する
     * @return Clock
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
