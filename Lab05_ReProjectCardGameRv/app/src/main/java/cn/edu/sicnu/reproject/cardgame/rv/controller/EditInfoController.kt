package cn.edu.sicnu.reproject.cardgame.rv.controller

import android.app.Activity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import cn.edu.sicnu.reproject.cardgame.rv.R
import cn.edu.sicnu.reproject.cardgame.rv.model.PlayerInfo

/**
 * 实验四 · 信息修改界面的 Controller 层
 *
 * 负责把界面上零散的控件值 <-> [PlayerInfo] 之间来回搬运：
 *  - [loadInto]  把模型里的值填进各控件（进入界面时调用）
 *  - [collect]   把控件里的值收进一份新的 PlayerInfo（点"保存"时调用）
 *
 * 之所以不直接在 Activity 里写这两段，是因为"界面 <-> 数据"的字段对应关系
 * 属于控制逻辑，让它独占一个类，Activity 就只剩"装配"的几行代码。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class EditInfoController(private val activity: Activity) {

    lateinit var etName: EditText
    lateinit var etStudentId: EditText
    lateinit var etPhone: EditText
    lateinit var etEmail: EditText
    lateinit var rgGender: RadioGroup
    lateinit var rbMale: RadioButton
    lateinit var rbFemale: RadioButton
    lateinit var cbReading: CheckBox
    lateinit var cbGame: CheckBox
    lateinit var cbShopping: CheckBox
    lateinit var tvAvatar: TextView
    lateinit var btnSave: Button
    lateinit var btnCancel: Button

    /** 找控件：所有 findViewById 集中在这里，方便对照布局文件 */
    fun findViews(activity: Activity) {
        etName = activity.findViewById(R.id.et_name)
        etStudentId = activity.findViewById(R.id.et_student_id)
        etPhone = activity.findViewById(R.id.et_phone)
        etEmail = activity.findViewById(R.id.et_email)
        rgGender = activity.findViewById(R.id.rg_gender)
        rbMale = activity.findViewById(R.id.rb_male)
        rbFemale = activity.findViewById(R.id.rb_female)
        cbReading = activity.findViewById(R.id.cb_reading)
        cbGame = activity.findViewById(R.id.cb_game)
        cbShopping = activity.findViewById(R.id.cb_shopping)
        tvAvatar = activity.findViewById(R.id.iv_avatar)
        btnSave = activity.findViewById(R.id.btn_save)
        btnCancel = activity.findViewById(R.id.btn_cancel)
    }

    /**
     * 用模型数据填充界面。
     * 爱好是"多个布尔值 -> 一组复选框"的映射，所以逐个判断。
     */
    fun loadInto(info: PlayerInfo) {
        etName.setText(info.name)
        etStudentId.setText(info.studentId)
        etPhone.setText(info.phone)
        etEmail.setText(info.email)

        if (info.gender == PlayerInfo.Gender.FEMALE) {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }

        cbReading.isChecked = info.hobbies.contains(PlayerInfo.HOBBY_READING)
        cbGame.isChecked = info.hobbies.contains(PlayerInfo.HOBBY_GAME)
        cbShopping.isChecked = info.hobbies.contains(PlayerInfo.HOBBY_SHOPPING)

        updateAvatarPreview(info.name)
    }

    /**
     * 从界面收集数据，返回一份**新的** PlayerInfo。
     * 保存前做最基础的空值处理：姓名留空则退回原值，避免主界面显示空白。
     */
    fun collect(fallback: PlayerInfo): PlayerInfo {
        val result = fallback.copy()
        result.name = etName.text.toString().trim().ifEmpty { fallback.name }
        result.studentId = etStudentId.text.toString().trim().ifEmpty { fallback.studentId }
        result.phone = etPhone.text.toString().trim()
        result.email = etEmail.text.toString().trim()
        result.gender = if (rbFemale.isChecked) PlayerInfo.Gender.FEMALE else PlayerInfo.Gender.MALE

        val hobbies = mutableSetOf<String>()
        if (cbReading.isChecked) hobbies.add(PlayerInfo.HOBBY_READING)
        if (cbGame.isChecked) hobbies.add(PlayerInfo.HOBBY_GAME)
        if (cbShopping.isChecked) hobbies.add(PlayerInfo.HOBBY_SHOPPING)
        result.hobbies = hobbies

        return result
    }

    /** 头像暂时用姓名首字占位 */
    fun updateAvatarPreview(name: String) {
        tvAvatar.text = name.take(1)
    }

    /** 把 "reading/game/shopping" 这类内部 key 翻译成当前语言下的显示文字 */
    fun displayHobby(hobby: String): String = when (hobby) {
        PlayerInfo.HOBBY_READING -> activity.getString(R.string.edit_hobby_reading)
        PlayerInfo.HOBBY_GAME -> activity.getString(R.string.edit_hobby_game)
        PlayerInfo.HOBBY_SHOPPING -> activity.getString(R.string.edit_hobby_shopping)
        else -> hobby
    }
}
