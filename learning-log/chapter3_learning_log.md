# 学習ログ：第3章 Webアプリケーション開発・はじめの一歩（詳細版）

---

# ■ この章の特徴と学習方針

この章はこれまでのJava単体の学習とは異なり、

- HTML
- HTTP（GET / POST）
- Spring（Controller, Model）
- テンプレートエンジン（Thymeleaf）

といった複数の概念が同時に登場する。

そのため、流れを追おうとしても理解が追いつかず、「何が分からないのか分からない状態」に陥った。

そこで今回は、

- まず分からないワード・文法・仕組みを全て洗い出す
- それぞれを単体で理解する
- その後で全体の流れに戻る

という順序で復習を行った。

---

# ■ 事前知識の整理（用語・仕組みの具体理解）

## ◆ HTTPのGETメソッド

GETはデータを「取得する」ためのリクエストである。

主な用途：
- 商品一覧を見る
- ページを表示する

特徴：
- サーバーの状態を変えない
- URLにデータが表示される

例：

https://example.com/search?q=apple

このときの `q=apple` は、

- q → キー（データの名前）
- apple → 値（データ本体）

つまり、URLの一部として「キーと値のデータ」がそのまま表示されている。

---

## ◆ HTTPのPOSTメソッド

POSTはデータを「送る」ためのリクエストである。

主な用途：
- ログイン
- 会員登録
- フォーム送信

特徴：
- サーバーの状態が変わる
- URLにデータが表示されない

疑問：
URLに出ないならデータはどこにあるのか？

理解：
データはリクエストの「body（見えない部分）」に含まれている。

---

## ◆ テンプレートエンジン

HTMLの空欄にデータを埋めて完成させる仕組み。

従来：
\<p>Hello Naoki\</p>（固定）

テンプレート：
\<p>Hello ○○\</p>（後から変わる）

例：
\<p th:text="${name}">\</p>  
model.addAttribute("name", "Naoki");

結果：
\<p>Naoki\</p>

つまり、HTMLは最初から完成しているのではなく、後からデータを埋め込んで完成することがある。

---

## ◆ Thymeleaf

Spring Bootで使用されるテンプレートエンジンであり、「HTMLの中にサーバー側のデータ（Model）を埋め込んで、最終的なHTMLを生成する仕組み」である。

### ■ なぜ必要なのか

通常のHTMLは固定された内容しか表示できない。

例：
\<p>Hello Naoki\</p>

この場合、常に「Naoki」と表示される。

しかしWebアプリでは、

- ユーザー入力
- データベースの値

などによって表示内容を変える必要がある。

そのため、HTMLの中に「後からデータを差し込む仕組み」が必要になる。それを担うのがThymeleafである。

---

### ■ 基本構文

\<p th:text="${name}">\</p>

この1行は、次の2つの要素で構成されている：

- th:text → 表示内容を書き換える命令（Thymeleafの属性）
- ${name} → Modelからnameというデータを取り出す（EL式）

---

### ■ 実際の処理の流れ

Controller側：
model.addAttribute("name", "Naoki");

この時点でModelの中は `name → "Naoki"` という状態になっている。

HTML側：
\<p th:text="${name}">\</p>

ThymeleafがこのHTMLを処理する際に、

1. ${name} を評価する
2. Modelの中から name を探す
3. "Naoki" を取得する
4. th:text によってタグの中身を書き換える

---

### ■ 最終的に生成されるHTML

\<p>Naoki\</p>

つまり、Thymeleafは「HTMLを直接表示するのではなく、一度処理して完成HTMLを作ってから表示する」という仕組みになっている。

---

## ◆ Model

ControllerからHTMLにデータを渡すための箱。

例：
model.addAttribute("name", "Naoki");

意味：
- "name" → キー（ラベル）
- "Naoki" → 値

Modelの中では `name → "Naoki"` という形で保持される。

重要：
これは変数ではなく、「キーと値のセット」である。

---

## ◆ コントローラー

コントローラーとは、「ブラウザから送られてきたリクエストを受け取り、その内容に応じてどの処理を行い、どの画面を返すかを決める役割」を持つクラスである。

### ■ 具体例

@GetMapping("/hello")  
public String getHello() {  
    return "hello";  
}

### ■ 何が起きているか（処理の流れ）

ブラウザで以下のURLにアクセスする：

http://localhost:8080/hello

このとき、内部では次のような処理が行われている：

1. ブラウザがサーバーにリクエストを送る  
   → GET /hello

2. Springがリクエストを受け取る  
   → 「/hello に対応する処理はどれか？」を探す

3. @GetMapping("/hello") が一致  
   → getHello() メソッドが呼ばれる

