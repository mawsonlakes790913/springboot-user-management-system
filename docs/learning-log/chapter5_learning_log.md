# 第5章 学習ログ — Dependency Injection（DI）

## ■ 概要

第5章では、Springの土台機能であるDI（Dependency Injection：依存性注入）について学習した。

今回は：

- DI
- IoCコンテナ
- Bean
- 疎結合
- スコープ
- AOP

など、Spring内部の動作に深く関わる概念が大量に登場した。

特に今回は、

- Springが内部で何をしているのか
- なぜ@Autowiredだけで動くのか
- なぜnewを書かなくなるのか
- Beanとは何なのか

などが最初かなり曖昧だった。

そのため今回は：

- Spring内部の動作イメージ
- 普通のJavaとの比較
- 「実際には何が起きているのか」

を自分なりにかなり細かく整理しながら理解を進めた。

また今回は、単なる文法ではなく、

```text
オブジェクト指向設計
```

そのものの理解がかなり重要だった。

---

# ■ DI理解前の状態

最初は：

```text
@Autowiredを書くだけで
なぜ勝手にインスタンスが入るのか
```

が全く分からなかった。

また：

- Bean
- IoCコンテナ
- 依存
- 疎結合

などの用語もかなり曖昧だった。

さらに、

```java
new UserService()
```

を書かないというSpring特有の考え方にも違和感があった。

普通のJavaでは：

```java
UserService service
    = new UserService();
```

が当然だったため、

```text
Springは誰がnewしているのか
```

がかなり疑問だった。

---

# ■ 依存と結合度の理解

## ◆ 依存

あるクラスが別クラスを利用している状態。

例：

```java
public class Car {

    private NormalEngine engine
        = new NormalEngine();

}
```

この場合：

```text
CarクラスがNormalEngineクラスを利用している
```

ため、

```text
CarはNormalEngineへ依存している
```

と言う。

---

## ◆ 結合度

依存先との結びつきの強さ。

---

## ◆ 密結合

```java
private TurboEngine engine
    = new TurboEngine();
```

のように具体クラスへ直接依存している状態。

この状態では：

- TurboEngineしか使えない
- 実装変更時にCar側修正が必要

になる。

---

## ◆ 疎結合

```java
private Engine engine;
```

のようにインターフェースへ依存する状態。

```java
public interface Engine {
}
```

```java
public class NormalEngine
    implements Engine {
}
```

```java
public class TurboEngine
    implements Engine {
}
```

これにより：

- 実装交換可能
- 利用側修正不要

になる。

---

# ■ DIの理解

## ◆ DIとは

DIとは：

```text
依存オブジェクトを外部から注入する仕組み
```

である。

---

## ◆ DI前

```java
private Engine engine
    = new TurboEngine();
```

クラス内部で依存先を固定している。

---

## ◆ DI後

```java
public class Car {

    private Engine engine;

    public Car(Engine engine) {

        this.engine = engine;
    }
}
```

外部から：

```java
new Car(new TurboEngine())
```

のように注入する。

---

# ■ DIで重要だった理解

今回かなり重要だったのは：

```text
「newを書かなくなる」
```

という点。

最初は：

```text
DI = ただのコンストラクタ渡し
```

くらいの理解だった。

しかし実際には：

```text
依存先を外部から交換可能にする
```

ことが本質だった。

---

# ■ 【より具体的に】
## ◆ DIの本質

例えば：

- 本番環境
- テスト環境

で処理を切り替えたい場合を考える。

普通のJava：

```java
Engine engine
    = new NormalEngine();
```

↓

```java
Engine engine
    = new StubEngine();
```

へ書き換える必要がある。

しかしDIでは：

```java
@Autowired
private Engine engine;
```

だけ書けば、

```text
どの実装を使うか
```

をSpring側が切り替えられる。

つまり：

```text
「利用側コードを書き換えずに
中身だけ交換できる」
```

のがDIの大きな価値だと理解した。

---

# ■ IoCコンテナ理解

## ◆ 最初の疑問

最初かなり分からなかったのが：

```text
Springは誰がnewしているのか？
```

だった。

---

## ◆ 理解したこと

IoCコンテナとは：

```text
Springのオブジェクト管理工場
```

のようなものだった。

役割：

- インスタンス生成
- 保持
- DI
- ライフサイクル管理

を行う。

---

# ■ 【より具体的に】
## ◆ Spring内部イメージ

例えば：

```java
@Service
public class UserService {
}
```

```java
@Controller
public class UserController {

    @Autowired
    private UserService userService;
}
```

があるとする。

Spring起動時：

```text
① @Service発見
```

↓

内部イメージ：

```java
UserService serviceObj
    = new UserService();
```

↓

```text
IoCコンテナへ保存
```

