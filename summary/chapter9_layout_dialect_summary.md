# 第9章まとめ：レイアウト機能

## ■ 概要

この章では、Thymeleaf の Layout Dialect を利用し、

```text
Webアプリケーションの共通レイアウトを
分離・再利用する方法
```

について学習する。

Webアプリケーションでは：

- ヘッダー
- メニュー
- フッター

など、

```text
複数画面で共通するUI
```

を利用することが多い。

しかし各画面ごとに：

- 同じヘッダー
- 同じメニュー
- 同じCSS

を個別に記述すると、

- 修正箇所増加
- 保守性低下
- 開発効率低下

などの問題が発生する。

そこで本章では：

- Layout Dialect
- layout:replace
- layout:insert
- layout:fragment
- layout:decorate

などを利用し、

```text
レイアウト（共通部分）
```

と、

```text
コンテンツ（画面固有部分）
```

を分離する方法を学習する。

---

# ■ 9-1 Webアプリケーションのレイアウト

## ◆ レイアウトの基本構造

Webアプリケーションでは：

```text
┌───────────────────────┐
│       ヘッダー         │
├──────────┬────────────┤
│ メニュー  │ コンテンツ   │
└──────────┴────────────┘
```

のように、

```text
共通部分 + 画面固有部分
```

で構成されることが多い。

---

## ◆ 悪い例

各画面ごとに：

- ヘッダー
- メニュー

を個別作成すると、

```text
画面A
┌───────────────────────┐
│ ヘッダー               │
├──────────┬──────────┤
│ メニュー  │ コンテンツA│
└──────────┴──────────┘

画面B
┌───────────────────────┐
│ ヘッダー               │
├──────────┬──────────┤
│ メニュー  │ コンテンツB│
└──────────┴──────────┘
```

のようになり、

- 修正コスト増加
- 共通部品の重複
- 保守性低下

につながる。

---

## ◆ Layout Dialect の考え方

Layout Dialect を利用すると：

```text
共通レイアウト
```

と

```text
各画面のコンテンツ
```

を分離できる。

```text
開発者A → コンテンツA作成
開発者B → コンテンツB作成
```

だけを担当し、

共通レイアウトへ組み込むことで：

```text
最終画面
```

が生成される。

---

## ◆ レイアウト構築の流れ

Layout Dialect では：

```text
① レイアウトテンプレート作成
② 共通部品（フラグメント）作成
③ コンテンツ画面からテンプレート指定
```

という流れで画面を構成する。

---

# ■ 9-2 レイアウト画面の作成

## ▽ サンプルアプリケーションの作成

---

## 1. ファイルなどの作成

以下のファイルを新規作成する。

```text
src/main/resources/templates/layout
├── layout.html
├── header.html
└── menu.html

src/main/resources/templates/user
└── list.html

src/main/resources/static/css
├── layout.css
└── list.css
```

また、

```text
UserListController.java
```

も作成する。

---

## ◆ UserListController.java の役割

```text
ユーザー一覧画面を表示するController
```

である。

```java
@Controller
@RequestMapping("/user")
public class UserListController {

    @GetMapping("/list")
    public String getUserList() {

        return "user/list";
    }
}
```

ブラウザから：

```text
/user/list
```

へアクセスすると、

```text
list.html
```

が表示される。

---

## ◆ list.html / list.css の役割

今回の章では：

```text
共通レイアウト
```

を作るだけでなく、

```text
そのレイアウトを使う実際の画面
```

も必要になる。

そのため：

- list.html
- list.css

が存在する。

---

## 2. Layout Dialect の追加

pom.xml に：

