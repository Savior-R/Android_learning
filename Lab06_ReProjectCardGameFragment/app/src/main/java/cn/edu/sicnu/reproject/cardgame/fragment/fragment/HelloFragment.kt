package cn.edu.sicnu.reproject.cardgame.fragment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.edu.sicnu.reproject.cardgame.fragment.CardGameApp
import cn.edu.sicnu.reproject.cardgame.fragment.R
import cn.edu.sicnu.reproject.cardgame.fragment.model.PlayerInfo
import cn.edu.sicnu.reproject.cardgame.fragment.util.Insets

/**
 * 实验六 · 第 1 个 Fragment：个人信息
 *
 * 对应实验要求“第 1 个 Fragment 是个人信息”，并且个人信息要能序列化保存：
 *  - 打开时从 [CardGameApp] 里取全局 [PlayerInfo]（Application 启动时已从文件反序列化）；
 *  - 点“保存”时把表单内容写回模型，再调用 [CardGameApp.savePlayer] 用
 *    ObjectOutputStream 写进 player_info.obj；
 *  - 退出程序重新打开，Application 会读回这个文件，信息自动恢复。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class HelloFragment : Fragment() {

    private val app: CardGameApp
        get() = requireActivity().application as CardGameApp

    private val playerInfo: PlayerInfo
        get() = app.playerInfo

    private lateinit var etName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var cbReading: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShopping: CheckBox
    private lateinit var tvAvatar: TextView
    private lateinit var tvBestScore: TextView
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_hello, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Insets.applyTopPaddingToHeader(view.findViewById(R.id.header_hello))
        Insets.applyBottomInset(view.findViewById(R.id.scroll_hello))

        tvAvatar = view.findViewById(R.id.tv_avatar)
        tvBestScore = view.findViewById(R.id.tv_best_score)
        etName = view.findViewById(R.id.et_name)
        etStudentId = view.findViewById(R.id.et_student_id)
        etPhone = view.findViewById(R.id.et_phone)
        etEmail = view.findViewById(R.id.et_email)
        rbMale = view.findViewById(R.id.rb_male)
        rbFemale = view.findViewById(R.id.rb_female)
        cbReading = view.findViewById(R.id.cb_reading)
        cbGame = view.findViewById(R.id.cb_game)
        cbShopping = view.findViewById(R.id.cb_shopping)
        btnSave = view.findViewById(R.id.btn_save)

        load()
        btnSave.setOnClickListener { save() }
    }

    override fun onResume() {
        super.onResume()
        // 从游戏 Fragment 回来时最高分可能已经变了
        if (::tvBestScore.isInitialized) renderBestScore()
    }

    /** 把模型数据填进表单 */
    private fun load() {
        etName.setText(playerInfo.name)
        etStudentId.setText(playerInfo.studentId)
        etPhone.setText(playerInfo.phone)
        etEmail.setText(playerInfo.email)
        if (playerInfo.gender == PlayerInfo.Gender.FEMALE) rbFemale.isChecked = true else rbMale.isChecked = true
        cbReading.isChecked = playerInfo.hobbies.contains(PlayerInfo.HOBBY_READING)
        cbGame.isChecked = playerInfo.hobbies.contains(PlayerInfo.HOBBY_GAME)
        cbShopping.isChecked = playerInfo.hobbies.contains(PlayerInfo.HOBBY_SHOPPING)
        tvAvatar.text = playerInfo.name.take(1)
        renderBestScore()
    }

    private fun renderBestScore() {
        tvBestScore.text = getString(R.string.hello_best_score_value, playerInfo.bestScore)
    }

    /** 把表单内容写回模型，并序列化到文件 */
    private fun save() {
        playerInfo.name = etName.text.toString().trim().ifEmpty { playerInfo.name }
        playerInfo.studentId = etStudentId.text.toString().trim().ifEmpty { playerInfo.studentId }
        playerInfo.phone = etPhone.text.toString().trim()
        playerInfo.email = etEmail.text.toString().trim()
        playerInfo.gender =
            if (rbFemale.isChecked) PlayerInfo.Gender.FEMALE else PlayerInfo.Gender.MALE

        val hobbies = mutableSetOf<String>()
        if (cbReading.isChecked) hobbies.add(PlayerInfo.HOBBY_READING)
        if (cbGame.isChecked) hobbies.add(PlayerInfo.HOBBY_GAME)
        if (cbShopping.isChecked) hobbies.add(PlayerInfo.HOBBY_SHOPPING)
        playerInfo.hobbies = hobbies

        tvAvatar.text = playerInfo.name.take(1)

        // 关键一步：ObjectOutputStream 写入 player_info.obj
        app.savePlayer()
        Toast.makeText(requireContext(), R.string.hello_toast_saved, Toast.LENGTH_SHORT).show()
    }
}