次に：

```text
② @Autowired発見
```

↓

Spring内部：

```java
controllerObj.userService
    = serviceObj;
```

つまり：

```text
Springが勝手にnewして
代入している
```

ということだった。

---

# ■ 【疑問】
## ◆ なぜ@Autowiredだけで動くのか？

最初は：

```java
private UserService userService;
```

だけでは：

```text
nullになるはず
```

と思っていた。

しかし：

```java
@Autowired
private UserService userService;
```

があると、

Springが：

```text
UserService型Beanを探し、
自動代入する
```

という流れだった。

---

# ■ Bean理解

## ◆ Beanとは

最初は：

```text
Bean = クラス
```

だと思っていた。

しかし実際には：

```text
Spring管理中インスタンス
```

だった。

つまり：

```java
@Service
public class UserService {
}
```

そのものではなく、

Spring内部で生成された：

```java
new UserService()
```

相当のインスタンスがBean。

---

# ■ コンポーネントスキャン理解

## ◆ コンポーネントスキャン

Spring起動時：

```java
@Component
@Service
@Controller
@Repository
```

などを探す。

これを：

```text
コンポーネントスキャン
```

という。

---

# ■ 各アノテーション整理

| アノテーション | 役割 |
|---|---|
| @Component | 汎用部品 |
| @Controller | 画面遷移 |
| @Service | 業務処理 |
| @Repository | DB操作 |
| @RestController | API |
| @Configuration | 設定クラス |
| @Bean | 手動Bean登録 |

---

# ■ Bean切り替えサンプル作成

## ◆ 実際に作成したもの

今回は実際に：

```text
Bean切り替え
```

を確認するサンプルを作成した。

構成：

```text
com.example.demo
└─ di
   ├─ SampleComponent.java
   ├─ SampleComponent1.java
   └─ SampleComponent2.java
```

---

## ◆ SampleComponentインターフェース

```java
public interface SampleComponent {

    String getStr();
}
```

---

## ◆ 実装クラス①

```java
@Component("SampleComponent1")
public class SampleComponent1
    implements SampleComponent {

    private String str
        = "SampleComponent1";

    @Override
    public String getStr() {

        return this.str;
    }
}
```

---

## ◆ 実装クラス②

```java
@Component("SampleComponent2")
public class SampleComponent2
    implements SampleComponent {

    private String str
        = "SampleComponent2";

    @Override
    public String getStr() {

        return this.str;
    }
}
```

---

# ■ 【疑問】
## ◆ Bean切り替えとは？

最初は：

```text
Beanを切り替える
```

という意味が曖昧だった。

理解したこと：

```text
同じ役割を持つ複数Beanのうち、
どれをDIするか選ぶこと
```

だった。

---

# ■ @Qualifier理解

## ◆ なぜ必要なのか？

```java
@Autowired
private SampleComponent sampleComponent;
```

だけだと、

```text
SampleComponent1
SampleComponent2
```

どちらを使うべきかSpringが判断できない。

そのため：

```java
@Autowired
@Qualifier("SampleComponent1")
private SampleComponent sampleComponent;
```

でBean名指定する必要があった。

---

# ■ 【理解したこと】
## ◆ @Componentと@Qualifierの役割違い

最初は：

```java
@Component("SampleComponent1")
```

だけで切り替えられると思っていた。

しかし実際には：

| アノテーション | 役割 |
|---|---|
| @Component("xxx") | Bean名登録 |
| @Qualifier("xxx") | Bean選択 |

だった。

---

# ■ @Slf4j理解

## ◆ 最初の混乱

最初は：

```java
log.info()
```

の意味がかなり曖昧だった。

特に：

```text
Slf4jはログライブラリの窓口
```

という説明が理解しづらかった。

---

# ■ 【より具体的に】
## ◆ Slf4jとは

Javaには：

- Logback
- Log4j

など複数ログライブラリがある。

Slf4jは：

```text
それらを共通APIで扱う窓口
```

だった。

イメージ：

```text
自分のコード
    ↓
Slf4j
    ↓
Logback / Log4j
```

---

## ◆ @Slf4j

```java
@Slf4j
```

を書くと、

Lombokが内部で：

```java
private static final Logger log =
    LoggerFactory.getLogger(
        HelloController.class
    );
```

相当を自動生成する。

---

# ■ Beanライフサイクル理解

## ◆ スコープ

最初は：

```text
singleton
prototype
```

の違いが曖昧だった。

---

## ◆ singleton

```text
Spring起動時に1回生成
```

され、

```text
全体共有
```

される。

---

## ◆ prototype

```text
Bean取得ごとに新規生成
```

される。

---

# ■ 【より具体的に】
## ◆ singletonの本当の意味

最初は：

```text
同じフィールドを共有
```

