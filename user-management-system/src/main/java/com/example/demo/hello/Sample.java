package com.example.demo.hello;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Sample {

    @Id
    private String id;
    private String str;
}
