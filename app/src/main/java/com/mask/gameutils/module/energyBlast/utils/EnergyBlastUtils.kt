package com.mask.gameutils.module.energyBlast.utils

import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixSkill
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixStat
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.viewmodel.EnergyBlastViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentCombinationVo
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * Create by lishilin on 2025-06-16
 */
object EnergyBlastUtils {

    /**
     * 初始化预览数据
     */
    fun initPreviewData(viewModel: EnergyBlastViewModel) {
        val typeSize = EnergyBlastEquipmentType.entries.size
        val affixStatSize = EnergyBlastAffixStat.entries.size
        val affixSkillSize = EnergyBlastAffixSkill.entries.size

        // 创建装备
        val equipmentList = mutableListOf<EnergyBlastEquipmentVo>()
        repeat(100) { index ->
            val type = EnergyBlastEquipmentType.entries[index % typeSize]
            val affixList = mutableListOf<IEnergyBlastAffix>()
            repeat(4) { affixIndex ->
                affixList.add(EnergyBlastAffixStat.entries[(index + affixIndex) % affixStatSize])
            }
            repeat(2) { affixIndex ->
                affixList.add(EnergyBlastAffixSkill.entries[(index + affixIndex) % affixSkillSize])
            }
            val affixMain = if (type.hasAffixMain()) {
                EnergyBlastAffixStat.entries[index % affixStatSize]
            } else {
                null
            }
            val equipment = EnergyBlastEquipmentVo(index.toLong(), type, affixList, affixMain)
            equipmentList.add(equipment)
            viewModel.addEquipment(equipment)
        }

        // 创建组合
        val combinationList = mutableListOf<EnergyBlastEquipmentCombinationVo>()
        for (i in 0 until (equipmentList.size - typeSize) step typeSize) {
            val weapon = equipmentList[i + EnergyBlastEquipmentType.WEAPON.ordinal]
            val armor = equipmentList[i + EnergyBlastEquipmentType.ARMOR.ordinal]
            val ring = equipmentList[i + EnergyBlastEquipmentType.RING.ordinal]
            combinationList.add(
                EnergyBlastEquipmentCombinationVo.newInstance(
                    true,
                    weapon,
                    armor,
                    ring
                )
            )
        }
        val combinationListVM = viewModel.combinationList
        if (combinationListVM is MutableList<EnergyBlastEquipmentCombinationVo>) {
            combinationListVM.clear()
            combinationListVM.addAll(combinationList)
        }
    }
}