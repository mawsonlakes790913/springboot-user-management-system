package com.example.demo.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { LengthMinValidator.class })
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LengthMin {

    /** デフォルトメッセージ */
    String message() default "{length.min.message}";

    /** グループ */
    Class<?>[] groups() default {};

    /** ペイロード */
    Class<? extends Payload>[] payload() default {};

    /** 最低文字数 */
    int min() default 0;
}
