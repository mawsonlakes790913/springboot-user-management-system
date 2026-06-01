# 第12章 学習ログ ― Spring Data JPA

## 学習概要

第12章では、Spring Data JPA の基本から応用までを学習した。

これまで MyBatis では、

```text
Mapper
↓
XML(SQL)
↓
DB
```

という形で SQL を自分で記述していたが、JPA では

```text
Entityクラス
↓
JPA(Hibernate)
↓
SQL自動生成
↓
DB
```

という流れでデータベース操作を行う。

本章では、

- JPAの役割とメリット
- JpaRepositoryによるCRUD
- JPQLによる任意SQL実行
- Example / ExampleMatcherによる動的SQL
- 多対1のテーブル結合
- 1対多のテーブル結合
- 複合主キー

について学習した。

特に、

```text
DB中心
↓
Java

ではなく

業務(ドメイン)
↓
Javaオブジェクト
↓
DB
```

という DDD（ドメイン駆動設計）的な考え方と JPA の親和性について理解が深まった。 :contentReference[oaicite:0]{index=0}

---

# 12-1 JPAの役割とメリット

## 学んだこと

JPA（Java Persistence API）は Java標準の O/Rマッパーであり、Entityクラスの情報から SQL を自動生成できる。

例えば、

```java
@Entity
@Table(name="m_user")
public class MUser {
    @Id
    private String userId;
}
```

と定義するだけで、

```text
Javaオブジェクト
↓
SQL生成
↓
DB操作
```

が可能になる。

MyBatis のように XML に SQL を記述する必要がないため、開発効率を高められる。 :contentReference[oaicite:1]{index=1}

---

## 【補足】DDD（ドメイン駆動設計）との関係

DDDは、

```text
DB
↓
Java
```

ではなく、

```text
業務
↓
Javaオブジェクト
↓
DB
```

の順に設計する考え方である。

例えば、

```java
private Department department;
private List<Salary> salaryList;
```

のように、

「ユーザーは部署に所属する」

「ユーザーは複数の給与履歴を持つ」

という業務上の概念をそのままオブジェクトとして表現する。

JPAはオブジェクト同士の関係を中心に設計できるため、DDDとの相性が良いと理解した。 :contentReference[oaicite:2]{index=2}

---

# 12-2 基本編（1）アノテーションによるCRUD操作

## 学んだこと

JpaRepositoryを利用することで、

```java
findById()
findAll()
save()
deleteById()
existsById()
```

などのCRUD処理を SQL を書かずに実現できる。 :contentReference[oaicite:3]{index=3}

---

## 【疑問】なぜ ddl-auto を none にするのか？

JPAには Entityクラスからテーブルを自動生成する機能がある。

しかし本書では既に

```text
schema.sql
```

でテーブルを作成している。

そのため、

```yaml
ddl-auto: create
```

にすると、

```text
schema.sql
↓
テーブル作成

Hibernate
↓
テーブル作成
```

となり衝突する可能性がある。

さらに本番環境で誤って create を使うと、

```text
DROP TABLE
↓
CREATE TABLE
```

が実行される危険性もある。

実務では、

```yaml
ddl-auto: none
```

または

```yaml
ddl-auto: validate
```

が使われることが多いと理解した。 :contentReference[oaicite:4]{index=4}

---

## 【補足】@Transient の役割

JPAは Entityクラスを見ると、

```java
private Department department;
private List<Salary> salaryList;
```

についても DBとの関連を解釈しようとする。

しかしこの段階ではまだテーブル結合を学習していない。

そのため、

```java
@Transient
private Department department;

@Transient
private List<Salary> salaryList;
```

を付けて、

```text
JPAさん、
department と salaryList は
まだ無視してください
```

と伝えている。

つまり、

```text
MUser
↓
m_userテーブルのみ
```

を対象とし、

```text
m_department
t_salary
```

との関連付けを一時的に無効化している。 :contentReference[oaicite:5]{index=5}

