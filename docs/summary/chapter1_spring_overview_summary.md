# 第1章まとめ：SpringとSpring Bootの関係

## ■ 概要

この章では、

```text
「そもそもフレームワークとは何か」
```

という基本から始まり、

- Springとは何か
- Spring Bootとは何か
- なぜ現在のJava開発でSpring Bootが標準になっているのか

について学習する。

本章は、まだ実際のコーディングや環境構築を行う前段階であり、

```text
Spring系技術全体の考え方や役割
```

を理解することが目的となる。

特に重要なのは：

- フレームワークとライブラリの違い
- IoC（制御の反転）
- DI（依存性の注入）
- Spring Framework の役割
- Spring Data や Spring Security など各機能の概要
- Spring Boot の「設定より規約」

など、

```text
今後のSpring Boot学習全体の土台
```

となる概念である。

---

# ■ 1-1 フレームワークとは？

## ◆ フレームワークの役割

フレームワークとは：

```text
「よく使う機能の土台」
```

をあらかじめ用意してくれる仕組みである。

例えばWebアプリケーションでは：

- ログイン画面
- 入力チェック
- ユーザー検索
- パスワード認証

などの処理が頻繁に必要になる。

これらを毎回ゼロから実装するのは非常に大変なため、

```text
共通部分をフレームワークが提供
```

することで開発効率を向上させる。

---

## ◆ カスタマイズ可能な仕組み

フレームワークは：

```text
デフォルト機能
```

を提供するだけではなく、

```text
必要な部分だけを差し替え可能
```

になっている。

例えば：

- ログイン画面のデザイン変更
- 独自認証処理の追加
- ログイン保持機能の追加

など、

プロジェクトごとの要件へ柔軟に対応できる。

---

## ◆ ライブラリとの違い

ライブラリは：

```text
必要な時に開発者側から呼び出すもの
```

である。

一方フレームワークは：

```text
フレームワーク側が処理全体を管理し、
開発者が必要な処理を実装する
```

という特徴がある。

---

## ◆ IoC（制御の反転）

通常は：

```text
開発者が処理全体の流れを管理
```

する。

しかしフレームワークでは：

```text
フレームワーク側が流れを管理
```

する。

この考え方を：

```text
IoC（Inversion of Control）
```

という。

---

# ■ 1-2 Springとは？

## ◆ Springの役割

Springは、

```text
Java開発におけるデファクトスタンダード
```

となっているフレームワーク群である。

特徴として：

- 開発効率が高い
- 標準でセキュア
- 豊富な機能
- 継続的に進化している

などがある。

---

## ◆ Spring Framework

Spring Framework は：

```text
Spring全体の土台
```

となる機能群である。

代表的機能：

- DI（依存性の注入）
- Spring MVC
- トランザクション管理

など。

---

## ◆ DI（Dependency Injection）

DIとは：

```text
プログラム部品を外部から差し替え可能にする仕組み
```

である。

これにより：

- 部品交換
- 機能差し替え
- テスト容易化

などが可能になる。

DIは：

```text
Springのコア技術
```

として非常に重要である。

---

# ■ Spring Data

Spring Data は：

```text
データベース操作を共通化する仕組み
```

である。

RDBだけでなく、

- MongoDB
- Cassandra
- Redis

などのNoSQLにも対応している。

---

## ◆ CRUD操作

Spring Dataでは：

```java
public interface EmployeeRepository
    extends CrudRepository<Employee, String> {
}
```

のようなRepository定義だけで、

- Create
- Read
- Update
- Delete

という基本操作を利用できる。

---

## ◆ メソッド名によるクエリー自動生成

Spring Dataでは：

```java
findByLastName
```

のようなメソッド名を書くことで、

```text
メソッド名から検索クエリーを自動生成
```

できる。

これは：

```text
「設定より規約」
```

の代表例である。

ただし、

```text
対象エンティティに対応フィールドが存在する
```

ことが前提となる。

---

# ■ Spring Security

Spring Security は：

```text
認証・認可
```

を提供するセキュリティ機能である。

---

## ◆ 認証

```text
本人確認
```

を行う。

例：

- ログイン処理

---

## ◆ 認可

```text
ユーザー権限管理
```

を行う。

例：

- 閲覧専用ユーザー
- 管理者ユーザー

などの制御。

---

# ■ Spring Batch

Spring Batch は：

```text
大量データ処理
```

を効率よく実装するための機能である。

例：

- 売上集計
- DBバックアップ
- CSV一括処理

など。

---

# ■ Spring Cloud

Spring Cloud は：

```text
クラウド環境向け機能群
```

である。

AWS や Azure などのクラウドサービスを、

```text
Javaから利用しやすくする
```

役割を持つ。

---

# ■ 1-3 Spring Bootとは？

## ◆ Springの問題点

従来のSpringでは：

- XML設定が複雑
- 環境構築が大変
- Tomcat設定が必要

など、

```text
「使い始めるまで」が非常に大変
```

だった。

---

## ◆ Spring Bootの目的

Spring Boot は：

```text
Spring開発を簡単・高速化するための仕組み
```

である。

特に：

```text
面倒な初期設定の自動化
```

が最大の特徴となる。

---

# ■ 「設定より規約」

Spring Bootでは：

```text
Convention over Configuration
（設定より規約）
```

という考え方が採用されている。

---

## ◆ デフォルト設定

よく使う設定を：

```text
最初から自動設定
```

する。

例えば：

- Tomcat組み込み
- MVC設定
- ログ設定

など。

---

## ◆ 設定変更

設定変更が必要な場合は：

```text
application.yml
application.properties
```

などの：

```text
プロパティファイル
```

を編集する。

従来の複雑なXML設定と比較して、

```text
大幅に簡略化
```

されている。

---

## ◆ 処理の自動生成

Spring Boot / Spring Dataでは：

```java
findByLastName
```

のように、

```text
規約に従った名前
```

を書くことで、

```text
内部処理を自動生成
```

できる。

これにより、

```text
開発者はビジネスロジックへ集中できる
```

ようになる。

---

# ■ 最終まとめ

第1章では、

- フレームワーク
- ライブラリ
- IoC
- DI
- Spring
- Spring Boot
- Spring Data
- Spring Security
- Spring Batch
- Spring Cloud

など、

```text
Spring Boot学習全体の基礎概念
```

について学習した。

特に重要なのは：

```text
Spring Bootは、
Springの複雑な設定を自動化し、
「設定より規約」によって
開発効率を大幅に向上させた
```

という点である。

また、

```text
Spring Bootは設定を完全になくしたのではなく、
「ほとんど」の設定を自動化した
```

という考え方も重要なポイントとなる。
