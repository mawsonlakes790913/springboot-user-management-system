# 第15章 学習ログ ― Spring Security

## 学習概要

第15章では、Spring Securityを利用した認証（Authentication）と認可（Authorization）について学習した。

これまで作成してきたSpring Bootアプリケーションは、誰でもURLへアクセスできる状態だった。しかし実際のWebアプリケーションでは、

- ログイン機能
- 権限制御
- パスワード保護
- CSRF対策

などのセキュリティ機能が必須である。

本章では、

- SecurityFilterChain
- 認証要否の設定
- フォームログイン
- インメモリ認証
- データベース認証
- PasswordEncoder
- ログアウト
- CSRF対策
- Remember-Me認証
- ログインユーザー情報取得
- URL認可
- 画面表示認可
- メソッド認可

について学習した。

---

# 15-1 認証と認可

## 学んだこと

Spring Securityでは大きく

- 認証（Authentication）
- 認可（Authorization）

の2つを扱う。

認証は、

```text
そのユーザーが誰なのか
```

を確認する仕組みであり、ログイン機能そのものである。

一方認可は、

```text
そのユーザーに何を許可するか
```

を制御する仕組みである。

例えば、

| ロール | できること |
|----------|----------|
| 一般ユーザー | 自分の情報のみ閲覧 |
| 人事担当 | ユーザー情報管理 |
| 管理者 | 全機能利用可能 |

のような制御を行う。

---

## 【補足】認証と認可は別物

最初は両者を同じものだと思っていた。

しかし実際には、

```text
認証
↓
本人確認

認可
↓
権限確認
```

であり、役割が全く異なる。

ログインできたとしても、

```text
管理者画面へ入れる
```

とは限らない。

これは認可で制御されているためである。

---

## 【補足】MFA（多要素認証）

近年はユーザーIDとパスワードだけではなく、

- SMS認証
- 認証アプリ
- 指紋認証
- 顔認証

などを組み合わせるMFA（Multi Factor Authentication）が一般的になっている。

パスワードが漏洩しても追加要素が必要になるため、セキュリティを大幅に向上できる。

---

## 【重要】Spring Securityは認証と認可を一元管理する

Spring Securityを利用すると、

- ログイン
- ログアウト
- 権限制御
- セッション管理
- CSRF対策

などを統一的に管理できる。

---

# 15-2 SecurityConfigとSecurityFilterChain

## 学んだこと

Spring Securityの設定は、

```java
@Configuration
@EnableWebSecurity
```

を付けた設定クラスに記述する。

その中心となるのが

```java
SecurityFilterChain
```

である。

---

## 【補足】フィルターチェーンとは何か

Spring Securityでは、

```text
HTTPリクエスト
↓
Filter1
↓
Filter2
↓
Filter3
↓
Controller
```

という流れで処理される。

つまり、

Controllerへ到達する前にセキュリティチェックが実行される。

---

## 【疑問】フィルターチェーンができるのはSecurityFilterChainだけ？

はい。

Spring Securityがフィルターチェーンとして認識するのは、

```java
SecurityFilterChain
```

型のBeanだけである。

そのため、

```java
return http.build();
```

によって最終的にSecurityFilterChainオブジェクトを生成する必要がある。

---

## 【疑問】build()とは何か？

最初は単なる終了処理だと思っていた。

しかし実際には、

```java
http
  .authorizeHttpRequests(...)
  .formLogin(...)
  .csrf(...)
```

などで登録した設定をもとに、

```java
SecurityFilterChain
```

を完成させるメソッドである。

つまり、

```text
HttpSecurity
↓
設計図

build()
↓
完成品
```

という関係になっている。

---

## 【重要】HttpSecurityは設定オブジェクト

```java
HttpSecurity
```

はフィルターチェーンそのものではない。

認可設定やログイン設定を蓄積するための設定オブジェクトであり、

最後に

```java
http.build()
```

で実際のSecurityFilterChainが生成される。

---

# 15-3 認証要否の設定

## 学んだこと

認証の必要・不要は、

```java
http.authorizeHttpRequests(...)
```

で設定する。

例えば、

```java
.requestMatchers("/login").permitAll()
```

でログイン画面を公開し、

```java
.anyRequest().authenticated()
```

でそれ以外のURLをログイン必須にできる。

---

## 【補足】authorizeHttpRequests()の正体

最初はラムダ式の意味が分からなかった。

```java
http.authorizeHttpRequests(
    authorize -> authorize
        .requestMatchers(...)
        .permitAll()
)
```

は、

```text
authorizeという設定オブジェクトを受け取ったら

requestMatchers()
permitAll()

を実行してください
```

という処理を渡している。

---

## 【疑問】なぜラムダ式を使うのか？

最初は、

```java
authorize.requestMatchers(...)
```