---

## 【重要】JpaRepositoryのジェネリクス

JpaRepositoryは、

```java
JpaRepository<エンティティクラス, 主キー型>
```

というジェネリクスを持つ。

例えば、

```java
JpaRepository<MUser, String>
```

なら、

```text
MUser
↓
対象テーブル

String
↓
主キー型
```

を意味する。

この情報を基に、

```java
findById()
existsById()
deleteById()
```

などが自動生成される。

---

## 【重要】save()は登録と更新の両方を行う

save()は、

```text
主キーが存在しない
↓
INSERT

主キーが存在する
↓
UPDATE
```

を自動で判定する。

そのため、本書では

```java
existsById()
```

で存在確認してから登録している。

---

# 12-3 基本編（2）任意のSQLの実行

## 学んだこと

JPAは SQL を自動生成できるが、

```java
@Query
```

を利用することで任意のSQL（JPQL）も実行できる。

また、

```java
@Modifying
```

を付けることで UPDATE / DELETE を実行できる。 :contentReference[oaicite:6]{index=6}

---

## 【疑問】@Param は何のためにあるのか？

JPAは、

```java
@Query("""
update MUser
set password = :password
where userId = :userId
""")
```

を解析すると、

```text
:userId
:password
```

という名前付きパラメータを発見する。

そして、

```java
@Param("userId")
String userId

@Param("password")
String password
```

を見て対応付けを行う。

内部的には、

```text
"userId" → user1
"password" → abc123
```

のようなMapを作成し、

PreparedStatementへバインドしている。 :contentReference[oaicite:7]{index=7}

---

## 【疑問】@Param は今でも必要なのか？

現代のJavaでは、

```java
Integer updateUser(
 String password,
 String userName,
 String userId
);
```

のように書いても動作する。

しかし昔のJavaではコンパイル後に引数名が保持されないことがあり、

```text
第1引数
第2引数
第3引数
```

しか認識できなかった。

そのため、

```java
@Param("userId")
```

によって、

```text
:userId
↓
この引数です
```

と明示する必要があった。

現在では必須ではないが、可読性向上のために利用されることが多いと理解した。 :contentReference[oaicite:8]{index=8}

---

# 12-4 応用編（1）動的SQL

## 学んだこと

Spring Data JPAでは、

```java
Example
ExampleMatcher
```

を利用して動的SQLを実現できる。

検索値はオブジェクトとして保持し、

検索ルールは ExampleMatcher に保持する。 :contentReference[oaicite:9]{index=9}

---

## 【補足】ExampleMatcherの正体

ExampleMatcherはインタフェースである。

そのため、

```java
new ExampleMatcher()
```

はできない。

代わりに、

```java
ExampleMatcher.matchingAll()
```

を実行すると、

ExampleMatcher実装クラスのオブジェクトが返される。

つまり、

```java
ExampleMatcher matcher =
    ExampleMatcher.matchingAll()
```

の matcher はインタフェース型だが、

実際にはオブジェクトを保持している。 :contentReference[oaicite:10]{index=10}

---

## 【疑問】JPAではサービス側で検索条件を定義するのか？

今回の Example検索では、

```text
MUser
↓
何を検索するか

ExampleMatcher
↓
どのように検索するか
```

を保持し、

```java
Example.of(user, matcher)
```

で組み合わせている。

つまり検索条件の組み立ては Service側で行われる。

ただしこれは Example検索特有の仕組みであり、

```java
@Query
```

を使う場合は Repository側で検索条件を定義する。

したがって、

```text
JPAでは必ずService側
```

ではなく、

```text
Example検索ではService側
```

と理解するのが正確である。 :contentReference[oaicite:11]{index=11}

---

## 【補足】クエリメソッド

JPAには、

```java
findByUserName()
```

のようにメソッド名だけで SQL を生成する機能がある。

これはクエリメソッドと呼ばれる。

