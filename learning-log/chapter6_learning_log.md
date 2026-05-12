# ■ 第6章 画面作成

# ■ この章の目的

この章では、

- ログイン画面
- ユーザー登録画面

を作成しながら、

- Bootstrap
- WebJars
- Thymeleaf
- messages.properties
- MessageSource
- リダイレクト

などを利用した、

```text
Spring Bootにおける画面作成
```

を学習した。

これまでの章では、

- Controller
- Model
- Repository
- DB
- MVC

など、

```text
Spring Boot内部の処理
```

を中心に学んでいた。

しかし今回は、

```text
実際にユーザーが見る画面
```

を作成するため、

- HTML
- CSS
- JavaScript
- Bootstrap

など、

```text
フロントエンド側の知識
```

も必要になった。

そのため今回は、

```text
Spring Boot + HTML/CSS
```

を同時に学ぶ章だった。

---

# ■ この章の流れ

この章では大きく分けて、

1. WebJarsでBootstrap導入
2. ログイン画面作成
3. ユーザー登録画面作成
4. messages.properties導入
5. MessageSourceでメッセージ取得
6. リダイレクト処理

という流れで進んだ。

---

# ■ なぜWebJarsを扱うのか？

Web画面では通常、

- CSS
- JavaScript

を利用して画面デザインや動きを制御する。

例えば：

- Bootstrap
- jQuery

などが有名。

しかし通常は：

```html
<link rel="stylesheet" href="bootstrap.css">
<script src="jquery.js"></script>
```

のように、

- CSSファイル
- JavaScriptファイル

を自分でダウンロードし、

```text
src/main/resources/static
```

へ配置する必要がある。

しかし：

- 手動ダウンロード
- バージョン管理
- 差し替え

はかなり面倒。

これは、

```text
昔のJava開発で
jarを手動管理していた時代
```

に近い。

Javaでは：

```text
jar
↓
Maven管理
```

へ移行した。

そこで、

```text
CSS / JavaScriptライブラリも
Maven管理したい
```

という発想から、

```text
WebJars
```

が使われる。

つまり：

```text
「フロントエンドライブラリ版Maven」
```

のようなもの。

---

# ■ なぜプロパティファイルを扱うのか？

画面には、

- ログイン
- ユーザー登録
- エラーメッセージ

など大量の文字列が表示される。

これらを：

```java
String message = "ログインしてください";
```

のようにJavaコードへ直接書くと、

- 修正時に再ビルド必要
- 表記ゆれ
- 多言語対応困難

などの問題がある。

そのため：

```properties
login.message=ログインしてください
```

のように、

```text
文字列だけ外部ファイル化
```

して管理する。

Spring Bootでは通常：

```text
src/main/resources/messages.properties
```

へ配置する。

そして：

- Thymeleaf
- MessageSource

を利用して、

```text
HTMLやJavaから
messages.propertiesを読む
```

という流れになっている。

---

# ■ この章に取り組む前に必要な事前知識

この章では、

```text
Spring Boot
```

だけでなく、

- HTML
- CSS
- Bootstrap
- URL
- HTTP

なども登場する。

そのため、まずは最低限の用語を整理した。

---

# ■ ◆ Bootstrap

CSSフレームワーク。

- ボタン
- 入力欄
- レイアウト
- 余白

などを簡単に整えられる。

今回は：

- btn
- btn-primary
- form-control
- mt-3

など大量のBootstrap classが登場した。

Bootstrapとは、

```text
便利CSS class集
```

のようなもの。

---

# ■ ◆ class属性

HTML部品へ名前を付けるための属性。

主にCSS適用時に使う。

例えば：

```html
class="form-login"
```

。

CSS側：

```css
.form-login {
    width: 300px;
}
```

。

これにより：

```text
form-login classを持つ要素
```

へCSS適用できる。

また、

```html
class="form-group mt-2"
```

のように、

```text
スペース区切りで複数class指定
```

も可能。

---

# ■ ◆ Bootstrap class

Bootstrapがあらかじめ用意しているclass。

