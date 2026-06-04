package com.example.demo.user.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.user.domain.model.MUser;

@Mapper
public interface UserMapper {
    /** ユーザー登録 */
    public int insertOne(MUser user);
}
