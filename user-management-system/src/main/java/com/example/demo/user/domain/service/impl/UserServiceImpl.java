package com.example.demo.user.domain.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.user.domain.model.MUser;
import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.repository.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;

    @Override
    public void signup(MUser user) {
        user.setDepartmentId(1); // 部署
        user.setRole("ROLE_GENERAL"); // ロール
        int count = mapper.insertOne(user);
        log.info("登録件数={}件", count);
    }

    /** ユーザー取得 */
    @Override
    public List<MUser> getUsers(MUser user) {
        return mapper.findMany(user);
    }

    @Override
    public MUser getUserOne(String userId) {
        return mapper.findOne(userId);
    }

    @Override
    public void updateUserOne(String userId, String password, String userName) {
        int count = mapper.updateOne(userId, password, userName);
        log.info("更新件数={}", count);
    }

    @Override
    public void deleteUserOne(String userId) {
        int count = mapper.deleteOne(userId);
        log.info("削除件数={}", count);
    }
}