例えば：

| class | 役割 |
|---|---|
| btn | ボタン化 |
| btn-primary | 青ボタン |
| mt-3 | 上余白 |
| me-3 | 右余白 |
| text-center | 中央寄せ |
| form-control | 入力欄デザイン |

など。

---

# ■ ◆ HTMLタグ整理

## ◆ formタグ

入力内容送信用エリア。

例えば：

```html
<form method="post">
```

なら、

```text
フォーム送信
```

を行う。

---

## ◆ inputタグ

ユーザー入力部品。

例えば：

```html
<input type="text">
```

なら文字入力欄。

```html
<input type="radio">
```

ならラジオボタン。

---

## ◆ labelタグ

入力欄説明文字。

例えば：

```html
<label>ユーザーID</label>
```

。

---

## ◆ divタグ

画面部品をまとめるためのタグ。

レイアウト整理によく使う。

---

## ◆ aタグ

リンク作成タグ。

例えば：

```html
<a href="/login">
```

なら：

```text
/login へのリンク
```

を作る。

---

# ■ ◆ href と th:href

## href

通常HTMLのリンク指定。

---

## th:href

Thymeleaf版href。

Spring / Thymeleaf側が：

- URL生成
- コンテキストパス調整

などを自動処理してくれる。

---

# ■ ◆ Maven

Javaのライブラリ管理 + ビルドツール。

昔は：

```text
jar手動管理
```

だったが、

Mavenにより：

- DL
- バージョン管理
- 依存解決

を自動化できる。

---

# ■ ◆ dependencies

pom.xmlで：

```text
このプロジェクトで使うライブラリ一覧
```

を書く場所。

---

# ■ ◆ WebJars-Locator

WebJars利用時に：

```text
Bootstrapのバージョン番号省略
```

を可能にする補助ライブラリ。

---

# ■ ◆ Thymeleaf

Spring Bootでよく使われるテンプレートエンジン。

HTML内で：

- Modelデータ表示
- URL生成
- ループ
- 条件分岐

などを行える。

---

# ■ ◆ MessageSource

Java側から：

```text
messages.properties
```

を読むための仕組み。

---

# ■ ◆ 埋め込みパラメータ {}

```properties
hello={}さん
```

の：

```text
{}
```

は、

```text
あとで値を入れる場所
```

。

---

# ■ 6-1 ライブラリの使用

この節では、

- WebJars
- Bootstrap
- Thymeleaf

を利用しながら、

- ログイン画面
- ユーザー登録画面

を作成した。

---

# ■ ファイル構成

今回は、

- login.controller
- user.application
- user.controller

など、

```text
機能・役割ごと
```

にパッケージ分割した。

---

# ■ 【疑問】login.controller と user を分ける理由

機能単位整理のため。

- login.controller
    - ログイン機能

- user
    - ユーザー機能

という構成。

---

# ■ 【疑問】user内でapplicationとcontrollerを分ける理由

役割分離のため。

- controller
    - HTTP受付

- application
    - 業務処理

を担当している。

---

# ■ 【疑問】UserApplicationService の業務処理とは？

今回は：

```text
性別Map生成
```

のみ。

実務では：

- DB保存
- 重複チェック
- メール送信

などが入る。

---

# ■ ログイン画面作成

Controller：

```java
@GetMapping("/login")
public String getLogin() {
    return "login/login";
}
```

。

これは：

```text
templates/login/login.html
```

表示という意味。

---

# ■ WebJarsによるBootstrap読込

```html
<link rel="stylesheet"
      th:href="@{/webjars/bootstrap/css/bootstrap.min.css}">
```

。

これは：

```text
MavenがDLしたBootstrap CSS読込
```

という意味。

---

# ■ ◆ WebJars内部構造

Bootstrapは内部的には：

```text
bootstrap-5.3.3.jar
└ META-INF
  └ resources
```

へ格納される。

Spring Bootでは：

```text
META-INF/resources
```

を公開フォルダ扱いするため、

```html
/webjars/bootstrap/...
```

