package cn.edu.sicnu.reproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.edu.sicnu.reproject.controller.MainController
import cn.edu.sicnu.reproject.model.ProgramAdviserModel
import cn.edu.sicnu.reproject.view.MainView

/**
 * 实验二：UI 控件代码生成以及 MVC
 *
 * 本 Activity 只负责「装配」MVC 三层，不写界面代码、也不写业务逻辑：
 *
 *   Model       -> [ProgramAdviserModel]  语言咨询知识库 + adviceAt() 查询接口
 *   View        -> [MainView]             用代码生成标题栏与区1/区2 全部控件，负责显示
 *   Controller  -> [MainController]       绑定事件、调用 Model、回填 View
 *
 * 装配顺序（典型的 MVC 启动流程）：
 *   1. 创建 Model（数据层）；
 *   2. 创建 View（界面层，此时还只是一棵空容器）；
 *   3. 创建 Controller，把 View / Model 注入进去，由 Controller 驱动界面生成与事件绑定；
 *   4. 把 View 的根容器交给 Activity 显示（setContentView）。
 *
 * 项目中没有 res/layout 目录，界面 100% 由代码生成（pure code）。
 *
 * @author 犹锐 2024110544 计算机科学学院 5 班
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mainView: MainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ① Model：模型层
        val model = ProgramAdviserModel()

        // ② View：视图层
        mainView = MainView(this)

        // ③ Controller：控制器层，注入 View 与 Model，由它驱动界面生成与事件响应
        val controller = MainController(this, mainView, model)
        controller.start()

        // ④ 把代码生成的视图树交给 Activity 显示
        setContentView(mainView.getRootContainer())

        // ⑤ Android 15（API 35）起窗口强制 edge-to-edge，内容会画到状态栏/导航栏下面，
        //    这里把系统栏的实际高度交给 View 处理，避免标题被状态栏压住、按钮被手势条挡住。
        ViewCompat.setOnApplyWindowInsetsListener(mainView.getRootContainer()) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            mainView.applySystemBarInsets(bars.top, bars.bottom)
            insets
        }
    }
}
