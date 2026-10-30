package cn.edu.sicnu.reproject.cardgame.controller

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.reproject.cardgame.R
import cn.edu.sicnu.reproject.cardgame.model.PlayerInfo

/**
 * 实验四 · 主界面的 Controller 层
 *
 * 主界面要显示的字段最多（姓名、性别、学号、电话、邮件、爱好、最高分、头像），
 * 把它们一一映射到控件上是典型的"控制逻辑"，因此单独抽到这里。
 *
 * 这里也提供 [hobbiesToText]，把存起来的 hobby key 拼成可读文字。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class MainController(private val activity: AppCompatActivity) {

    private lateinit var tvAvatar: TextView
    private lateinit var tvName: TextView
    private lateinit var tvStudentId: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHobbies: TextView
    private lateinit var tvBestScore: TextView
    lateinit var btnEditInfo: Button
    lateinit var btnPlayGame: Button

    fun findViews() {
        tvAvatar = activity.findViewById(R.id.iv_avatar)
        tvName = activity.findViewById(R.id.tv_name)
        tvStudentId = activity.findViewById(R.id.tv_student_id)
        tvGender = activity.findViewById(R.id.tv_gender)
        tvPhone = activity.findViewById(R.id.tv_phone)
        tvEmail = activity.findViewById(R.id.tv_email)
        tvHobbies = activity.findViewById(R.id.tv_hobbies)
        tvBestScore = activity.findViewById(R.id.tv_best_score)
        btnEditInfo = activity.findViewById(R.id.btn_edit_info)
        btnPlayGame = activity.findViewById(R.id.btn_play_game)
    }

    /** 把模型数据渲染到界面上；每次 onResume 都会调用，因此改完信息返回即可见 */
    fun render(info: PlayerInfo) {
        val unknown = activity.getString(R.string.main_unknown)

        tvAvatar.text = info.name.take(1)
        tvName.text = info.name.ifEmpty { unknown }
        tvStudentId.text = info.studentId.ifEmpty { unknown }
        tvGender.text = activity.getString(
            if (info.gender == PlayerInfo.Gender.FEMALE) R.string.edit_gender_female
            else R.string.edit_gender_male
        )
        tvPhone.text = info.phone.ifEmpty { unknown }
        tvEmail.text = info.email.ifEmpty { unknown }
        tvHobbies.text = hobbiesToText(info.hobbies).ifEmpty { unknown }
        tvBestScore.text = activity.getString(R.string.main_best_score_value, info.bestScore)
    }

    /** 把爱好集合拼成 "阅读 / 游戏" 这样的可读文本，顺序固定，避免每次显示顺序乱跳 */
    fun hobbiesToText(hobbies: Set<String>): String {
        val ordered = PlayerInfo.ALL_HOBBIES.filter { hobbies.contains(it) }
        return ordered.joinToString(" / ") { key ->
            when (key) {
                PlayerInfo.HOBBY_READING -> activity.getString(R.string.edit_hobby_reading)
                PlayerInfo.HOBBY_GAME -> activity.getString(R.string.edit_hobby_game)
                PlayerInfo.HOBBY_SHOPPING -> activity.getString(R.string.edit_hobby_shopping)
                else -> key
            }
        }
    }
}
