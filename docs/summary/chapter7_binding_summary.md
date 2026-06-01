# 第7章まとめ：バインド

## ■ 概要

この章では、Spring Bootにおける「バインド（Binding）」について学習する。

バインドとは：

```text
画面の入力内容を
Javaクラスへ自動的に紐づける仕組み
```

である。

これまでの章では：

```java
@RequestParam(...)
```

を使って、
画面入力を1項目ずつ取得していた。

しかし実務では入力項目が大量になるため、

- Controller肥大化
- 可読性低下
- 修正困難化
- 項目追加時の変更箇所増加

などの問題が起きる。

そこで本章では：

- Formクラス
- バインド
- Thymeleafとの連携
- エラーメッセージ表示
- BindingResult

などを利用し、

```text
画面入力をJavaオブジェクトとして扱う方法
```

を学習する。

---

# ■ 7-1 バインドの実装

## ▽ サンプルアプリケーションの作成

---

## 1. ファイルなどの作成

フォームクラス用として：

```text
com.example.demo.user.form
```

パッケージを作成する。

また：

```text
SignupForm.java
```

を作成する。

---

## 2. フォームクラスの作成

```java
package com.example.demo.user.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SignupForm {

    private String userId;

    private String password;

    private String userName;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date birthday;

    private Integer age;

    private Integer gender;
}
```

画面入力内容を：

```text
SignupFormオブジェクト
```

へまとめて格納できるようにする。

---

## ◆ バインドのイメージ

```text
┌──────────────────────────────┐
│         ユーザー登録          │
│                              │
│ ユーザーID                    │
│ ┌────────────────────────┐   │
│ │ tom                    │   │ ← private String userId;
│ └────────────────────────┘   │
│                              │
│ パスワード                    │
│ ┌────────────────────────┐   │
│ │ password               │   │ ← private String password;
│ └────────────────────────┘   │
│                              │
│ ユーザー名                    │
│ ┌────────────────────────┐   │
│ │ Tom                    │   │ ← private String userName;
│ └────────────────────────┘   │
│                              │
│ 誕生日                        │
│ ┌────────────────────────┐   │
│ │ 1986/11/05             │   │ ← private Date birthday;
│ └────────────────────────┘   │
│                              │
│ 年齢                          │
│ ┌────────────────────────┐   │
│ │ 36                     │   │ ← private Integer age;
│ └────────────────────────┘   │
│                              │
│ 性別                          │
│ ○ 男性   ○ 女性              │ ← private Integer gender;
│                              │
└──────────────────────────────┘
```

画面の各入力欄は、
SignupFormクラス内の各フィールドへ対応している。

Springは：

- フォームのname属性
- Javaフィールド名

を対応付けることで、
入力値を自動的にJavaオブジェクトへ格納する。

例えば：

```text
name="userId"
```

という入力欄なら、

```java
private String userId;
```

へ値が代入される。

これが：

```text
バインド（Binding）
```

である。

---

## ◆ @DateTimeFormat

```java
@DateTimeFormat(pattern = "yyyy/MM/dd")
```

を利用し、

```text
String → Date型
```

変換ルールを指定する。

例えば：

```text
1986/11/05
```

ならDate型へ変換可能だが、

```text
19861105
```

など異なる形式では、
バインドエラーになる。

---

## 3. コントローラーと画面の修正

### ◆ SignupController

```java
@Controller
@RequestMapping("/user")
@Slf4j
public class SignupController {

    private final UserApplicationService userApplicationService;

    @Autowired
    public SignupController(
            UserApplicationService userApplicationService) {

        this.userApplicationService
            = userApplicationService;
    }

    /** ユーザー登録画面を表示 */
    @GetMapping("/signup")
    public String getSignup(
            Model model,
            @ModelAttribute SignupForm form) {

        // 性別を取得
        Map<String, Integer> genderMap
            = userApplicationService.getGenderMap();

        model.addAttribute(
            "genderMap",
            genderMap
        );

        return "user/signup";
    }

    /** ユーザー登録処理 */
    @PostMapping("/signup")
    public String postSignup(
            @ModelAttribute SignupForm form) {

        log.info(form.toString());

        return "redirect:/login";
    }
}
```

---

## ◆ GET時の流れ

```text
┌───────────────┐
│ クライアント    │
└──────┬────────┘
       │
       │ ① リクエスト（GET）
       │    http://localhost:8080/user/signup
       ▼
┌────────────────────────────────────────────────────┐
│ サーバー（Spring Boot）                              │
│                                                    │
│  @RequestMapping("/user")                          │
│  SignupController                                  │
│  ┌────────────────────────────────────────────┐    │
│  │ @GetMapping("/signup") のメソッド            │    │
│  │ SignupForm インスタンスを生成して              │    │
│  │ Model に登録                                │    │
│  └────────────────────────────────────────────┘    │
│                                                    │
│                     │                              │
│                     │ ② HTML生成(SignupForm)       │
│                     ▼                              │
│  ┌────────────────────────────────────────────┐    │
│  │ ユーザー登録画面のHTMLテンプレート              │    │
│  │                                            │    │
│  │ signup.html                                │    │
│  └────────────────────────────────────────────┘    │
│      │                                             │
└──────┬─────────────────────────────────────────────┘
       │
       │ ③ レスポンス
       ▼
┌───────────────┐
│ signup.html   │
└───────────────┘
```

