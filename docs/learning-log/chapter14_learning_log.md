# 第14章 学習ログ ― 例外処理

## 学習概要

第14章では、Spring Boot における例外処理について学習した。

アプリケーションでは、

- 入力ミス
- データ重複
- URL誤り
- 想定外のシステム障害

などによって例外が発生する。

その際、

```text
エラーを発生させない
```

のではなく、

```text
エラー発生時に
どのようにユーザーへ伝えるか
どのように開発者へ通知するか
```

が重要であることを学んだ。

本章では、

- 共通エラー画面
- HTTPエラーごとの画面
- @AfterThrowing
- @ExceptionHandler
- @ControllerAdvice
- flashスコープ

について学習した。 :contentReference[oaicite:0]{index=0}

---

# 14-1 エラー画面の種類

## 学んだこと

Spring Boot ではエラー発生時、

```text
Whitelabel Error Page
```

という標準画面が表示される。

しかし、

- エラー詳細が表示される
- セキュリティ上好ましくない
- ユーザーが次に何をすればよいか分からない

という問題がある。 :contentReference[oaicite:1]{index=1}

そのため、

- 共通エラー画面
- HTTPエラーごとの専用画面

を作成することが重要であると理解した。 :contentReference[oaicite:2]{index=2}

---

# 14-2 共通エラー画面

## 学んだこと

Spring Boot では、

```text
templates/error.html
```

を作成するだけで、

アプリケーション全体の共通エラー画面として自動認識される。 :contentReference[oaicite:3]{index=3}

---

## 【疑問】error.html に値を渡していないのにエラー情報が表示されるのはなぜ？

最初は、

```html
${status}
${error}
${message}
```

へ値をセットしている箇所が見つからず不思議だった。

調べてみると、

```text
エラー発生
↓
Spring Boot が自動でエラー情報を準備
↓
error.html に渡す
```

という仕組みになっていた。

そのため、

```html
<h1 th:text="${status} + ' ' + ${error}">
```

のようなコードを書くだけで、

HTTPステータスやエラー内容を表示できる。 :contentReference[oaicite:4]{index=4}

---

## 【補足】共通エラー画面は最後の保険

共通エラー画面は、

```text
想定外のエラー
```

が発生した場合の最終手段である。

本来は、

```text
呼び出し元画面
↓
エラーメッセージ表示
```

が理想であり、

共通エラー画面へ遷移する状況はできるだけ少なくするべきである。 :contentReference[oaicite:5]{index=5}

---

# 14-3 HTTPエラーごとのエラー画面

## 学んだこと

Spring Boot では、

```text
templates/error/404.html
```

のように、

HTTPステータスコードと同じ名前のHTMLを作成することで、

エラーごとの専用画面を作成できる。 :contentReference[oaicite:6]{index=6}

---

## 【補足】HTTPステータスコード

HTTPレスポンスには、

処理結果を示すステータスコードが含まれている。

代表例は以下の通り。

| コード | 意味 |
|----------|----------|
| 200 | 正常終了 |
| 403 | 権限不足 |
| 404 | URLが存在しない |
| 500 | サーバー内部エラー |

また、

```text
200番台
↓
成功

400番台
↓
クライアント側エラー

500番台
↓
サーバー側エラー
```

という分類で覚えると理解しやすい。 :contentReference[oaicite:7]{index=7}

---

# 14-4 例外処理の実装方法

## 学んだこと

Springには例外処理を共通化する仕組みとして、

- @AfterThrowing
- @ExceptionHandler
- @ControllerAdvice

が存在する。 :contentReference[oaicite:8]{index=8}

それぞれ役割が異なり、

```text
ログ出力
↓
AfterThrowing

画面ごとの例外処理
↓
ExceptionHandler

アプリ全体の例外処理
↓
ControllerAdvice
```

という整理で理解できた。

---

# 14-5 @AfterThrowing による例外処理

## 学んだこと

AOP の

```java
@AfterThrowing
```

を利用すると、

例外発生時だけ共通処理を実行できる。 :contentReference[oaicite:9]{index=9}

今回は、

```text
DataAccessException
```

発生時にログ出力するAOPを作成した。

---

## 【補足】@AfterThrowing と throwing 属性

最初は、

```java
throwing = "ex"
```

の意味が分からなかった。

調べると、

```java
@AfterThrowing(
 value="applicationLayer()",
 throwing="ex"
)
```

は、

```text
発生した例外を
ex という名前で受け取る
```

という意味だった。 :contentReference[oaicite:10]{index=10}

そのため、

```java
public void throwingNull(DataAccessException ex)
```

の引数名も、

```text
ex
```

で一致させる必要がある。 :contentReference[oaicite:11]{index=11}

---

## 【補足】例外オブジェクトの利用

受け取った例外オブジェクトから、

```java
ex.getMessage()
```

で詳細メッセージを取得できる。

さらに、

```java
log.error("例外発生", ex);
```

と書けば、

スタックトレースも出力できる。 :contentReference[oaicite:12]{index=12}

---

## 【補足】Pointcutの組み合わせ

Pointcutは、

```java
||
```

で OR条件、

```java
&&
```

で AND条件を指定できる。 :contentReference[oaicite:13]{index=13}

また、

