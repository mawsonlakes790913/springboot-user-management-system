# 第13章 学習ログ ― AOP

## 学習概要

第13章では、Spring AOP（Aspect Oriented Programming：アスペクト指向プログラミング）について学習した。

これまでの章では、Controller や Service に業務処理を実装してきたが、実際の開発では、

- ログ出力
- トランザクション管理
- セキュリティ
- 例外処理

など、多くのクラスに共通して必要となる処理が存在する。

AOPを利用することで、こうした共通処理を業務ロジックから分離し、一元管理できることを理解した。

本章では、

- AOPの基本概念
- Advice
- JoinPoint
- Pointcut
- Spring AOPの内部構造（Proxy）
- Before / After
- Around

について学習した。

---

# 13-1 AOPの基本

## 学んだこと

AOP（Aspect Oriented Programming）は、アプリケーション全体で共通して必要となる処理をまとめて管理するための仕組みである。

例えばログ出力を行う場合、

```java
@GetMapping("/sample")
public String get() {

    log.info("開始");

    // 処理

    log.info("終了");
}
```

のようなコードをすべてのControllerやServiceに書く必要がある。

しかしAOPを利用すると、

```java
@Before(...)
public void startLog() {}

@After(...)
public void endLog() {}
```

を1箇所に定義するだけで、対象となるすべてのメソッドに共通処理を適用できる。

これにより、

- 可読性向上
- 保守性向上
- 実装漏れ防止

が実現できる。

---

## 【補足】AOPのデメリット

AOPは便利である一方、

```text
自分が書いていない処理が動いている
```

ように見えることがある。

そのため、

```text
aspect
```

など専用パッケージにまとめ、

AOPであることが分かりやすい構成にすることが重要である。

---

## 【重要】@TransactionalもAOPで実現されている

第11章で学習した

```java
@Transactional
```

も内部的にはSpring AOPで実装されている。

トランザクション管理は業務ロジックそのものではなく、

```text
業務処理を正しく実行するための共通処理
```

だからである。

AOPはログ出力だけでなく、

- トランザクション管理
- セキュリティ
- 例外処理

などにも利用されていることを理解した。

---

# 13-2 AOPの専門用語

## 学んだこと

AOPでは以下の3つの用語が重要である。

| 用語 | 説明 |
|--------|--------|
| Advice | AOPで実行する処理内容 |
| JoinPoint | Adviceを挿入できる対象 |
| Pointcut | Adviceを適用する対象を指定する条件 |

---

## 【補足】Adviceの実行タイミング

Spring AOPには以下の5種類のAdviceがある。

| 実行タイミング | 説明 |
|--------|--------|
| Before | メソッド実行前 |
| After | メソッド実行後（正常・異常問わず） |
| AfterReturning | 正常終了時のみ |
| Around | 実行前後 |
| AfterThrowing | 例外発生時のみ |

---

## 【重要】Spring AOPのJoinPointはメソッドのみ

AOP一般では、

- メソッド
- コンストラクタ
- フィールドアクセス

などがJoinPointになり得る。

しかしSpring AOPでは、

```text
メソッド実行のみ
```

がJoinPointである。

---

# 13-3 Pointcutの理解

## 学んだこと

Pointcutとは、

```text
どのJoinPointにAdviceを適用するか
```

を指定する条件式である。

例えば、

```java
@Pointcut(
 "@within(org.springframework.stereotype.Controller)"
)
public void controllerClass() {}
```

は、

```text
@Controllerが付いたクラス内のメソッド
```

を対象とする条件である。

---

## 【補足】Pointcutメソッドは実行されない

最初は、

```java
public void controllerClass() {}
```

が呼ばれるように見えた。

しかし実際には、

```java
@Pointcut(...)
```

が付いた時点で、

```text
実行するためのメソッド
```

ではなく、

```text
条件に名前を付けるためのラベル
```

になる。

つまり、

```java
@Pointcut("条件")
public void 名前() {}
```

は、

```text
条件
↓
名前を付ける
```

だけである。

---

## 【疑問】@within(org.springframework.stereotype.Controller)とは何か？

最初は、

```java
@within(org.springframework.stereotype.Controller)
```

が何を指しているのか理解できなかった。

調べた結果、

```text
@Controllerが付いているクラスの中にあるメソッド
```

を対象にする条件であることが分かった。

つまり、

```java
@Controller
public class UserController {
}
```

なら、

```text
UserController内のメソッド
```

が対象になる。

---

