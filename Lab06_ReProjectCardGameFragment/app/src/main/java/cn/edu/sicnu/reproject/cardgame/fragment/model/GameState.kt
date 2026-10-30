package cn.edu.sicnu.reproject.cardgame.fragment.model

import java.io.Serializable

/**
 * 实验六 · 一局游戏的快照（可序列化）
 *
 * 实验六要求“翻牌游戏的历史信息要用序列化方式存储，退出程序后再打开可以恢复”。
 * 直接把 Model 对象写进文件并不是好做法：Model 里有随机牌堆等与本次运行绑定的东西。
 * 因此这里定义一个只包含“恢复一局所需最小信息”的快照对象：
 *  - [cardsToMatch]：这一局是几张牌配对（2 或 3），恢复时要对得上；
 *  - [score]：当前得分；
 *  - 每张牌的花色 / 点数 / 是否翻开 / 是否已配对。
 *
 * 它实现 [Serializable]，配合 ObjectOutputStream / ObjectInputStream 直接落盘。
 *
 * @author 犹锐 2024110544 计算机科学学院 5班
 */
class GameState(
    val cardsToMatch: Int,
    val score: Int,
    val suits: List<String>,
    val ranks: List<String>,
    val chosen: List<Boolean>,
    val matched: List<Boolean>
) : Serializable {

    /** 快照里的牌数 */
    val cardCount: Int
        get() = suits.size

    companion object {
        private const val serialVersionUID = 2024110544L
    }
}
