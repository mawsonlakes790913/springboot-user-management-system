package com.example.demo.user.form;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.validator.LengthMin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupForm {
    @NotBlank
    @Email
    @LengthMin(min = 5)
    private String userId;

    @NotEmpty
    @Length(min = 4, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;

    @NotBlank
    @LengthMin(min = 3)
    private String userName;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @NotNull
    private Date birthday;

    @Min(20)
    @Max(100)
    private Integer age;

    @NotNull
    private Integer gender;
}
