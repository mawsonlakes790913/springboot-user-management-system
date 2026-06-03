package com.example.demo.validator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BirthdayAgeValidator implements ConstraintValidator<BirthdayAge, Object> {

    /** 誕生日 */
    private String birthdayFieldName;
    /** 年齢 */
    private String ageFieldName;
    /** メッセージ */
    private String message;

    // 初期化処理
    @Override
    public void initialize(BirthdayAge birthdayAge) {
        this.birthdayFieldName = birthdayAge.birthdayFieldName();
        this.ageFieldName = birthdayAge.ageFieldName();
        this.message = birthdayAge.message();
    }

    // チェック処理
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 値の取得
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Date birthday = (Date) beanWrapper.getPropertyValue(this.birthdayFieldName);
        Integer age = (Integer) beanWrapper.getPropertyValue(this.ageFieldName);
        // 空の場合は@NotNullなどでチェックする
        if (birthday == null || age == null) {
            return true;
        }
        // 誕生日から年齢を算出
        int calculateAge = calculateAge(birthday);
        // 算出した年齢と、入力された年齢が一致するかチェック
        if (calculateAge != age) {
            // エラーメッセージの出力フィールド設定
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message)
                    .addPropertyNode(this.birthdayFieldName)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    /** 誕生日から年齢を算出するメソッド */
    private int calculateAge(Date birthday) {
        // 現在日付を取得
        LocalDate today = LocalDate.now();
        // DateをLocalDateに変換
        LocalDate birthdayLocalDate = convertToLocalDate(birthday);
        // 誕生日と現在の日付の差を計算
        Period period = Period.between(birthdayLocalDate, today);
        // 差から年齢を取得
        return period.getYears();
    }

    /** DateをLocalDateに変換するメソッド */
    private LocalDate convertToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
