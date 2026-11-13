# 任务清单（Tasks）— 书籍信息管理 APP

- [x] **T1 工程骨架**
  - Acceptance: 目录与构建脚本齐备；`assembleDebug` 成功；空壳 Activity 可启动
  - Verify: `./gradlew :app:assembleDebug --offline`（CP1 通过）
  - Files: `settings.gradle.kts`、`build.gradle.kts`、`gradle.properties`、`app/build.gradle.kts`、`app/src/main/AndroidManifest.xml`、`res/values/*`、`res/mipmap*`、wrapper

- [x] **T2 数据层（TDD）**
  - Acceptance: `Book` 含四字段；`BookRepository` 返回 8 本，字段非空、价格为正且不全相同
  - Verify: `BookRepositoryTest` 先红（2 项失败）后绿
  - Files: `model/Book.kt`、`model/BookRepository.kt`、`test/.../BookRepositoryTest.kt`

- [x] **T3 价格升序排序（TDD）**
  - Acceptance: 排序后相邻价格非递减、元素数量不变、相等价格顺序稳定
  - Verify: `BookSorterTest` 先红（2 项失败）后绿
  - Files: `controller/BookController.kt`、`test/.../BookSorterTest.kt`

- [x] **T4 首屏列表**
  - Acceptance: 标题为「犹锐2024110544」；列表项显示书名/作者/出版社/价格；全部文字为蓝色
  - Verify: 模拟器实跑 + 截图取色（图 1、图 2）
  - Files: `view/MainActivity.kt`、`view/BookAdapter.kt`、`res/layout/activity_main.xml`、`res/layout/item_book.xml`

- [x] **T5 跳转与详情（TDD）**
  - Acceptance: 单击条目启动详情页；详情页四项与源对象一致
  - Verify: `BookSerializationTest` 序列化往返断言 + 模拟器实跑（图 3、图 4）
  - Files: `view/MainActivity.kt`、`view/DetailActivity.kt`、`res/layout/activity_detail.xml`、`test/.../BookSerializationTest.kt`

- [x] **T6 质量收口**
  - Acceptance: `assembleDebug` 成功且 warning 为 0；5 项单元测试全部通过
  - Verify: 构建 + 测试两条命令
  - Files: 全工程

- [x] **T7 实跑截图**
  - Acceptance: 覆盖首屏列表、价格升序、点击跳转、详情页、标题、蓝色文字；截图来自 `Lab1_AVD` 真实画面
  - Verify: `adb devices` + `screencap`，逐张核对
  - Files: `D:\soft\HomeWork\Android\YouRuiBookManager_上机测试_截图\`（4 张）

- [x] **T8 实验报告**
  - Acceptance: 表头齐全；三节正文完整；含主要代码；截图插入对应位置并附连续图题；成绩栏留空
  - Verify: 成品 docx 已保存，`doc_get_images` 确认 4 张图片
  - Files: `D:\soft\HomeWork\Android\上机测试_书籍信息管理_犹锐2024110544.docx`

- [x] **T9 仓库交付**
  - Acceptance: 干净树进入 `Test01_YouRuiProject_BookManager/`；≥2 次提交；推送后远端一致
  - Verify: `git log`、`git ls-remote`、`git status`（HEAD = `bcd3cdb`）
  - Files: `D:\soft\HomeWork\Android\Android_learning\Test01_YouRuiProject_BookManager\`

- [x] **T10 汇总说明**
  - Acceptance: 代码补全内容、分支推送情况、截图来源与插入位置三部分齐全，结论带证据定位
  - Verify: 对照规格第 9 节逐条核验
  - Files: 回复正文
