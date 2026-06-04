# chapter10_learning_log.md

# 第10章 学習ログ ― MyBatis基本編

## 学習概要

第10章では、Spring Boot における O/Rマッパー「MyBatis」の基本を学習した。

これまで学習してきた Spring Data JDBC と比較しながら、

- SQLをXMLで管理する仕組み
- JavaオブジェクトとDBテーブルの対応付け
- MapperとXMLのマッピング
- INSERT / SELECT / UPDATE / DELETE の基本
- FormクラスとEntityクラスの責務分離
- MyBatis内部で何が起きているか

などを理解した。

特に本章では、

```text
HTML
↓
Form
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

という Spring Boot + MyBatis の全体構造がかなり明確になった。

---

# 10-1 MyBatisの仕組みと特徴

## 学んだこと

MyBatis は SQL を XML に分離して管理できる O/Rマッパーであり、

- SQLをJavaに直書きしなくてよい
- 複雑なSQLを扱いやすい
- 動的SQLを書ける
- SQL結果をJavaオブジェクトへ自動変換できる

という特徴がある。

Spring Data JDBC と比較すると、特に JOIN や複雑なSELECT時の扱いやすさに大きな差があることを理解した。

---

## 【疑問】JavaクラスとDBテーブルを「対応付ける」とは？

JavaとDBでは、そもそもデータの持ち方が異なる。

```text
Java
↓
オブジェクト

