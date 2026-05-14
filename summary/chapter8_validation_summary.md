# 第8章まとめ：バリデーション

## ■ 概要

この章では、Spring Boot における「バリデーション（Validation）」について学習した。

バリデーションとは、

- 入力値が空ではないか
- 正しい形式か
- 指定した範囲内か

など、

「入力内容そのものが妥当かどうか」

をチェックする仕組みである。

第7章では：

- String → Integer
- String → Date

などの「型変換エラー（バインドエラー）」を扱った。

一方、本章では：

- 未入力チェック
- 文字数チェック
- メールアドレス形式チェック
- 正規表現チェック
- 数値範囲チェック

など、

「入力値の妥当性チェック」

を扱う。

また本章では：

- 標準バリデーション
- エラーメッセージ編集
- カスタムバリデーション
- 複数項目の相関チェック

まで実装し、Spring Boot における Validation の基礎から応用までを学習した。

---

# ■ 8-1 バリデーション

## ▽ サンプルアプリケーションの作成

以下の流れで実装する。

1. バリデーションライブラリ追加
2. フォームクラスへアノテーション付与
3. Controller で Validation 実行
4. エラーメッセージ表示

---

## 1. バリデーションライブラリ追加

### ◆ pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>
        spring-boot-starter-validation
    </artifactId>
</dependency>
```

Validation 機能を利用するためには：

- Jakarta Bean Validation
- Hibernate Validator

などのライブラリが必要になる。

---

## ◆ 第7章との違い

第7章で扱っていたのは：

```text
型変換エラー
```

である。

例：

```text
abc → Integer
```

これは Spring MVC 標準機能であり、
starter-web に含まれていた。

一方、本章の：

- @NotBlank
- @Email
- @Min
- @Max

などは：

```text
Jakarta Bean Validation
```

という別ライブラリ機能である。

そのため dependency の追加が必要になる。

---

## 2. バリデーションの実装

### ◆ SignupForm.java

```java
@Data
public class SignupForm {

    @NotBlank
    @Email
    private String userId;

    @NotEmpty
    @Length(min = 4, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;

    @NotBlank
    private String userName;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @NotNull
    private Date birthday;

    @Min(20)
    @Max(100)
    private Integer age;

    @NotNull
    private Integer gender;
}
```

各フィールドへ：

```text
バリデーション用アノテーション
```

を付与することで、
入力ルールを定義できる。

---

## ◆ 主なアノテーション

| アノテーション | 内容 |
|---|---|
| @NotNull | null禁止 |
| @NotEmpty | null・空文字禁止 |
| @NotBlank | null・空文字・空白禁止 |
| @Min | 最小値 |
| @Max | 最大値 |
| @Pattern | 正規表現 |
| @Email | メール形式 |
| @Length | 文字数範囲 |

---

## ◆ @NotNull / @NotEmpty / @NotBlank の違い

| アノテーション | null | 空文字 | 空白 |
|---|---|---|---|
| @NotNull | NG | OK | OK |
| @NotEmpty | NG | NG | OK |
| @NotBlank | NG | NG | NG |

実務では：

```java
@NotBlank
```

がよく利用される。

---

## 3. Controller で Validation 実行

### ◆ SignupController.java

```java
@PostMapping("/signup")
public String postSignup(
        Model model,
        @ModelAttribute @Validated SignupForm form,
        BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

        return getSignup(model, form);
    }

    log.info(form.toString());

