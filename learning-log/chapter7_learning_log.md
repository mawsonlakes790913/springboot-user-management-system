# ■ 第7章 バインドとエラーメッセージ

## ■ この章の目的

この章では、

- バインド（Binding）
- フォームクラス
- `@ModelAttribute`
- `th:object`
- `th:field`
- `BindingResult`
- バインドエラーメッセージ

などを利用し、

> 画面入力値をJavaオブジェクトへ自動的に紐づける仕組み

を学習した。

これまでの章では、

- Controller
- Model
- Thymeleaf

などを利用し、

> Java → HTML

方向のデータ受け渡しを中心に学んでいた。

しかし実際のWebアプリでは、

> ユーザーが入力した値を  
> サーバー側で受け取る

必要がある。

そのため今回は、

> HTML入力  
> ↓  
> Javaオブジェクト

への変換を自動化する、

> バインド

を学習した。

また、

- 数値欄へ文字列
- 日付欄へ不正フォーマット

など、

> 型変換失敗

が発生した際に、

> Whitelabel Error Page

ではなく、

> 入力欄の近くへ  
> 分かりやすいエラーメッセージを表示する

ところまで実装した。

---

## ■ この章の流れ

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

## ■ 6章までとの違い

6章までは、主に：

> Controller  
> ↓  
> HTML表示  
> ↓  
> ボタン押下  
> ↓  
> 別画面へ遷移

という、

> 画面遷移

が中心だった。

例えば：

```java
@PostMapping("/signup")
public String signup() {
    return "redirect:/login";
}
```

のように、

> POSTされた  
> ↓  
> 画面遷移した

だけで、

> 入力値そのもの

はほぼ扱っていなかった。

つまり6章までは、

> Java → HTML

方向のみだった。

しかし7章では、

```java
@PostMapping("/signup")
public String signup(SignupForm form)
```

のように、

> HTML → Javaオブジェクト

方向の受け渡しが追加された。

これにより、

> 画面入力値  
> ↓  
> Javaオブジェクト  
> ↓  
> 将来的にはDB保存

というWebアプリの本格的な流れの土台が作られた。

---

## ■ なぜバインドが必要なのか？

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

> 入力値を1個ずつ受け取る

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

> 画面入力専用クラス

へまとめて受け取る。

これがバインド。

---

## ■ ◆ バインド（Binding）

画面入力値を：

> Javaオブジェクトへ自動格納

する仕組み。

例えば：

```html
<input type="text" name="userId">
```

これがPOST送信されると、

```java
private String userId;
```

へ自動で入る。

---

## ■ 7-1 バインドの実装

この節では、

- SignupForm
- `@ModelAttribute`
- `th:field`

を利用し、

> 画面入力 → Javaオブジェクト

を実装した。

---

## ■ SignupForm 作成

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

---

## ■ 【補足】SignupForm は何なのか？

最初は、

> 「入力値を保存するだけのクラス？」

くらいの認識だった。

しかし実際には、

> HTMLフォームとJavaを繋ぐ受け皿

という役割だった。

つまり：

> 画面入力値を  
> 1個ずつバラバラで扱うのではなく、  
> 1つのJavaオブジェクトへまとめる

ためのクラスだった。

---

## ■ 【補足】Date型へ変換する理由

ブラウザのinputは、

> 全部文字列

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

> 文字列  
> ↓  
> Date型

へ変換しなければならない。

その変換ルールを書くのが：

```java
@DateTimeFormat(pattern = "yyyy/MM/dd")
```

だった。

---

## ■ ◆ @ModelAttribute

フォームクラスを：

> 自動生成 + Model登録

するアノテーション。

内部的には：

```java
SignupForm form = new SignupForm();
model.addAttribute("signupForm", form);
```

のような処理をSpringが自動で行っている。

---

## ■ 【疑問】なぜGET時にSignupFormを渡す必要がある？

結論から言うと、

> HTMLフォームが  
> どのJavaオブジェクトと接続するのか

をThymeleafへ教えるため。

例えば：

```html
<form th:object="${signupForm}">
```

を書くことで、

```html
<input th:field="*{userId}">
```

が：

```text
signupForm.userId
```

を参照できるようになる。

つまりGET時にSignupFormを渡している理由は、

> HTMLフォームとJavaオブジェクトを接続するため

だった。

---

## ■ 【疑問】th:field="*{userId}" だけでなぜ name="userId" になるのか？

これは、

> Thymeleafがth:fieldを解析し、  
> name属性やvalue属性を自動生成している

ため。

ブラウザはフォーム送信時、

```html
<input name="userId">
```

を見てPOSTデータを作る。

つまり最終的には：

```text
userId=Naoki
```

のようなHTTPリクエストを送る必要がある。

そのためThymeleafは、

```text
th:field="*{userId}"
```

を見て内部的に：

> signupForm.userId と接続したい  
> ↓  
> POST時には name="userId" が必要  
> ↓  
> 自動生成しよう

という処理をしている。

---

## ■ 【疑問】th:field はname属性を置き換えるだけ？

最初は、

> th:field = name属性の省略記法

くらいに思っていた。

しかし実際には、

- name属性
- value属性
- checked属性
- selected属性

など、

> Javaオブジェクトとの接続に必要な属性

を自動生成する仕組みだった。

例えば：

```java
form.setUserId("Naoki");
```

状態なら、

```html
<input value="Naoki">
```

相当も自動生成される。

つまり：

> th:fieldは  
> HTML入力欄とJavaオブジェクトを繋ぐ総合機能

だった。

---

## ■ POST時のバインド

```java
@PostMapping("/signup")
public String postSignup(
        @ModelAttribute SignupForm form)
```

