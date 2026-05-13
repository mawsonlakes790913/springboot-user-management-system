# ■ 第7章 バインドとエラーメッセージ

# ■ この章の目的

この章では、

- バインド（Binding）
- フォームクラス
- `@ModelAttribute`
- `th:field`
- `BindingResult`
- バインドエラーメッセージ

などを利用しながら、

```text
画面入力値をJavaオブジェクトへ自動的に紐づける仕組み
```

を学習した。

これまでの章では、

- Controller
- Model
- Thymeleaf

などを利用し、

```text
画面を表示する
```

ところまでを中心に学んでいた。

しかし実際のWebアプリでは、

```text
ユーザーが入力した値を
サーバー側で受け取る
```

必要がある。

そのため今回は、

```text
画面入力
↓
Javaオブジェクト
```

を自動で結びつける、

```text
バインド
```

を学習した。

また、

- 数値欄へ文字列
- 日付欄へ不正フォーマット

など、

```text
型変換失敗
```

が発生した際に、

```text
Spring Bootのデフォルトエラー画面
```

ではなく、

```text
入力欄の近くへ
分かりやすいエラーメッセージを表示する
```

ところまで実装した。

---

# ■ この章の流れ

この章では大きく分けて、

1. SignupForm作成
2. `@ModelAttribute`
3. `th:object`
4. `th:field`
5. POST時の自動バインド
6. ValidationMessages.properties
7. BindingResult
8. バインドエラー時の画面制御

という流れで進んだ。

---

# ■ なぜバインドが必要なのか？

バインド登場前は、

```java
@PostMapping("/signup")
public String signup(
        @RequestParam("userId") String userId,
        @RequestParam("password") String password,
        @RequestParam("userName") String userName) {

    return "signup";
}
```

のように、

```text
入力値を1個ずつ受け取る
```

必要があった。

しかし実務では、

- userId
- password
- birthday
- age
- address
- email

など大量の入力欄が存在する。

すると：

```text
@RequestParam(...)
@RequestParam(...)
@RequestParam(...)
```

だらけになり、

- Controller肥大化
- 可読性低下
- 修正困難

などの問題が発生する。

そのため：

```java
public class SignupForm {

    private String userId;
    private String password;
    private String userName;

}
```

のような、

```text
画面入力専用クラス
```

へまとめて受け取る。

これがバインド。

---

# ■ なぜエラーメッセージ設定が必要なのか？

現状のままでは、

- age に abc
- birthday に 19950815

などを入力すると、

```text
Whitelabel Error Page
```

のような、

```text
開発者向けエラー画面
```

が表示されてしまう。

しかし実際のWebアプリでは、

```text
ユーザーが理解できる形で
入力ミスを表示する
```

必要がある。

そのため今回は、

- ValidationMessages.properties
- BindingResult

を利用し、

```text
エラー時でも登録画面へ戻し、
入力ミスを表示する
```

ように修正した。

---

# ■ ◆ バインド（Binding）

画面入力値を：

```text
Javaオブジェクトへ自動格納
```

する仕組み。

例えば：

```html
<input type="text" name="userId">
```

。

これがPOST送信されると、

```java
private String userId;
```

へ自動で入る。

---

# ■ ◆ @ModelAttribute

フォームクラスを：

```text
自動生成 + Model登録
```

するアノテーション。

例えば：

```java
@GetMapping("/signup")
public String getSignup(
        Model model,
        @ModelAttribute SignupForm form)
```

。

内部的には：

```java
SignupForm form = new SignupForm();
model.addAttribute("signupForm", form);
```

のような処理をSpringが自動で行っている。

---

# ■ ◆ th:field

入力欄とJavaフィールドを紐づける。

```html
<input th:field="*{userId}">
```

。

これにより：

```text
input
↓
SignupForm.userId
```

へ自動バインドされる。

---

# ■ ◆ BindingResult

バインド結果を保存するオブジェクト。

内部には：

- エラー有無
- エラー対象
- エラーメッセージ

などが入っている。

---

# ■ 7-1 バインドの実装

この節では、

- SignupForm
- `@ModelAttribute`
- `th:field`

を利用し、

```text
画面入力 → Javaオブジェクト
```

を実装した。

---

# ■ SignupForm 作成

