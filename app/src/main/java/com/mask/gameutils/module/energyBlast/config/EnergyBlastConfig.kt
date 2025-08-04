package com.mask.gameutils.module.energyBlast.config

import androidx.compose.ui.graphics.Color
import com.mask.gameutils.ui.theme.Color_Text_EnergyBlast_Affix_Skill
import com.mask.gameutils.ui.theme.Color_Text_EnergyBlast_Affix_Stat
import com.mask.gameutils.ui.theme.Color_Text_EnergyBlast_EquipmentType

/**
 * 配置
 */
object EnergyBlastConfig {

    const val GRID_COLUMN_NUM = 5 // 网格列数

    var lastAddType = EnergyBlastEquipmentType.WEAPON // 最后一次添加的装备类型
}

/**
 * 装备类型
 */
enum class EnergyBlastEquipmentType(
    val title: String, // 标题
    val textColor: Color = Color_Text_EnergyBlast_EquipmentType // 文本颜色
) {
    WEAPON("武器"),
    ARMOR("铠甲"),
    RING("戒指")
    ;

    fun hasAffixMain(): Boolean {
        return this == RING
    }

    companion object {
        const val AFFIX_MAX_NUM = 6 // 最大词条数量
    }
}

/**
 * 装备词条
 */
sealed interface IEnergyBlastAffix {
    val title: String // 标题
    val value: Int // 数值
    val max: Int // 最大值
    val textColor: Color // 文本颜色

    val priority: Int // 优先级（大的靠前，用于不同枚举类型的排序，属性词条、技能词条级别的排序）

    val ordinal: Int // enum 类型关键字
}

/**
 * 装备词条 属性
 */
enum class EnergyBlastAffixStat(
    override val title: String,
    override val value: Int,
    override val max: Int,
    override val textColor: Color = Color_Text_EnergyBlast_Affix_Stat,
    override val priority: Int = 200
) : IEnergyBlastAffix {
    HP("总生命", 25, Int.MAX_VALUE),
    ATTACK("总攻击", 25, Int.MAX_VALUE),
    DODGE_RATE("闪避", 20, 60),
    DAMAGE_REDUCTION("减伤", 20, 60),
    ATTACK_SPEED("自动攻速", 25, 100),
    CRIT_RATE("暴击概率", 25, 100),
    CRIT_DAMAGE("暴击伤害", 50, Int.MAX_VALUE),
    WATER_DAMAGE("水系伤害", 50, Int.MAX_VALUE),
    FIRE_DAMAGE("火系伤害", 50, Int.MAX_VALUE),
    WOOD_DAMAGE("木系伤害", 50, Int.MAX_VALUE)
    ;

    companion object {
        // 必备集合
        val NECESSARY_LIST = listOf(DODGE_RATE, DAMAGE_REDUCTION, ATTACK_SPEED, CRIT_RATE)
    }
}

/**
 * 装备词条 技能
 */
enum class EnergyBlastAffixSkill(
    override val title: String,
    override val value: Int,
    override val max: Int,
    override val textColor: Color = Color_Text_EnergyBlast_Affix_Skill,
    override val priority: Int = 100
) : IEnergyBlastAffix {
    HEALING_BODY("治愈之体", 1, 1),

    WATER_0("水系掌控", 1, 1),
    WATER_1("水爆", 1, 1),
    WATER_2("水龙卷", 1, 1),
    WATER_3("水浪滔天", 1, 1),
    WATER_4("水神附体", 1, 1),

    FIRE_0("火系掌控", 1, 1),
    FIRE_1("火柱", 1, 1),
    FIRE_2("火炎爆", 1, 1),
    FIRE_3("火海燎原", 1, 1),
    FIRE_4("火神附体", 1, 1),

    WOOD_0("木系掌控", 1, 1),
    WOOD_1("木斩", 1, 1),
    WOOD_2("木雷击", 1, 1),
    WOOD_3("木林旋风", 1, 1),
    WOOD_4("木神附体", 1, 1)
    ;
}