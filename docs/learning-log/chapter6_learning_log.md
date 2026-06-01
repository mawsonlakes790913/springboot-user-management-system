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
- PRGパターン

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
- HTTP
- URL

など、

```text
フロントエンド側の知識
```

も必要になった。

また、復習を通して、

```text
Spring BootはJavaだけで完結しているわけではなく、
HTML・CSS・HTTP・Thymeleafなどと連携しながら
Webアプリを構築している
```

という全体像も少し見えるようになった。

---

# ■ この章の流れ

この章では大きく分けて、

1. WebJarsでBootstrap導入
2. ログイン画面作成
3. ユーザー登録画面作成
4. messages.properties導入
5. MessageSourceでメッセージ取得
6. リダイレクト処理（PRGパターン）

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

Javaでは：

```text
jar手動管理
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

また復習を通して、

- pom.xml
- Maven
- WebJars

の主語が混ざると混乱しやすいことも理解した。

整理すると：

| 名前 | 役割 |
|---|---|
| pom.xml | 使いたいライブラリを書く設定ファイル |
| Maven | pom.xmlを読み取りライブラリをDL・管理するツール |
| WebJars | フロントエンドライブラリをMaven管理できる仕組み |

である。

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

また復習を通して、

```text
メッセージプロパティは
単なる文字列管理だけでなく、
国際化(i18n)の土台でもある
```

ことを理解した。

---

# ■ 【疑問】今回作るファイルがhelloパッケージでないのはなぜ？

前章までに作成していた：

- /hello
- /hello/response
- /hello/db

などは、

```text
前章までの学習用サンプル
```

であり、今回の：

- /login
- /user/signup

とは、同じSpringBootSampleプロジェクト内に存在しているものの、

```text
現時点ではほぼ独立した別ページ
```

である。

ただし完全に無関係ではなく、

```text
今後統合されていく前段階
```

という理解が正しい。

---

# ■ 【疑問】loginにはなぜServiceがない？

現時点のLoginControllerは：

```text
/login にアクセスされたら
login.html を返すだけ
```

だからである。

つまり：

```text
画面表示だけ
```

なのでControllerだけで成立している。

一方 user 側では：

```text
性別Map生成
```

という画面表示用データ生成が存在する。

そのため：

```text
Controllerから業務処理を分離
```

するために、

```text
UserApplicationService
```

が存在している。

---

# ■ 【疑問】なぜMap<String,Integer> genderMap = new LinkedHashMap<>()なのか？

```java
Map<String, Integer> genderMap =
    new LinkedHashMap<>();
```

となっている理由は、

```text
Mapはインタフェースだから
```

である。

また：

```java
public LinkedHashMap<String,Integer>
```

にしない理由も重要だった。

これは：

```text
実装ではなく抽象へ依存する
```

というJava/Springの重要な設計思想による。

---

# ■ 【疑問】リダイレクト仕様がreturn "/login"しようが、結局LoginControllerへ行くのは同じでは？

確かに見た目上は：

```text
SignupController → LoginController
```

へ移動している。

しかし重要なのは：

```text
HTTPリクエストの流れ
```

が異なる点。

---

## リダイレクトしない場合

```java
return "login/login";
```

。

この場合ブラウザは：

```text
POST /user/signup
```

の結果画面を表示している。

そのためF5すると：

```text
POST /user/signup 再送
```

となり、二重登録危険がある。

---

## リダイレクトする場合

```java
return "redirect:/login";
```

。

この場合：

```text
POST /user/signup
↓
302 Redirect
↓
GET /login
```

という流れになる。

つまり最終的にブラウザが表示しているのは：

```text
GET /login の結果画面
```

。

そのためF5しても：

```text
GET /login
```

しか再送されない。

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

これを：

```text
PRG(Post-Redirect-Get)
```

パターンという。

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
- HTTP
- URL

などが連携しながら、

```text
Web画面
```

を構成していた。

さらに、復習によって：

- formとaタグの違い
- GETとPOSTの違い
- redirectと通常returnの違い
- RequestMappingの役割
- Modelとth:eachの関係
- MapとLinkedHashMapの設計思想
- label / id / for の関連

など、曖昧だった部分を整理できた。

---

# ■ 学び・気づき

- Bootstrapは便利CSS class集
- WebJarsはCSS/JS版Mavenのようなもの
- Spring Bootはresourcesを特別扱いする
- ThymeleafはSpringとHTMLを繋ぐ
- th:hrefはURL生成支援
- MessageSourceはproperties読込機構
- Localeで国際化対応できる
- RequestMappingはURL共通化
- redirectはHTTPリクエスト自体を変える
- PRGパターンは二重送信防止
- label/id/forは相互に関連している
- Mapを返すのは抽象依存の設計思想

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

```text
Java以外の知識
```

が大量に登場した。

特に：

```text
どこまでがSpring Bootで、
どこからがHTML/HTTPなのか
```

の切り分けが最初は非常に難しかった。

しかし復習によって、

```text
Spring BootはHTTPリクエストを受け取り、
Thymeleafを通してHTMLへデータを渡している
```

という全体像が少し理解できるようになった。
