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

# ■ Spring Web MVC専用スコープ

## ◆ request

HTTPリクエスト単位。

---

## ◆ session

セッション単位。

---

## ◆ application

Webアプリ全体共有。

---

## ◆ websocket

WebSocket接続単位。

---

# ■ 5-2 DIを使った実装方法

DI実装には主に：

1. アノテーション方式
2. JavaConfig方式

がある。

---

# ■ フィールドインジェクション

```java
@Autowired
private HogeComponent hogeComponent;
```

特徴：

- 記述が短い

---

# ■ コンストラクタインジェクション

```java
private final HogeComponent hogeComponent;

@Autowired
public Hoge(
    HogeComponent hogeComponent
) {

    this.hogeComponent
        = hogeComponent;
}
```

特徴：

- final化可能
- Spring推奨

---

# ■ Lombok

## ◆ @RequiredArgsConstructor

コンストラクタ自動生成。

```java
@RequiredArgsConstructor
```

---

# ■ Setterインジェクション

```java
@Autowired
public void setHogeComponent(
    HogeComponent hogeComponent
) {

    this.hogeComponent
        = hogeComponent;
}
```

setter経由でDIする。

---

# ■ JavaConfig

## ◆ JavaConfig

Bean生成専用クラス。

```java
@Configuration
public class JavaConfig {
}
```

---

## ◆ @Bean

```java
@Bean
public Hoge getHoge() {

    return new Hoge();
}
```

戻り値をBean登録する。

---

# ■ JavaConfigを使う場面

主に：

- 外部ライブラリ
- 複雑生成
- 特殊設定

など。

---

# ■ 最終まとめ

第5章では、

- DI
- IoCコンテナ
- Bean
- 疎結合
- スコープ
- @Autowired

など、Springの根幹機能を学習した。

また、

- インターフェース設計
- 疎結合
- オブジェクト管理

など、Spring以前のオブジェクト指向設計についても理解する章だった。
