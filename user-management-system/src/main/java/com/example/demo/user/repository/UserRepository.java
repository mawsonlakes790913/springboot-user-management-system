package com.example.demo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.user.domain.model.MUser;

public interface UserRepository extends JpaRepository<MUser, String> {

    /** ユーザー更新 */
    @Query("update MUser"
            + " set"
            + " password = :password"
            + " , userName = :userName"
            + " where"
            + " userId = :userId")
    @Modifying
    public Integer updateUser(@Param("userId") String userId,
            @Param("password") String password,
            @Param("userName") String userName);
}
