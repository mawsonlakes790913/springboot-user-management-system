# 第10章まとめ：MyBatis基本編

## ■ 概要

この章では、Spring Bootで利用される代表的なO/Rマッパーである

```text
MyBatis
```

の基本について学習する。

第3章では Spring Data JDBC を利用してデータベース操作を行ったが、実際の開発では：

- 複雑なSQL
- 動的SQL
- テーブル結合
- Javaクラスへの複雑なマッピング

などが必要になることが多い。

Spring Data JDBC では：

- SQLをJavaコード内へ直接書く必要がある
- 複雑なSELECT結果のマッピングが大変
- 動的SQLが扱いづらい

などの課題がある。

そこで利用されるのが MyBatis である。

MyBatisでは：

- SQLをXMLへ分離できる
- SQLを管理しやすい
- 動的SQLを扱える
- SELECT結果をJavaクラスへ柔軟にマッピングできる

という特徴がある。

本章では：

- MyBatisの基本構造
- INSERT
- SELECT（複数件）
- SELECT（1件）
- UPDATE
- DELETE

という、

```text
CRUD操作の基本
```

を中心に学習する。

---

# ■ 10-1 MyBatisの仕組みと特徴

## ◆ O/Rマッパーとは

O/Rマッパーとは：

```text
Object（Javaクラス）
```

と、

```text
Relation（DBテーブル）
```

を対応付ける仕組みである。

つまり：

```text
DBのデータ
↓
Javaオブジェクト
```

へ自動変換するためのフレームワークである。

---

## ◆ Spring Data JDBC の問題点

Spring Data JDBCでは：

```text
SELECT結果
↓
Javaクラス
```

への変換処理を自分で実装する必要がある。

特に：

- テーブル結合
- List保持
- 複雑なSELECT

などになると、

```text
SELECT結果をJavaへ変換するコード
```

が非常に複雑になる。

---

## ◆ MyBatis の特徴

MyBatisでは：

```text
SQL
```

と、

```text
Java
```

を分離して管理できる。

さらに：

```text
SELECT結果
↓
Javaクラス
```

への変換も、

```xml
resultMap
```

などを利用して定義できる。

そのため：

- SQL管理
- 保守性
- 開発効率

が向上する。

---

## ◆ MyBatis の基本構造

MyBatisでは：

```text
Java Mapper
↓
XML
↓
SQL
```

という構造で動作する。

---

## ◆ Mapper

Mapperとは：

```text
DBアクセス用インターフェース
```

である。

例：

```java
@Mapper
public interface UserMapper {

    public int insertOne(MUser user);
}
```

---

## ◆ XML

実際のSQLは：

```text
UserMapper.xml
```

へ記述する。

例：

```xml
<insert id="insertOne">
    INSERT INTO ...
</insert>
```

---

## ◆ 3つのマッピング

MyBatisでは：

```text
Java ↔ XML ↔ SQL
```

を連携させるために、

```text
3つのマッピング
```

が必要になる。

---

### ① Mapper と XML のマッピング

```xml
<mapper namespace="com.example.demo.user.repository.UserMapper">
```

namespaceへ：

```text
Mapperの完全修飾名
```

を書く。

---

### ② Mapperメソッド と SQL のマッピング

```java
public int insertOne(MUser user);
```

↓

```xml
<insert id="insertOne">
```

メソッド名と id を一致させる。

---

### ③ 引数 と SQLパラメーター のマッピング

```xml
#{userId}
```

のように、

```text
Javaオブジェクトのフィールド
```

をSQLへ渡す。

---

# ■ 10-2 INSERT文

## ▽ サンプルアプリケーションの作成

---

## 1. ファイルなどの作成

以下を作成する。

```text
config
├── JavaConfig.java

user
├── domain/model
│   └── MUser.java
├── repository
│   └── UserMapper.java
├── service
│   ├── UserService.java
│   └── impl/UserServiceImpl.java

resources
├── mapper/h2
│   └── UserMapper.xml
```

---

## 2. MyBatis の追加

pom.xmlへ：

```xml
<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

を追加する。

さらに：

```xml
<!-- ModelMapper -->
<dependency>
    <groupId>org.modelmapper.extensions</groupId>
    <artifactId>modelmapper-spring</artifactId>
    <version>3.2.0</version>
