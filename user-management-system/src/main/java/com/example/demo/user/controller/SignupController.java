package com.example.demo.user.controller;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.user.application.UserApplicationService;
import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.form.SignupForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class SignupController {

    private final UserApplicationService userApplicationService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    /** ユーザー登録画面を表示 */
    @GetMapping("/signup")
    public String getSignup(Model model, @ModelAttribute SignupForm form) {
        // 性別を取得
        Map<String, Integer> genderMap = userApplicationService.getGenderMap();
        model.addAttribute("genderMap", genderMap);
        // ユーザー登録画面に遷移
        return "user/signup";
    }

    /** ユーザー登録処理 */
    @PostMapping("/signup")
    public String postSignup(Model model, @ModelAttribute @Validated SignupForm form,
            BindingResult bindingResult) {
        // 入力チェック結果
        if (bindingResult.hasErrors()) {
            // NG：ユーザー登録画面に戻ります
            return getSignup(model, form);
        }

        log.info(form.toString());
        // formをMUserクラスに変換
        MUser user = modelMapper.map(form, MUser.class);
        // ユーザー登録
        userService.signup(user);

        // ログイン画面にリダイレクト
        return "redirect:/login";
    }
}
