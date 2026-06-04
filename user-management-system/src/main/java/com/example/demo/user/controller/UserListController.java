package com.example.demo.user.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserListController {

    private final UserService userService;

    /** ユーザー一覧画面を表示 */
    @GetMapping("/list")
    public String getUserList(Model model) {
        // ユーザー一覧取得
        List<MUser> userList = userService.getUsers();
        // Modelに登録
        model.addAttribute("userList", userList);
        // ユーザー一覧画面を表示
        return "user/list";
    }
}
