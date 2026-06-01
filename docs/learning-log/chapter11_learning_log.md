# 第11章 学習ログ ― MyBatis応用編

## 学習概要

第11章では、MyBatis の応用機能について学習した。

10章では、

```text
Controller
↓
Service
↓
Mapper
↓
XML(SQL)
↓
DB
```

という MyBatis の基本構造を理解することに苦戦したが、本章ではその流れを既に理解していたため、応用内容でありながら比較的スムーズに学習を進めることができた。

特に本章では、

- 動的SQL
- ページネーション
- 多対1テーブル結合
- 1対多テーブル結合
- トランザクション

など、実務で頻繁に使用される機能を学習した。

また、

```text
SQL結果
↓
Javaオブジェクト構造
```

への変換を MyBatis がどのように行っているのかについて、10章以上に深く理解できるようになった。

---

# 11-1 動的SQL

## 学んだこと

検索条件に応じて SQL を動的に変更する方法を学習した。

特に、

```xml
<where>
<if test="">
```

を使うことで、

```text
未入力
↓
WHERE句を作らない

入力あり
↓
WHERE句を追加
```

という柔軟なSQL生成ができることを理解した。

---

## 【重要】11章では「検索条件をDBへ渡す」必要が生まれた

10章のSELECTは、

```java
findMany()
```

のように引数なしで全件取得していた。

しかし11章では、

- ユーザーID検索
- ユーザー名検索
- 両方検索
- 未入力時全件

など、検索条件に応じてSQLを変える必要がある。

そのため、

```java
findMany(MUser user)
```

のように検索条件を保持したオブジェクトを渡す構造へ変更された。

つまり11章では、

```text
HTML
↓
Controller
↓
Service
↓
Mapper
↓
SQL
```

へ検索条件を受け渡す処理が追加された。

---

## 【疑問】なぜUserListFormが新規追加されたのか？

UserListForm は、

```text
検索欄入力専用Form
```

として追加されている。

ここには、

```java
private String userId;
private String userName;
```

のみを保持する。

これは SignupForm と同様、

```text
画面入力用
```

のクラスであり、

```text
DB Entity(MUser)
```

とは責務が異なる。

---

## 【補足】ModelMapperが追加された理由

10章では HTML → DB への検索条件受け渡しが存在しなかったため、

```java
private final ModelMapper modelMapper;
```

は不要だった。

しかし11章では、

```text
HTML入力
↓
UserListForm
↓
MUser
```

へ変換する必要が生まれた。

そのため、

```java
MUser user =
    modelMapper.map(form, MUser.class);
```

が追加された。

---

## 【疑問】findMany(MUser user) の MUser は何を保持している？

例えば、

```text
userId = "user1"
```

だけ入力した場合でも、

実際の MUser は、

```text
userId = "user1"
userName = null
password = null
birthday = null
age = null
gender = null
departmentId = null
role = null
```

のようなオブジェクトになっている。

つまり、

```text
必要なフィールドだけ値が入り、
残りはnull
```

で保持されている。

---

## 【補足】LIKE '%xxx%' は部分一致検索

```sql
LIKE '%' || #{userId} || '%'
```

は、

```text
前後に何があってもよい
```

という意味になるため部分一致検索になる。

また、

```text
|| 
```

は PostgreSQL における文字列連結演算子である。

---

## 【疑問】WHERE句のANDは何を意味している？

```sql
AND user_name LIKE ...
```

の AND は SQL の条件ANDであり、

```text
user_id 条件
かつ
user_name 条件
```

を意味する。

つまり、

```text
両方満たしたデータのみ取得
```

となる。

---

# 11-2 ページネーション

## 学んだこと

ページネーションを実装した。

ここでは、

- Pageable
- Page
- LIMIT
- OFFSET
- PageImpl

など、実務で非常に重要な概念を学習した。

---

## 【重要】ページネーションの目的

ページネーションを行わず、

```text
10万件
```

などを全件取得すると、

- DB負荷増大
- サーバー負荷増大
- 描画速度低下

が発生する。

そのため、

```text
必要な件数だけ取得
```

することが重要である。

---

## 【補足】LIMITとOFFSETの意味

```sql
LIMIT 3
OFFSET 5
```

の場合、

```text
最初の5件を飛ばし、
6件目から3件取得
```

という意味になる。

つまり、

| SQL | 意味 |
|---|---|
| LIMIT | 何件取得するか |
| OFFSET | 何件スキップするか |

である。

---

## 【疑問】1ページ3件はどこで決めている？

これは Controller の：

```java
@PageableDefault(page = 0, size = 3)
```

で指定されている。

```text
page = 0
↓
最初は1ページ目

size = 3
↓
1ページ3件表示
```

という意味。

---

## 【補足】Pageableとは何か？

Pageable は、

```text
現在何ページ目か
1ページ何件か
OFFSETはいくつか
```

などを保持する Spring Data のインターフェースである。

ユーザーが2ページ目を押すと、

```text
?page=1
```

が送信され、

Spring が自動的に：

```text
page = 1
size = 3
offset = 3
```

を持つ Pageable を生成する。

---

## 【疑問】なぜ戻り値がListからPageへ変わったのか？

ページネーションでは、

- 実データ
- 総件数
- 現在ページ
- 総ページ数

なども必要になる。

そのため、

```java
List<MUser>
```

では情報不足になる。

そこで、

```java
Page<MUser>
```

へ変更された。

---

## 【補足】PageImplの役割

```java
new PageImpl<>(userList, pageable, count)
```

では、

| 引数 | 意味 |
|---|---|
| userList | 実データ |
| pageable | ページ情報 |
| count | 総件数 |

をまとめて保持している。

---

## 【疑問】なぜ検索条件をSessionへ保存するのか？