既存の Pointcut を組み合わせて、

新しい Pointcut を作ることもできる。 :contentReference[oaicite:14]{index=14}

---

## 【疑問】なぜ Pointcut を4つも作るのか？

最初は、

```java
bean(*Repository)
|| bean(*Mapper)
|| bean(*Service)
|| bean(*Controller)
```

を直接書けば良いと思った。 :contentReference[oaicite:15]{index=15}

しかし調べると、

目的は例外ログではなく、

```text
再利用性
保守性
```

であることが分かった。 :contentReference[oaicite:16]{index=16}

例えば、

```java
@Before("controllerLayer()")
```

や

```java
@Around("serviceLayer()")
```

のように、

後から別のAOPでも再利用できる。 :contentReference[oaicite:17]{index=17}

つまり、

```text
例外処理のため
↓
Pointcutを分けている

ではなく

将来の再利用のため
↓
Pointcutを部品化している
```

という考え方である。

---

# 14-6 コントローラーごとの例外処理

## 学んだこと

Springでは、

```java
@ExceptionHandler
```

を使うことで、

Controller単位の例外処理を実装できる。 :contentReference[oaicite:18]{index=18}

これにより、

各メソッドへ大量の

```java
try-catch
```

を書く必要がなくなる。

---

## 【疑問】@ExceptionHandler とは何か？

```java
@ExceptionHandler(DuplicateKeyException.class)
```

は、

```text
このController内で
DuplicateKeyExceptionが発生したら
このメソッドを呼ぶ
```

という意味である。 :contentReference[oaicite:19]{index=19}

AOPと同じく、

```text
例外処理を1箇所へまとめる仕組み
```

と考えると理解しやすかった。

---

## 【疑問】なぜ HttpServletRequest が必要なのか？

通常の

```java
@PostMapping
```

では、

```java
SignupForm
```

を直接受け取れる。

しかし、

```java
@ExceptionHandler
```

では Form を受け取れない。 :contentReference[oaicite:20]{index=20}

そこで、

```java
request.getParameter(...)
```

を使って、

HTTPリクエストから値を取得し、

SignupForm を再生成している。 :contentReference[oaicite:21]{index=21}

---

## 【重要】リダイレクトすると Model の値は消える

最初は、

```java
model.addAttribute(...)
```

を使えば良いと思った。

しかし、

```text
リダイレクト
↓
新しいリクエスト
```

になるため、

requestスコープである Model の値は消えてしまう。 :contentReference[oaicite:22]{index=22}

---

## 【補足】flashスコープ

リダイレクト後も値を保持したい場合、

```java
RedirectAttributes
```

を利用する。 :contentReference[oaicite:23]{index=23}

```java
addFlashAttribute()
```

で登録すると、

リダイレクト先でも値を参照できる。 :contentReference[oaicite:24]{index=24}

---

# 14-7 Webアプリケーション全体の例外処理

## 学んだこと

Controllerごとの例外処理だけでは、

- 実装漏れ
- 想定外の例外

に対応できない。 :contentReference[oaicite:25]{index=25}

そのため、

```java
@ControllerAdvice
```

を利用して、

アプリケーション全体の例外処理を用意する。 :contentReference[oaicite:26]{index=26}

---

## 【疑問】@ControllerAdvice とは何か？

```java
@ControllerAdvice
```

を付けると、

全Controllerで共有される例外処理を作成できる。 :contentReference[oaicite:27]{index=27}

つまり、

```text
各Controllerの最後の砦
```

として動作する。

---

## 【補足】@InitBinder

@ControllerAdvice では、

- @ExceptionHandler
- @InitBinder
- @ModelAttribute

を共有できる。 :contentReference[oaicite:28]{index=28}

その中でも、

```java
@InitBinder
```

は、

画面入力とJavaオブジェクトのバインド時の処理を定義するアノテーションである。 :contentReference[oaicite:29]{index=29}

ただし実務では、

JavaScript や Service 層で処理することが多く、

利用頻度は高くないことも理解した。 :contentReference[oaicite:30]{index=30}

---

## 【重要】例外処理の優先順位

例外発生時の流れは、

```text
Controller
↓
@ExceptionHandler

捕捉できない
↓
@ControllerAdvice

それでも無理
↓
共通エラー画面
```

である。 :contentReference[oaicite:31]{index=31}

つまり、

まずは呼び出し元画面へ戻し、

最後の手段として共通エラー画面を使うという考え方である。

---

# 総括

本章では、

- 共通エラー画面
- HTTPエラーごとの画面
- @AfterThrowing
- @ExceptionHandler
- flashスコープ
- @ControllerAdvice

について学習した。

特に、

```text
例外が発生した
↓
共通エラー画面へ飛ばす
```

ではなく、

```text
可能な限り
呼び出し元画面へ戻す
↓
ユーザーに修正してもらう
```

という考え方が重要であることを理解した。

また、

```text
ログ出力
↓
AOP

Controller単位
↓
@ExceptionHandler

アプリ全体
↓
@ControllerAdvice
```

という役割分担も整理できた。

例外処理は単にエラーを隠すための仕組みではなく、

```text
ユーザーへ適切な情報を伝え
開発者へ原因を残す
```

ための仕組みであると理解できた。