4. メソッドが実行される  
   → return "hello" を返す

5. Springが「hello」というビュー名を受け取る

6. ViewResolverが  
   → templates/hello.html を探す

7. hello.html をブラウザに返す

---

## 【疑問①】@GetMappingは何か

@GetMapping("/hello") は、Springが提供するアノテーションであり、「GETメソッドで /hello にアクセスが来たときに、このメソッドを実行する」という対応関係を定義している。

### ■ 何をしているのか（より具体的に）

Springはアプリ起動時に、

- どのクラスがControllerか（@Controller）
- どのメソッドがどのURLに対応しているか（@GetMapping など）

をすべて読み取り、「URLとメソッドの対応表（ルーティング情報）」を内部に作成する。

例：
@GetMapping("/hello")  
public String getHello() {  
    return "hello";  
}

は内部的に、`GET /hello → getHello()` を実行するルールとして登録される。

### ■ リクエスト時の動き

ブラウザから `GET /hello` というリクエストが来ると、

1. Springがリクエストを受け取る
2. URL（/hello）とHTTPメソッド（GET）を確認する
3. 登録済みの対応表から一致するものを探す
4. @GetMapping("/hello") が一致
5. getHello() メソッドを呼び出す

という処理が行われる。

### ■ なぜ「Java単体では意味を持たない」のか

@GetMapping はJavaの文法ではなく、

- if文
- for文

のような標準機能ではない。

これはSpringが用意したアノテーションであり、Springがそれを読み取って処理を振り分けているため、Springが存在しない環境では単なる「目印」にすぎない。

---

## 【疑問②】なぜ return "hello" でHTMLが表示されるのか

@GetMapping("/hello")  
public String getHello() {  
    return "hello";  
}

この `return "hello"` はHTMLそのものではなく、「表示する画面の名前（ビュー名）」を返しているだけである。

ではなぜこれでHTMLが表示されるのかというと、Springの内部で ViewResolver（ビューリゾルバー）という仕組みが動いているためである。

### ■ ViewResolverの役割

ViewResolverは、「Controllerが返したビュー名を、実際のHTMLファイルの場所に変換する役割」を持つ。

### ■ 具体的に何をしているのか

return "hello"; と書いたとき、ViewResolverは次のような処理を行う：

1. "hello" をビュー名として受け取る
2. 既定のルールに従ってパスを組み立てる

例（Spring Bootのデフォルト設定）：

templates/ + hello + .html  
→ templates/hello.html

3. そのファイルを探す
4. 見つかったHTMLをレスポンスとして返す

### ■ 重要なポイント

- returnしているのはHTMLではなく「名前」だけ
- 実際のファイル探索はViewResolverが行う
- ファイルの場所や拡張子はルールで決まっている

### ■ ルール（デフォルト）

Spring Bootでは通常：

- フォルダ → templates
- 拡張子 → .html

したがって、return "hello" は自動的に `templates/hello.html` に変換される。

---

## 【疑問③】コントローラーは1つなのか？それとも複数存在するのか？

最初の疑問：
コントローラーは1つだけ存在するのか、それとも複数作れるのかが分からなかった。

また、1つのコントローラーに複数の @GetMapping を持たせるのか、それとも機能ごとに分けるべきなのかが不明だった。

### ■ 実際に確認した結果

結論としては、どちらも可能であり、制限はない。

### ■ パターン①：1つのコントローラーにまとめる

@Controller  
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/world")
    public String world() {
        return "world";
    }
}

この場合：
- 1つのクラスが複数のURLを担当する
- 小規模なアプリでは問題ない

### ■ パターン②：複数のコントローラーに分ける

@Controller  
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}

@Controller  
public class WorldController {

    @GetMapping("/world")
    public String world() {
        return "world";
    }
}

この場合：
- URLごとに責任を分離できる
- クラスごとの役割が明確になる

### ■ なぜ実務では分けるのか

1つのクラスにすべて書くと、

- コード量が増える
- 可読性が下がる
- 修正時の影響範囲が広くなる

そのため、機能単位でコントローラーを分割するのが一般的である。

---

## 【疑問④】ViewResolverは同名ファイルをどう扱うのか？

最初の疑問：
templatesフォルダ内に同じ名前のHTMLファイルが複数存在する場合、

例：
templates/
 ├── test/
 │    └── hello.html
 └── deploy/
      └── hello.html

このとき、return "hello"; と書いた場合に、どのhello.htmlが選ばれるのかが不明だった。

### ■ 実際の挙動

結論：
return "hello"; では特定できないため、正しく動作しない。

### ■ 理由

