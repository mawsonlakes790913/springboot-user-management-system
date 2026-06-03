package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LengthMinValidator implements ConstraintValidator<LengthMin, String> {

    /** 文字列の長さ */
    private int minLength;

    // 初期化処理
    @Override
    public void initialize(LengthMin lengthMin) {
        this.minLength = lengthMin.min();
    }

    // チェック処理
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 空の場合は@NotNullなどでチェックする
        if (value == null || value.isEmpty()) {
            return true;
        }
        // 文字数チェック
        if (value.length() < this.minLength) {
            return false;
        }
        return true;
    }
}
