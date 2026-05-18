# 第11章まとめ：MyBatis応用編

## ■ 概要

この章では、第10章で学習した MyBatis の基本を踏まえ、

- 動的SQL
- ページネーション
- テーブル結合
- トランザクション

など、

```text
実務レベルで頻繁に使用されるMyBatisの応用機能
```

について学習する。

特に本章では：

```text
検索条件によってSQLを変化させる
```

や、

```text
複数テーブルの結合結果をJavaオブジェクトへマッピングする
```

など、

```text
実際のWebシステムで必須となるDB操作
```

を中心に扱う。

また、

```text
データの整合性を守るトランザクション
```

についても学習する。

---

# ■ 11-1 動的SQL

## ■ この節の目的

検索条件に応じて：

```text
WHERE句を動的に変更する
```

方法を学習する。

---

## ◆ 動的SQLとは

実行時の条件に応じて：

```text
SQLを動的に変化させる仕組み
```

である。

---

## ◆ 実装内容

ユーザー一覧画面へ：

- ユーザーID
- ユーザー名

による検索機能を追加する。

---

## ◆ 使用した主な機能

### whereタグ

```xml
<where>
```

WHERE句を自動生成する。

---

### ifタグ

```xml
<if test="">
```

条件に応じてSQLを追加する。

---

## ◆ 実装の流れ

### 1. フォームクラス作成

```text
UserListForm.java
```

を作成する。

---

### 2. Mapper修正

```java
public List<MUser> findMany(MUser user);
```

のように、

```text
検索条件オブジェクト
```

を引数へ渡す。

---

### 3. XML修正

```xml
<where>
    <if test="userId != null">
```

を使用し、

```text
条件付きWHERE句
```

を生成する。

---

### 4. Controller修正

フォーム値を：

```text
MUser
```

へ変換して検索する。

---

### 5. HTML修正

検索フォームを追加する。

---

# ■ 11-2 ページネーション

## ■ この節の目的

大量データを：

```text
ページ単位で分割表示する
```

方法を学習する。

---

## ◆ ページネーションとは

一覧データを：

```text
複数ページへ分割表示する仕組み
```

である。

---

## ◆ ページネーションのメリット

- DB負荷軽減
- 表示速度向上
- UX改善

など。

---

## ◆ 使用した主な機能

### Pageable

```text
ページ番号
表示件数
```

などを保持する。

---

### Page

```text
一覧データ
総件数
総ページ数
```

などを保持する。

---

### PageImpl

Pageインターフェース実装クラス。

---

## ◆ SQL修正

### count()

検索結果件数取得用SELECTを追加。

---

### LIMIT句

```sql
LIMIT
```

取得件数制限。

---

### OFFSET句

```sql
OFFSET
```

取得開始位置指定。

---

## ◆ Controller修正

### @PageableDefault

```java
@PageableDefault(page = 0, size = 3)
```

で初期ページ設定。

---

### @SessionAttributes

検索条件をセッション保持する。

---

## ◆ HTML修正

Bootstrapを利用し：

- 前へ
- 次へ
- ページ番号

を表示する。

---

## ◆ 使用した主なThymeleaf機能

### th:classappend

条件に応じてCSSクラス追加。

---

### th:block

HTMLへ出力されない繰り返し用タグ。

---

### #numbers.sequence()

連続数値生成。

---

# ■ 11-3 テーブル結合 ― 多対1

## ■ この節の目的

```text
ユーザー → 部署
```

のような：

```text
多対1のテーブル結合
```

を学習する。

---

## ◆ ER構造

```text
1部署
↓
複数ユーザー
```

---

## ◆ 実装内容

ユーザー詳細画面へ：

```text
部署名
```

を表示する。

---

## ◆ 実装の流れ

### 1. Departmentクラス作成

```text
Department.java
```

を追加。

---

### 2. MUser修正

```java
private Department department;
```

を追加。

---

### 3. resultMap修正

```xml
<association>
```

を使用。

---

## ◆ associationタグ

```text
1件の関連データ
```

をマッピングする。

---

## ◆ SQL修正

```sql
LEFT JOIN
```

を利用して：

```text
m_department
```

を結合する。

---

# ■ 11-4 テーブル結合 ― 1対多

## ■ この節の目的

```text
ユーザー → 給料一覧
```

のような：

```text
1対多のテーブル結合
```

を学習する。

---

## ◆ ER構造

```text
1ユーザー
↓
複数給料データ
```

---

## ◆ 実装内容

ユーザー詳細画面へ：

```text
年月別給料一覧
```

を表示する。

---

## ◆ 実装の流れ

### 1. Salaryクラス作成

```text
Salary.java
```

を追加。

---

### 2. MUser修正

```java
private List<Salary> salaryList;
```

を追加。

---

### 3. resultMap修正

```xml
<collection>
```

を使用。

---

## ◆ collectionタグ

```text
複数データ(List)
```

をマッピングする。

---

## ◆ columnPrefix

```xml
columnPrefix="salary_"
```

で：

```text
カラム名衝突回避
```

を行う。

---

## ◆ SQL修正

```sql
LEFT JOIN t_salary
```

を追加。

---

# ■ 11-5 トランザクション

## ■ この節の目的

```text
複数DB処理をまとめて管理する
```

方法を学習する。

---

## ◆ トランザクションとは

DB処理を：

```text
一つの処理単位
```

として扱う仕組み。

---

## ◆ 主な目的

```text
データ整合性維持
```

である。

---

## ◆ 使用した主な機能

### @Transactional

```java
@Transactional
```

を付与することで：

- 成功 → COMMIT
- 失敗 → ROLLBACK

を自動実行する。

---

## ◆ ロールバック

途中でエラーが発生した場合：

```text
処理前状態へ戻す
```

こと。

---

# ■ この章で重要だったポイント

## ◆ 動的SQL

```xml
<if>
<where>
```

によるSQL制御。

---

## ◆ ページネーション

- Pageable
- Page
- LIMIT
- OFFSET

の連携。

---

## ◆ テーブル結合

### 多対1

```xml
<association>
```

---

### 1対多

```xml
<collection>
```

---

## ◆ トランザクション

```text
データ整合性維持
```

のための重要機能。

---

# ■ 最終まとめ

第11章では：

- 動的SQL
- ページネーション
- 多対1結合
- 1対多結合
- トランザクション

など、

```text
実務レベルで必要となるMyBatis機能
```

を中心に学習した。

特に：

```text
単純なCRUDだけではなく、
実際の検索・一覧表示・テーブル結合・整合性管理
```

まで扱うようになり、

```text
実際のWebシステム開発へかなり近づいた内容
```

となった。

また、

```text
Mapper
↓
XML
↓
SQL
↓
Javaオブジェクト
```

というMyBatis特有の流れに加え、

```text
Pageable
association
collection
@Transactional
```

など、

今後の実務でも頻繁に登場する重要な仕組みを学習した章であった。
