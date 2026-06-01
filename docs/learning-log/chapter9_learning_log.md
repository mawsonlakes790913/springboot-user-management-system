# ■ 第9章 レイアウト機能

# ■ この章の目的

この章では、

```text
Layout Dialect を利用した
レイアウト分離
```

を実際に実装しながら、

```text
共通部品とコンテンツを
組み合わせて画面生成する流れ
```

を学習した。

今回は：

- html
- css
- Java
- Thymeleaf

を横断しながら、

```text
複数ファイルが
どのように繋がるか
```

を理解する章だった。

---

# ■ 今回の章で特に感じたこと

今回は：

- layout.html
- header.html
- menu.html
- list.html

など、

```text
複数HTML同士の関係
```

を理解する必要があった。

そのため、

```text
「どのファイルが
どこへ組み込まれるのか」
```

を追うのが最初は少し大変だった。

ただし、

5章や7章のように：

- IoC
- DI
- Binding
- Spring内部処理

など、

```text
Spring内部の抽象概念
```

を理解するタイプではなく、

今回は：

```text
実際の画面構造
```

を見ながら学べたため、

```text
HTML初心者でも
比較的理解しやすい章
```

だった。

特に：

```text
ブラウザ表示
↓
HTML
↓
layout.html
↓
header/menu/list
```

の対応関係が見えやすかったため、

```text
「Webページが
組み立てられている感覚」
```

をかなり掴みやすかった。

---

# ■ 【疑問】UserListController.java, list.html, list.css は何なのか？

最初、

```text
「共通レイアウトを作る章なのに、
なぜ list.html が必要？」
```

と疑問に思った。

しかし実際には、

```text
レイアウトだけでは画面にならず、
その中へ組み込むコンテンツも必要
```

だった。

つまり：

```text
layout.html
↓
共通レイアウト

list.html
↓
実際に表示する中身
```

という役割分担になっていた。

また、

```text
UserListController.java
```

は、

```text
/user/list にアクセスされた時に
list.html を返す
```

ためのコントローラーであり、

```text
list.css
```

は、

```text
ユーザー一覧画面専用の装飾
```

だった。

つまり今回は、

```text
共通部品だけではなく、
「そのレイアウトを実際に使う画面」
```

も同時に作成していた。

---

# ■ 【疑問】なぜ header.css や menu.css がないのか？

最初、

```text
header.html や menu.html があるなら、
専用CSSも必要では？
```

と思った。

しかし今回は：

```text
Bootstrap のクラスだけで
見た目が成立している
```

ため、

```text
header.css
menu.css
```

を作成していなかった。

例えば：

```html
class="navbar navbar-dark bg-dark"
```

や、

```html
class="nav nav-pills"
```

などは、

```text
Bootstrap が提供しているCSS
```

であり、

```text
Bootstrapだけで
ナビゲーションの見た目が完成している
```

状態だった。

---

# ■ 【補足】今回のログイン処理

ログイン画面では：

```java
@PostMapping("/login")
public String postLogin() {

    return "redirect:/user/list";
}
```

を追加した。

最初、

```text
「ログイン認証していないのに
一覧画面へ行ける？」
```

と少し混乱した。

しかし今回は：

```text
レイアウト学習用
```

なので、

```text
ログイン成功したことにして
画面遷移だけ行っている
```

状態だった。

つまり：

```text
ログイン画面
↓
POST /login
↓
/user/list
```

という画面遷移確認が主目的だった。

---

# ■ 【疑問】layout:replace と layout:insert は同じでは？

今回かなり疑問だったのが：

```text
layout:replace
layout:insert
```

の違いだった。

最初は：

```text
「どちらもHTML読み込みでは？」
```

と思った。

しかし実際には：

```text
layout.html 側のタグを
残すかどうか
```

の違いだった。

例えば：

```html
<div layout:replace="..."></div>
```

では、

```text
元のdivタグ自体が消える
```

。

一方：

```html
<div layout:insert="..."></div>
```

では、

```text
divタグの中へHTML追加
```

となる。

ここは、

```text
生成後HTMLを見る
```

ことでかなり理解しやすくなった。

---