ViewResolverは基本的に「ビュー名をそのままパスに変換する」だけであり、曖昧な検索や優先順位判断は行わない。

つまり、`hello → templates/hello.html` という単純な変換しかできない。

### ■ 正しい指定方法

フォルダ構造を含めた相対パスを明示する必要がある。

例：
return "test/hello";  
return "deploy/hello";

---

## 【疑問⑤】URLは誰が決めているのか？自動で決まるのか？

最初の疑問：
ブラウザでアクセスするURL（/hello や /world）は、

- Springが自動で決めているのか
- それとも開発者が自分で決めているのか

が分からなかった。

### ■ 実際の挙動

結論：
URLは自動では決まらず、@GetMapping や @PostMapping で開発者が明示的に定義したものだけが有効になる。

### ■ 具体例

@GetMapping("/hello")  
public String getHello() {  
    return "hello";  
}

この場合：

- 有効なURL → /hello
- それ以外のURL（例：/world）→ 対応する定義がなければエラー（404）

### ■ 別の例（意図的にずらした場合）

@GetMapping("/world")  
public String getHello() {  
    return "hello";  
}

この場合：

- /world にアクセス → hello.html が表示される
- /hello にアクセス → 何も起きない（404）

---

## 【疑問⑥】@Controllerがないと何が起きるのか？

最初の疑問：
@Controller を付ける意味が分からず、

- なくても動くのではないか
- 単なる目印ではないのか

という疑問があった。

### ■ 実際の挙動

結論：
@Controller が付いていないクラスは、Springに「コントローラー」として認識されないため、@GetMapping("/hello")を書いてもURLアクセス時に一切呼び出されない。

### ■ 具体例

@Controller がある場合：

@Controller  
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}

→ /hello にアクセスすると表示される

@Controller がない場合：

public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}

→ /hello にアクセスしても反応しない（404）

### ■ なぜこうなるのか

Springはアプリ起動時に、「どのクラスをコントローラーとして扱うか」をスキャンして決定している。

その判断基準が @Controller であり、これが付いているクラスだけが、

- URLと紐づけられ
- リクエスト処理の対象になる

---

## ◆ formタグ

入力データをまとめてサーバーに送るための箱。

段階的理解：

1. \<form>\</form> → 空の箱
2. input追加 → 入力だけ可能
3. button追加 → 送信可能
4. action / method追加 → 完全

完成形：
\<form action="/submit" method="post">  
    \<input type="text" name="fruit">  
    \<button type="submit">送信\</button>  
\</form>

意味：
入力データをfruitという名前で、/submitにPOSTで送る。

---

## ◆ inputタグ

\<input type="text" name="fruit">

- type → 入力の種類（文字）
- name → データのラベル

重要：
nameは変数ではなく、「送信時のキー」。

---

## ◆ buttonタグ

\<button type="submit">送信\</button>

押すことでPOSTリクエストが発生する。

---

## ◆ @PostMapping

@PostMapping("/submit")

意味：
「/submit に対してPOSTリクエストが来たときに、このメソッドを実行する」という指定である。

ここで重要なのは、「URLだけでなく、HTTPメソッド（GETかPOSTか）も含めて判断している」という点。

例えば同じ /submit でも：

- GET /submit → @GetMapping が処理
- POST /submit → @PostMapping が処理

というように、同じURLでも別の処理として扱われる。

実際の流れ：
\<form action="/submit" method="post">  
\<input type="text" name="fruit">  
\<button type="submit">送信\</button>  
\</form>

ユーザーが「apple」と入力して送信すると、

POST /submit  
fruit=apple

というリクエストがサーバーに送られる。

Springはこのリクエストを受け取り、

1. URLが /submit
2. メソッドが POST

であることを確認し、@PostMapping("/submit")に対応するメソッドを呼び出す。

つまり@PostMappingは、「どのリクエストを、どのJavaメソッドで処理するかを決めるルーティングの役割」を持っている。

---

## ◆ @RequestParam

最大の疑問ポイント。

理由：
Javaは「fruitがどこから来たか分からない」ため、@RequestParam String fruitと書くことで、「fruitという名前のデータをこの引数に入れる」と明示する。

### ◆ 処理の流れ（具体）

\<form action="/submit" method="post">  
\<input type="text" name="fruit">  
\<button type="submit">送信\</button>  
\</form>

ユーザー入力：apple

↓

POST /submit  
fruit=apple

↓

@RequestParamで取得

fruit = "apple"

↓

メソッド実行

return "result"

---

## ◆ Modelの必要性

疑問：
@PostMappingで public String submitForm(@RequestParam String fruit) のようにfruitを受け取っているなら、そのままHTMLで使えるのではないか？