を直接渡せばよいと思った。

しかし、

```text
authorize
```

というオブジェクトはSpring Security側が生成するため、

事前には存在しない。

そのため、

```text
生成されたらこういう処理をしてください
```

という形でラムダ式を渡している。

---

## 【重要】設定順序が重要

```java
.requestMatchers(...)
.permitAll()

.anyRequest().authenticated()
```

は上から順に適用される。

そのため、

```java
.anyRequest().authenticated()
```

を先に書くと、

すべてのURLがログイン必須になる。

---

# 15-4 フォームログイン

## 学んだこと

ログイン機能は、

```java
.formLogin(...)
```

で設定する。

ここでは、

- ログイン画面
- ユーザーID項目名
- パスワード項目名
- ログイン成功時
- ログイン失敗時

などを設定できる。

---

## 【疑問】パスワードとユーザーIDを入力欄から取得するのはなぜ？

```java
.usernameParameter("userId")
.passwordParameter("password")
```

が必要なのは、

Spring SecurityがHTMLを見ていないからである。

Spring Securityのデフォルトは、

```html
<input name="username">
<input name="password">
```

である。

しかし今回のアプリでは、

```html
<input name="userId">
```

になっている。

そのため、

```java
.usernameParameter("userId")
```

で入力欄名を教えている。

なお、

```java
.passwordParameter("password")
```

はデフォルトと同じなので省略できる場合もある。

---

## 【重要】permitAll()が必要な理由

ログイン画面自体が認証必須になると、

```text
ログインするためにログインが必要
```

という矛盾が発生する。

そのため、

```java
.permitAll()
```

でログイン関連URLを公開する必要がある。

# 15-5 インメモリ認証

## 学んだこと

開発初期段階では、

- データベースが未完成
- usersテーブルが存在しない
- ユーザー設計が未確定

ということがある。

そのような場合、

```java
InMemoryUserDetailsManager
```

を利用すると、

メモリ上に仮ユーザーを作成してログイン機能を確認できる。

---

## 【補足】UserDetailsServiceの役割

Spring Securityは、

```java
loadUserByUsername()
```

を呼び出してユーザー情報を取得する。

つまり、

```text
ユーザー情報をどこから取得するか
```

を担当するのが

```java
UserDetailsService
```

である。

---

## 【疑問】なぜ自分でUserDetailsServiceを実装していないのに動くのか？

```java
@Bean
UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(...);
}
```

となっているが、

実は

```java
InMemoryUserDetailsManager
```

自身が

```java
UserDetailsService
```

を実装済みである。

つまり、

```text
Spring Securityさん、
ユーザー検索は
InMemoryUserDetailsManagerに任せます
```

と言っているのと同じである。

---

## 【重要】UserDetailsServiceを差し替えるだけで認証方式を変更できる

Spring Securityは、

```text
ユーザー情報取得
```

だけを

```java
UserDetailsService
```

へ委譲している。

そのため、

- インメモリ認証
- DB認証
- LDAP認証

などへ柔軟に切り替えられる。

---

# 15-6 データベース認証

## 学んだこと

実務では、

```java
UserDetailsService
```

を実装したクラスを作成し、

データベースからユーザー情報を取得する。

```java
@Service
public class UserDetailsServiceImpl
        implements UserDetailsService {
}
```

のように実装する。

---

## 【補足】認証処理全体の流れ

Spring Securityの認証処理を簡単に表すと、

```text
ログイン画面
↓
ID・PW入力
↓
Spring Security
↓
loadUserByUsername()
↓
DBからユーザー取得
↓
パスワード照合
↓
ログイン成功
```

である。

---

## 【疑問】UserDetailsとは何か？

最初は

```java
UserDetails
```

と

```java
MUser
```

の違いが分からなかった。

しかし実際には、

```text
MUser
↓
アプリケーション独自のユーザー

UserDetails
↓
Spring Securityが理解できるユーザー情報
```

である。

そのため、

```java
new User(...)
```

を生成してSpring Securityへ渡している。

---

## 【重要】UsernameNotFoundException

ユーザーが存在しない場合は、

```java
throw new UsernameNotFoundException(...)
```

を送出する。

これによりSpring Securityは

```text
認証失敗
```

として扱う。

---

# 15-7 PasswordEncoderとパスワードハッシュ化

## 学んだこと

パスワードは平文保存してはいけない。

そのため、

```java
PasswordEncoder
```

を利用してハッシュ化する。

---

## 【補足】PasswordEncoderとは

PasswordEncoderは、

```text
平文パスワード
↓
ハッシュ化
```

を行うインターフェースである。

代表的な実装が、

```java
BCryptPasswordEncoder
```

である。

---

## 【疑問】なぜハッシュ化するのか？

例えばデータベースが漏洩した場合、

```text
password
```

がそのまま保存されていると、

