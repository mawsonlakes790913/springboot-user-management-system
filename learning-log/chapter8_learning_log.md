# ■ 第8章 バリデーション

# ■ この章の目的

この章では、

- バリデーション（Validation）
- `@Validated`
- バリデーション用アノテーション
- `ValidationMessages.properties`
- カスタムバリデーション
- 単項目チェック
- 相関チェック（複数項目チェック）

などを利用しながら、

```text
「入力値が業務的に正しいか」
```

を検査する仕組みを学習した。

第7章では、

```text
String → Integer
String → Date
```

などの、

```text
型変換失敗
```

を扱っていた。

しかし今回は、

- 必須入力か
- メールアドレス形式か
- 文字数が足りているか
- 数値範囲内か
- 項目同士の関係が正しいか

など、

```text
入力内容そのものの妥当性
```

をチェックする、

```text
正式な入力検証（Validation）
```

を実装した。

さらに今回は、

```text
Spring標準機能を使うだけ
```

ではなく、

```text
独自ルールを持つ
カスタムバリデーション
```

まで作成した。

---

# ■ この章の流れ

この章では大きく分けて、

1. Validationライブラリ追加
2. フォームクラスへのバリデーション定義
3. `@Validated`
4. `ValidationMessages.properties`
5. デフォルトメッセージ変更
6. カスタムバリデーション
7. 独自アノテーション
8. Validatorクラス
9. 相関チェック

という流れで進んだ。

---

# ■ なぜバリデーションが必要なのか？

第7章まででも、

```text
abc → Integer
```

のような、

```text
型変換エラー
```

は検出できた。

しかし実際のWebアプリでは、

- 空欄禁止
- メール形式
- 最低文字数
- パスワード強度
- 日付整合性

など、

```text
業務ルール
```

のチェックが必要になる。

例えば：

```text
userId = a
```

は、

```text
String型としては成立
```

している。

しかし：

```text
「ユーザーIDとして妥当か？」
```

は別問題。

そのため：

```text
業務ルールを宣言的に書く仕組み
```

として、

```text
Bean Validation
```

を利用した。

---

# ■ なぜエラーメッセージ編集が必要なのか？

デフォルト状態では：

```text
空白は許可されていません
null は許可されていません
```

など、

```text
機械的で分かりづらい
```

メッセージになる。

しかし実際のWebアプリでは、

```text
「ユーザーIDは必須入力です」
```

のように、

```text
利用者向けメッセージ
```

へ変換する必要がある。

そのため今回は、

```text
ValidationMessages.properties
```

を利用し、

```text
バリデーション内容
↓
表示メッセージ
```

を分離した。

---

# ■ ◆ Validation（バリデーション）

入力値が：

```text
業務的に正しいか
```

を検査する仕組み。

例えば：

- 必須入力
- 数値範囲
- メール形式
- 文字数

など。

---

# ■ ◆ @Validated

```java
@Validated SignupForm form
```

。

意味：

```text
SignupFormに付いた
Validationルールを実行しろ
```

。

Springは：

- `@NotBlank`
- `@Email`
- `@Min`

などを自動実行する。

---

# ■ ◆ カスタムバリデーション

Spring標準では存在しない、

```text
独自入力ルール
```

を自分で作る仕組み。

例えば：

- 社員番号形式
- パスワード強度
- 年齢と誕生日整合性

など。

---

# ■ ◆ アノテーションとValidatorの違い

例えば：

```java
@LengthMin(min = 5)
```

。

これは：

```text
「LengthMinルールでチェックして」
```

という目印。

実際の判定：

```java
if (value.length() < 5)
```

を書くのは、

```text
Validatorクラス
```

側。

---

# ■ 8-1 バリデーション

この節では、

- Validationライブラリ
- Validationアノテーション
- `@Validated`
- BindingResult

を利用し、

```text
フォーム入力値の検証
```

を実装した。

---

# ■ Validationライブラリ追加

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

。

---

# ■ 【疑問】なぜ7章では不要だったのか？

最初、

```text
7章でもエラー処理していたのに、
なぜValidationだけ別ライブラリ？
```

が分からなかった。

調べると：

- 7章 → Spring MVC標準Binding
- 8章 → Bean Validation

であり、

```text
別機能
```

だった。

つまり：

```text
型変換失敗
```

と、

```text
業務ルール違反
```

は別概念だった。

---

# ■ SignupFormへのValidation追加

```java
@NotBlank
@Email
private String userId;
```

。

---

# ■ 【補足】今回の本質

今回最も重要だったのは、

