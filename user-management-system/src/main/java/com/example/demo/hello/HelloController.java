package com.example.demo.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.di.SampleComponent;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HelloController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    @Qualifier("SampleComponent1")
    private SampleComponent sampleComponent;

    @GetMapping("/hello")
    public String getHello() {
        log.info(sampleComponent.getStr());
        // hello.htmlに画面遷移
        return "hello";
    }

    @PostMapping("/hello")
    public String postHello(@RequestParam("text1") String text1, Model model) {
        // 画面から受け取った文字列をModelに登録
        model.addAttribute("response", text1);
        // response.htmlに画面遷移
        return "hello/response";
    }

    @GetMapping("/hello/db")
    public String getSample(@RequestParam("id") String id, Model model) {
        // 1件検索
        Sample sample = sampleService.getSample(id);
        // 検索結果をModelに登録
        model.addAttribute("sample", sample);
        // db.htmlに画面遷移
        return "hello/db";
    }
}