```java
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

。

---

# ■ 【補足】SignupForm は何なのか？

最初は：

```text
「入力値を保存するだけのクラス？」
```

くらいの認識だった。

しかし実際には、

```text
HTMLフォームとJavaを繋ぐ
受け皿
```

という役割だった。

つまり：

```text
画面入力値を
1個ずつバラバラで扱うのではなく、
1つのJavaオブジェクトへまとめる
```

ためのクラス。

---

# ■ 【疑問】バインド登場前はどうしていたのか？

以前は：

```java
@RequestParam
```

で1個ずつ受け取っていた。

しかし入力欄が増えると、

```text
Controllerが
@RequestParamだらけ
```

になる。

そのため：

```text
フォーム専用クラス
```

へまとめる設計が必要だった。

---

# ■ 【補足】Date型へ変換する理由

ブラウザのinputは、

```text
全部文字列
```

として送信される。

つまり：

```text
birthday=1986/11/05
```

という文字列が送られてくる。

しかしJava側は：

```java
private Date birthday;
```

なので、

```text
文字列
↓
Date型
```

へ変換しなければならない。

その変換ルールを書くのが：

```java
@DateTimeFormat(pattern = "yyyy/MM/dd")
```

。

---

# ■ Controller 修正

```java
@GetMapping("/signup")
public String getSignup(
        Model model,
        @ModelAttribute SignupForm form)
```

。

---

# ■ 【疑問】GET時なのにSignupFormを渡す理由

最初、

```text
まだ入力していないのに
なぜSignupFormが必要なのか？
```

が理解できなかった。

調べると、

```html
<form th:object="${signupForm}">
```

を書く以上、

```text
HTML側はsignupFormを前提
```

としていることが分かった。

そのためController側で、

```java
model.addAttribute("signupForm", form);
```

相当の処理を行う必要があった。

---

# ■ th:field の設定

```html
<input type="text"
       id="userId"
       class="form-control"
       th:field="*{userId}">
```

。

---

# ■ 【疑問】th:object と th:field の関係が難しかった

最初、

```text
th:field="${signupForm.userId}"
```

のような理解をしており、

```text
formタグにも
th:fieldがあるのでは？
```

と誤解していた。

しかし実際には：

```html
<form th:object="${signupForm}">
```

で：

```text
このform全体はsignupFormを使う
```

と宣言し、

その内部で：

```html
th:field="*{userId}"
```

のように、

```text
*{}でフィールドだけを書く
```

構造だった。

ここは、

- HTML
- Thymeleaf
- Spring

の役割境界が分かりづらく、
かなり混乱した。

---

# ■ POST時のバインド

```java
@PostMapping("/signup")
public String postSignup(
        @ModelAttribute SignupForm form)
```

。

---

# ■ 【補足】POST後に何が起きているのか？

内部的には：

```text
ユーザー入力
↓
POST送信
↓
SpringがSignupForm生成
↓
setUserId(...)
setPassword(...)
```

などを自動実行している。

つまり：

```text
Spring内部でsetterが大量実行
```

されているイメージだった。

---

# ■ ログ確認

```java
log.info(form.toString());
```

。

これにより：

```text
SignupForm(
 userId=mawsonlakes,
 age=30
)
```

などが表示された。

---

# ■ 【疑問】入力データはどこで処理しているのか？

最初、

```text
DB保存していないのに
なぜ登録完了？
```

と混乱した。

しかし今回は：

```text
バインド学習用
```

なので、

```text
SignupFormへ値が入ること
```

だけ確認していた。

つまり：

```text
保存処理は未実装
```

だった。

---

# ■ 【間違い】誕生日入力で失敗

最初、

```text
19950815
```

と入力した。

しかし：

```text
yyyy/MM/dd
```

形式ではないため、

```text
Failed to convert...
```

エラーになった。

UIに表示されていた：

```text
/
```

を、

```text
自動補完されるもの
```

と誤解していた。

---

# ■ 7-2 エラーメッセージの編集

この節では、

- ValidationMessages.properties
- BindingResult

を利用し、

```text
バインド失敗時でも
画面へ戻す
```

実装を行った。

---

# ■ ValidationMessages.properties

```properties
typeMismatch.signupForm.age=数値を入力してください
typeMismatch.signupForm.birthday=yyyy/MM/dd形式で入力してください
```

。

---

# ■ 【補足】messages.propertiesを分割する理由

最初は：

```text
messages.propertiesだけで十分では？
```

と思った。

しかし：

- 画面表示文言
- エラーメッセージ

を全部混ぜると、

```text
巨大化して管理しづらい
```

ことが分かった。

そのため：

- messages.properties
- ValidationMessages.properties

へ分割していた。

---

# ■ BindingResult

```java
public String postSignup(
        Model model,
        @ModelAttribute SignupForm form,
        BindingResult bindingResult)