---

## ■ 【補足】POST後に何が起きているのか？

内部的には：

> ユーザー入力  
> ↓  
> POST送信  
> ↓  
> SpringがSignupForm生成  
> ↓  
> setUserId(...)  
> setPassword(...)

などを自動実行している。

つまり：

> Spring内部でsetterが大量実行

されているイメージだった。

---

## ■ 【疑問】POSTされた入力値はいつSignupFormへ入るのか？

流れとしては：

> ① ユーザー入力  
> ↓  
> ② POST送信  
> ↓  
> ③ SpringがpostSignup()を発見  
> ↓  
> ④ SignupFormをnew  
> ↓  
> ⑤ setterを自動実行  
> ↓  
> ⑥ postSignup(form)呼び出し

となる。

つまり：

> postSignup()実行前

に既にバインドは完了している。

---

## ■ 【疑問】なぜPOST時の@ModelAttributeにもModel登録機能があるのか？

POST時の`@ModelAttribute`は、

> HTML → Java

のためだけではなかった。

内部的には：

```java
model.addAttribute("signupForm", form);
```

も行っている。

これは、

> バリデーションエラー時に  
> 入力内容を画面へ戻すため

である。

もしModelへformが無いと、

> 入力済み内容を再表示

できない。

つまりPOST時の`@ModelAttribute`は、

> バインド + 再表示準備

を同時に行っていた。

---

## ■ BindingResult

```java
public String postSignup(
        Model model,
        @ModelAttribute SignupForm form,
        BindingResult bindingResult)
```

---

## ■ ◆ BindingResult

バインド結果を保存するオブジェクト。

内部には：

- エラー有無
- エラー対象
- エラーメッセージ

などが入っている。

---

## ■ 【補足】BindingResult は何なのか？

最初、

> なぜ突然BindingResultが必要？

となった。

調べると、

> バインド成功 / 失敗結果

を保存しているオブジェクトだった。

つまり：

```text
age = abc
```

のような：

> Integer変換失敗

を内部に記録している。

---

## ■ バインド失敗時処理

```java
if (bindingResult.hasErrors()) {
    return getSignup(model, form);
}
```

---

## ■ 【疑問】BindingResult の位置が重要なのはなぜか？

教科書では：

```java
@ModelAttribute SignupForm form,
BindingResult bindingResult
```

の順番になっていた。

最初は：

> なぜこの順番固定？

が分からなかった。

調べるとSpringは：

> 「どのFormのBindingResultか」

を対応付ける必要があり、

> 対象Formの直後

に書く必要があった。

---

## ■ ValidationMessages.properties

```properties
typeMismatch.signupForm.age=数値を入力してください
typeMismatch.signupForm.birthday=yyyy/MM/dd形式で入力してください
```

---

## ■ 【補足】messages.propertiesを分割する理由

最初は、

> messages.propertiesだけで十分では？

と思った。

しかし：

- 画面表示文言
- エラーメッセージ

を全部混ぜると、

> 巨大化して管理しづらい

ことが分かった。

そのため：

- messages.properties
- validationMessages.properties

へ分割していた。

---

## ■ spring.messages.basename

```yaml
spring:
  messages:
    basename: messages,validationMessages
```

ここでは、

> Spring Bootが読み込む  
> メッセージファイル一覧

を指定している。

ルールは：

- src/main/resources基準
- 拡張子不要
- カンマ区切り

である。

---

## ■ 今回特に重要だった理解

今回最も重要だったのは、

> Springが  
> HTML入力値を  
> 自動でJavaオブジェクトへ変換している

という点だった。

特に：

> th:field  
> ↓  
> POST送信  
> ↓  
> Binding  
> ↓  
> SignupForm完成

という流れが繋がったことで、

> 画面とJavaの連携

をかなり具体的に理解できた。

---

## ■ 学び・気づき

- バインドは入力値自動格納機能
- SignupFormは入力専用オブジェクト
- `@ModelAttribute`はModel登録も行う
- `th:field`は入力欄とフィールド紐づけ
- Springはname属性を自動生成する
- Springは文字列→Date/Integer変換も行う
- `BindingResult`はエラー保存オブジェクト
- ValidationMessages.propertiesでエラー管理する
- BindingResultでWhitelabel Error Pageを防げる
- GET時はJava→HTML接続準備
- POST時はHTML→Javaバインド

---

## ■ 苦戦した理由

今回は、

- HTML
- Thymeleaf
- Spring内部Binding
- Date変換
- BindingResult

など、

> 画面側とSpring内部処理

が強く結びついていた。

特に：

> どのタイミングで  
> SignupFormへ値が入るのか

がかなり難しかった。

また、

> GET時の空フォーム  
> POST時の自動バインド

の違いも最初は混乱した。

さらに：

> th:object  
> th:field  
> ${}  
> *{}

の関係が複雑で、

> HTMLとThymeleafの境界

を理解するのにかなり苦戦した。

しかし一度：

> 画面入力  
> ↓  
> POST送信  
> ↓  
> Spring内部でBinding  
> ↓  
> SignupForm完成  
> ↓  
> エラー時はBindingResultへ保存

という全体像が見えると、

> 「内部は複雑だが、  
> 実装パターン自体は再現性が高い」

と感じた。

また今回特に印象的だったのは、

> 普段日常的に使っている  
> 「ユーザー登録画面」  
> 「ログイン画面」

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

> Spring側がかなり自動化してくれているため、  
> 仕組みさえ理解できれば  
> 実装自体は一定の型へ落とし込める

という、

> 「内部は複雑だが、  
> 実装パターンは再現性が高い」

という二面性を強く感じた章だった。