だけ書けばアクセスできる。

---

# ■ ◆ th:each

Thymeleafで：

```text
Collectionをループ
```

するための属性。

Javaの：

```java
for (item : collection)
```

に近い。

今回のコード：

```html
<div th:each="item : ${genderMap}">
```

。

これは：

```text
genderMapを1個ずつ取り出し、
itemへ代入しながらループ
```

している。

---

今回のgenderMap内容：

```text
{
  男性 = 1,
  女性 = 2
}
```

。

つまり：

## 1回目

```text
item.key   = 男性
item.value = 1
```

。

---

## 2回目

```text
item.key   = 女性
item.value = 2
```

。

その結果、

```html
<input type="radio">
<label>男性</label>

<input type="radio">
<label>女性</label>
```

が自動生成される。

つまり：

```text
Map内容を利用して
ラジオボタンを自動生成
```

している。

---

# ■ ◆ MessageSource

Java側から：

```text
messages.properties
```

を読むための仕組み。

例えば：

```java
messageSource.getMessage(
    "male",
    null,
    null
);
```

。

これは：

```properties
male=男性
```

を取得している。

---

## getMessage() の引数

```java
getMessage(
    キー名,
    埋め込みパラメータ,
    ロケール
)
```

。

---

## 第一引数

```java
"male"
```

。

messages.propertiesのキー名。

---

## 第二引数

```java
null
```

。

埋め込みパラメータ。

今回は：

```properties
male=男性
```

のように：

```text
{}を使っていない
```

ため不要。

---

## 第三引数

```java
null
```

。

ロケール（国・言語設定）。

今回は未指定。

---

# ■ ◆ 埋め込みパラメータ {}

messages.propertiesでは：

```properties
hello={}さん、こんにちは。今日は{}ですね。
```

のように：

```text
{}
```

を書くことで、

```text
あとから値を埋め込む場所
```

を作れる。

---

例えばJava側：

```java
String[] strArray = {
    "佐藤",
    "晴れ"
};
```

。

すると：

```text
1個目の{} ← 佐藤
2個目の{} ← 晴れ
```

となる。

結果：

```text
佐藤さん、こんにちは。今日は晴れですね。
```

。

つまり：

```text
{}は順番対応
```

であることが重要。

---

# ■ ◆ MessageSource + Map

今回のコード：

```java
String male =
    messageSource.getMessage(
        "male",
        null,
        null
    );
```

。

これは：

```properties
male=男性
```

の：

```text
値側
```

を取得している。

つまり：

```java
male = "男性"
```

となる。

---

その後：

```java
genderMap.put(male, 1);
```

を実行。

これにより：

```text
男性 → 1
```

をMapへ登録している。

---

同様に：

```java
genderMap.put(female, 2);
```

で：

```text
女性 → 2
```

を登録。

---

最終的なMap：

```text
{
  男性 = 1,
  女性 = 2
}
```

。

このMapが：

```java
model.addAttribute(
    "genderMap",
    genderMap
);
```

によってHTMLへ渡され、

```html
th:each="item : ${genderMap}"
```

でループされている。

---

# ■ ◆ RequestMapping

クラス側URLとメソッド側URLを合体する仕組み。

例えば：

```java
@RequestMapping("/user")
```

+

```java
@GetMapping("/signup")
```

。

これにより：

```text
/user/signup
```

が完成する。

---

つまり：

```text
クラス側
↓
URL共通部分

メソッド側
↓
個別URL
```

という役割分担。

---

## なぜ@RequestMappingを使うのか？

技術的には：

```java
@GetMapping("/user/signup")
```

だけでも動く。

しかし：

```text
/user
```

を毎回書く必要がある。

そのため：

```java
@RequestMapping("/user")
```

で共通化する。

---

また：

```text
「このControllerは
user機能担当」
```

という整理にもなる。

---

# ■ ◆ @GetMapping は省略版

```java
@GetMapping("/signup")
```

は実際には：

```java
@RequestMapping(
    value="/signup",
    method=RequestMethod.GET
)
```

