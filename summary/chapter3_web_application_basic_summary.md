# 第3章まとめ：Webアプリケーション開発・はじめの一歩

## ■ 概要
- Spring Bootで実際に動くWebアプリを作成し、MVCモデル（画面・処理・データ分離）の基本を理解する
- Controller / Service / Repository の役割分担を理解する
- HTMLフォームとThymeleafを使ったデータ受け渡しを学ぶ
- H2 Databaseを使ったDB連携を学ぶ

---

## ■ 3-1 シンプルなWebアプリケーション

### ■ 使用コード

#### ● Controller（Java）

    package com.example.demo.hello;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;

    @Controller
    public class HelloController {

        @GetMapping("/hello")
        public String getHello() {
            return "hello";
        }
    }

---

#### ● HTML（hello.html）

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>Hello World</title>
    </head>
    <body>
        <h1>Hello World</h1>
    </body>
    </html>

---

### ■ 何をしているか

- ブラウザで `/hello` にアクセスする
- Controllerの `@GetMapping("/hello")` が実行される
- `return "hello"` により templates/hello.html が返される
- 「Hello World」が表示される

---

### ■ 処理の流れ（GET）

ブラウザ → GET /hello  
↓  
Controller実行  
↓  
return "hello"  
↓  
templates/hello.html  
↓  
ブラウザ表示  

---

## ■ 3-2 画面から別画面に値を渡す

### ■ 使用コード

#### ● HTML（hello.html）

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>Hello World</title>
    </head>
    <body>
        <h1>Hello World</h1>

        <form method="post" action="/hello">
            <input type="text" name="text1" />
            <input type="submit" value="クリック" />
        </form>

    </body>
    </html>

---

#### ● Controller（Java）

    package com.example.demo.hello;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    @Controller
    public class HelloController {

        @GetMapping("/hello")
        public String getHello() {
            return "hello";
        }

        @PostMapping("/hello")
        public String postHello(@RequestParam("text1") String text1, Model model) {

            model.addAttribute("response", text1);

            return "hello/response";
        }
    }

---

#### ● HTML（response.html）

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>Hello Response</title>
    </head>
    <body>
        <h1>Hello Response</h1>
        <p th:text="${response}"></p>
    </body>
    </html>

---

### ■ 何をしているか

- 入力欄に文字を入力して送信する
- POST /hello が送られる
- @PostMapping が実行される
- 入力値が text1 に入る
- Modelに response として保存する
- response.html に遷移する
- Thymeleafが値を埋め込む
- 入力した文字が画面に表示される

---

### ■ 処理の流れ（POST）

GET /hello  
↓  
画面表示  
↓  
入力（例：Spring）  
↓  
POST /hello  
↓  
@PostMapping 実行  
↓  
@RequestParam → text1 = Spring  
↓  
Model → response = Spring  
↓  
return "hello/response"  
↓  
response.html  
↓  
th:text により表示  
↓  
Spring 表示  

---

## ■ 3-3 データベースから値を取得する

### ■ 概要

- 指定したIDを使ってデータベースからデータを取得する
- Service / Repository / Database を使ったMVC構成を学ぶ
- H2 Databaseを使ったDB連携を行う

---

## ■ 使用ファイル

| ファイル | 役割 |
|---|---|
| Sample.java | DBの1レコードを表すクラス |
| SampleRepository.java | DBアクセス |
| SampleService.java | ビジネスロジック |
| db.html | 検索結果画面 |
| application.yml | Spring Boot設定 |
| schema.sql | テーブル作成 |
| data.sql | 初期データ投入 |

---

## ■ application.yml

### ■ 使用コード

    spring:
      datasource:
        url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
        driver-class-name: org.h2.Driver
        username: sa
        password: password

      sql:
        init:
          encoding: UTF-8
          mode: embedded
          schema-locations: classpath:schema.sql
          data-locations: classpath:data.sql

      h2:
        console:
          enabled: true

---

### ■ 何をしているか

- H2 Database接続設定
- SQL初期化設定
- H2コンソール有効化

---

## ■ schema.sql

### ■ 使用コード

    CREATE TABLE IF NOT EXISTS sample (
      id VARCHAR(50) PRIMARY KEY,
      str VARCHAR(50)
    );

---

### ■ 何をしているか

- sampleテーブル作成
- id → 主キー
- str → 文字列カラム

---

## ■ data.sql

### ■ 使用コード

    INSERT INTO sample (id, str)
    VALUES('1', 'Hello');

---

### ■ 何をしているか

- 初期データ投入
- ID=1, str=Hello を登録

