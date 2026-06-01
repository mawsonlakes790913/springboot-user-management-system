# 第2章まとめ：開発環境の構築とSpring Bootプロジェクト

## ■ 概要
- Spring Bootを使った開発を始めるために、開発環境を構築しプロジェクトの雛形を作成する章
- EclipseとSpring Initializrを利用して、必要な機能を持ったプロジェクトを自動生成する

---

## ■ Spring Bootプロジェクトの作成

### ■ Spring Initializrとは
- Spring Bootプロジェクトの雛形を作るサービス
- 必要な機能（ライブラリ）を選ぶだけでプロジェクトが生成される

---

### ■ 設定内容（例）
- 名前：SpringBootSample
- ビルドツール：Maven
- Javaバージョン：17
- Spring Boot：バージョン3.5.11(後述)
- パッケージ：com.example.demo

---

## ■ 選択した機能（依存関係）

### 1. Spring Web
- Webアプリ開発の土台
- URL処理・画面制御・APIを担当（Spring MVC）

### 2. Thymeleaf
- HTMLテンプレートエンジン
- データを埋め込んだ動的画面を作成

### 3. JDBC API / Spring Data JDBC
- Javaからデータベースを操作する仕組み
- JDBC：SQLを直接記述
- Spring Data JDBC：記述を簡略化

### 4. H2 Database
- インメモリ型データベース
- セットアップ不要ですぐ利用可能
- 再起動でデータは消える（テスト向け）

### 5. Lombok
- getter / setterなどの定型コードを自動生成
- コード量削減・可読性向上

### 6. Spring Boot DevTools
- コード保存時にサーバーを自動再起動
- 開発効率を向上させる補助ツール

---

## ■ Maven（プロジェクト管理）

### ■ Mavenとは
- ライブラリを管理するツール
- 必要な機能を自動でダウンロード・管理する

---

### ■ 主な役割
- ライブラリのダウンロード
- バージョン管理
- プロジェクトへの組み込み
- ビルド（jarファイル作成）

---

## ■ pom.xml

### ■ 概要
- Mavenへの指示を書くファイル
- 使用するライブラリを定義する

### ■ dependencyの役割
- dependencyを記述することでライブラリを追加できる

例：
<dependency>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

→ 「Spring Webを使う」という意味

---

## ■ ライブラリ導入の流れ

1. 機能を選択（またはpom.xmlに記述）
2. dependencyがpom.xmlに追加される
3. Mavenが内容を読み取る
4. リポジトリからライブラリをダウンロード
5. プロジェクトで利用可能になる

---

## ■ Mavenリポジトリ

### ■ 概要
- ライブラリの保管場所（インターネット上）

### ■ 特徴
- 必要なライブラリを自動取得
- mvnrepositoryなどで検索可能
- dependencyを追加するだけで導入できる

---

## ■ プロジェクト構成

プロジェクト作成後、以下が存在すれば準備完了

- pom.xml：ライブラリ管理
- Maven依存関係：使用ライブラリ一覧
- JREシステムライブラリ：Java実行環境
- src/main/java：Javaコード
- src/main/resources：設定・HTML
- src/test/java：テストコード
- target：ビルド成果物

→ 開発の準備が整った状態

---

## ■ バージョン管理の注意点
- Spring Bootのバージョン違いで動作が変わる場合がある
- 教科書と合わせる場合はpom.xmlで調整する(3.5.14 → 3.5.11へ変更)

---

## ■ 最終まとめ

- Spring Bootプロジェクトは「雛形」から作る
- 機能の正体はすべてライブラリ
- Mavenがライブラリ管理を自動化する
- pom.xmlがプロジェクトの中心

→ 結論
Spring Bootでは環境構築と依存関係管理が自動化され、
すぐに開発を始められる状態を作ることができる
