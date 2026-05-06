---

# ■ 3-3 データベースから値を取得する

この節では、

- H2 Database
- Repository
- Service
- Entity
- SQL
- CRUD
- Optional

など、これまで以上に多くの概念が同時に登場した。

特に今回は、

- 「Javaだけで完結しない」
- 「Spring内部で自動実行される処理が多い」
- 「DBという外部システムが追加された」

という点で難易度が一気に上がった。

そのため今回も、

- 用語
- 自動処理
- データの流れ
- Spring内部で何をしているか

を分解しながら理解を進めた。

---

# ■ 事前知識の整理（用語・仕組みの具体理解）

## ◆ ビジネスロジック

業務に必要な処理。

より具体的には、「そのシステムが現実世界で何を解決したいのか」に対応する処理である。

例：銀行システムの場合

業務：
- お金を管理する

ビジネスロジック：
- 残高確認
- 振込
- 手数料計算

今回のアプリでは、「IDを使ってデータを取得する」という処理がビジネスロジックになる。

---

## ◆ Service

ビジネスロジックを担当するクラス。

Controller：
- リクエスト受付
- 画面遷移
- Model登録

Service：
- データ取得
- 計算
- 判定

つまり、

- Controller = 受付  
- Service = 本処理

という役割分担になっている。

---

## ◆ Repository

データベース関連処理を担当するクラス（正確にはインタフェース）。

主な役割：
- DB検索
- DB保存
- DB更新
- DB削除

注意：
Git / GitHub のリポジトリとは全く別物。

---

## ◆ YAML（yml）

設定を書くための形式。
今回の application.yml は、

- DB接続設定
- Spring設定
- H2設定

などを管理している。

### ■ propertiesとの違い

properties：

    spring.datasource.url=...
    spring.datasource.username=...

YAML：

    spring:
      datasource:
        url: ...
        username: ...

YAMLの方が階層構造を整理しやすい。

---

## 【疑問①】なぜ前回までは application.yml が不要だったのか？

最初は、「なぜ今まで設定ファイルなしで動いたのか？」が分からなかった。

### ■ 理解したこと

Spring Bootは、設定を書かなくてもデフォルト値で動く部分が多い。

3-2までは：

- DBなし
- 特殊設定なし

だったため、設定不要だった。

しかし今回は：

- H2 Database使用
- JDBC接続
- SQL初期化

などが必要になったため、設定ファイルが必要になった。

---

## ◆ JDBCドライバ

JavaとDBの通訳。

DB製品ごとに通信方法が違うため、専用ドライバが必要になる。

今回使用するorg.h2.DriverはH2用ドライバ。

---

## ◆ インメモリ型データベース

メモリ上で動くDB。

特徴：

- 高速
- 再起動で消える

通常DB：

- HDD / SSD保存
- 消えない

インメモリDB：

- RAM保存
- 消える

---

## ◆ 組み込みデータベース

アプリ内部に組み込まれているDB。

今回の場合、Spring Bootアプリ内にH2が含まれている。

特徴は：

- インストール不要
- すぐ使える
- 学習向け
- テスト向け

である。

通常のDBは：

- MySQL
- PostgreSQL

などを別途インストールする必要がある。

しかしH2は、Spring Bootアプリ内に最初から含まれているため、環境構築が非常に簡単。

また今回はインメモリ型DBでもあるため、

- Spring Boot再起動
  
↓

- DB再作成
- テーブル再作成
- 初期データ再投入

が毎回自動実行される。

つまり、UPDATE / DELETE / INSERTなどでデータを壊しても、再起動すれば元に戻る。

そのため、

- 学習中に気軽に試せる
- テストデータを何度でも初期化できる

という点で、学習・テスト向けである。

---

## ◆ classpath:

Spring Bootでは、src/main/resources を classpath として扱う。

つまり、classpath:schema.sql は src/main/resources/schema.sql を意味する。

---

## ◆ SQLによる初期化

Spring Boot起動時に：

1. テーブル作成
2. データ投入

を自動実行する仕組み。
つまり、

- UPDATE
- DELETE
- INSERT