メソッド名を解析して SQL を生成するため、単純な検索では非常に便利である。 :contentReference[oaicite:12]{index=12}

---

# 12-5 応用編（2）テーブル結合 ― 多対1

## 学んだこと

JPAでは、

```java
@ManyToOne
@JoinColumn
```

を利用して多対1の関連を表現できる。

今回の例では、

```text
複数のユーザー
↓
1つの部署
```

という関係を表現している。 :contentReference[oaicite:13]{index=13}

---

## 【補足】@ManyToOne の本当の意味

@ManyToOneとは、

```text
複数のレコード（Many）
↓
1つのレコード（One）
```

を表すアノテーションである。

重要なのは、

```text
このユーザーが何個の部署を持つか
```

ではなく、

```text
m_user テーブル全体
↓
m_department テーブル
```

の関係を表している点である。

つまり、

```java
@ManyToOne
private Department department;
```

は、

```text
ユーザーテーブル全体で見ると
多くのユーザーが
1つの部署を共有できる
```

という意味である。 :contentReference[oaicite:14]{index=14}

---

## 【重要】optional の意味

```java
@ManyToOne(optional = true)
```

の場合、

```text
部署なしユーザー
```

も取得できるため、

LEFT JOIN と同じ動作になる。

逆に、

```java
@ManyToOne(optional = false)
```

なら、

部署が存在しないユーザーは取得されず、

INNER JOIN と同じ動作になる。 :contentReference[oaicite:15]{index=15}

---

# 12-6 応用編（3）テーブル結合 ― 1対多

## 学んだこと

1人のユーザーに対して複数の給料データが存在する

```text
1対多
```

の関係を JPA で表現した。 :contentReference[oaicite:16]{index=16}

---

## 【重要】複合主キー

給料テーブルでは、

```text
user_id
year_month
```

の2つで主キーを構成している。

このような主キーを複合主キーと呼ぶ。 :contentReference[oaicite:17]{index=17}

---

## 【疑問】なぜ主キー用クラスを作るのか？

JpaRepositoryは、

```java
JpaRepository<エンティティクラス, 主キー型>
```

という形式で定義する。

主キー型は1つしか指定できない。

そのため、

```text
user_id
year_month
```

を

```java
SalaryKey
```

というクラスにまとめ、

```java
JpaRepository<Salary, SalaryKey>
```

として扱えるようにしている。 :contentReference[oaicite:18]{index=18}

---

## 【疑問】Serializableインタフェースとは？

本来はシリアライズのためのインタフェースである。

しかし現段階では、

```java
implements Serializable
```

は、

```text
私は複合主キー用クラスです
```

というJPAのお約束だと理解した。

つまり、

```text
@Embeddable
↓
複合主キー用クラス

implements Serializable
↓
JPAのお約束
```

という認識で十分である。 :contentReference[oaicite:19]{index=19}

---

## 【重要】@EmbeddedId

単一主キーでは、

```java
@Id
```

を使用する。

一方、

複合主キーでは、

```java
@EmbeddedId
private SalaryKey salaryKey;
```

を使用する。

これにより、

```text
userId
yearMonth
```

を1つの主キーオブジェクトとして扱える。 :contentReference[oaicite:20]{index=20}

---

# 総括

本章では、

- Spring Data JPA の基本構造
- JpaRepositoryによるCRUD
- JPQLによる任意SQL
- Example検索による動的SQL
- 多対1の関連
- 1対多の関連
- 複合主キー
- DDDとJPAの関係

を体系的に学習した。

特に MyBatis との比較を通して、

```text
MyBatis
↓
SQL中心

JPA
↓
オブジェクト中心
```

という設計思想の違いが理解できた。

また、

```java
@ManyToOne
@OneToMany
@EmbeddedId
@Embeddable
```

などのアノテーションを通して、

JPAではテーブルではなく「オブジェクト同士の関係」を記述することが重要であると理解できた。 
