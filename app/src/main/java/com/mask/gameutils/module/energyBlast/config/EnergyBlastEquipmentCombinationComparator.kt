package com.mask.gameutils.module.energyBlast.config

import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentCombinationVo

/**
 * 装备组合 Comparator
 *
 * Create by lishilin on 2025-06-18
 */
class EnergyBlastEquipmentCombinationComparator : Comparator<EnergyBlastEquipmentCombinationVo> {

    private val affixP0 = EnergyBlastAffixSkill.FIRE_0
    private val affixListP0 by lazy {
        listOf(
            EnergyBlastAffixSkill.WATER_0,
            EnergyBlastAffixSkill.FIRE_0,
            EnergyBlastAffixSkill.WOOD_0,
        )
    }

    private val affixP1 = EnergyBlastAffixSkill.FIRE_4
    private val affixListP1 by lazy {
        listOf(
            EnergyBlastAffixSkill.WATER_4,
            EnergyBlastAffixSkill.FIRE_4,
            EnergyBlastAffixSkill.WOOD_4
        )
    }

    private val affixListP2 by lazy {
        listOf(
            EnergyBlastAffixSkill.FIRE_1,
            EnergyBlastAffixSkill.FIRE_2,
            EnergyBlastAffixSkill.FIRE_3
        )
    }

    override fun compare(leftData: EnergyBlastEquipmentCombinationVo?, rightData: EnergyBlastEquipmentCombinationVo?): Int {
        // 为 null 的在前
        if (leftData == null && rightData == null) {
            return 0
        }
        if (leftData == null) {
            return -1
        }
        if (rightData == null) {
            return 1
        }

        val leftAffixMap = leftData.affixMap
        val rightAffixMap = rightData.affixMap

        val leftAffixNumP0 = affixListP0.count { leftAffixMap.containsKey(it) }
        val rightAffixNumP0 = affixListP0.count { rightAffixMap.containsKey(it) }
        if (leftAffixNumP0 != rightAffixNumP0) {
            // P0 数量不同，按 P0 数量排序
            return rightAffixNumP0 - leftAffixNumP0
        }
        // P0 数量相同，按 P0 是否存在排序
        val leftAffixContainsP0 = leftAffixMap.containsKey(affixP0)
        val rightAffixContainsP0 = rightAffixMap.containsKey(affixP0)
        if (leftAffixContainsP0 && !rightAffixContainsP0) {
            return -1
        }
        if (!leftAffixContainsP0 && rightAffixContainsP0) {
            return 1
        }

        val leftAffixNumP1 = affixListP1.count { leftAffixMap.containsKey(it) }
        val rightAffixNumP1 = affixListP1.count { rightAffixMap.containsKey(it) }
        if (leftAffixNumP1 != rightAffixNumP1) {
            // P1 数量不同，按 P1 数量排序
            return rightAffixNumP1 - leftAffixNumP1
        }
        // P1 数量相同，按 P1 是否存在排序
        val leftAffixContainsP1 = leftAffixMap.containsKey(affixP1)
        val rightAffixContainsP1 = rightAffixMap.containsKey(affixP1)
        if (leftAffixContainsP1 && !rightAffixContainsP1) {
            return -1
        }
        if (!leftAffixContainsP1 && rightAffixContainsP1) {
            return 1
        }

        val leftAffixNumP2 = affixListP2.count { leftAffixMap.containsKey(it) }
        val rightAffixNumP2 = affixListP2.count { rightAffixMap.containsKey(it) }
        if (leftAffixNumP2 != rightAffixNumP2) {
            // P2 数量不同，按 P2 数量排序
            return rightAffixNumP2 - leftAffixNumP2
        }

        // 都相同，不排了
        return 0
    }
}