でデータを変更しても、再起動すると：

- schema.sql
- data.sql

が再実行され、初期状態に戻る。

---

## ◆ H2コンソール

ブラウザからH2 Databaseを操作できる画面。

URL：http://localhost:8080/h2-console

---

## ◆ Lombok

getter / setterなどを自動生成するライブラリ。

通常：

public String getName()

などを自分で書く必要がある。

しかしLombokでは：

@Dataを付けるだけで自動生成される。

---

## ◆ @Data

Lombokのアノテーション。

自動生成されるもの：

- getter
- setter
- toString
- equals
- hashCode

---

## ◆ @Id

「このフィールドが主キーである」ことを示すアノテーション。

今回は private String id; が主キー。

---

## ◆ CrudRepository

Spring Dataが用意しているインタフェース。

継承するだけで：

- Create
- Read
- Update
- Delete

の基本操作が使える。

---

## ◆ CRUD

データベース操作における

- Create → 作成
- Read → 取得
- Update → 更新
- Delete → 削除
  
のこと

---

## ◆ findById()

IDで1件取得するメソッド。

sampleRepository.findById(id)

だと、内部では、 SELECT * FROM sample WHERE id = ?;

のようなSQLが生成される。

---

## ◆ Optional

「値があるかもしれない箱」。

重要なのは：

- データがあるとは限らない
- null安全のために存在する

という点。

今回のコード：

    Optional<Sample> optionalSample
        = sampleRepository.findById(id);

では、

- 指定したIDのデータが存在する場合
- 存在しない場合
  
の両方があり得る。

findById("1")ならデータが見つかるかもしれないが、

findById("999")なら存在しない可能性がある。

そのため、findById()は直接Sampleを返すのではなく、Optional<Sample> という「存在するかもしれない箱」を返している。

### ■ イメージ

データあり：[ Sample ]

データなし：[ 空 ]

重要なのは、「データが見つからなかった可能性」をOptionalで表現している点である。

もしOptionalを使わず直接Sampleを返す場合、データが存在しなかった時はnullを返すしかなくなる。

しかしnullは：

- NullPointerException
- nullチェック漏れ

などの問題を起こしやすい。そのためJavaでは、「存在しない可能性」を安全に扱うためにOptionalが使われる。

---

## 【疑問②】なぜ Optional<Sample> なのか？

最初は、「なぜ直接Sampleを返さないのか？」が分からなかった。

### ■ 理解したこと

ID検索では：

- 見つかる場合
- 見つからない場合

の両方がある。そのため、「存在しない可能性」を表現する必要がある。

そこで Optional が使われている。

---

## 【疑問③】Optional が空だったらどうなるのか？

コード：optionalSample.get();

を見た時、「空ならどうなるのか？」が疑問だった。

### ■ 理解したこと

Optional.empty の状態で get() を呼ぶと例外になる。

つまり：

    Optional.empty
    ↓
    get()
    ↓
    エラー

となる。教科書では簡略化のため例外処理を省略していた。

---

## ◆ @Service

「このクラスはServiceです」とSpringに登録するためのアノテーション。

自動生成する機能ではなく、役割を示す目印。

---

## ◆ @Autowired

DI（依存性注入）のためのアノテーション。

現時点では、「Springが必要なインスタンスを自動で入れてくれる」程度で理解しておけば十分。

例：

    @Autowired
    private SampleRepository sampleRepository;
    
↓
イメージ：

    private SampleRepository sampleRepository
        = （Springが用意したもの）

---
## ◆ @RequestParam

画面入力値を受け取るためのアノテーション。

### 例

HTML：

    <input type="text" name="id">

↓

Controller：

    @RequestParam("id") String id

重要ポイント：

- "id" → HTML側の名前
- String id → Java変数

つまり、HTML側で入力された値を、

    name="id"

という名前で送信し、

Controller側で、

    @RequestParam("id")

を使って取り出している。

その後、

    String id

というJava変数に代入される。

### イメージ

入力された値
↓
name="id" の箱に入る
↓
@RequestParam("id") で取り出す
↓
String id に代入