```text
「入力ルールを
フォームクラス側へ書く」
```

という設計。

Controllerへ：

```java
if (userId == null || ...)
```

などを書いていない。

つまり：

```text
画面制御
```

と

```text
入力ルール
```

を分離していた。

---

# ■ 【疑問】@NotNull と @NotBlank の違い

最初、

```text
全部「未入力禁止」
```

に見えた。

しかし実際には：

| アノテーション | 空白 | 空文字 | null |
|---|---|---|---|
| `@NotNull` | OK | OK | NG |
| `@NotEmpty` | OK | NG | NG |
| `@NotBlank` | NG | NG | NG |

だった。

特に：

```text
" "
```

は：

```text
空白文字列
```

であり、

```text
nullではない
```

点が重要だった。

---

# ■ 【疑問】なぜbirthdayは@NotBlankじゃない？

```java
private Date birthday;
```

だから。

`@NotBlank` は：

```text
String専用
```

だった。

つまり：

```text
Validationアノテーションには
対象型がある
```

ことを理解した。

---

# ■ POST時Validation

```java
@PostMapping("/signup")
public String postSignup(
        Model model,
        @ModelAttribute @Validated SignupForm form,
        BindingResult bindingResult)
```

。

---

# ■ 【補足】@Validated の実際の流れ

Spring内部では：

```text
POST送信
↓
SignupForm生成
↓
Binding
↓
@Validated検出
↓
@NotBlank
@Email
@Min
などを実行
↓
エラーをBindingResultへ保存
```

している。

つまり：

```text
Binding
+
Validation
```

が連続して動いていた。

---

# ■ 【補足】th:errors の実際の役割

今回新たに理解できたのは、

```text
th:errors は
BindingResult を自動参照している
```

という点。

HTML側では：

```html
<div th:errors="*{userId}"></div>
```

と書くだけで、

```text
「userId に関するエラーを表示」
```

できる。

内部では：

```text
@Validated
↓
BindingResultへエラー保存
↓
th:errors が自動参照
```

していた。

つまり：

```text
Controller側で
エラーメッセージ文字列を
直接HTMLへ渡しているわけではない
```

という理解が重要だった。

---

# ■ 【疑問】BindingErrorとValidationErrorの違い

最初かなり混乱した。

例えば：

```text
age = abc
```

。

これは：

```text
Integer変換失敗
```

なので、

```text
BindingError
```

。

一方：

```text
age = 10
```

。

これは変換成功。

しかし：

```java
@Min(20)
```

違反なので、

```text
ValidationError
```

。

つまり：

```text
型変換
```

と

```text
業務ルール
```

は別だった。

---

# ■ ValidationMessages.properties

```properties
NotBlank={0}は必須入力です
Length={0}は{2}桁以上、{1}桁以下で入力してください
```

。

---

# ■ 【疑問】なぜ別ファイルなのに自動で繋がる？

最初、

```text
@Length
```

と、

```properties
Length=
```

が：

```text
どう結びつくのか？
```

分からなかった。

調べると：

```text
Validator側が
「Length違反発生」
↓
Length用メッセージ検索
```

を内部で自動実行していた。

つまり：

```text
Bean Validation側が
アノテーション名とキー名を対応付け
```

していた。

---

# ■ 【疑問】{1} と {2} の順番

```java
@Length(min = 4, max = 100)
```

なのに、

```text
{1} = 100
{2} = 4
```

になる理由が分かりづらかった。

調べると：

```text
属性名アルファベット順
```

で並んでいた。

つまり：

```text
max
↓
min
```

順だった。

---

# ■ 【補足】エラーメッセージ設定方法の違い

ValidationMessages.properties には、

```properties
NotBlank.signupForm.userId=
NotBlank.userId=
```

など複数の書き方が存在した。

最初は違いが曖昧だったが、

```text
どの範囲へ適用するか
```

の違いだった。

| パターン | 適用範囲 |
|---|---|
| `NotBlank.signupForm.userId` | 特定Form限定 |
| `NotBlank.userId` | 全Form共通 |
| 独自キー | 個別指定 |

つまり：

```text
メッセージ共通化の粒度
```

を調整していた。

---

# ■ 8-3 カスタムバリデーション（単項目）

この節では、

```text
独自Validation
```

を作成した。

---

# ■ 【疑問】なぜ自作する必要があるのか？

最初、

```text
Spring標準で大量にあるのに、
なぜ自作？
```

と思った。

しかし実務では：

- 社員番号形式
- パスワード強度
- 独自禁止ワード
- システム固有コード

など、

