# 第13章まとめ：AOP

## ■ 概要

この章では、

```text
AOP（Aspect Oriented Programming）
```

について学習する。

これまでの章では：

- Controller
- Service
- Repository
- DBアクセス

など、

```text
業務処理そのもの
```

を中心に実装してきた。

しかし実際の開発では：

- ログ出力
- トランザクション管理
- セキュリティ
- 例外処理

など、

```text
複数クラスで共通して必要になる処理
```

も多く存在する。

これらを各クラスへ直接記述すると：

- コード重複
- 可読性低下
- 修正漏れ
- 保守性低下

などの問題が発生する。

そこで利用されるのが：

```text
AOP（アスペクト指向プログラミング）
```

である。

AOPを利用すると：

```text
共通処理
```

を、

```text
1箇所へまとめて管理
```

できるようになる。

本章では：

- AOPの基本概念
- Advice
- JoinPoint
- Pointcut
- Spring AOP の仕組み
- Before / After
- Around

などについて学習する。

また、

```text
ログ出力AOP
```

を実際に実装しながら、

```text
Spring AOP の基本的な使い方
```

を学習する。

---

# ■ 13-1 AOPの基本

## ◆ AOPとは

AOPとは：

```text
Aspect Oriented Programming
```

の略である。

日本語では：

```text
アスペクト指向プログラミング
```

という。

AOPでは：

```text
複数箇所で共通して必要になる処理
```

を、

```text
業務ロジックから分離
```

して管理する。

---

## ◆ AOPを使わない場合の問題

ログ出力を例にすると、

```text
開始ログ
終了ログ
```

を、

```text
すべてのControllerやService
```

へ書かなければならない。

その結果：

- コード重複
- 可読性低下
- 修正箇所増加
- ログ記述漏れ

などの問題が発生する。

---

## ◆ AOPを使うメリット

AOPを利用すると：

```text
共通処理
```

を、

```text
Aspectクラス
```

へ集約できる。

これにより：

- 保守性向上
- 可読性向上
- 修正容易化

を実現できる。

---

## ◆ @Transactional もAOP

第11章で学習した：

```java
@Transactional
```

も、

```text
Spring AOP
```

を利用して実現されている。

---

# ■ AOPの専門用語

## ◆ Advice

AOPで実行される：

```text
具体的な処理内容
```

のこと。

例：

- ログ出力
- トランザクション開始
- セキュリティチェック

など。

---

## ◆ JoinPoint

Adviceを挿入できる：

```text
対象
```

のこと。

Spring AOPでは：

```text
メソッド
```

のみが対象となる。

---

## ◆ Pointcut

どのクラス・メソッドへ：

```text
Adviceを適用するか
```

を指定する式。

---

# ■ Adviceの種類

Spring AOPでは、

```text
Adviceの実行タイミング
```

を指定できる。

---

## ◆ Before

```text
対象メソッド実行前
```

に処理を行う。

---

## ◆ After

```text
対象メソッド終了後
```

に処理を行う。

正常終了・異常終了の両方で実行される。

---

## ◆ AfterReturning

```text
正常終了時のみ
```

実行される。

---

## ◆ Around

```text
実行前後
```

の両方で処理を行う。

---

## ◆ AfterThrowing

```text
例外発生時のみ
```

実行される。

---

# ■ Pointcutの指定方法

Pointcutでは、

```text
どの処理を対象にするか
```

を指定する。

主な指定方法：

- execution
- within
- @within
- @annotation
- bean

など。

---

## ◆ execution

最も利用頻度が高いPointcut指定方法。

例：

```java
@Pointcut("execution(* *..*UserService*.*(..))")
```

---

## ◆ execution の構文

```text
execution(
    修飾子
    戻り値
    パッケージ名.クラス名.メソッド名(引数)
    例外
)
```

---

## ◆ ワイルドカード

### *

```text
任意の文字列
```

を表す。

---

### ..

```text
0個以上
```

を表す。

---

### +

```text
サブクラス・実装クラス
```

も対象に含める。

---

# ■ Spring AOP の仕組み

Spring AOPでは：

```text
Proxy（代理オブジェクト）
```

を利用してAOPを実現している。

---

## ◆ Proxyの役割

通常：

```text
Controller
↓
Service
```

という流れになる。