## 【疑問】@Before("controllerClass()")の意味

最初は、

```java
@Before("controllerClass()")
```

が、

```text
controllerClass()メソッドの前
```

に見えた。

しかし実際には、

```text
controllerClassが表している条件
```

に一致したJoinPointの前という意味である。

今回の場合、

```java
controllerClass()
```

↓

```java
@within(org.springframework.stereotype.Controller)
```

↓

```text
@Controllerクラス内のメソッド
```

なので、

```text
Controllerのメソッドが実行される直前
```

にAdviceが実行される。

---

# 13-4 Spring AOPの内部構造

## 学んだこと

Spring AOPでは、

```text
呼び出し元
↓
Proxy
↓
本物のBean
```

という構造になっている。

そのため、

呼び出し元は直接Beanを呼び出しているわけではない。

---

## 【補足】なぜAdviceを挿入できるのか？

SpringはBeanを直接呼び出さず、

Proxy（代理人）を経由して呼び出す。

イメージとしては、

```java
log.info("開始");

service.someMethod();

log.info("終了");
```

をProxyが自動生成しているようなものである。

この仕組みにより、

業務ロジックを書き換えることなく共通処理を割り込ませることができる。

---

## 【重要】DIがあるからProxyを挟める

SpringはIoCコンテナーでBeanを管理している。

そのため、

```java
new UserService()
```

を直接呼ぶのではなく、

コンテナー管理下のProxyを返すことができる。

AOPが成立する大前提はDIにあると理解した。

---

# 13-5 Before・Afterによるログ出力

## 学んだこと

Before と After を利用して、

UserServiceの実行前後にログ出力を行った。

---

## 【補足】executionの意味

```java
@Pointcut(
 "execution(* *..*UserService*.*(..))"
)
```

は、

```text
クラス名にUserServiceを含むクラスの
すべてのメソッド
```

を対象にする。

---

## 【疑問】executionのワイルドカード

```java
execution(* *..*UserService*.*(..))
```

の意味を調べた。

```text
最初の *
↓
戻り値は何でもよい

*..*UserService*
↓
クラス名にUserServiceを含む

.*
↓
メソッド名は何でもよい

(..)
↓
引数は何個でもよい
```

つまり、

```text
UserService系クラスの全メソッド
```

という意味である。

---

## 【補足】JoinPoint.getSignature()

```java
jp.getSignature()
```

を呼ぶと、

対象メソッドのシグネチャを取得できる。

シグネチャとは、

```text
メソッド名
引数
戻り値
```

などを特定する情報である。

---

# 13-6 Aroundによるログ出力

## 学んだこと

Aroundを利用すると、

```text
実行前
↓
対象メソッド
↓
実行後
```

を1つのAdviceで制御できる。

---

## 【補足】複数Pointcutの指定

```java
@Around(
 "getMapping() || postMapping()"
)
```

のように、

```text
||
↓
OR

&&
↓
AND

!
↓
NOT
```

を使って条件を組み合わせられる。

---

## 【疑問】なぜ jp.proceed() でControllerを呼び出せるのか？

最初は、

```java
Object result = jp.proceed();
```

だけでControllerメソッドを呼び出せる理由が分からなかった。

調べた結果、

ProceedingJoinPoint は

```text
本来実行されるはずだったメソッド
```

の情報を保持していることが分かった。

そして、

```java
jp.proceed();
```

を呼ぶと、

SpringのProxyが本物のControllerメソッドを実行する。

つまり、

```java
jp.proceed();
```

は、

```text
ここで本来のメソッドを実行してください
```

という命令である。

---

## 【重要】proceed()とreturnを忘れると対象メソッドが実行されない

Aroundでは、

```java
Object result = jp.proceed();

return result;
```

が必須である。

これを書かないと、

```text
Adviceだけ実行
↓
本来のControllerは実行されない
```

状態になってしまう。

---

# 総括

本章では、

- AOPの考え方
- Advice
- JoinPoint
- Pointcut
- Proxy
- Before
- After
- Around

について学習した。

特に、

```text
業務ロジック
↓
Controller / Service

共通処理
↓
AOP
```

という責務分離の考え方が理解できた。

また、

```text
呼び出し元
↓
Proxy
↓
本物のBean
```

というSpring AOPの内部構造を理解できたことで、

@Transactionalやログ出力などの仕組みもイメージできるようになった。

AOPは単なるログ出力の仕組みではなく、

```text
共通処理を業務ロジックから分離するための仕組み
```

であると理解できた。
