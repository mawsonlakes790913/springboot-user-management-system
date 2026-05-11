# 第6章まとめ：画面作成

## ■ 概要

この章では、Spring Bootを使った画面作成について学習する。

主に：

- Bootstrapの導入
- WebJarsによるライブラリ管理
- ログイン画面作成
- ユーザー登録画面作成
- Thymeleafによる画面表示
- メッセージプロパティ化

などを扱う。

また、

- CSSファイル
- JavaScriptファイル
- HTMLテンプレート
- コントローラー
- プロパティファイル

など、Webアプリ特有のファイル構成についても扱う。

---

# ■ 6-1 ライブラリの使用

## ◆ WebJars導入

pom.xmlへ：

- bootstrap
- webjars-locator

を追加し、
BootstrapをMaven依存関係として管理できるようにする。

```xml
<!-- webjars-bootstrap -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.3.3</version>
</dependency>

<!-- webjars-locator -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator</artifactId>
    <version>0.52</version>
</dependency>
```

pom.xml保存時に、
Mavenがライブラリを自動ダウンロードする。

WebJars Locatorを利用することで、
HTML側ではバージョン番号を書かずにBootstrapを読み込めるようになる。

---

## ◆ ファイル構成

画面作成用として以下の構成を作成する。

```text
src/main/resources
├── static
│   └── css
│       ├── login.css
│       └── signup.css
│
└── templates
    ├── login
    │   └── login.html
    │
    └── user
        └── signup.html
```

また、

- LoginController
- SignupController
- UserApplicationService

などのJavaクラスも作成する。

static配下には：

- CSS
- JavaScript
- 画像

などの静的ファイルを配置する。

templates配下には、
Thymeleafで使用するHTMLテンプレートを配置する。

---

# ■ ログイン画面作成

## ◆ LoginController

```java
@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin() {

        return "login/login";
    }
}
```

/loginへアクセスした際に、
login/login.htmlを表示するコントローラーを作成する。

---

## ◆ login.html

ログイン画面を作成する。

主に：

- Bootstrap読込
- 独自CSS読込
- ログインフォーム
- ログインボタン
- 新規登録リンク

などを実装する。

```html
<link rel="stylesheet"
      th:href="@{/css/login.css}">

<link rel="stylesheet"
      th:href="@{/webjars/bootstrap/css/bootstrap.min.css}">
```

```html
<form method="post"
      th:action="@{/login}"
      class="form-login">
```

th:hrefを利用して：

- static/css/login.css
- WebJarsのBootstrap

を読み込む。

また、

```html
<script ... defer></script>
```

を利用し、
HTML読込とJavaScript読込を並行実行する。

[画像]

---

## ◆ login.css

ログインフォーム用CSSを作成する。

```css
.form-login {
  width: 100%;
  max-width: 350px;
  margin: auto;
}
```

フォームの横幅制限と中央寄せを行い、
Bootstrapだけでは不足する細かなレイアウトを調整する。

---

# ■ ユーザー登録画面作成

## ◆ UserApplicationService

性別ラジオボタン表示用Mapを作成する。

```java
public Map<String, Integer> getGenderMap() {

    Map<String, Integer> genderMap
        = new LinkedHashMap<>();

    genderMap.put("男性", 1);
    genderMap.put("女性", 2);

    return genderMap;
}
```

表示名と内部値をセットで管理する。

---

## ◆ SignupController

ユーザー登録画面表示と、
ログイン画面へのリダイレクト処理を実装する。

```java
@Controller
@RequestMapping("/user")
public class SignupController {
```

```java
@GetMapping("/signup")
```

```java
@PostMapping("/signup")
```

```java
return "redirect:/login";
```

@RequestMapping("/user")を利用することで、
URLの共通プレフィックスを設定する。

また、

```java
model.addAttribute(
    "genderMap",
    genderMap
);
```

を利用し、
HTML側へ性別Mapを渡す。

---

## ◆ signup.html

ユーザー登録画面を作成する。

主に：

- ユーザーID
- パスワード
- ユーザー名
- 誕生日
- 年齢
- 性別ラジオボタン

などを実装する。

また、

```html
<div th:each="item : ${genderMap}">
```

を利用し、
Mapからラジオボタンを動的生成する。

[画像]

---

## ◆ signup.css

ユーザー登録画面用CSSを作成する。

```css
.form-signup {
  width: 100%;
  max-width: 350px;
  margin: auto;
}
```

ログイン画面と同様に、
フォームの横幅制限と中央寄せを行う。

---

## ◆ PRGパターン

ユーザー登録後：

```java
return "redirect:/login";
```

を利用し、
ログイン画面へリダイレクトする構成を作る。

これにより：

- F5更新時のPOST再送信
- 二重登録

などを防止する。

[画像]

---

# ■ 6-2 メッセージプロパティ

## ◆ messages.properties作成

```text
src/main/resources/messages.properties
```

を作成する。

Spring Bootでは、
messages.propertiesがメッセージ管理用のデフォルトファイルとして利用される。

---

## ◆ メッセージ定義

messages.propertiesへ：

```properties
user.signup.title=ユーザー登録
userId=ユーザーID
password=パスワード
```

などの画面表示文字列を定義する。

これにより、
画面文言を一括管理できるようになる。

---

## ◆ signup.html修正

画面へ直接書いていた文字列を：

```html
th:text="#{userId}"
```

のように、
messages.propertiesから取得する形へ変更する。

これにより：

- 表記統一
- メッセージ管理
- 多言語化対応

などがしやすくなる。

---

## ◆ MessageSource利用

Java側でもMessageSourceを利用して、
messages.propertiesから値取得を行う。

```java
private final MessageSource messageSource;
```

```java
String male
    = messageSource.getMessage(
        "male",
        null,
        null
    );
```

これにより、
Javaコード側でもプロパティファイルの値を利用できるようになる。

また、

- 埋め込みパラメータ
- Locale

を利用することで、
国際化対応も可能になる。

---

# ■ 最終まとめ

第6章では、

- Bootstrap導入
- WebJars利用
- ログイン画面作成
- ユーザー登録画面作成
- Thymeleafによる画面制御
- MessageSource利用
- messages.properties利用

など、

Spring Bootにおける画面作成の基礎を扱った。

また、

- HTML
- CSS
- JavaScript
- Controller
- Service
- プロパティファイル

など、
Webアプリケーションを構成する基本的なファイル群についても学習した。
