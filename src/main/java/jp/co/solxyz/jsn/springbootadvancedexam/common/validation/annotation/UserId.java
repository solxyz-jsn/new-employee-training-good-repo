package jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.validator.UserIdValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ユーザIDバリデーションアノテーション
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserIdValidator.class)
public @interface UserId {
    /**
     * バリデーションエラーメッセージを取得する
     * @return バリデーションエラーメッセージ
     */
    String message() default "ユーザIDが不正です。";

    /**
     * バリデーショングループを取得する
     * @return バリデーショングループ
     */
    Class<?>[] groups() default {};

    /**
     * バリデーションペイロードを取得する
     * @return バリデーションペイロード
     */
    Class<? extends Payload>[] payload() default {};
}
