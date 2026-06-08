package com.example.demo.user.domain.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class LoginUser extends User {

    /** 画面に表示するユーザー名 */
    private String displayUserName;

    /** コンストラクタ */
    public LoginUser(String username, String password,
            Collection<? extends GrantedAuthority> authorities, String displayUserName) {
        super(username, password, authorities);
        this.displayUserName = displayUserName;
    }
}
