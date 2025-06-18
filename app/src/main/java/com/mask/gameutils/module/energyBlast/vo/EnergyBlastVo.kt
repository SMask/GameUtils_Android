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
    var positionRowColumn: String = "", // 位置（行、列）
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
    fun isOptimal(affixExtraNum: Int): Boolean {
        var extraNumForMax = 0
        // 属性词条
        EnergyBlastAffixStat.entries.forEach { affix ->
            val extraNum = getAffixExtraNumForMax(affix)
            // 某一项词条溢出
            if (extraNum < 0) {
                return false
            }
            extraNumForMax += extraNum
            if (extraNumForMax > affixExtraNum) {
                return false
            }
        }
        // 技能词条
        EnergyBlastAffixSkill.entries.forEach { affix ->
            val extraNum = getAffixExtraNumForMax(affix)
            // 某一项词条溢出
            if (extraNum < 0) {
                return false
            }
        }
        return true
    }

    /**
     * 获取 词条达到最大值还需要的额外数量
     */
    fun getAffixExtraNumForMax(affix: IEnergyBlastAffix): Int {
        if (affix.max == Int.MAX_VALUE) {
            return 0
        }
        return (affix.max - affixMap.getOrDefault(affix, 0)) / affix.value
    }
}