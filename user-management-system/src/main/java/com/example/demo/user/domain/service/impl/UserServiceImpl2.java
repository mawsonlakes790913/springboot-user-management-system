package com.example.demo.user.domain.service.impl;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class UserServiceImpl2 implements UserService {

    private final UserRepository repository;

    @Override
    public void signup(MUser user) {
        // 既登録チェック
        boolean isExists = repository.existsById(user.getUserId());
        if (isExists) {
            throw new DuplicateKeyException("既に存在するユーザーです");
        }

        user.setDepartmentId(1); // 部署
        user.setRole("ROLE_GENERAL"); // ロール
        repository.save(user);
    }

    /** ユーザー取得 */
    @Override
    public Page<MUser> getUsers(MUser user, Pageable pageable) {
        // 検索条件
        ExampleMatcher matcher = ExampleMatcher.matchingAll() // and条件
                // userIdの部分一致
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.contains())
                // userNameの部分一致
                .withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase(); // 大文字・小文字を無視

        // ユーザー一覧取得
        Page<MUser> userList = repository.findAll(Example.of(user, matcher), pageable);
        return userList;
    }

    @Override
    public MUser getUserOne(String userId) {
        Optional<MUser> option = repository.findById(userId);
        MUser user = option.orElse(null);
        return user;
    }

    @Transactional
    @Override
    public void updateUserOne(String userId, String password, String userName) {
        int count = repository.updateUser(userId, password, userName);
        log.info("更新件数={}", count);
    }

    @Transactional
    @Override
    public void deleteUserOne(String userId) {
        repository.deleteById(userId);
    }
}