```text
システム独自ルール
```

が大量に存在する。

そのため：

```text
独自Validation作成
```

が必要だった。

---

# ■ validatorパッケージ作成

```text
validator
```

パッケージへ：

- `LengthMin.java`
- `LengthMinValidator.java`

を作成。

---

# ■ 【疑問】1つのValidationなのにファイルが2つある理由

最初、

```text
LengthMin.java
LengthMinValidator.java
```

の2つが必要な理由が分からなかった。

しかし役割が全く別だった。

| ファイル | 役割 |
|---|---|
| `LengthMin.java` | アノテーション定義 |
| `LengthMinValidator.java` | 実際の判定処理 |

つまり：

```text
「目印」
```

と

```text
「実際の判定」
```

を分離していた。

---

# ■ 【補足】役割分担

| クラス | 役割 |
|---|---|
| `LengthMin.java` | アノテーション定義 |
| `LengthMinValidator.java` | 実際の判定 |

。

---

# ■ LengthMin.java

```java
public @interface LengthMin
```

。

これは：

```text
@LengthMin(min = 5)
```

のように使える、

```text
独自アノテーション定義
```

だった。

---

# ■ 【重要理解】アノテーション属性

最初かなり混乱したのが：

```java
String message();
int min();
```

。

最初は：

```text
「メソッド？」
```

に見えた。

しかし実際には：

```text
アノテーション設定項目
```

だった。

つまり：

```java
int min()
```

を書くことで、

```java
@LengthMin(min = 5)
```

を書けるようになる。

ここは：

```text
Javaアノテーション独自文法
```

であり、
かなり理解に苦戦した。

---

# ■ 【補足】@interface の意味

```java
public @interface LengthMin
```

は、

```text
LengthMinという
アノテーション型を定義
```

している。

つまり：

```text
classではなく、
アノテーション専用型
```

を作っていた。

---

# ■ 【補足】@Documented の意味

```java
@Documented
```

は、

```text
Javadoc生成時に
アノテーション情報も出力
```

する指定。

つまり：

```text
API仕様書へ
このアノテーション情報も含める
```

という意味だった。

---

# ■ @Constraint

```java
@Constraint(validatedBy = {
    LengthMinValidator.class
})
```

。

意味：

```text
LengthMin の実際の処理は
LengthMinValidator が担当
```

。

つまり：

```text
アノテーション
↓
Validator
```

を紐づけていた。

---

# ■ 【補足】@Target の意味

```java
@Target(ElementType.FIELD)
```

は、

```text
このアノテーションを
どこへ付けられるか
```

を制御していた。

今回は：

```text
FIELD
```

なので、

```text
フィールド専用
```

だった。

---

# ■ 【補足】@Retention の意味

```java
@Retention(RetentionPolicy.RUNTIME)
```

は、

```text
実行時まで
アノテーション情報を保持
```

する設定。

Validationは：

```text
実行時に
アノテーションを読み取る
```

必要があるため、

```text
RUNTIME
```

が必要だった。

---

# ■ 【疑問】message() の default の意味

```java
String message()
default "{length.min.message}";
```

。

最初：

```text
default が何を意味するのか
```

分からなかった。

しかしこれは：

```text
message省略時の初期値
```

だった。

つまり：

```java
@LengthMin(min = 5)
```

だけ書いた場合でも、

```text
"{length.min.message}"
```

が自動利用される。

---

# ■ 【補足】groups() の意味

```java
Class<?>[] groups() default {};
```

。

これは：

```text
Validationグループ制御
```

用。

例えば：

```text
基本チェック
↓
応用チェック
```

のように、

```text
Validationを段階実行
```

できる。

また：

```text
default {}
```

は、

```text
どのグループにも属さない
```

状態を意味していた。

---

# ■ 【補足】payload() の意味

```java
Class<? extends Payload>[] payload()
default {};
```

。

これは：

```text
Validationへ追加情報を持たせる仕組み
```

。

例えば：

- Warning
- Fatal
- ログ分類

など。

つまり：

```text
単なるOK/NG以外の
メタ情報
```

を持たせられる。

---

# ■ LengthMinValidator

```java
implements ConstraintValidator<
        LengthMin,
        String>
```

。

意味：

```text
LengthMin用で、
String型をチェックするValidator
```

。

---

# ■ initialize()

```java
this.minLength = lengthMin.min();
```

。

ここでは：

```java
@LengthMin(min = 5)
```

で設定された：

```text
min = 5
```

を取得していた。

---

# ■ 【疑問】initialize() は何のために必要なのか？

最初、