結論：
そのままではHTMLには渡らない。

理由：
Java（Controller）とHTML（Thymeleaf）は、直接同じ変数を共有しているわけではないため。

より具体的に説明すると、Controllerの中で fruit = "apple" という状態になっていても、その値はあくまで「Javaのメソッド内のローカル変数」であり、そのままではHTML側には一切見えない。

HTML側（Thymeleaf）は、Controllerの変数を直接参照することはできず、参照できるのは「Modelに登録されたデータだけ」である。

つまり、

Controller（Java）側：
fruit = "apple"（ローカル変数）

HTML側：
この変数は存在しないため、何も表示できない

ここで必要になるのがModelである。

model.addAttribute("fruit", fruit);

とすることで、

- "fruit" → データの名前（キー）
- fruit → 実際の値（"apple"）

をModelに登録する。

すると内部的には、Modelの中に `fruit → "apple"` というデータが保持される。

その状態で return "result"; とすると、Springは、

- result.html に遷移する
- 同時にModelの中身もHTMLに渡す

という処理を行う。

その結果、HTML側で \<p th:text="${fruit}">\</p> と書くと、Modelの中からfruitを探し、"apple" を取り出して表示することができる。

つまりModelの役割は、「Javaの変数を、そのまま渡すのではなく、HTMLから参照可能な形に変換して橋渡しすること」である。

---

## ◆ Model + addAttribute

model.addAttribute("fruit", fruit);

意味：
この1行は、「Javaの変数で持っている値を、HTML側から参照できる形に登録する処理」である。

より具体的には、

- "fruit" → HTMLから取り出すときの名前（キー）
- fruit → Javaの変数（例："apple"）

という対応関係を作っている。

例えば、@RequestParam String fruit によって fruit = "apple" という状態になっていた場合、model.addAttribute("fruit", fruit); を実行すると、Modelの中は次のようになる：

fruit → "apple"

ここで重要なのは、これは「変数を渡している」のではなく、「キーと値のセットとして登録している」という点である。

Javaのローカル変数はそのままではHTMLに渡らないため、Modelという入れ物に入れることで、初めてHTML側から参照できる状態になる。

---

## ◆ HTML側（Thymeleaf）

\<p th:text="${fruit}">\</p>

意味：
このコードは、「Modelの中からfruitという名前のデータを取り出して、このタグの中身として表示する」という処理を行う。

流れとしては：

1. Controller側で  
   fruit → "apple" をModelに登録

2. return "result" によって result.html に遷移

3. ThymeleafがHTMLを処理する際に  
   ${fruit} を評価する

4. Modelの中から  
   fruit → "apple" を取得

5. 最終的にHTMLは  
   \<p>apple\</p>  
   としてブラウザに送られる

ここで重要なのは、th:textは「HTMLの表示内容を書き換える」役割を持つという点である。

---

## ◆ EL式

${...} の形で記述されるものをEL式（Expression Language）という。

役割：
「Modelに登録されたデータを取り出すための記法」

基本動作：
${fruit}と書くと、Modelの中から fruit → "apple" というデータを探し、その値である "apple" を取得する。

---

### ■ 具体例

#### ① 単純な値の取得

${fruit}

→ "apple"

#### ② オブジェクトのプロパティ取得

${user.name}

→ Modelに user → { name: "Naoki" } のようなデータが入っている場合、userの中のnameを取り出す。

#### ③ 簡単な計算

${age + 1}

→ Modelに age → 20 があれば、21として表示される。

---

# ■ 学び・気づき

- HTMLは表示だけでなく通信にも関与する
- データは直接渡せずModelを介する
- キー（名前）がすべてを接続する
- Java単体の理解では不十分

---

# ■ 苦戦した理由

この章が難しかった原因は明確で、

- HTTP
- HTML
- Java
- Spring

が同時に出てきたためである。

それぞれ単体なら理解可能だが、組み合わさることで急激に難易度が上がった。

---

# ■ 最終理解（全体の流れ）

1. 入力
2. form送信
3. POST発生
4. Controller受信
5. @RequestParamで取得
6. Modelに登録
7. HTMLへ渡す
8. Thymeleafで埋め込み
9. 表示

---

# ■ 自分の理解の変化

最初：
全体像が見えず、個々の意味も曖昧

途中：
流れは分かるが仕組みが不明

最終：
全体の流れと各要素の役割が接続された

---

# ■ 結論

この章は文法ではなく、「Webアプリケーションの構造そのもの」を理解する章である。

また、難しく感じた理由は、Javaの知識不足ではなく、複数の仕組みを同時に理解する必要があったためである。