---

## ■ Sample.java

### ■ 使用コード

    package com.example.demo.hello;

    import org.springframework.data.annotation.Id;
    import lombok.Data;

    @Data
    public class Sample {

        @Id
        private String id;

        private String str;
    }

---

### ■ 何をしているか

- DBの1行を表すクラス
- id → 主キー
- str → 通常カラム
- @Data → getter/setter自動生成

---

## ■ SampleRepository.java

### ■ 使用コード

    package com.example.demo.hello;

    import org.springframework.data.repository.CrudRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface SampleRepository
        extends CrudRepository<Sample, String> {

    }

---

### ■ 何をしているか

- DBアクセスを行う
- CrudRepositoryを継承
- CRUD操作メソッドを使用可能にする

---

## ■ SampleService.java

### ■ 使用コード

    package com.example.demo.hello;

    import java.util.Optional;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    @Service
    public class SampleService {

        @Autowired
        private SampleRepository sampleRepository;

        public Sample getSample(String id) {

            Optional<Sample> optionalSample
                = sampleRepository.findById(id);

            Sample sample = optionalSample.get();

            return sample;
        }
    }

---

### ■ 何をしているか

- IDを使ってDB検索
- Repositoryに検索依頼
- DBから取得したデータを返す

---

### ■ 処理の流れ

    public Sample getSample(String id)

- idを受け取る
- findById(id)でDB検索
- OptionalからSample取得
- 呼び出し元へ返す

---

## ■ HelloController.java

### ■ 使用コード

    package com.example.demo.hello;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    @Controller
    public class HelloController {

        @Autowired
        private SampleService sampleService;

        @GetMapping("/hello")
        public String getHello() {
            return "hello";
        }

        @PostMapping("/hello")
        public String postHello(
                @RequestParam("text1") String text1,
                Model model) {

            model.addAttribute("response", text1);

            return "hello/response";
        }

        @GetMapping("/hello/db")
        public String getSample(
                @RequestParam("id") String id,
                Model model) {

            Sample sample = sampleService.getSample(id);

            model.addAttribute("sample", sample);

            return "hello/db";
        }
    }

---

### ■ 何をしているか

- ID入力値を受け取る
- ServiceでDB検索
- Modelにデータ登録
- db.htmlへ画面遷移

---

## ■ db.html

### ■ 使用コード

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
      <meta charset="UTF-8">
      <title>ResponseSample</title>
    </head>
    <body>

      <h1>ResponseSample</h1>

      <table>
        <tr>
          <td>ID:</td>
          <td th:text="${sample.id}"></td>
        </tr>

        <tr>
          <td>文字:</td>
          <td th:text="${sample.str}"></td>
        </tr>
      </table>

    </body>
    </html>

---

### ■ 何をしているか

- Modelから受け取ったデータ表示
- sample.id → ID表示
- sample.str → 文字表示

---

## ■ hello.html（最終版）

### ■ 使用コード

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
      <meta charset="UTF-8">
      <title>Hello World</title>
    </head>
    <body>

      <h1>Hello World</h1>

      <form method="post" action="/hello">
        入力:
        <input type="text" name="text1">
        <input type="submit" value="クリック">
      </form>

      <br/>

      <form method="get" action="/hello/db">
        ID:
        <input type="text" name="id">
        <input type="submit" value="クリック">
      </form>

    </body>
    </html>

---

### ■ 何をしているか

- POSTフォーム → 入力値表示
- GETフォーム → DB検索

---

## ■ GETとPOSTの違い

| 項目 | GET | POST |
|---|---|---|
| 用途 | データ取得 | データ送信 |
| データ位置 | URL | Body |

---

## ■ 処理の流れ（DB検索）

GET /hello/db?id=1  
↓  
@GetMapping("/hello/db")  
↓  
@RequestParam → id = 1  
↓  
Service実行  
↓  
Repository.findById(id)  
↓  
DB検索  
↓  
Sample取得  
↓  
Modelに登録  
↓  
return "hello/db"  
↓  
db.html  
↓  
Thymeleaf表示  

---

## ■ H2コンソール

ブラウザから：

    http://localhost:8080/h2-console

にアクセスするとH2 Databaseを操作できる

---

## ■ 最終まとめ

- Controller → リクエスト受付
- Service → ビジネスロジック
- Repository → DB操作
- Model → データ受け渡し
- Thymeleaf → HTML表示
- H2 → 学習用DB

画面入力  
↓  
Controller  
↓  
Service  
↓  
Repository  
↓  
DB  
↓  
Model  
↓  
HTML表示
