package com.example.demo.di;

import org.springframework.stereotype.Component;

@Component("SampleComponent1")
public class SampleComponent1 implements SampleComponent {
    /** getStrの戻り値 */
    private String str = "SampleComponent1";

    @Override
    public String getStr() {
        return this.str;
    }
}
