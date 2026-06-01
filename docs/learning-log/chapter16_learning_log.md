# 第16章 学習ログ ― ログ

## 学習概要

第16章では、Spring Bootにおけるログ出力について学習した。

アプリケーション開発においてログは、

- 障害調査
- 動作確認
- 監査証跡

などに利用される重要な機能である。

本章では、

- application.ymlによるログ設定
- logback-spring.xmlによるログ設定
- 標準出力
- ファイル出力
- ログローテーション
- ログレベル

について学習した。 :contentReference[oaicite:0]{index=0}

---

# 16-1 プロパティファイルによるログ設定

## 学んだこと

Spring Bootでは、

```yaml
logging:
```

以下に設定を記述することでログ出力を簡単に設定できる。

また、

- ログファイル名
- 出力先
- ログローテーション

なども設定できることを学習した。 :contentReference[oaicite:1]{index=1}

---

## 【重要】プロパティファイルは簡単だが柔軟性は低い

application.ymlによる設定は簡単である一方、

細かなログ出力設定には向いていない。

実務ではXMLによる設定が利用されることが多いことを学んだ。 :contentReference[oaicite:2]{index=2}

---

# 16-2 XMLファイルによるログ設定

## 学んだこと

Spring Bootでは、

```text
logback-spring.xml
```

を利用してログ出力の詳細設定を行う。

XML形式で設定することで、

- 出力先
- 出力形式
- ログローテーション
- ログレベル

などを細かく制御できることを学習した。 :contentReference[oaicite:3]{index=3}

---

## 【重要】Spring Bootではlogback-spring.xmlを使用する

Logback本来の設定ファイルは

```text
logback.xml
```

である。

しかしSpring Bootでは、

Springの機能を利用できる

```text
logback-spring.xml
```

を使用することが推奨されている。 :contentReference[oaicite:4]{index=4}

---

# 16-3 標準出力

## 学んだこと

XMLファイルでは、

```xml
<appender>
```

を利用してログの出力先を設定する。

ConsoleAppenderを使用することで、

Eclipseのコンソールへログを出力できることを学習した。 :contentReference[oaicite:5]{index=5}

---

# 16-4 ファイル出力

## 学んだこと

RollingFileAppenderを利用することで、

ログをファイルへ出力できる。

また、

```xml
<rollingPolicy>
```

を利用することで、

日次や月次でログファイルを切り替えるログローテーションを設定できることを学習した。 :contentReference[oaicite:6]{index=6}

---

## 【重要】ログローテーションは必須

ログを1つのファイルへ出力し続けると、

- ファイルサイズ肥大化
- 検索性低下
- ディスク容量不足

などの問題が発生する。

そのため実務ではログローテーションを設定することが重要である。 :contentReference[oaicite:7]{index=7}

---

# 16-5 ログレベル

## 学んだこと

ログにはレベルが存在する。

```text
trace
↓
debug
↓
info
↓
warn
↓
error
```

の順に重要度が高くなる。 :contentReference[oaicite:8]{index=8}

また、

```xml
<logger>
```

タグを利用することで、

パッケージ単位でログレベルを変更できることを学習した。 :contentReference[oaicite:9]{index=9}

---

# 総括

本章では、

- application.yml
- logback-spring.xml
- ConsoleAppender
- RollingFileAppender
- ログローテーション
- logger
- ログレベル

について学習した。

今回扱った内容は、これまでにXML設定ファイルやプロパティファイルを利用した経験があったため、比較的スムーズに理解できた。

Spring Boot特有の設定方法はあったものの、

```text
設定ファイルに値を定義する
↓
フレームワークが読み込む
↓
動作を変更する
```

という基本的な考え方はこれまで学習してきた内容の延長線上にあり、大きな混乱なく理解することができた。

また、ログは単なるデバッグ用途ではなく、

```text
障害調査
監査証跡
運用保守
```

において重要な役割を持つことを改めて理解できた。
