package com.example.demo.user.domain.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class SalaryKey {
    private String userId;
    private String yearMonth;
}
