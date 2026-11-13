# 任务清单（Tasks）— 书籍信息管理 APP

- [ ] **T1 工程骨架**
  - Acceptance: 目录与构建脚本齐备；`assembleDebug` 成功；空壳 Activity 可启动
  - Verify: `./gradlew :app:assembleDebug --offline`
  - Files: `settings.gradle.kts`、`build.gradle.kts`、`gradle.properties`、`app/build.gradle.kts`、`app/src/main/AndroidManifest.xml`、`res/values/*`、`res/mipmap*`、wrapper

- [ ] **T2 数据层（TDD）**
  - Acceptance: `Book` 含四字段；`BookRepository` 返回 ≥6 本，字段非空、价格为正且不全相同
  - Verify: `BookRepositoryTest` 先红后绿
  - Files: `model/Book.kt`、`model/BookRepository.kt`、`test/.../BookRepositoryTest.kt`

- [ ] **T3 价格升序排序（TDD）**
  - Acceptance: 排序后相邻价格非递减、元素数量不变、相等价格顺序稳定
  - Verify: `BookSorterTest` 先红后绿
  - Files: `controller/BookController.kt`、`test/.../BookSorterTest.kt`

- [ ] **T4 首屏列表**
  - Acceptance: 标题为「犹锐2024110544」；列表项显示书名/作者/出版社/价格；全部文字为蓝色
  - Verify: 模拟器实跑 + 截图取色
  - Files: `view/MainActivity.kt`、`view/BookAdapter.kt`、`res/layout/activity_main.xml`、`res/layout/item_book.xml`

- [ ] **T5 跳转与详情（TDD）**
  - Acceptance: 单击条目启动详情页；详情页四项与源对象一致
  - Verify: `BookControllerTest` 打包字段断言先红后绿 + 模拟器实跑
  - Files: `view/MainActivity.kt`、`view/DetailActivity.kt`、`res/layout/activity_detail.xml`、`test/.../BookControllerTest.kt`

- [ ] **T6 质量收口**
  - Acceptance: `assembleDebug` 成功且 warning 为 0；全量单元测试通过；lint 无 error
  - Verify: 构建 + 测试 + lint 三条命令
  - Files: 全工程

- [ ] **T7 实跑截图**
  - Acceptance: 至少覆盖首屏列表、价格升序、点击跳转、详情页、标题、蓝色文字；截图来自 `Lab1_AVD` 真实画面
  - Verify: `adb devices` + `screencap`，逐张核对
  - Files: `D:\soft\HomeWork\Android\YouRuiBookManager_上机测试_截图\`

- [ ] **T8 实验报告**
  - Acceptance: 表头齐全；三节正文完整；含主要代码；截图插入对应位置并附连续图题；成绩栏留空
  - Verify: 打开成品 docx 逐节核对 + `doc_get_images` 清点图片数
  - Files: `D:\soft\HomeWork\Android\上机测试_书籍信息管理_犹锐2024110544.docx`

- [ ] **T9 仓库交付**
  - Acceptance: 干净树进入 `Test01_YouRuiProject_BookManager/`；≥2 次提交（新建一次、检查前一次）；推送后远端一致；检查后无新提交
  - Verify: `git log`、`git ls-remote`、`git status`
  - Files: `D:\soft\HomeWork\Android\Android_learning\Test01_YouRuiProject_BookManager\`

- [ ] **T10 汇总说明**
  - Acceptance: 代码补全内容、分支推送情况、截图来源与插入位置三部分齐全，结论带证据定位
  - Verify: 对照规格第 9 节逐条核验
  - Files: 回复正文 + 必要时落盘
