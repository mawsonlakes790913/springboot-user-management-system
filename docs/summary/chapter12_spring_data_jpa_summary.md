# 第12章まとめ：Spring Data JPA

## ■ 概要

この章では、Spring Bootで利用される代表的なO/Rマッパーである

```text
Spring Data JPA
```

について学習する。

これまでの章では：

```text
MyBatis
```

を利用してSQLをXMLへ記述してきた。

MyBatisでは：

- SQLを自由に記述できる
- 複雑なクエリを書きやすい
- SQLを細かく制御できる

という特徴がある。

一方でJPAでは：

```text
Javaクラス（エンティティ）
```

を元に、

```text
SQLを自動生成
```

できる。

そのため：

- SQL記述量の削減
- 開発効率向上
- オブジェクト指向との親和性向上

などのメリットがある。

本章では：

- Spring Data JPA の導入
- CRUD操作
- JPQLによる任意SQL
- 動的SQL（QBE）
- 多対1テーブル結合
- 1対多テーブル結合

などを通して、

```text
JPAによるデータ操作の基本
```

を学習する。

---

# ■ 12-1 JPAの役割とメリット

## ◆ JPAとは

JPA（Java Persistence API）は：

```text
Java標準のO/Rマッパー
```

である。

Javaクラスの構造をもとに：

```text
SQLを自動生成
```

する。

---

## ◆ JPAの特徴

JPAでは：

```java
@Entity
@Table(name="t_sample")
public class Sample {

    @Id
    private String sampleId;
}
```

のように：

- エンティティクラス
- アノテーション

を定義することで、

```text
Javaクラス ↔ テーブル
```

のマッピングを行う。

---

## ◆ Spring Data JPA

Spring Data JPAを利用すると：

- CRUD操作
- SQL生成
- Repository管理

などを簡単に実装できる。

---

## ◆ JPAのメリット

- SQL記述量を削減できる
- Javaクラス修正時にSQLも追従する
- オブジェクト指向との相性が良い
- DDD（ドメイン駆動設計）と親和性が高い

---

# ■ 12-2 基本編（1）アノテーションによるCRUD操作

## ▽ サンプルアプリケーションの作成

---

## 1. ファイルなどの作成

以下の構成を追加・修正する。

```text
user
├── domain
│   ├── model
│   ├── service
│   │   ├── impl
│   │   │   ├── UserServiceImpl.java
│   │   │   └── UserServiceImpl2.java
│   │   └── UserService.java
│   └── repository
│       ├── UserMapper.java
│       └── UserRepository.java
```

---

## 2. Spring Data JPA の追加

pom.xmlへ以下を追加する。

```xml
<!-- JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

## 3. application.yml の設定

```yml
spring:
  jpa:
    hibernate:
      ddl-auto: none
```

を追加する。

---

## ◆ ddl-auto

JPAによる：

```text
テーブル自動生成
```

を制御する設定。

本章では：

```text
schema.sql
```

を利用するため、

```text
none
```

を指定する。

---

## 4. JPAログ設定

```yml
logging:
  level:
    '[org.hibernate.SQL]': debug
```

などを設定する。

---

## ◆ JPAログ

Hibernateが生成した：

```text
実行SQL
```

を確認できる。

---

## 5. Repository作成

```java
public interface UserRepository
        extends JpaRepository<MUser, String> {
}
```

を作成する。

---

## ◆ JpaRepository

JPA用Repository。

以下のCRUDメソッドを自動利用できる。

- findById()
- findAll()
- save()
- deleteById()
- existsById()

---

## 6. エンティティ修正

MUser.javaへ：

```java
@Entity
@Table(name="m_user")
```

を追加する。

---

## ◆ @Entity

JPAの：

```text
エンティティクラス
```

として認識させる。

---

## ◆ @Table

対応する：

```text
DBテーブル名
```

を指定する。

---

## ◆ @Id

主キーを表す。

---

## ◆ @Transient

JPAのマッピング対象外にする。

---

## 7. UserServiceImpl2 作成

```java
@Primary
public class UserServiceImpl2
        implements UserService
```

を作成する。

---

## ◆ @Primary

同じインターフェース実装が複数ある場合：

```text
優先的にDIするBean
```

を指定する。

---

## 8. CRUDメソッド実装

### 登録

```java
repository.save(user);
```

---

### 一覧取得

```java
repository.findAll(pageable);
```

---

### 1件取得

```java
repository.findById(userId);
```

---

### 削除

```java
repository.deleteById(userId);
```

---

## ◆ save()

```text
登録・更新
```

の両方を行う。

---

## 9. Sampleクラス修正

```java
@Entity
public class Sample {
}
```

へ修正する。

---

## ◆ Spring Data JDBCとの競合回避

JPA追加後は：

```text
JDBC と JPA
```

のどちらを利用するかSpringが判断できなくなるため、

```text
@Entity
```

を追加してJPA対応する。

---

## ▽ 実行確認

以下を確認する。

- ユーザー一覧
- ユーザー詳細
- ユーザー登録
- ユーザー削除

また：

```text
org.hibernate.SQL
```

ログが出力されることを確認する。

---

# ■ 12-3 基本編（2）任意のSQLの実行

## ▽ サンプルアプリケーションの作成

---

## 1. Repository修正

```java
@Query("update MUser ...")
@Modifying
public Integer updateUser(...)
```

を追加する。

---

## ◆ @Query

任意SQL（JPQL）を実行する。

---

## ◆ JPQL

JPQLでは：

- テーブル名ではなくクラス名
- カラム名ではなくフィールド名

を使用する。

---

## ◆ @Param

メソッド引数と：

```text
SQLパラメーター
```

をマッピングする。

---

## ◆ @Modifying

UPDATE／DELETE系SQL実行時に必要。

---

## 2. UserServiceImpl2 修正

```java
@Transactional
public void updateUserOne(...)
```

を修正する。

---

## ◆ @Transactional

トランザクション管理を行う。

---

## ▽ 実行確認

ユーザー詳細画面から：

- ユーザー名変更
- パスワード変更

を行い、

```text
更新件数=1
```

ログを確認する。

---

# ■ 12-4 応用編（1）動的SQL

## ▽ ユーザーサービス修正

---

## 1. ExampleMatcher作成

```java
ExampleMatcher matcher =
    ExampleMatcher.matchingAll()
