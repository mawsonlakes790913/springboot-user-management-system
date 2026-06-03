package com.example.demo.user.application;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final MessageSource messageSource;

    /** 性別のMapを生成する */
    public Map<String, Integer> getGenderMap() {
        Map<String, Integer> genderMap = new LinkedHashMap<>();
        String male = messageSource.getMessage("male", null, null);
        String female = messageSource.getMessage("female", null, null);
        genderMap.put(male, 1);
        genderMap.put(female, 2);
        return genderMap;
    }


    /** 性別のMapを生成する
    public Map<String, Integer> getGenderMap() {
        Map<String, Integer> genderMap = new LinkedHashMap<>();
        genderMap.put("男性", 1);
        genderMap.put("女性", 2);
        return genderMap;
    } */
}
