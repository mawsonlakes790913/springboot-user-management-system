package com.example.demo.user.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.user.application.UserApplicationService;
import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.form.SignupForm;

import jakarta.servlet.http.HttpServletRequest;
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

    /** ユーザーID重複の例外処理 */
    @ExceptionHandler(DuplicateKeyException.class)
    public String duplicateKeyExceptionHandler(DuplicateKeyException e, Model model,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // 入力内容の取得
        SignupForm form = generateFormFromRequest(request);
        redirectAttributes.addFlashAttribute("signupForm", form);
        // エラーメッセージ
        String errorMessage = "このユーザーIDは既に使用されています。";
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/user/signup";
    }

    /** その他の例外処理 */
    @ExceptionHandler(Exception.class)
    public String allExceptionHandler(Exception e, Model model,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {

        // 入力内容の取得
        SignupForm form = generateFormFromRequest(request);
        redirectAttributes.addFlashAttribute("signupForm", form);
        // エラーメッセージ
        String errorMessage = "システムエラーが発生しました。";
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/user/signup";
    }

    /** リクエストからSignupFormを生成する */
    private SignupForm generateFormFromRequest(HttpServletRequest request) {
        // リクエストの値をFormにセットする
        SignupForm form = new SignupForm();
        form.setUserId(request.getParameter("userId"));
        form.setPassword(request.getParameter("password"));
        form.setUserName(request.getParameter("userName"));
        form.setAge(Integer.valueOf(request.getParameter("age")));
        form.setGender(Integer.valueOf(request.getParameter("gender")));
        // 日付文字列からDateを生成して、Formにセットする
        String birthdayString = request.getParameter("birthday");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.parse(birthdayString, formatter);
        Date birthday = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        form.setBirthday(birthday);
        return form;
    }
}
