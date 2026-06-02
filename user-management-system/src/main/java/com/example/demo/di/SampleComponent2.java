package com.example.demo.di;

import org.springframework.stereotype.Component;

@Component("SampleComponent2")
public class SampleComponent2 implements SampleComponent {
    /** getStrの戻り値 */
    private String str = "SampleComponent2";

    @Override
    public String getStr() {
        return this.str;
    }
}
