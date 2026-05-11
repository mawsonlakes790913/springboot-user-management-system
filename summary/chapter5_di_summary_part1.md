# 第5章まとめ：Dependency Injection（DI）

## ■ 概要

この章では、Springの重要機能であるDI（Dependency Injection：依存性の注入）について学習した。

DIはSpringの根幹機能であり、

- オブジェクト同士の依存関係
- クラス間の結合度
- IoCコンテナによるオブジェクト管理

など、Spring Bootを理解するための土台となる概念が登場した。

また、この章では単なるアノテーションの使い方ではなく、

- 疎結合
- インターフェース設計
- Bean管理
- スコープ

など、オブジェクト指向設計そのものについても扱われた。

---

# ■ 5-1 DIとは

## ◆ 依存

あるクラスが別クラスを利用している状態を「依存」という。

例：

```java
public class Car {

    private NormalEngine engine
        = new NormalEngine();

}
```

この場合：

- Carクラス
- NormalEngineクラス

が直接結びついている。

---

## ◆ 結合度

依存先との結びつきの強さを「結合度」という。

### ■ 密結合

```java
private TurboEngine engine
    = new TurboEngine();
```

のように具体クラスへ直接依存している状態。

この状態では：

- TurboEngineしか利用できない
- 実装変更時にCar修正が必要

になる。

---

### ■ 疎結合

```java
private Engine engine;
```

のようにインターフェースへ依存している状態。

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

# ■ DI（Dependency Injection）

## ◆ DIの仕組み

DIとは、依存オブジェクトを外部から注入する仕組みである。

```java
public class Car {

    private Engine engine;

    public Car(Engine engine) {

        this.engine = engine;
    }
}
```

Car内部で：

```java
new TurboEngine()
```

を書かず、

外部から：

```java
new Car(new TurboEngine())
```

のように渡す。

---

## ◆ DIのメリット

### ■ 1. 処理の切り替えが容易

実装クラスを差し替えられる。

例：

- DB認証
- LDAP認証
- Googleログイン

など。

---

### ■ 2. テストしやすい

スタブ（ダミークラス）を利用できる。

```java
public class StubEngine
    implements Engine {

    @Override
    public void start(String key) {

        System.out.println(
            "StubEngine Start"
        );
    }
}
```

これを注入することで：

- 本物クラス未完成でもテスト可能
- 外部依存排除可能

になる。

---

# ■ IoCコンテナ

## ◆ IoCコンテナとは

Springが使用するオブジェクト管理機構。

役割：

- インスタンス生成
- 保持
- DI
- 破棄

を自動で行う。

---

## ◆ 通常Javaとの違い

通常Java：

```java
Engine engine
    = new TurboEngine();

Car car = new Car(engine);
```

Spring：

```java
@Autowired
private Engine engine;
```

だけで利用可能。

---

# ■ IoCコンテナの流れ

## ◆ 1. DI対象クラス探索

コンポーネントスキャンを行う。

対象例：

```java
@Component
@Service
@Controller
@Repository
@RestController
@Configuration
```

---

## ◆ 2. Bean登録

DI対象クラスをIoCコンテナへ登録。

Spring管理オブジェクトをBeanという。

---

## ◆ 3. @Autowiredへ注入

```java
@Autowired
private HogeService hogeService;
```

を書くだけで、

Springが自動的にBeanを代入する。

---

# ■ DI関連アノテーション

## ◆ @Component

汎用コンポーネント。

---

## ◆ @Controller

画面遷移担当。

---

## ◆ @Service

業務処理担当。

---

## ◆ @Repository

DBアクセス担当。

---

## ◆ @RestController

JSON返却API用。

---

## ◆ @Configuration

Spring設定クラス。

---

## ◆ @Bean

Bean手動登録用。

---

# ■ IoCコンテナの機能

## ◆ 1. DI対象の切り替え

環境ごとに：

- 本番
- テスト
- スタブ

などを切り替え可能。

---

## ◆ 2. AOP

共通処理を一括管理する仕組み。

例：

- ログ
- 例外処理
- 実行時間計測

など。

---

## ◆ 3. ライフサイクル管理

Springが：

- インスタンス生成
- 保持
- 破棄

を管理する。

---

# ■ スコープ

## ◆ singleton

デフォルト。

Spring起動時に1個生成し共有する。

---

## ◆ prototype

取得ごとに新規生成。

