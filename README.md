# Spring Boot ユーザー管理システム

## 概要

本リポジトリは、インプレス社の書籍 **「手を動かしてわかるSpring Boot入門」** を学習しながら作成したユーザー管理システムです。

本書で作成するサンプルアプリケーションを題材に、Spring Bootを用いたWebアプリケーション開発の基本から実践的な機能までを体系的に学習しました。

私はこれまでWebアプリケーション開発の経験がなく、HTML・CSS・JavaScriptなどのフロントエンド知識もほとんどない状態から学習を開始しました。

そのため、1周目は書籍の内容をそのまま実装しながら全体像を把握することを優先し、2周目では各クラスや設定ファイルの役割、処理の流れを意識しながら構造理解を深めました。

今回は「オリジナルアプリケーションを作ること」よりも、「Spring Bootを使ったWebアプリケーション開発の方法を学ぶこと」を目的として取り組んでいます。

今後は本プロジェクトで得た知識と経験を基に、自分自身で要件定義から設計・実装まで行うオリジナルアプリケーションの開発に挑戦する予定です。

---

## アプリケーション概要

本書で作成したアプリケーションは、ユーザー情報を管理するためのWebアプリケーションです。

ユーザー情報の登録・検索・更新・削除（CRUD）を中心に、実際の業務システムで利用される様々な機能を段階的に実装しています。 :contentReference[oaicite:0]{index=0}

### 主な機能

- ログイン機能
- ログアウト機能
- Remember-Me機能
- ユーザー登録
- ユーザー検索
- ユーザー一覧表示
- ユーザー詳細表示
- ユーザー更新
- ユーザー削除
- 入力バリデーション
- ページネーション
- 部署情報との関連付け
- 給与情報表示
- Administrator専用画面
- ロールによるアクセス制御
- 共通エラーページ
- ログ出力
- 設定の外部化

アプリケーションにはログイン不要画面とログイン後画面が存在し、ユーザー権限によるアクセス制御も実装しています。 :contentReference[oaicite:1]{index=1}

---

## 学習した技術

### Spring Framework

- DI（Dependency Injection）
- IoCコンテナ
- Bean管理
- Java Config
- プロファイル管理

### Spring MVC

- Controller
- Service
- Repository
- Form
- Model
- Thymeleaf

### データベース

- H2 Database
- JDBC
- MyBatis
- Spring Data JPA
- Entity Mapping
- 動的SQL
- テーブル結合
- ページネーション

### Spring Security

- 認証（Authentication）
- 認可（Authorization）
- ログイン処理
- Remember-Me
- ロール管理
- メソッドレベル認可（@PreAuthorize）
- BCryptによるパスワードハッシュ化

### Spring AOP

- ログ出力
- 例外処理
- Pointcut
- Advice

### ログ・設定管理

- Logback
- ログレベル管理
- ログローテーション
- 環境変数
- application.yml
- プロファイルごとの設定切り替え
- 設定の外部化

---

## 使用技術

| 分類 | 技術 |
|--------|--------|
| Language | Java 21 |
| Framework | Spring Boot |
| Build Tool | Maven |
| Database | H2 Database |
| ORM | Spring Data JPA |
| SQL Mapper | MyBatis |
| Template Engine | Thymeleaf |
| Security | Spring Security |
| Logging | Logback |
| AOP | Spring AOP |

---

## 学習の進め方

本プロジェクトでは、単にサンプルコードを写経するだけではなく、

- なぜその設定が必要なのか
- Spring Boot内部で何が起きているのか
- MyBatisとJPAの違いは何か
- Spring Securityはどのように認証・認可を行うのか

といった点を調査しながら学習を進めました。

また、理解を深めるために各章ごとに学習ログを作成し、疑問点や気付き、重要だと感じた内容を記録しています。

---

## リポジトリ構成

```text
docs/
├── learning-log/      # 各章の学習ログ
├── summary/           # 学習まとめ
└── notes/             # 補足資料

user-management-system/
├── src/
├── pom.xml
└── ...
```

---

## 学習ログ

本リポジトリには各章の学習ログを保存しています。

学習ログでは、

- 学んだ内容
- 疑問に思ったこと
- 調査して理解したこと
- 実装時につまずいた箇所
- 重要だと感じたポイント

を記録しています。

---

## 今後の予定

本プロジェクトはSpring Boot学習の第一歩として作成したものです。

今後はここで学んだ内容を基に、以下のような技術へ発展させていく予定です。

- PostgreSQL
- AWS
- Docker
- REST API
- テストコード作成
- CI/CD
- オリジナルWebアプリケーション開発

---

## 参考書籍

**手を動かしてわかるSpring Boot入門（インプレス社）**

本リポジトリは上記書籍の学習成果物として作成しています。

---

## 作者

N K

本プロジェクトは私にとって初めて完成させたSpring BootのWebアプリケーションです。

このリポジトリで得た知識や経験を基に、今後はより実践的なバックエンド開発やクラウド技術の学習に取り組んでいきます。