# ■ 【疑問】replaceは置き換える側HTMLが完全体でないとダメ？

復習して理解が深まった部分として、

```text
replace と insert は、
「読み込まれる側HTMLが
どこまで完成している必要があるか」
```

にも関係していた。

### replace

```text
layout.html 側のタグを
完全に置き換える
```

ため、

```text
読み込まれる側HTMLが
完成済み部品である必要が強い
```

。

例えば：

```html
<span layout:fragment="header"></span>
```

しかなければ、

```text
最終HTMLも span のみ
```

になる。

つまり：

```text
replace は
「完成済み部品を丸ごと交換」
```

という感覚だった。

### insert

一方 insert は、

```text
layout.html 側の外枠を残したまま
中へHTMLを入れる
```

。

つまり：

```text
menu.html 側が
中身だけでも成立する
```

ケースがある。

```html
layout.html
<div class="menu-area"
     layout:insert="~{layout/menu::menu}">
</div>
```

```html
menu.html
<ul layout:fragment="menu">
</ul>
```

最終HTML：

```html
<div class="menu-area">
    <ul>
    </ul>
</div>
```

つまり：

```text
外側レイアウトは
layout.html 側が保持
```

している。

---

# ■ 【重要】insertでもreplaceでも差し込む側はfragment

今回かなり混乱したのが：

```text
差し込まれる側
↓
replace / insert

差し込む側
↓
fragment
```

という構造だった。

最初は：

```text
「insertなのに
なぜfragment？」
```

と思った。

しかし実際には、

```text
replace / insert は
「どう読み込むか」

fragment は
「どの部分を使うか」
```

という役割分担だった。

つまり：

```html
layout:insert="~{layout/menu::menu}"
```

は、

```text
menu.html の
fragment="menu"
を取得する
```

という意味だった。

---

# ■ 【疑問】layout:fragment が最も難しかった

今回の章で最も難しかったのは：

```text
layout:fragment
```

だった。

特に、

```html
<div layout:fragment="content">
```

だけを見ると、

```text
「どこにもcontent使われてない」
```

ように見えた。

しかし実際には：

```text
layout側
↓
差し込み口定義

list.html側
↓
同じfragment名を書く
```

ことで紐づいていた。

つまり：

```text
layout.html
↓
contentという空欄作成

list.html
↓
contentへ入れる中身定義
```

という構造だった。

ここは：

```text
decorate
fragment
replace
insert
```

の役割を整理すると理解しやすくなった。

---

# ■ 【補足】replace/insert と fragment は役割が違った

最初は全部：

```text
「HTML読み込み系」
```

に見えて混乱した。

しかし整理すると：

```text
replace / insert
↓
固定部品を読み込む

fragment
↓
あとから差し込む空欄
```

という役割分担だった。

これを理解すると、

```text
header/menu は固定
content は画面ごとに変化
```

という設計意図がかなり見えやすくなった。

---

# ■ 【疑問】複数HTMLに同じfragment名があって衝突しないのか？

最初は、

```text
list.html
signup.html
```

など、

複数HTMLに：

```html
layout:fragment="content"
```

が存在していることに混乱した。

しかし実際には：

```text
現在表示しているHTMLだけ
```

がレイアウトへ組み込まれる。

つまり：

```text
Controller
↓
どの画面を返すか決定
↓
そのHTMLだけがlayoutへ適用
```

という流れだった。

そのため：

```text
fragment名が同じでも
衝突しない
```

。

---

# ■ 【疑問】html側にはfragmentしか書いてないのに、Springはどうやって判別してる？

今回復習してかなり理解が深まったのがここだった。

最初は：

```text
fragmentしか書いてないのに、
Springは
replace用なのか
decorate用なのか
どうやって判別してる？
```

と疑問だった。

しかし実際には：

```text
fragment自身には役割はない
```

。

重要なのは：

```text
呼び出し側
```

だった。

つまり：

```text
replace / insert / decorate
```

という：

```text
「呼び出し命令」
```

を Thymeleaf が見て、

```text
キー名一致するfragment
```

を探して適用していた。

復習前は：

```text
fragment自体に
特別な意味がある
```

ように感じていたが、