</dependency>
```

も追加する。

---

## 3. application.yml の設定

```yml
mybatis:
  mapper-locations: classpath*:/mapper/h2/*.xml
```

を追加する。

これにより：

```text
mapper/h2 配下の XML
```

をMyBatisが読み込めるようになる。

---

## 4. schema.sql の修正

以下のテーブルを追加する。

- m_user
- m_department
- t_salary

---

## ◆ ER図

```text
部署
 1
 │
 多
ユーザー
 1
 │
 多
給料
```

---

## ◆ テーブル関係

### ユーザーと部署

```text
ユーザーは1つの部署に所属
```

---

### ユーザーと給料

```text
ユーザーには年月ごとの給料が存在
```

---

## 5. エンティティクラス作成

```java
@Data
public class MUser {

    private String userId;
    private String password;
    private String userName;
    private Date birthday;
    private Integer age;
    private Integer gender;
    private Integer departmentId;
    private String role;
}
```

---

## ◆ エンティティクラス

テーブルに対応するJavaクラスを：

```text
エンティティクラス
```

という。

---

## 6. Mapper作成

```java
@Mapper
public interface UserMapper {

    public int insertOne(MUser user);
}
```

---

## ◆ @Mapper

@Mapper を付与すると：

```text
MyBatis用Mapper
```

としてBean登録される。

---

## 7. INSERT SQL 作成

```xml
<insert id="insertOne">

    INSERT INTO m_user(
        user_id,
        password,
        user_name,
        birthday,
        age,
        gender,
        department_id,
        role
    )
    VALUES(
        #{userId},
        #{password},
        #{userName},
        #{birthday},
        #{age},
        #{gender},
        #{departmentId},
        #{role}
    )

</insert>
```

---

## ◆ insertタグ

INSERT文を書く場合：

```xml
<insert>
```

を使用する。

---

## 8. Service作成

```java
public interface UserService {

    public void signup(MUser user);
}
```

---

## 9. Service実装

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;

    @Override
    public void signup(MUser user) {

        user.setDepartmentId(1);
        user.setRole("ROLE_GENERAL");

        int count = mapper.insertOne(user);

        log.info("登録件数={}", count);
    }
}
```

---

## ◆ @RequiredArgsConstructor

finalフィールドを引数に持つコンストラクタを自動生成する。

---

## ◆ プレースホルダー

```java
log.info("登録件数={}", count);
```

の：

```text
{}
```

をプレースホルダーという。

---

## 10. JavaConfig 作成

```java
@Configuration
public class JavaConfig {

    @Bean
    ModelMapper modelMapper() {

        return new ModelMapper();
    }
}
```

---

## ◆ @Configuration

設定クラスとして認識される。

---

## ◆ @Bean

戻り値を：

```text
Bean登録
```

する。

---

## 11. Controller修正

```java
MUser user = modelMapper.map(form, MUser.class);

userService.signup(user);
```

を追加する。

---

## ◆ ModelMapper

```text
クラス間の値コピー
```

を簡単に行うライブラリ。

---

## ◆ SignupForm を直接渡さない理由

```text
Controller用Form
```

と、

```text
Service用Model
```

を分離することで：

- MVC分離
- 保守性向上
- 再利用性向上

を実現する。

---

# ■ 10-3 SELECT文 ― 複数件

## 1. application.yml 修正

```yml
mybatis:
  type-aliases-package: com.example.demo.*
  configuration:
    map-underscore-to-camel-case: true
```

を追加する。

---

## ◆ type-aliases-package

resultTypeで：

```text
完全修飾名省略
```

が可能になる。

---

## ◆ map-underscore-to-camel-case

```text
user_id
↓
userId
```

のように：

```text
スネークケース → キャメルケース
```

へ自動変換する。

---

## 2. Mapper修正

```java
public List<MUser> findMany();
```

を追加する。

---

## ◆ 複数件取得

複数件SELECTでは：

```text
List
```

を戻り値にする。

---

## 3. SELECT SQL 作成

```xml
<select id="findMany" resultType="MUser">

    SELECT
        *
    FROM
        m_user

</select>
```

---

## ◆ selectタグ

SELECT文を書く場合：

```xml
<select>
```

を使用する。

---

## ◆ resultType

SELECT結果を：

```text
どのJavaクラスへ入れるか
```

を指定する。

---

## 4. Service修正

```java
public List<MUser> getUsers();
```

を追加する。

---

## 5. Controller修正

```java
List<MUser> userList = userService.getUsers();

model.addAttribute("userList", userList);
```

を追加する。

---

## 6. list.html 修正

```html
<tr th:each="item : ${userList}">
```

を利用し、

```text
一覧表示
```

を行う。

---

## ◆ th:each

```text
繰り返し処理
```

を行う。

---

## ◆ 日付フォーマット

```html
${#dates.format(item.birthday, 'yyyy/MM/dd')}
```

を使用する。

---

## ◆ 三項演算子

```html
${item.gender == 1 ? '男性' : '女性'}
```

のように：

```text
条件分岐
```

も可能。

---

# ■ 10-4 SELECT文 ― 1件

## 1. ファイル作成

```text
UserDetailController.java
UserDetailForm.java
detail.html
```

を追加する。

---

## 2. Mapper修正

```java
public MUser findOne(String userId);
```

を追加する。

---

## 3. resultMap作成

```xml
<resultMap type="MUser" id="user">

    <id column="user_id" property="userId" />

    <result column="password" property="password" />

</resultMap>
```

---

## ◆ resultMap

```text
SELECT結果
↓
Javaクラス
```

の：

```text
詳細マッピング定義
```

を行う。

---

## ◆ idタグ

主キー用マッピング。

---

## ◆ resultタグ

通常カラム用マッピング。

---

## ◆ column

```text
DBカラム名
```

を書く。

---

## ◆ property

```text
Javaフィールド名
```

を書く。

---

## 4. SELECT SQL

```xml
<select id="findOne" resultMap="user">

    SELECT
        *
    FROM
        m_user
    WHERE
        user_id = #{userId}

</select>
```

---

## ◆ resultMap属性

```text
どのresultMapを使うか
```

を指定する。

---

## 5. Service修正

```java
public MUser getUserOne(String userId);
```

を追加する。

---

## 6. 一覧画面リンク追加

```html
th:href="@{/user/detail/{userId}(userId=${item.userId})}"
```

を追加する。

---

## ◆ パスパラメーター

```text
URLの一部として値を渡す方法
```

である。

---

## 7. 詳細画面Controller作成

```java
@GetMapping("/detail/{userId}")
```

で：

```text
指定ユーザー
```

を取得する。

---

# ■ 10-5 UPDATE文 と DELETE文

## ◆ UPDATE

```xml
<update id="updateOne">
```

を利用する。

---

## ◆ DELETE

```xml
<delete id="deleteOne">
```

を利用する。

---

## ◆ UPDATE/DELETE の流れ

```text
Mapper
↓
XML
↓
SQL
↓
Service
↓
Controller
```

という構造はINSERT/SELECTと同じ。

---

# ■ MyBatisで重要なポイント

## ◆ SQLをXMLへ分離

```text
Java
```

と

```text
SQL
```

を分離できる。

---

## ◆ Mapper + XML

MyBatisでは：

```text
Mapperインターフェース
```

と、

```text
XML
```

を連携させる。

---

## ◆ resultType

単純マッピング。

---

## ◆ resultMap

複雑マッピング。

---

## ◆ map-underscore-to-camel-case

```text
user_id
↓
userId
```

を自動変換。

---

## ◆ MVC分離

```text
Form
```

と、

```text
Entity
```

を分離する。

---

# ■ 最終まとめ

第10章では：

- MyBatisの基本構造
- Mapper
- XML
- INSERT
- SELECT
- resultType
- resultMap
- MVC分離
- ModelMapper

などについて学習した。

特に重要なのは：

```text
JavaコードとSQLを分離し、
SQLを柔軟かつ保守しやすく管理できる
```

という点である。

また、

```text
Mapper ↔ XML ↔ SQL
```

というMyBatis独特の構造や、

```text
resultMapによるマッピング
```

は、

今後の：

- テーブル結合
- 動的SQL
- 複雑検索

などの実装の基礎となる重要な内容である。