    return "redirect:/login";
}
```

---

## ◆ @Validated

```java
@Validated
```

を付与すると：

```text
SignupForm に設定した
Validation アノテーション
```

が実行される。

---

## ◆ BindingResult

Validation 結果は：

```java
BindingResult
```

へ格納される。

```java
bindingResult.hasErrors()
```

を利用することで：

- バインドエラー
- Validation エラー

をまとめて判定できる。

---

## ◆ エラー表示

第7章で実装した：

```html
th:errors
```

が BindingResult の内容を自動参照する。

そのため：

```text
Validation 導入だけで
エラーメッセージ表示
```

が可能になる。

---

# ■ 8-2 エラーメッセージの編集

## ◆ ValidationMessages.properties

```properties
NotBlank={0}は必須入力です
NotEmpty={0}は必須入力です
Email={0}はメールアドレス形式で入力してください
Length={0}は{2}桁以上、{1}桁以下で入力してください
Pattern={0}は半角英数字で入力してください
```

---

## ◆ 埋め込みパラメータ

| パラメータ | 内容 |
|---|---|
| {0} | フィールド名 |
| {1} | 属性値 |
| {2} | 属性値 |

---

## ◆ messages.properties との連携

```properties
userId=ユーザーID
password=パスワード
gender=性別
```

を定義することで：

```text
gender は必須入力です
```

ではなく：

```text
性別は必須入力です
```

と表示できる。

---

## ◆ 独自キー

```properties
require_check=必須入力です
```

```java
@NotBlank(message = "{require_check}")
```

のように、
独自メッセージキー指定も可能。

---

# ■ 8-3 カスタムバリデーション（単一項目）

## ◆ カスタムバリデーション概要

独自 Validation では：

- アノテーション
- Validator

を自作する。

---

## ◆ 今回作成するもの

```java
@LengthMin
```

を実装し、

```text
最低文字数チェック
```

を行う。

---

## ◆ ファイル構成

```text
com.example.demo.validator
├── LengthMin.java
└── LengthMinValidator.java
```

---

## ◆ LengthMin.java

```java
@Documented
@Constraint(validatedBy = { LengthMinValidator.class })
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LengthMin
```

---

## ◆ @Constraint

```java
@Constraint(validatedBy = ...)
```

により：

```text
アノテーション
↓
Validator
```

を関連付ける。

---

## ◆ @Target

```java
@Target(ElementType.FIELD)
```

により：

```text
フィールド専用
```

アノテーションになる。

---

## ◆ @Retention

```java
@Retention(RetentionPolicy.RUNTIME)
```

を指定することで：

```text
実行時
```

までアノテーション情報を保持する。

---

## ◆ 必須メソッド

```java
message()
groups()
payload()
```

はカスタム Validation 作成時の基本構成。

---

## ◆ アノテーション引数

```java
int min() default 0;
```

を定義することで：

```java
@LengthMin(min = 5)
```

のように利用できる。

---

## ◆ Validator 作成

```java
public class LengthMinValidator
implements ConstraintValidator<LengthMin, String>
```

---

## ◆ ConstraintValidator<A,T>

| 型 | 内容 |
|---|---|
| A | アノテーション型 |
| T | チェック対象型 |

---

## ◆ initialize()

```java
public void initialize(LengthMin lengthMin)
```

アノテーション引数：

```java
min = 5
```

などを受け取る。

---

## ◆ isValid()

```java
public boolean isValid(
    String value,
    ConstraintValidatorContext context)
```

実際の Validation 処理を書く。

---

## ◆ 戻り値

| 戻り値 | 意味 |
|---|---|
| true | OK |
| false | NG |

---

# ■ 8-4 カスタムバリデーション（複数項目）

## ◆ 相関チェック

複数項目の関係を検証する。

例：

- 開始日 < 終了日
- パスワード一致
- 誕生日と年齢整合性

---

## ◆ 今回のチェック

```text
誕生日から算出した年齢
=
入力された年齢
```

を比較する。

---

## ◆ クラス対象 Validation

```java
@Target(ElementType.TYPE)
```

複数項目チェックでは：

```text
クラス全体
```

を対象にする必要がある。

---

## ◆ フィールド名引数

```java
String birthdayFieldName()
String ageFieldName()
```

を定義し：

```text
どのフィールド同士を比較するか
```

を指定する。

---

# ■ 最終まとめ

第8章では：

- Jakarta Bean Validation
- @Validated
- BindingResult
- ValidationMessages.properties
- カスタムエラーメッセージ
- 独自アノテーション
- ConstraintValidator
- initialize()
- isValid()
- 単一項目 Validation
- 相関チェック

などを利用し、

```text
Spring Boot における
入力検証機能の基礎から応用
```

を学習した。

また本章では：

```text
標準機能を利用するだけでなく、
独自 Validation を自作する方法
```

まで学習し、

```text
Spring Validation の内部構造
```

についても理解を深めた。
