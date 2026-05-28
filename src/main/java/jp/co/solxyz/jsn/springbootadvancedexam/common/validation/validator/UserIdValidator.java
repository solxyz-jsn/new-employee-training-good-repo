package jp.co.solxyz.jsn.springbootadvancedexam.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation.UserId;

/**
 * ユーザIDバリデータ
 */
public class UserIdValidator implements ConstraintValidator<UserId, String> {
    /**
     * ユーザIDがUUID形式かを判定する
     * @param value 検証対象のユーザID
     * @param context バリデーションコンテキスト
     * @return ユーザIDが未入力、またはUUID形式であればtrue
     */
    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
    }
}
