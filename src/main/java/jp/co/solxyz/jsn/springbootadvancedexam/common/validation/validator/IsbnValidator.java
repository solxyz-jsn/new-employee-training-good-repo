package jp.co.solxyz.jsn.springbootadvancedexam.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation.Isbn;

/**
 * ISBNバリデータ
 */
public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    /**
     * ISBNが13桁の数字かを判定する
     * @param value 検証対象のISBN
     * @param context バリデーションコンテキスト
     * @return ISBNが未入力、または13桁の数字であればtrue
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^\\d{13}$");
    }
}
