package com.example.demo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.domain.model.MUser;

public interface UserRepository extends JpaRepository<MUser, String> {

}
