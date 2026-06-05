package com.example.demo.user.controller;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.form.UserDetailForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserDetailController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    /** ユーザー詳細画面を表示 */
    @GetMapping("/detail/{userId}")
    public String getUser(UserDetailForm form, Model model,
            @PathVariable("userId") String userId) {
        // ユーザーを1件取得
        MUser user = userService.getUserOne(userId);
        user.setPassword(null);
        // MUserをformに変換
        form = modelMapper.map(user, UserDetailForm.class);
        // Modelに登録
        model.addAttribute("userDetailForm", form);
        // ユーザー詳細画面を表示
        return "user/detail";
    }

    /** ユーザー更新処理 */
    @PostMapping(value = "/detail", params = "update")
    public String updateUser(UserDetailForm form, Model model) {
        // ユーザーを更新
        userService.updateUserOne(form.getUserId(),
                form.getPassword(),
                form.getUserName());
        // ユーザー一覧画面にリダイレクト
        return "redirect:/user/list";
    }

    /** ユーザー削除処理 */
    @PostMapping(value = "/detail", params = "delete")
    public String deleteUser(UserDetailForm form, Model model) {
        try {
            // ユーザーを削除
            userService.deleteUserOne(form.getUserId());
        } catch (Exception e) {
            log.error("ユーザー削除でエラー", e);
        }
        // ユーザー一覧画面にリダイレクト
        return "redirect:/user/list";
    }
}