復習後は：

```text
fragmentは
「名前付きHTML部品」
```

であり、

```text
replace / insert / decorate が
その使い方を決めている
```

と理解できた。

また、

```text
雑に言えば、
呼び出している側が何であろうと
キー名一致するものを適用している
```

という感覚もかなり理解しやすかった。

---

# ■ 【疑問】なぜ layout:decorate は冒頭で、fragment は body に書く？

これも最初かなり混乱した。

しかし整理すると：

| 場所 | 役割 |
|---|---|
| `<html layout:decorate="...">` | どのレイアウトを使うか |
| `<div layout:fragment="content">` | どこを差し込むか |

だった。

つまり：

```text
decorate
↓
ページ全体設定
```

なので htmlタグ。

一方：

```text
fragment
↓
ページの一部分指定
```

なので body内だった。

---

# ■ 【補足】headタグ自動結合がかなり便利だった

今回かなり便利だと感じたのは：

```text
headタグの自動マージ
```

だった。

つまり：

```text
layout.html
↓
共通CSS

list.html
↓
画面固有CSS
```

を分離できる。

しかも：

```text
自動でheadへ統合
```

される。

これは：

```text
th:replace
```

にはない、

```text
Layout Dialect の大きな利点
```

だった。

---

# ■ 【補足】<nav>タグについて

今回、

```html
<nav>
```

が多用されていた。

最初は：

```text
「divではダメなの？」
```

と思った。

しかし `<nav>` は、

```text
ナビゲーション領域
```

を意味するセマンティックタグだった。

つまり：

```text
サイト移動
メニュー
ログアウト
```

など、

```text
ページ移動や操作のための領域
```

を表している。

特に：

```text
header.html
menu.html
```

は、

```text
サイト全体の移動・操作
```

を担当しているため、

```html
<nav>
```

が自然だった。

---

# ■ 【補足】生成後HTMLを見るのがかなり重要だった

今回特に理解へ繋がったのは：

```text
F12 → 開発者ツール
```

で、

```text
最終的に生成されたHTML
```

を確認できたことだった。

特に：

```text
layout:replace
↓
タグごと消える

layout:insert
↓
タグが残る
```

などは、

```text
説明だけではかなり分かりづらい
```

が、

実際の生成HTMLを見ると：

```text
「あ、本当にdivが消えてる」
```

のようにかなり理解しやすかった。

---

# ■ 今回特に重要だった理解

今回最も重要だったのは：

```text
Webページは
複数HTMLを組み合わせて
最終HTMLを生成している
```

という点だった。

特に：

```text
layout.html
↓
header/menu/list
↓
最終HTML
```

という流れが見えたことで、

```text
「Webページ全体を
どう構造化するのか」
```

をかなり具体的に理解できた。

また復習によって、

```text
replace
insert
fragment
```

は、

```text
最初に感じたほど複雑なものではなく、
「キー名一致」
```

という非常にシンプルな考え方を中心に動いていることが理解できた。

---

# ■ 学び・気づき

- レイアウトとコンテンツは分離できる
- 共通部品を再利用できる
- fragment名一致で画面が組み立てられる
- replace はタグごと置換
- insert はタグ内部へ追加
- decorate はレイアウト指定
- headタグは自動マージされる
- 開発者ツールで生成後HTML確認できる
- fragment自体は「名前付きHTML部品」
- replace/insert/decorate が使い方を決めている
- Layout Dialect により画面管理がかなり効率化される

---

# ■ 苦戦した理由

今回は：

- html
- css
- Java
- Thymeleaf

を頻繁に行き来する必要があり、

```text
「どのファイルが
どこへ繋がるのか」
```

を整理するまで少し混乱した。

ただし今回は：

```text
画面構造
```

が中心だったため、

```text
実際の表示結果
```

を見ながら理解できた。

そのため、

```text
HTML初心者でも
比較的理解しやすい章
```

だった。

また今回は：

```text
ブラウザ画面
↓
HTML構造
↓
レイアウト構成
```

の繋がりがかなり見えやすかったため、

```text
「Webページは
複数部品を組み合わせて作られる」
```

という感覚を強く持てた章だった。