```

。

---

# ■ 【補足】BindingResult は何なのか？

最初、

```text
なぜ突然BindingResultが必要？
```

となった。

調べると、

```text
バインド成功 / 失敗結果
```

を保存しているオブジェクトだった。

つまり：

```text
age = abc
```

のような：

```text
Integer変換失敗
```

を内部に記録している。

---

# ■ バインド失敗時処理

```java
if (bindingResult.hasErrors()) {
    return getSignup(model, form);
}
```

。

---

# ■ 【疑問】BindingResult の位置が重要なのはなぜか？

教科書では：

```java
@ModelAttribute SignupForm form,
BindingResult bindingResult
```

の順番になっていた。

最初は：

```text
なぜこの順番固定？
```

が分からなかった。

調べるとSpringは：

```text
「どのFormのBindingResultか」
```

を対応付ける必要があり、

```text
対象Formの直後
```

に書く必要があった。

---

# ■ 【疑問】パターン2・3は実務向きなのか？

調べると、

```text
小規模なら便利
```

だが、

実務では：

- 年齢
- 金額
- 在庫数

など、

同じIntegerでも意味が異なる。

そのため一般的には：

```text
Form + field
```

単位で設定する：

```properties
typeMismatch.signupForm.age
```

形式が最も実用的だった。

---

# ■ 今回特に重要だった理解

今回最も重要だったのは、

```text
Springが
HTML入力値を
自動でJavaオブジェクトへ変換している
```

という点だった。

特に：

```text
th:field
↓
POST送信
↓
Binding
↓
SignupForm完成
```

という流れが繋がったことで、

```text
画面とJavaの連携
```

をかなり具体的に理解できた。

---

# ■ 学び・気づき

- バインドは入力値自動格納機能
- SignupFormは入力専用オブジェクト
- `@ModelAttribute`はModel登録も行う
- `th:field`は入力欄とフィールド紐づけ
- `BindingResult`はエラー保存オブジェクト
- Springは文字列→Date/Integer変換も行う
- ValidationMessages.propertiesでエラー管理する
- BindingResultでWhitelabel Error Pageを防げる

---

# ■ 苦戦した理由

今回は、

- HTML
- Thymeleaf
- Spring内部Binding
- Date変換
- BindingResult

など、

```text
画面側とSpring内部処理
```

が強く結びついていた。

特に：

```text
どのタイミングで
SignupFormへ値が入るのか
```

がかなり難しかった。

また、

```text
GET時の空フォーム
POST時の自動バインド
```

の違いも最初は混乱した。

さらに：

```text
th:object
th:field
${}
*{}
```

の関係が複雑で、

```text
HTMLとThymeleafの境界
```

を理解するのにかなり苦戦した。

しかし一度：

```text
画面入力
↓
POST送信
↓
Spring内部でBinding
↓
SignupForm完成
↓
エラー時はBindingResultへ保存
```

という全体像が見えると、

5章や6章ほど概念が複雑に分岐しているわけではなく、

```text
「理解後はかなり再現性が高い仕組み」
```

だと感じた。

また今回特に印象的だったのは、

```text
普段日常的に使っている
「ユーザー登録画面」
「ログイン画面」
```

の裏側で、

- 型変換
- エラー制御
- オブジェクト生成
- HTMLテンプレート処理
- バインド
- リダイレクト

など、

非常に多くのロジックが動いていることだった。

その一方で、

```text
Spring側がかなり自動化してくれているため、
仕組みさえ理解できれば
実装自体は一定の型へ落とし込める
```

という、

```text
「内部は複雑だが、
実装パターン自体は再現性が高い」
```

という二面性を強く感じた章だった。