---

## ◆ signup.html

```html
<form id="signup-form"
      method="post"
      action="/user/signup"
      class="form-signup mt-5"
      th:object="${signupForm}">
```

```html
<input type="text"
       id="userId"
       class="form-control"
       th:field="*{userId}">
```

---

## ◆ th:object と th:field

```html
th:object="${signupForm}"
```

は：

```text
フォーム全体の基準オブジェクト
```

を指定する。

その内部では：

```html
th:field="*{userId}"
```

のように：

```text
*{}
```

を利用して、
キー名を省略できる。

内部的には：

```html
<input th:field="*{userId}">
```

↓

```text
signupForm.userId
```

として扱われる。

---

## ◆ HTMLテンプレート

signup.html は：

```text
未完成HTML
```

である。

Spring + Thymeleaf が：

- Model内のsignupForm
- th:object
- th:field

などを利用し、
ブラウザへ返す完成HTMLを生成する。

例えば：

```html
<input th:field="*{userId}">
```

は最終的に：

```html
<input type="text"
       id="userId"
       name="userId"
       value="">
```

のような完成HTMLへ変換される。

---

## ◆ POST時の流れ

```text
┌───────────────┐
│ クライアント    │
└──────┬────────┘
       │
       │ ① リクエスト（POST）
       │    http://localhost:8080/user/signup
       ▼
                 HTTPリクエスト
                ┌──────────┐
                │SignupForm│
                └──────────┘

      SignupForm の値が
      Controller に送られる

┌────────────────────────────────────────────────────┐
│ サーバー（Spring Boot）                              │
│                                                    │
│  @RequestMapping("/user")                          │
│  SignupController                                  │
│  ┌────────────────────────────────────────────┐    │
│  │ @PostMapping("/signup") のメソッド           │    │
│  └────────────────────────────────────────────┘    │
│                         │                          │
│                         │ ② リダイレクト             │
│                         ▼                          │
│  LoginController                                   │
│  ┌────────────────────────────────────────────┐    │
│  │ @GetMapping("/login") のメソッド             │    │
│  └────────────────────────────────────────────┘    │
│                                                    │
└──────┬─────────────────────────────────────────────┘
       │
       │ ③ レスポンス
       ▼
┌───────────────┐
│ login.html    │
└───────────────┘
```

---

## ◆ バインド

POST時には：

```text
画面入力値
↓
SignupForm
```

への自動代入が行われる。

例えば：

```text
userId = tom
```

なら、
Spring内部では：

```java
form.setUserId("tom");
```

のような処理が実行される。

---

## ◆ PRGパターン

```java
return "redirect:/login";
```

を利用し、

```text
POST
↓
Redirect
↓
GET
```

という流れを作る。

これにより：

- F5再送信
- 二重登録

などを防止できる。

---

# ■ 7-2 エラーメッセージの表示

## ▽ サンプルアプリケーションの作成

---

## 1. ファイルなどの作成

```text
src/main/resources
├── messages.properties
└── ValidationMessages.properties
```

を作成する。

---

## 2. メッセージファイルの分割設定

### ◆ application.yml

```yml
spring:
  messages:
    basename:
      messages,
      ValidationMessages
```

これにより、
複数のプロパティファイルを読み込める。

---

## 3. エラーメッセージの編集

### ◆ ValidationMessages.properties

```properties
typeMismatch.signupForm.age=
数値を入力してください

typeMismatch.signupForm.birthday=
yyyy/MM/dd形式で入力してください
```

バインド失敗時の
エラーメッセージを定義する。

---

## ◆ バインドエラー例

例えば：

```text
19950815
```

のように、
誕生日を異なる形式で入力すると：

```text
String → Date型
```

変換に失敗する。

---

## ◆ エラー例

```text
Failed to convert from type [java.lang.String]
to type [java.util.Date]
```

これは：

```text
文字列 → Date型変換失敗
```

を意味する。

---

## 4. コントローラーと画面の修正

### ◆ SignupController

```java
@PostMapping("/signup")
public String postSignup(
        Model model,
        @ModelAttribute SignupForm form,
        BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

        return getSignup(model, form);
    }

    log.info(form.toString());

    return "redirect:/login";
}
```

BindingResultを利用すると、
バインドエラーの有無を確認できる。

エラー時には：

```text
Whitelabel Error Page
```

へ遷移せず、
ユーザー登録画面へ戻す。

---

## ◆ signup.html

```html
<input type="text"
       th:field="*{age}"
       th:errorclass="is-invalid">
```

```html
<div class="invalid-feedback"
     th:errors="*{age}">
</div>
```

---

## ◆ エラー表示イメージ

```text
┌────────────────────┐
│ abc                │ ← 赤枠
└────────────────────┘
数値を入力してください
```

---

# ■ 最終まとめ

第7章では：

- Formクラス
- バインド
- @ModelAttribute
- th:object
- th:field
- @DateTimeFormat
- BindingResult
- th:errorclass
- th:errors

などを利用し、

```text
画面入力をJavaオブジェクトとして扱う方法
```

と、

```text
バインド失敗時の
エラーメッセージ表示
```

について学習した。