全ユーザーのパスワードが漏洩する。

しかし、

```text
$2a$10$...
```

のようなハッシュ値で保存しておけば、

元のパスワードへ戻すことは極めて困難である。

---

## 【重要】パスワード比較はequalsではない

Spring Securityは、

```java
encoder.matches()
```

によって比較する。

毎回ハッシュ値が変わるため、

```java
password.equals(...)
```

では比較できない。

---

# 15-8 ログアウト

## 学んだこと

ログアウトは、

```java
.logout(...)
```

で設定する。

例えば、

```java
.logoutUrl("/logout")
.logoutSuccessUrl("/login?logout")
```

のように設定する。

---

## 【補足】ログアウト時に行われること

ログアウトすると、

```text
セッション破棄
↓
認証情報削除
↓
未ログイン状態
```

になる。

---

## 【重要】ログアウト後は再認証が必要

ログアウト後は、

保護されたURLへアクセスすると再度ログインが必要になる。

---

# 15-9 CSRF対策

## 学んだこと

CSRFとは、

```text
Cross Site Request Forgery
```

の略である。

ログイン中ユーザーを騙して、

意図しないPOST処理を実行させる攻撃である。

---

## 【補足】攻撃の例

例えば銀行サイトへログインしたまま、

悪意あるサイトへアクセスすると、

```html
<form action="銀行サイト">
```

などを利用して、

勝手に送金処理を実行される可能性がある。

---

## 【疑問】なぜCSRFを無効化しているのか？

教科書では、

```java
http.csrf(csrf -> csrf.disable());
```

としている。

これは、

```text
学習しやすさ
＞
実務的セキュリティ
```

を優先しているためである。

実務では通常無効化しない。

---

## 【重要】CSRFトークン

Spring Securityは通常、

フォームへCSRFトークンを埋め込み、

送信時に照合することで攻撃を防いでいる。

---

# 15-10 Remember-Me認証

## 学んだこと

Remember-Me認証を利用すると、

セッションが切れてもログイン状態を維持できる。

---

## 【補足】通常ログインとの違い

通常は、

```text
セッション切れ
↓
再ログイン
```

となる。

Remember-Meでは、

Cookieに保存された情報を利用して再認証する。

---

## 【疑問】remember-meとは何か？

```html
<input name="remember-me">
```

のチェック状態が送信される。

Spring Securityは、

```java
.rememberMe(...)
```

によってこの情報を利用する。

---

## 【重要】Remember-Meはセッションとは別

Remember-Meは、

セッションが切れてもログイン状態を復元する仕組みであり、

セッションそのものを延長する機能ではない。

---

# 15-11 ログインユーザー情報取得

## 学んだこと

ログイン中ユーザーの情報は、

Spring Securityから取得できる。

例えば、

```java
Authentication
```

や

```java
Principal
```

を利用する。

---

## 【補足】取得できる情報

- ユーザーID
- ロール
- 認証状態

などを取得できる。

---

## 【重要】セッションから直接取得しない

Spring Securityが管理している認証情報から取得する。

---

# 15-12 認可

## 学んだこと

認可は、

```text
そのユーザーに何を許可するか
```

を制御する仕組みである。

Spring Securityでは、

- URL認可
- 画面認可
- メソッド認可

の3段階で制御できる。

---

## 【補足】URL認可

```java
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

のように設定する。

---

## 【補足】画面認可

Thymeleaf Security拡張ライブラリを利用して、

```html
sec:authorize="hasRole('ADMIN')"
```

のように表示制御できる。

---

## 【補足】メソッド認可

```java
@PreAuthorize("hasRole('ADMIN')")
```

によって、

メソッド実行自体を制御できる。

---

## 【重要】画面を隠すだけでは不十分

ボタンを非表示にしても、

URLを直接入力される可能性がある。

そのため、

```text
画面
↓
URL
↓
メソッド
```

の複数箇所で認可することが重要である。

---

# 総括

本章では、

- 認証
- 認可
- SecurityFilterChain
- フォームログイン
- インメモリ認証
- データベース認証
- UserDetailsService
- PasswordEncoder
- ログアウト
- CSRF
- Remember-Me
- URL認可
- 画面認可
- メソッド認可

について学習した。

特に、

```text
HTTPリクエスト
↓
SecurityFilterChain
↓
Controller
```

というSpring Securityの内部構造と、

```text
UserDetailsService
↓
ユーザー取得

PasswordEncoder
↓
パスワード保護
```

という認証の仕組みを理解できたことが大きな収穫だった。

また、

```text
認証
↓
誰かを確認

認可
↓
何ができるかを制御
```

という役割の違いも明確に理解できた。

Spring Securityは単なるログイン機能ではなく、

```text
アプリケーション全体のセキュリティ基盤
```

であることを理解できた。