DB
↓
テーブル
```

例えば、

```text
Java                  DB
userId         ←→     user_id
userName       ←→     user_name
```

のような対応関係を決める必要がある。

昔ながらの JDBC では、

```java
user.setUserId(rs.getInt("user_id"));
user.setUserName(rs.getString("user_name"));
```

のようなコピー処理を毎回自分で書かなければならなかった。

しかし MyBatis では、

```java
User user = userMapper.findById(1);
```

だけで、

- SQL実行
- DB取得
- User生成
- setter呼び出し

まで自動で行われる。

つまり O/Rマッパーとは、

「DBの表データをJavaオブジェクトへ変換する仕組み」

であると理解した。

---

# 10-2 INSERT文

## 学んだこと

MyBatis を使った INSERT処理を実装した。

ここでは、

- Mapper
- XML
- Service
- Entity
- ModelMapper

など、MyBatis開発に必要な基本構造を学習した。

特に、

```text
Controller
↓
Service
↓
Mapper
↓
XML(SQL)
```

という責務分離が非常に重要であると理解した。

---

## 【疑問】各フォルダ・クラスは何を担当している？

### JavaConfig.java

ModelMapper を Bean登録するための設定クラス。

Bean登録することで、

```java
private final ModelMapper modelMapper;
```

のように DI できるようになる。

---

### ModelMapper

Javaオブジェクト同士をコピーするライブラリ。

例えば、

```text
SignupForm
↓
MUser
```

への変換を、

```java
modelMapper.map(form, MUser.class);
```

だけで実行できる。

内部では、

```text
form.userId
↓
user.userId
```

のように、同名フィールドを自動コピーしている。

つまり ModelMapper は、

「Javaクラス同士のデータ転送機」

であると理解した。

---

### MUser.java

m_userテーブル1行を表現する Entityクラス。

```text
DB             Java
user_id   →    userId
user_name →    userName
```

のように対応付けされる。

---

### UserService / UserServiceImpl

業務ロジック担当。

単なる SQL実行ではなく、

- HTML側の操作内容に応じてDB処理を決定
- DBデータを画面へ渡す
- 業務全体の流れを制御

などを担当している。

実際の SQL は XML に書かれている。

---

### UserMapper.java

DBアクセス担当。

ここには SQL は書かれていない。

```java
findMany()
```

のように、

「どんなDB操作をするか」

だけを定義している。

実際の SQL は XML側の

```xml
<select id="findMany">
```

と対応付けられる。

---

### UserMapper.xml

実際の SQL を書く場所。

Mapper と XML がセットで動いている。

---

## 【補足】mybatis.mapper-locations の意味

```yaml
mybatis:
  mapper-locations: classpath*:/mapper/h2/*.xml
```

は、

「MyBatis が読む SQL(XML) はここにある」

という設定。

つまり、

```java
mapper.findMany()
```

を呼ぶと、

```xml
<select id="findMany">
```

が実行されるようになる。

---

## 【疑問】pom.xml と application.yml の違いは？

| ファイル | 役割 |
|---|---|
| pom.xml | どのライブラリを使うか |
| application.yml | ライブラリをどう動かすか |

例えば、

```xml
<dependency>
```

はライブラリ追加。

一方、

```yaml
mybatis:
```

は MyBatis の動作設定。

つまり、

```text
pom.xml
↓
部品追加

application.yml
↓
設定変更
```

という違いがある。

---

## 【補足】logging.level の仕組み

```yaml
logging:
  level:
    '[com.example.demo]': debug
```

を書くことで DEBUGログも表示される。

ログには優先順位があり、

```text
TRACE
DEBUG
INFO
WARN
ERROR
```

の順になっている。

例えば INFO設定なら、

- INFO
- WARN
- ERROR

のみ表示され、DEBUGは表示されない。

---

## 【補足】DEBUGログを開発中に使う理由

DEBUGログでは、

- SQL全文
- SQLパラメータ
- Bean生成
- HTTP詳細

など大量の情報が出る。

開発中は便利だが、本番では機密情報漏洩リスクもあるため、

```text
開発 → DEBUG
本番 → INFO / WARN
```

にすることが多い。

---

## 【重要】MyBatisの「3つのマッピング」

MyBatisで最重要なのは以下の3つ。

### ① Mapper ⇔ XML

```xml
<mapper namespace="...UserMapper">
```

---

### ② メソッド ⇔ SQL

```java
insertOne()
```

↓

```xml
<insert id="insertOne">
```

---

### ③ 引数 ⇔ SQLパラメータ

```xml
#{userId}
```

↓

```java
MUser.userId
```

この3つが一致して初めて MyBatis が動作する。

---

## 【疑問】`#{}` の正体は何か？

最初は、

```xml
#{userId}
```

を見ると、

「文字列置換」

のように見えた。

しかし実際には、

```sql
?
```

へ変換されている。

つまり MyBatis内部では PreparedStatement が使われている。

実際のログでも、

```text
Preparing:
INSERT INTO ...
VALUES (?, ?, ?)
```

となっていた。

その後、

```text
Parameters:
```

で実際の値が渡されている。

つまり、

```xml
#{userId}
```

は、

「SQLへ安全に値を渡すプレースホルダ」

であると理解した。

---

## 【補足】MyBatisのINSERT処理全体

実際には、

```text
HTML入力
↓
SignupForm
↓
ModelMapper
↓
MUser
↓
Service
↓
Mapper
↓
XML(SQL)
↓
DB
```

という流れになっている。

---

## 【疑問】SignupForm をそのまま Service に渡してはいけない理由

SignupForm は画面専用クラス。

MUser は DB専用クラス。

両者を分離することで、

- 画面変更がServiceへ波及しない
- DB専用項目を隠せる
- 不正更新防止
- MVC責務分離

などのメリットがある。

つまり、

```text
HTML ⇔ Form ⇔ Entity ⇔ DB
```

という分離が重要。

---

## 【補足】MVCモデルの責務分離

もし Service が SignupForm に依存すると、

```text
画面変更
↓
Service修正
↓
他画面へ影響
```

という密結合になる。

一方、

```text
SignupForm
↓
MUserへ変換
↓
Serviceへ渡す
```

にすると、Service は画面変更から独立できる。

これは MVC の責務分離として非常に重要。

---

## 【補足】INSERT後のMyBatisログ

MyBatis の DEBUGログでは、

```text
Preparing:
```

→ 実行SQL

```text
Parameters:
```

→ SQLへ渡した値

```text
Updates:
```

→ 更新件数

が確認できる。

つまり、

```text
SQLが本当に実行されたか
どんな値が渡ったか
```

を確認しやすい。

---

# 10-3 SELECT文（複数件）

## 学んだこと

MyBatis を使って複数件SELECTを実装した。

取得した List を、

```html
th:each
```

で一覧表示した。

ここで、

```text
DB
↓
SELECT結果
↓
MUser生成
↓
Thymeleaf表示
```

という流れが理解できた。

---

## 【疑問】MyBatisはどうやってMUserを生成している？

最初は、

```java
new MUser()
```

を書いていないのに、

なぜ MUser オブジェクトが生成されるのかわからなかった。

実際には MyBatis が内部で、

- MUserインスタンス生成
- setter呼び出し
- 値格納

を自動で行っている。

つまり、

```xml
<select resultType="MUser">
```

を書くことで、

SELECT結果 → MUser変換

が自動化されている。

---

## 【疑問】`SELECT * FROM` の実態

最初は単に、

「全部取得するSQL」

としか理解していなかった。

しかし実際には、

```text
m_userテーブル
↓
全レコード取得
↓
各行ごとにMUser生成
↓
Listへ格納
```

という処理が内部で行われている。

つまり、

```java
List<MUser>
```

が返るということは、

「MUserオブジェクトが複数生成されている」

という意味だった。

---

## 【補足】`<thead>` の役割

```html
<thead>
```

はテーブルの見出し部分。

```text
ユーザーID
ユーザー名
誕生日
```

などを表示している。

なお、

```html
<th>
```

は Thymeleaf ではなく、

```text
table header
```

の略。

実データは `<tbody>` に入っている。

---

# 10-4 SELECT文（1件）

## 学んだこと

1件取得では、

```java
MUser
```

単体を返す。

複数件取得とは異なり、

```java
List<MUser>
```

ではない。

また、

```text
URL
↓
ユーザーID取得
↓
SELECT
↓
MUser生成
↓
画面表示
```

という流れが理解できた。

---

# 10-5 UPDATE文とDELETE文

## 学んだこと

UPDATE と DELETE の基本構造を学習した。

特に、

```text
画面
↓
Form
↓
Controller
↓
Service
↓
Mapper
↓
UPDATE / DELETE
```

という処理構造が、INSERTとほぼ共通であることを理解した。

---

# 総括

本章では、

- MyBatis の基本構造
- Mapper と XML の関係
- SQL と Java のマッピング
- INSERT / SELECT / UPDATE / DELETE
- MVC責務分離
- Form と Entity の役割分担
- MyBatis内部で何が起きているか

などを体系的に学習できた。

特に、

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

という Spring Boot + MyBatis の全体像がかなり理解できるようになった。

また、

```text
Formクラス
↓
Entityクラス
```

へ変換する理由や、

MVCにおける責務分離の重要性についても理解が深まった。

MyBatis は単なる SQL実行ツールではなく、

「JavaオブジェクトとSQLを安全かつ柔軟に接続する仕組み」

であると理解できた。

:contentReference[oaicite:0]{index=0}