しかしSpringでは：

```text
Controller
↓
Proxy
↓
Service
```

という構造になる。

---

## ◆ Proxyによる割り込み

Proxyが：

- Advice実行
- 実際のメソッド呼び出し

を仲介することで、

```text
メソッド前後へ共通処理
```

を挿入できる。

---

# ■ 13-2 AOPの実装（1）Before・After

## ◆ 作成する内容

UserService実行時に：

- 開始ログ
- 終了ログ

を出力するAOPを実装する。

---

## ◆ 作業手順

### 1. ファイル作成

以下を作成する。

```text
aspect
└── LogAspect.java
```

---

### 2. Spring AOP の追加

pom.xmlへ：

```xml
<!-- Spring AOP -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>

<!-- AspectJ -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

を追加する。

---

### 3. AOPクラス作成

```java
@Aspect
@Component
@Slf4j
public class LogAspect {
}
```

を作成する。

---

## ◆ @Aspect

```text
AOPクラス
```

であることを表す。

---

## ◆ @Component

Beanとして：

```text
IoCコンテナーへ登録
```

する。

---

## ◆ Pointcut作成

```java
@Pointcut("execution(* *..*UserService*.*(..))")
public void userService() {}
```

を定義する。

---

## ◆ Pointcutの意味

```text
クラス名に UserService を含む
すべてのメソッド
```

を対象にしている。

---

## ◆ Beforeログ

```java
@Before("userService()")
```

を利用する。

---

## ◆ Afterログ

```java
@After("userService()")
```

を利用する。

---

## ◆ JoinPoint

```java
jp.getSignature()
```

を利用すると：

```text
実行対象メソッド情報
```

を取得できる。

---

## ◆ 実行確認

```text
http://localhost:8080/user/list
```

へアクセスし、

```text
開始ログ
終了ログ
```

が出力されれば成功。

---

# ■ 13-3 AOPの実装（2）Around

## ◆ Aroundの追加

@GetMapping と @PostMapping を対象に：

```text
Controller実行ログ
```

を出力する。

---

## ◆ Pointcut追加

```java
@Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
```

```java
@Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
```

を追加する。

---

## ◆ Around作成

```java
@Around("getMapping() || postMapping()")
```

を定義する。

---

## ◆ ProceedingJoinPoint

Aroundでは：

```java
ProceedingJoinPoint
```

を利用する。

---

## ◆ jp.proceed()

```java
Object result = jp.proceed();
```

で：

```text
実際の対象メソッド
```

を実行する。

---

## ◆ Aroundの重要ポイント

Aroundでは：

```text
jp.proceed()
```

を書かないと、

```text
対象メソッド自体が実行されない
```

ため注意が必要。

---

## ◆ returnも必要

```java
return result;
```

を返す必要がある。

---

## ◆ Pointcutの複数指定

```java
getMapping() || postMapping()
```

のように：

```text
OR条件
```

で指定できる。

---

## ◆ 条件演算子

### ||

```text
OR
```

---

### &&

```text
AND
```

---

### !

```text
NOT
```

---

## ◆ 実行確認

```text
http://localhost:8080/login
```

へアクセスし、

```text
Controller開始ログ
Controller終了ログ
```

が出力されれば成功。

---

# ■ AOPで重要なポイント

## ◆ 共通処理を分離できる

ログ出力などを：

```text
業務ロジックから分離
```

できる。

---

## ◆ Proxyで実現される

Spring AOPは：

```text
Proxy
```

によって動作する。

---

## ◆ Pointcutが重要

```text
どこへAdviceを適用するか
```

を柔軟に指定できる。

---

## ◆ Aroundは強力

```text
前後処理
例外処理
戻り値制御
```

などを柔軟に実装できる。

---

# ■ 最終まとめ

第13章では：

- AOP
- Advice
- JoinPoint
- Pointcut
- execution
- Proxy
- Before
- After
- Around

などについて学習した。

特に重要なのは：

```text
共通処理を業務ロジックから分離し、
1箇所で管理できる
```

という点である。

また、

```text
Spring AOP は Proxy を利用して動作する
```

という内部構造も重要である。

本章の内容は：

- ログ出力
- トランザクション管理
- セキュリティ
- 例外処理

など、

実務でも頻繁に利用される重要な技術である。