```java
@Scope("prototype")
```

---

# ■ 5-3 DIサンプル作成

論理的な説明だけでなく、実際にSpring Boot上でDIを利用したサンプルアプリケーションを作成した。

この章では主に：

1. Beanの切り替え（簡易版）
2. Beanライフサイクル確認

を行った。

---

# ■ 1. Beanの切り替え（簡易版）

## ◆ 目的

同じインターフェースを実装したBeanが複数存在する場合に、

```text
どのBeanをDIするか
```

を切り替える方法を確認した。

---

## ◆ SampleComponentインターフェース

```java
package com.example.demo.di;

public interface SampleComponent {

    public String getStr();

}
```

役割だけを定義したインターフェース。

---

## ◆ 実装クラス①

```java
package com.example.demo.di;

import org.springframework.stereotype.Component;

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
package com.example.demo.di;

import org.springframework.stereotype.Component;

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

## ◆ Bean名

```java
@Component("SampleComponent1")
```

のように、

@Componentの引数へ文字列を渡すことでBean名を設定できる。

同じインターフェース実装クラスが複数存在する場合、
Bean名が異なることでIoCコンテナへ登録可能になる。

---

## ◆ HelloControllerでDI

```java
@Autowired
@Qualifier("SampleComponent1")
private SampleComponent sampleComponent;
```

@Qualifierを使うことで：

```text
どのBeanをDIするか
```

を明示的に指定した。

---

## ◆ ログ出力確認

```java
@GetMapping("/hello")
public String getHello() {

    log.info(
        sampleComponent.getStr()
    );

    return "hello";
}
```

ブラウザから：

```text
http://localhost:8080/hello
```

へアクセスすると、

```text
SampleComponent1
```

がログへ出力された。

---

## ◆ Bean切り替え確認

```java
@Qualifier("SampleComponent1")
```

を：

```java
@Qualifier("SampleComponent2")
```

へ変更して保存すると、
Spring Boot DevToolsによって自動再起動された。

再度アクセスすると：

```text
SampleComponent2
```

がログへ出力された。

これにより：

```text
DI対象Beanを切り替えられる
```

ことを確認した。

---

# ■ @Slf4j

## ◆ LombokによるLogger生成

```java
@Slf4j
```

をクラスへ付与すると、

```java
private static final Logger log =
    LoggerFactory.getLogger(
        HelloController.class
    );
```

相当のコードが自動生成される。

---

## ◆ log.info()

```java
log.info(sampleComponent.getStr());
```

は：

```text
INFOレベルログ
```

を出力する。

ログレベル例：

| レベル | 用途 |
|---|---|
| error | エラー |
| warn | 警告 |
| info | 一般情報 |
| debug | デバッグ用 |

---

# ■ 2. Beanライフサイクル確認

## ◆ 目的

Beanが：

- いつ生成されるか
- いつ破棄されるか

をログで確認した。

---

## ◆ singletonスコープ

デフォルトでは：

```text
singleton
```

スコープとなる。

これは：

```text
Spring起動時に1回だけ生成
```

され、

```text
アプリケーション全体で共有
```

されることを意味する。

---

## ◆ prototypeスコープ

```java
@Scope("prototype")
```

を指定すると、

```text
Bean取得のたびに新規生成
```

される。

---

## ◆ ライフサイクル管理

IoCコンテナは：

- インスタンス生成
- 保持
- 破棄

を自動管理する。

これを：

```text
ライフサイクル管理
```

という。

---

## ◆ singletonの特徴

singletonでは：

```text
同じインスタンスを共有
```

する。

例えば：

```java
@Autowired
private UserService userService;
```

を複数クラスで使用しても、

内部的には：

```text
同じUserServiceインスタンス
```

が使われる。

---

# ■ 5章で学習した重要ポイント

第5章では：

- DI
- IoCコンテナ
- Bean
- 疎結合
- @Autowired
- @Qualifier
- Bean切り替え
- スコープ
- Beanライフサイクル
- @Slf4j
- AOP

など、Springの土台となる重要概念を学習した。

また、この章では：

```text
自分でnewしない
```

というSpring特有の考え方が非常に重要だった。

開発者は：

```java
@Autowired
private UserService userService;
```

のように必要な型だけ宣言し、

実際の：

- インスタンス生成
- 保持
- 注入
- 管理

はSpring IoCコンテナが担当する。