```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

を追加する。

これにより：

```text
layout:***
```

系の属性が利用可能になる。

---

## 3. ログイン画面からの遷移

```java
@PostMapping("/login")
public String postLogin() {

    return "redirect:/user/list";
}
```

を追加し、

```text
ログインボタン押下
↓
POST /login
↓
ユーザー一覧画面へ遷移
```

するようにする。

なお現時点では：

```text
本物のログイン認証機能
```

は未実装であり、

```text
学習用に画面遷移だけ実装
```

している。

---

# ■ レイアウトの作成

## ◆ layout.html

```html
<nav layout:replace="~{layout/header :: header-contents}"></nav>
```

```html
<div layout:insert="~{layout/menu :: menu-contents}"></div>
```

```html
<div layout:fragment="content"></div>
```

を利用し、

- ヘッダー
- メニュー
- コンテンツ

の配置を定義する。

---

## ◆ layout:replace

```text
タグごと置き換える
```

例：

```html
<div layout:replace="..."></div>
```

↓

```html
<header>...</header>
```

つまり：

```text
元のタグが消える
```

---

## ◆ layout:insert

```text
タグ内部へ挿入する
```

例：

```html
<div layout:insert="..."></div>
```

↓

```html
<div>
    <header>...</header>
</div>
```

つまり：

```text
元のタグが残る
```

---

## ◆ replace と insert の違い

違いは：

```text
layout.html 側のタグを残すかどうか
```

である。

---

## ◆ layout:fragment

```html
<div layout:fragment="content"></div>
```

は：

```text
あとで別画面から中身を差し込む場所
```

を定義する。

---

## ◆ fragment の動作イメージ

### layout.html

```html
<div layout:fragment="content"></div>
```

↓

```text
content という名前の空欄
```

を作る。

---

### list.html

```html
<div layout:fragment="content">
    ユーザー一覧
</div>
```

↓

```text
content に入れる中身
```

を定義する。

---

## ◆ layout:decorate

```html
<html layout:decorate="~{layout/layout}">
```

を利用し、

```text
この画面は layout.html を使う
```

と宣言する。

---

## ◆ fragment の紐づけ

Layout Dialect は：

```text
layout側
layout:fragment="content"

↓

コンテンツ側
layout:fragment="content"
```

のように、

```text
同じ fragment 名
```

を対応付けることで、

```text
レイアウトへコンテンツを組み込む
```

仕組みになっている。

---

# ■ 共通部品の作成

## ◆ header.html

```html
<nav layout:fragment="header-contents">
```

を利用し、

```text
header-contents
```

というキー名を定義する。

layout.html の：

```html
layout:replace="~{layout/header :: header-contents}"
```

と対応する。

---

## ◆ menu.html

```html
<ul layout:fragment="menu-contents">
```

を利用し、

```text
menu-contents
```

というキー名を定義する。

---

# ■ CSS

## ◆ layout.css

```css
.sidebar {
    position: fixed;
}
```

などを利用し、

- 左側メニュー固定
- コンテンツスクロール
- ヘッダー固定

などを実現する。

---

## ◆ list.css

```css
.th-width {
    width: 200px;
}
```

を利用し、

```text
ユーザー一覧画面専用CSS
```

を作成する。

---

# ■ headタグの自動結合

Layout Dialect を利用すると：

```text
各HTML側の headタグ
```

が、

```text
layout.html 側へ自動マージ
```

される。

そのため：

- 共通CSS → layout.html
- 画面固有CSS → 各画面

という管理が可能になる。

これは：

```text
th:replace
```

にはない、

```text
Layout Dialect の大きなメリット
```

である。

---

# ■ 実行確認

```text
http://localhost:8080/login
```

へアクセスし、

```text
ログインボタン押下
↓
ユーザー一覧画面表示
```

を確認する。

さらに：

```text
F12 → 開発者ツール
```

を利用すると、

```text
実際に組み立て後のHTML
```

を確認できる。

---

# ■ 最終まとめ

第9章では：

- Layout Dialect
- layout:replace
- layout:insert
- layout:fragment
- layout:decorate

などを利用し、

```text
共通レイアウトと
コンテンツを分離する方法
```

について学習した。

これにより：

- 共通部品の再利用
- 保守性向上
- 画面追加の効率化
- CSS/JS管理の簡略化

などを実現できるようになった。