```text
なぜ直接 min を使えない？
```

と感じた。

しかし：

```text
アノテーションで設定された値
```

を、

```text
Validator側へ受け渡す
```

必要があった。

つまり：

```text
@LengthMin(min = 3)
↓
initialize() で受け取る
↓
Validator内部フィールドへ保存
```

という流れだった。

---

# ■ isValid()

```java
if (value.length() < this.minLength)
```

。

ここが実際のValidation本体。

つまり：

```text
入力値長さ
<
設定最小文字数
```

なら：

```java
return false;
```

していた。

---

# ■ 【疑問】String value の正体

最初、

```java
isValid(String value, ...)
```

の：

```text
value がどこから来るのか
```

分からなかった。

しかし実際には：

```text
フォームへ入力された
対象フィールドの値
```

だった。

つまり：

```java
@LengthMin
private String userName;
```

なら、

```text
userName の入力値
```

が自動で渡されていた。

---

# ■ 【重要理解】

ここで特に重要だったのは：

```text
アノテーション自体は
何も判定していない
```

という点。

実際の判定は：

```text
Validatorクラス
```

側だった。

---

# ■ 8-4 相関チェック（複数項目）

この節では：

```text
birthday と age
```

を利用した、

```text
複数項目Validation
```

を実装した。

---

# ■ 【補足】なぜObject型なのか？

前回：

```java
ConstraintValidator<LengthMin, String>
```

だった。

しかし今回は：

```java
ConstraintValidator<BirthdayAge, Object>
```

。

これは：

```text
フォーム全体
```

を見る必要があるから。

つまり：

```text
birthday単体
```

ではなく、

```text
SignupForm全体
```

を受け取っていた。

---

# ■ BeanWrapper

```java
BeanWrapper beanWrapper =
        new BeanWrapperImpl(value);
```

。

これは：

```text
フィールド名から値取得
```

を行うための道具。

例えば：

```java
beanWrapper.getPropertyValue("birthday")
```

で、

```java
signupForm.getBirthday()
```

相当を行っていた。

---

# ■ addPropertyNode()

```java
.addPropertyNode(this.birthdayFieldName)
```

。

これは：

```text
エラー表示位置
```

を指定していた。

相関チェックは：

```text
フォーム全体エラー
```

なので、

```text
どの入力欄へ表示するか
```

を自分で指定する必要があった。

---

# ■ 今回特に重要だった理解

今回最も重要だったのは、

```text
Spring Validationは
単なる「入力禁止機能」
ではない
```

という点だった。

内部では：

- アノテーション
- Validator
- BindingResult
- メッセージ解決
- フィールド特定
- フォーム全体Validation

など、

かなり複雑な仕組みが動いていた。

特に：

```text
アノテーション
↓
Validator
↓
BindingResult
↓
Thymeleaf
```

が全て連携している点が非常に印象的だった。

---

# ■ 学び・気づき

- Validationは業務ルール検査
- BindingErrorとValidationErrorは別
- `@Validated`でValidation実行
- Validation結果はBindingResultへ入る
- ValidationMessages.propertiesでメッセージ編集
- アノテーションとValidatorは別役割
- カスタムValidationは実務で重要
- 相関チェックではフォーム全体を見る
- addPropertyNodeで表示場所を指定する

---

# ■ 苦戦した理由

今回は、

- Spring Validation
- Jakarta Validation
- Hibernate Validator
- アノテーション文法
- Validator
- 相関チェック

など、

```text
Java文法
+
Spring内部機構
```

がかなり深く結びついていた。

特に：

```text
String message()
int min()
```

が、

```text
普通のメソッドではない
```

という点は非常に難しかった。

また：

```text
アノテーション
↓
Validator呼び出し
```

の流れも最初はかなり抽象的だった。

しかし：

```text
「アノテーションは目印」
「実際の判定はValidator」
```

という役割分担が見えると、
理解がかなり進んだ。

また今回は、

```text
普段Webサイトで当たり前に見ている
「入力エラー表示」
```

の裏側で、

- Validation
- BindingResult
- エラーオブジェクト
- メッセージ解決
- フィールドマッピング
- フォーム全体判定

など、

非常に多くの仕組みが動いていることを強く実感した。

一方で、

```text
一度仕組みが分かれば、
Validation実装はかなりパターン化されている
```

とも感じた。

特に：

```text
独自アノテーション
↓
Validator作成
↓
isValid実装
```

という流れは再現性が高く、

```text
「内部は複雑だが、
実装パターン自体は整理されている」
```

という印象を持った章だった。