ページ切り替え時にも、

```text
userId = user1
```

などの検索条件を維持する必要がある。

しかし毎回ブラウザから検索条件を送り直すのは非効率。

そのため、

```java
@SessionAttributes
```

を使って、

```text
検索条件をSession保持
```

している。

---

## 【疑問】なぜ setUserListForm() で new しているのか？

```java
return new UserListForm();
```

は、

```text
空の検索フォーム生成
```

を意味する。

初回アクセス時はまだユーザー入力が存在しないため、

```text
userId = null
userName = null
```

の空オブジェクトで問題ない。

その後、

```text
th:object
↓
th:field
↓
Spring MVC
```

によって入力値が自動バインドされる。

---

## 【補足】@ModelAttribute の役割

```java
@ModelAttribute("userListForm")
```

は、

```text
Modelへ userListForm を登録
```

する役割。

結果として、

```html
th:object="${userListForm}"
```

と接続できるようになる。

つまり、

```text
HTML
↔ UserListForm
```

を結び付ける橋渡しになっている。

---

## 【補足】PageオブジェクトをModelへ登録する理由

```java
model.addAttribute("page", userPage);
```

を行うことで、

- page.first
- page.last
- page.number
- page.totalPages

などを Thymeleaf から利用できる。

---

## 【補足】Thymeleafでは getXXX() / isXXX() を省略できる

例えば、

```java
page.isFirst()
```

は、

```html
${page.first}
```

と書ける。

これは Thymeleaf が、

```text
get
is
```

を自動補完しているため。

---

## 【補足】th:block の役割

```html
<th:block th:each="">
```

は、

```text
ループだけ行い、
タグ自体はHTMLへ出力しない
```

特殊タグ。

ページ番号生成で利用されていた。

---

# 11-3 テーブル結合（多対1）

## 学んだこと

多対1テーブル結合を学習した。

今回は、

```text
m_user
↓ 多
m_department
↓ 1
```

の関係を扱った。

---

## 【重要】Departmentクラスを分離する理由

一応、

```java
private String departmentName;
```

を MUser に直接持たせることも可能。

しかし今回は、

```java
private Department department;
```

として、

```text
MUser
 └ Department
```

というオブジェクト構造にしている。

これは、

```text
ユーザー情報
```

と

```text
部署情報
```

を責務分離するため。

---

## 【疑問】associationは何をしているのか？

```xml
<association property="department"
             resultMap="department"/>
```

は、

```text
department関連カラム
↓
Department object化
↓
MUser.departmentへ格納
```

を行っている。

つまり、

```text
SQLの横並び結果
```

を、

```text
Javaオブジェクト構造
```

へ変換する役割を持つ。

---

## 【補足】associationのpropertyはJava側

```xml
property="department"
```

は、

```java
private Department department;
```

を指している。

つまり association は、

```text
完成したDepartment objectを
MUserのどこへ入れるか
```

を指定している。

---

# 11-4 テーブル結合（1対多）

## 学んだこと

1対多テーブル結合を学習した。

今回は、

```text
1ユーザー
↓
複数Salary
```

という構造を扱った。

---

## 【重要】collectionはList構造を作る

```xml
<collection property="salaryList"
            resultMap="salary"/>
```

は、

```text
複数Salaryオブジェクト
↓
List<Salary>
↓
MUser.salaryListへ格納
```

を行っている。

---

## 【疑問】なぜproperty="salaryList"はJava側なのか？

```xml
property="salaryList"
```

は DBカラムではなく、

```java
private List<Salary> salaryList;
```

を指している。

collection は、

```text
完成したSalary object群を
どのListへ入れるか
```

を指定しているため。

---

## 【補足】columnPrefixの意味

JOINすると、

```text
user_id
```

など同名カラムが衝突する。

そのため、

```sql
t_salary.user_id AS salary_user_id
```

のように AS句で別名を付ける。

しかし resultMap 側は、

```xml
column="user_id"
```

のまま。

そこで、

```xml
columnPrefix="salary_"
```

を書くことで、

```text
salary_ + user_id
↓
salary_user_id
```

のように自動補完してくれる。

---

# 11-5 トランザクション

## 学んだこと

トランザクションについて学習した。

特に、

```text
処理途中で失敗したら
すべて元に戻す
```

という考え方が重要であると理解した。

---

## 【補足】トランザクション分離レベル

以下の違いを理解した。

| 分離レベル | 特徴 |
|---|---|
| READ_UNCOMMITTED | 未コミットデータも読める |
| READ_COMMITTED | コミット済みのみ読める |
| REPEATABLE_READ | 同一Tx内で同じ値保証 |
| SERIALIZABLE | 完全直列化 |

---

## 【補足】伝播レベル

```text
既存トランザクションへ参加するか
新規作成するか
```

を制御する。

特に、

| 伝播レベル | 特徴 |
|---|---|
| REQUIRED | 既存利用 / なければ新規 |
| REQUIRES_NEW | 常に新規 |
| SUPPORTS | あれば利用 |
| NEVER | Tx禁止 |

などを学習した。

---

# 総括

第11章では、

- 動的SQL
- ページネーション
- Session管理
- 多対1
- 1対多
- association
- collection
- トランザクション

など、実務レベルで必要となる MyBatis の応用機能を体系的に学習できた。

特に、

```text
SQL結果
↓
Javaオブジェクト構造
```

への変換について理解が深まり、

```text
association
collection
resultMap
columnPrefix
```

などの意味がかなり明確になった。

また、10章で MyBatis 全体構造に苦戦した分、

11章では、

```text
HTML
↓
Controller
↓
Service
↓
Mapper
↓
XML(SQL)
↓
DB
```

という流れを前提知識として使えるようになり、応用内容への理解速度が大きく向上した。
