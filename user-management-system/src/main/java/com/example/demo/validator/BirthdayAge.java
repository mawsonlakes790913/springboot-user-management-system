package com.example.demo.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { BirthdayAgeValidator.class })
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthdayAge {

    /** デフォルトメッセージ */
    String message() default "{birthday.age.message}";

    /** グループ */
    Class<?>[] groups() default {};

    /** ペイロード */
    Class<? extends Payload>[] payload() default {};

    /** 誕生日のフィールド名 */
    String birthdayFieldName() default "";

    /** 年齢のフィールド名 */
    String ageFieldName() default "";
}