するのかと思っていた。

しかし実際には：

```text
同じ実体オブジェクトを共有
```

だった。

例えば：

```java
@Controller
public class UserController {

    @Autowired
    private UserService userService;
}
```

```java
@Controller
public class AdminController {

    @Autowired
    private UserService userService;
}
```

フィールド自体は別。

しかし内部では：

```java
UserService obj
    = new UserService();

userController.userService = obj;
adminController.userService = obj;
```

のように、

```text
同じインスタンス
```

を共有していた。

---

# ■ AOP理解

## ◆ AOPとは何か

AOP（Aspect Oriented Programming）は、

```text
メソッドの前後に共通処理を割り込ませる仕組み
```

である。

Springでは主に、

- ログ出力
- 認証
- 例外処理
- トランザクション管理

など、複数のクラスやメソッドで共通して必要になる処理をまとめて管理するために利用される。

---

## ◆ なぜ必要なのか

例えば通常のコードでは、ログを出力したい場合、

```java
public void login() {
    System.out.println("開始");
    System.out.println("ログイン処理");
    System.out.println("終了");
}
```

のように書く必要がある。

しかし、同じようなログ処理を複数メソッドに書き始めると、

- 同じコードが増殖する
- 修正箇所が増える
- 本来の処理が見えづらくなる

という問題が起こる。

---

## ◆ AOPの本質

AOPの本質は、

```text
Springがメソッド呼び出しを横取りし、
前後に別処理を追加している
```

という点にある。

これはソースコードを書き換えているわけではなく、

```text
Proxy（代理オブジェクト）
```

を間に挟むことで実現している。

---

## ◆ Proxyのイメージ

本来：

```text
Controller
    ↓
UserService.login()
```

という呼び出しだったものを、

Springは内部的に：

```text
Controller
    ↓
Proxy
    ↓
本物のUserService
```

という構造に変更する。

---

## ◆ Proxyが行っていること

例えば、

```java
userService.login();
```

を実行した場合、実際にはProxy側が先に呼ばれる。

Proxyは、

```text
① 開始ログ
② 本物のlogin()実行
③ 終了ログ
```

の順で処理を行う。

つまり、

```java
public void login() {
    System.out.println("ログイン処理");
}
```

しか書いていなくても、実行時には前後にログ処理が追加される。

---

## ◆ 「共通処理を自動挿入」の意味

AOPでよく言われる：

```text
共通処理を自動挿入する
```

とは、

```text
SpringがProxyを利用して、
メソッド実行前後に処理を割り込ませる
```

という意味である。

---

# ■ MVCとの繋がり理解

今回かなり感じたのは：

```text
Springは役割分担を非常に重視する
```

という点。

- Controller
- Service
- Repository

などを分離する思想は、

前章MVC理解とも強く繋がっていた。

---

# ■ 今回特に重要だった理解

今回最も重要だったのは：

```text
Springでは自分でnewしない
```

という点だった。

通常のJavaでは、

```java
UserService userService = new UserService();
```

のように、開発者自身がインスタンスを生成していた。

しかしSpringでは、

```java
@Autowired
private UserService userService;
```

のように、

```text
必要な型だけ宣言
```

しておけば、

- インスタンス生成
- Beanとしての保持
- DI（依存性注入）
- ライフサイクル管理

などをIoCコンテナが内部で自動的に行っている。

最初は、

```text
「DI」
「Bean」
「IoCコンテナ」
「AOP」
```

など、新しい用語や概念が次々登場し、それぞれが何を意味しているのか理解にかなり苦戦した。

特にSpringは、

```text
内部で自動的に動いている処理
```

が多いため、

```text
「結局誰が何をしているのか」
```

が見えづらく、最初はかなり混乱した。

しかし、

```java
log.info(sampleComponent.getStr());
```

のように、

```text
実際のコードを具体的に分解しながら考える
```

ことで、

- どのオブジェクトのメソッドを呼んでいるのか
- 誰がBeanを生成しているのか
- どこでDIされているのか
- Proxyがどのように割り込んでいるのか

などを徐々に理解できるようになった。

特に、

```text
Javaの基本構文やオブジェクト指向の知識が既にあった
```

ことは非常に大きかった。

Spring特有の概念は難しかったものの、

- フィールド
- インスタンス
- メソッド呼び出し
- 型
- オブジェクト参照

などのJava基礎が理解できていたため、

```text
コードベースで具体例を確認すると
一気に理解が進む
```

場面が非常に多かった。

その意味では、

```text
先にJava基礎を学習していたことが
Spring理解の大きな助けになった
```

と感じた。

最終的には、

```text
「Springが内部で何をしているのか」
```

を以前よりかなり具体的にイメージできるようになったことが、
今回最も大きな学びだった。
