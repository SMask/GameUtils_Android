package com.mask.gameutils.module.energyBlast.vo

import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixComparator
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixSkill
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixStat
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import java.util.TreeMap

/**
 * 装备 实体类
 */
data class EnergyBlastEquipmentVo(
    val id: Long = System.currentTimeMillis(), // 唯一标识（目前用时间戳）
    val type: EnergyBlastEquipmentType, // 装备类型
    val affixList: List<IEnergyBlastAffix>, // 词条列表
    val affixMain: EnergyBlastAffixStat? = null, // 主词条（只有戒指有，数值是普通词条的两倍）

    // 以下数据需要特殊计算
    var positionText: String = "", // 位置文本
)

/**
 * 装备组合 实体类
 */
data class EnergyBlastEquipmentCombinationVo(
    val equipmentList: List<EnergyBlastEquipmentVo>,
    val affixMap: Map<IEnergyBlastAffix, Int>,
) {
    companion object {
        fun newInstance(vararg equipmentArr: EnergyBlastEquipmentVo): EnergyBlastEquipmentCombinationVo {
            val equipmentList = equipmentArr.toList()
            val affixMap = TreeMap<IEnergyBlastAffix, Int>(EnergyBlastAffixComparator())
            equipmentList.forEach { equipment ->
                equipment.affixList.forEach { affix ->
                    affixMap[affix] = affixMap.getOrDefault(affix, 0) + affix.value
                }
                equipment.affixMain?.let { affix ->
                    affixMap[affix] = affixMap.getOrDefault(affix, 0) + affix.value * 2
                }
            }
            return EnergyBlastEquipmentCombinationVo(equipmentList, affixMap)
        }
    }

    /**
     * 是否最佳
     */
    fun isOptimal(affixStatExtraNum: Int, isAffixStatDamageReductionRequired: Boolean): Boolean {
        // 如果不需要减伤
        if (!isAffixStatDamageReductionRequired && affixMap.containsKey(EnergyBlastAffixStat.DAMAGE_REDUCTION)) {
            return false
        }
        // 属性词条
        var affixStatRequiredNumForMax = 0
        for (affix in EnergyBlastAffixStat.entries) {
            // 如果不需要减伤
            if (!isAffixStatDamageReductionRequired && affix == EnergyBlastAffixStat.DAMAGE_REDUCTION) {
                continue
            }
            val requiredNum = getAffixRequiredNumForMax(affix)
            // 某一项词条溢出
            if (requiredNum < 0) {
                return false
            }
            affixStatRequiredNumForMax += requiredNum
            // 额外属性词条数量不够
            if (affixStatRequiredNumForMax > affixStatExtraNum) {
                return false
            }
        }
        // 技能词条
        EnergyBlastAffixSkill.entries.forEach { affix ->
            val requiredNum = getAffixRequiredNumForMax(affix)
            // 某一项词条溢出
            if (requiredNum < 0) {
                return false
            }
        }
        return true
    }

    /**
     * 获取 词条达到最大值还需要的额外数量
     */
    fun getAffixRequiredNumForMax(affix: IEnergyBlastAffix): Int {
        if (affix.max == Int.MAX_VALUE) {
            return 0
        }
        return (affix.max - affixMap.getOrDefault(affix, 0)) / affix.value
    }
}