```

を利用する。

---

## ◆ matchingAll()

```text
AND検索
```

を行う。

---

## ◆ matchingAny()

```text
OR検索
```

を行う。

---

## 2. 部分一致設定

```java
.withMatcher(
    "userName",
    ExampleMatcher.GenericPropertyMatchers.contains()
)
```

を追加する。

---

## ◆ contains()

```text
部分一致検索
```

を行う。

---

## ◆ その他の検索方法

- startsWith()
- contains()
- endsWith()

---

## 3. Example生成

```java
Example.of(user, matcher)
```

を利用する。

---

## ◆ QBE

QBE（Query By Example）は：

```text
オブジェクトを検索条件として使う仕組み
```

である。

---

## ▽ 実行確認

一覧画面で：

- ユーザーID
- ユーザー名

検索を確認する。

---

# ■ 12-5 応用編（2）テーブル結合 ― 多対1

## ▽ エンティティ修正

---

## 1. Department.java 修正

```java
@Entity
@Table(name="m_department")
```

を追加する。

---

## 2. MUser.java 修正

```java
@ManyToOne
@JoinColumn(name="departmentId")
private Department department;
```

を追加する。

---

## ◆ @ManyToOne

```text
多対1
```

の関係を表す。

---

## ◆ @JoinColumn

結合キーを指定する。

---

## ◆ optional属性

```java
@ManyToOne(optional = true)
```

を指定すると：

```text
LEFT JOIN相当
```

の動作になる。

---

## ◆ insertable / updatable

```java
insertable = false
updatable = false
```

を指定すると：

```text
更新対象から除外
```

される。

---

## ▽ 実行確認

ユーザー詳細画面で：

```text
部署名
```

が表示されることを確認する。

---

# ■ 12-6 応用編（3）テーブル結合 ― 1対多

## ▽ サンプルアプリケーションの作成

---

## 1. SalaryKey.java 作成

```java
@Embeddable
public class SalaryKey
```

を作成する。

---

## ◆ 複合主キー

```text
複数カラムで構成される主キー
```

を表す。

---

## ◆ @Embeddable

複合主キー用クラスを表す。

---

## 2. Salary.java 修正

```java
@EmbeddedId
private SalaryKey salaryKey;
```

を追加する。

---

## ◆ @EmbeddedId

複合主キーを組み込む。

---

## 3. MUser.java 修正

```java
@OneToMany
private List<Salary> salaryList;
```

を追加する。

---

## ◆ @OneToMany

```text
1対多
```

の関係を表す。

---

## 4. detail.html 修正

```html
<tr th:each="item : *{salaryList}">
```

を追加する。

---

## 5. UserMapper.xml 修正

```xml
<id column="user_id"
    property="salaryKey.userId" />
```

を追加する。

---

## ◆ ネストしたプロパティ

```text
salaryKey.userId
```

のように：

```text
オブジェクト内部
```

へマッピングできる。

---

## ▽ 実行確認

ユーザー詳細画面で：

```text
給料一覧
```

が表示されることを確認する。

---

# ■ この章で重要なポイント

## ◆ JPA

JavaクラスからSQLを自動生成する。

---

## ◆ JpaRepository

CRUDメソッドを自動利用できる。

---

## ◆ JPQL

JPA用クエリ言語。

---

## ◆ QBE

オブジェクトを検索条件として利用する。

---

## ◆ リレーション

- @ManyToOne
- @OneToMany

などでテーブル結合を表現する。

---

## ◆ 複合主キー

```text
@Embeddable
@EmbeddedId
```

を利用する。

---

# ■ 最終まとめ

第12章では：

- Spring Data JPA
- JpaRepository
- CRUD操作
- JPQL
- QBE
- 多対1結合
- 1対多結合
- 複合主キー

などについて学習した。

特に重要なのは：

```text
Javaクラスを中心に設計し、
SQLを自動生成できる
```

という点である。

また：

```text
@Entity
@ManyToOne
@OneToMany
```

などのアノテーションによって、

```text
オブジェクト同士の関係
```

をそのままDB構造へ反映できる点も、

JPAの大きな特徴である。