の簡略版。

つまり：

```text
@GetMapping
↓
GET専用RequestMapping
```

という関係。

---

同様に：

```java
@PostMapping
```

は：

```text
POST専用RequestMapping
```

。

---

# ■ ◆ リダイレクト

```java
return "redirect:/login";
```

。

意味：

```text
/loginへ移動し直す
```

。

---

通常：

```java
return "login/login";
```

なら：

```text
HTML表示
```

を行う。

しかし：

```java
redirect:
```

を付けると、

```text
別URLへ再アクセス
```

になる。

---

# ■ ◆ PRGパターン

POST → Redirect → GET。

フォーム送信後に：

```text
直接HTML表示
```

してしまうと、

```text
F5
↓
POST再送信
↓
二重登録危険
```

がある。

そのため：

```text
POST後はリダイレクト
```

する。

---

流れ：

```text
フォーム送信
↓
POST /user/signup
↓
redirect:/login
↓
GET /login
↓
ログイン画面表示
```

。

---

# ■ ◆ th:href と th:action

Thymeleaf版：

- href
- action

。

例えば：

```html
th:href="@{/login}"
```

。

これは：

```text
Spring / Thymeleaf側で
URL生成
```

を行っている。

---

通常：

```html
href="/login"
```

でも動く。

しかし：

```html
th:href="@{/login}"
```

なら：

- コンテキストパス
- URL変更
- 配置変更

などへ安全に対応できる。

---

同様に：

```html
th:action="@{/login}"
```

も、

```text
フォーム送信URLを
Spring側で安全生成
```

している。

---

# ■ ◆ Model

```java
model.addAttribute(
    "genderMap",
    genderMap
);
```

。

これは：

```text
ControllerからHTMLへ
データを渡す
```

という意味。

---

## 第一引数

```java
"genderMap"
```

。

HTML側で使う名前。

---

## 第二引数

```java
genderMap
```

。

実際のJavaデータ。

---

つまり：

```text
「genderMapという名前で
MapデータをHTMLへ渡す」
```

という意味。

---

HTML側では：

```html
${genderMap}
```

として取得できる。

---

# ■ ◆ コンストラクタインジェクション

```java
@Autowired
public SignupController(
    UserApplicationService
        userApplicationService
)
```

。

これは：

```text
SignupController生成時に、
必要なServiceをSpringが注入
```

している。

---

つまり：

```java
private final UserApplicationService
    userApplicationService;
```

へ、

Springが：

```text
UserApplicationServiceインスタンス
```

を自動で入れている。

---

これにより：

```java
new UserApplicationService()
```

を自分で書かなくても、

Spring側で：

- 生成
- 保持
- 注入

を行ってくれる。
---

# ■ 今回特に重要だった理解

今回特に重要だったのは、

```text
Spring Bootは
Javaだけで動いているわけではない
```

という点だった。

実際には：

- HTML
- CSS
- Bootstrap
- Thymeleaf
- Maven
- WebJars

などが連携しながら、

```text
Web画面
```

を構成していた。

また、

```text
Thymeleafが
SpringとHTMLを繋いでいる
```

ことも理解できた。

---

# ■ 学び・気づき

- Bootstrapは便利CSS class集
- WebJarsはCSS/JS版Mavenのようなもの
- Spring Bootはresourcesを特別扱いする
- th:hrefはURL生成支援
- MessageSourceはproperties読込機構
- {}は埋め込み用プレースホルダ
- RequestMappingはURL共通化
- PRGパターンは二重送信防止
- ThymeleafはHTMLとSpringを繋ぐ

---

# ■ 苦戦した理由

今回は：

- HTML
- CSS
- Bootstrap
- Thymeleaf
- URL
- HTTP

など、

Java以外の知識が大量に登場した。

特に：

```text
どこまでがSpringで、
どこまでがHTMLなのか
```

が混乱しやすかった。

また、

教科書では：

- div
- input
- label
- class
- href

など、

HTML基礎部分をかなり省略していたため、
そこを自分で補完しながら理解を進めた。
