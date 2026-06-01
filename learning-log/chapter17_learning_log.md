# 第17章 学習ログ ― 設定の外部化

## 学習概要

第17章では、Spring Bootにおける設定の外部化について学習した。

実際のシステム開発では、

- 開発環境（local）
- テスト環境（dev）
- ステージング環境
- 本番環境（prod）

など複数の環境を利用することが一般的である。 :contentReference[oaicite:0]{index=0}

本章では、

- Profile
- 環境変数
- 起動引数
- 環境ごとの設定ファイル
- 環境ごとのBean切り替え
- 環境ごとのログ設定

について学習した。

---

# 17-1 設定の外部化の考え方

## 学んだこと

設定の外部化とは、

```text
環境によって変わる設定を
アプリケーションの外で管理する仕組み
```

である。 :contentReference[oaicite:1]{index=1}

例えば、

- DB接続先
- パスワード
- API接続先
- ログ出力先

などを環境ごとに切り替えられる。

---

## 【重要】jarファイルを環境ごとに作り直さない

設定をソースコードへ直接書いてしまうと、

環境が変わるたびに

```text
修正
↓
ビルド
↓
再配布
```

が必要になる。

設定の外部化を利用することで、

```text
1つのjarファイル
↓
環境ごとに設定だけ変更
```

という運用が可能になることを学んだ。 :contentReference[oaicite:2]{index=2}

---

# 17-2 設定値を環境ごとに切り替える

## 学んだこと

Spring Bootでは、

```yaml
spring:
  profiles:
    active:
```

を利用して有効なプロファイルを切り替える。 :contentReference[oaicite:3]{index=3}

また、

```text
application-local.yml
application-dev.yml
```

のような環境別設定ファイルを用意することで、

環境ごとに異なる設定値を利用できる。 :contentReference[oaicite:4]{index=4}

---

# 17-3 処理の実装を環境ごとに切り替える

## 学んだこと

Springでは、

```java
@Profile("local")
```

のように指定することで、

環境ごとに異なるBean定義を有効化できる。 :contentReference[oaicite:5]{index=5}

今回のサンプルでは、

- local → MyBatis版UserService
- dev → JPA版UserService

を切り替えていた。 :contentReference[oaicite:6]{index=6}

---

## 【重要】DIされる実装クラス自体を変更できる

設定値だけでなく、

```text
どの実装クラスをIoCコンテナーへ登録するか
```

まで環境によって変更できることを理解した。

これにより、

- スタブ
- テスト用実装
- 本番実装

などを簡単に切り替えられる。 :contentReference[oaicite:7]{index=7}

---

# 17-4 ログ出力先の切り替え

## 学んだこと

Logbackでは、

```xml
<springProperty>
```

を利用することで、

YAMLの設定値を取得できる。 :contentReference[oaicite:8]{index=8}

そのため、

環境ごとに異なるログ出力先を利用できることを学習した。

---

# 17-5 ログレベルの切り替え

## 学んだこと

Logbackでは、

```xml
<springProfile>
```

を利用することで、

環境ごとに異なるログ設定を適用できる。 :contentReference[oaicite:9]{index=9}

例えば、

- local → DEBUG
- dev → INFO

のような設定が可能である。 :contentReference[oaicite:10]{index=10}

---

# 総括

本章では、

- 設定の外部化
- Profile
- 環境変数
- 起動引数
- application-local.yml
- application-dev.yml
- @Profile
- springProperty
- springProfile

について学習した。

今回扱った内容は、過去にプロパティファイルやXMLによる設定管理を何度も経験していたため、比較的スムーズに理解できた。

また、

```text
環境変数
↓
Profile
↓
設定ファイルやBeanを切り替える
```

というSpring Bootの仕組みを理解できたことで、実際の開発環境・テスト環境・本番環境の運用イメージを持てるようになった。 :contentReference[oaicite:11]{index=11}
