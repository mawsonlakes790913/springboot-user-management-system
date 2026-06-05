package com.example.demo.hello;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Sample {

    @Id
    private String id;
    private String str;
}