なお、

    @RequestParam("a") String b

の場合、

    <input type="text" name="a">

で送られた値が、

    b

という変数に入る。

つまり重要なのは、

- HTML側のname
- @RequestParam("")

が一致していること

であり、Java側の変数名は自由につけられる。

---

## ◆ Model

HTMLにデータを渡すための箱。

Controllerで取得したデータは、そのままではHTML側から見えない。

例えば、

    Sample sample = sampleService.getSample(id);

で取得したsample変数は、Controller内部のローカル変数であり、HTML側から直接アクセスできない。

そのため、

    model.addAttribute("sample", sample);

を使って、HTML側にデータを渡す必要がある。

### ■ addAttribute の意味

- "sample" → HTML側で使う名前（キー）
- sample → Javaのデータ（Sampleインスタンス）

つまり、

    model.addAttribute("sample", sample);

は、

「sampleという名前でSampleデータをHTMLに渡す」

という意味になる。

その後、Thymeleaf側では：

    ${sample.id}
    ${sample.str}

のように取得できる。

ここでの：

    ${sample}

は、

    model.addAttribute("sample", sample);

の第一引数 "sample" を指している。

### 流れ

Java側：

    model.addAttribute("sample", sample);

↓

Modelの中：

    "sample" → Sampleデータ

↓

HTML側：

    ${sample.id}

という対応関係になっている。

---

## 【疑問④】Modelがないとどうなる？

最初は、

「sample変数をそのままHTMLで使えないのか？」

と思った。

実際、

    return "hello/db";

だけでも画面自体は表示される。

しかし、Modelにデータ登録していないため、

    ${sample.id}

などが取得できず、画面には何も表示されない。

---

## ◆ Thymeleafでの表示

例：

    <td th:text="${sample.id}"></td>

意味：

- sample → Modelのキー名
- id → Sampleクラスのフィールド

つまり、

    ${sample.id}

は、

「Modelからsampleを取り出し、その中のidを表示する」

という意味になる。

---

# ■ 学び・気づき

- MVCの役割分担がかなり明確になった
- Controllerは「処理そのもの」を行う場所ではない
- Serviceがビジネスロジックを担当する
- RepositoryはSQLを隠蔽してくれる
- Spring内部では大量の自動生成が行われている
- Optionalは「存在しない可能性」を安全に扱うための仕組み
- HTMLとJavaは直接繋がっていない
- Modelが橋渡しをしている

---

# ■ 苦戦した理由

今回難しかった最大の理由は、「Springが内部で大量の処理を自動実行している」ことである。

特に：
- Repository実装
- SQL生成
- DI
- ViewResolver
- Thymeleaf

などが自動化されており、「自分で書いていないのに動く」という点が非常に混乱しやすかった。

また今回は：
- Java
- SQL
- DB
- Spring
- HTML
- Thymeleaf

が同時接続されたため、前章以上に全体像を掴みにくかった。

---

# ■ 最終理解（DB検索全体の流れ）

1. ブラウザ入力
2. GET /hello/db?id=1
3. Controller受信
4. @RequestParamでid取得
5. Service呼び出し
6. Repository.findById(id)
7. SQL生成
8. DB検索
9. Sample取得
10. Model登録
11. db.htmlへ遷移
12. Thymeleafで表示

---

# ■ 自分の理解の変化

### 最初
- Repositoryが何をしているか分からない
- DBとJavaの関係が曖昧
- Spring内部の自動処理が見えない

### 途中
- Service / Repository の役割分担が見え始める
- ModelとHTMLの繋がりを理解
- CRUDやOptionalの意味が見える

### 最終
- MVC + DB連携の全体像が繋がった
- Spring Bootが何を自動化しているか理解できた
- 「JavaだけではWebアプリは成立しない」ことを理解した

---

# ■ 結論

この節は単なるDB接続ではなく、

- MVC
- DI
- ORM的発想
- SQL抽象化
- Webアプリの役割分担

など、Spring Bootにおける「実務的Webアプリ構造」の入口だった。
また、難しかった理由は文法ではなく、「複数技術が同時接続されたこと」にあった。
