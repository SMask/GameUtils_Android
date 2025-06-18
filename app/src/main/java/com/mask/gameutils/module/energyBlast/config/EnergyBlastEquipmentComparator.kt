package com.mask.gameutils.module.energyBlast.config

import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * 装备 Comparator
 *
 * Create by lishilin on 2025-06-17
 */
class EnergyBlastEquipmentComparator : Comparator<EnergyBlastEquipmentVo> {

    override fun compare(leftData: EnergyBlastEquipmentVo?, rightData: EnergyBlastEquipmentVo?): Int {
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

        val leftType = leftData.type
        val leftId = leftData.id

        val rightType = rightData.type
        val rightId = rightData.id

        return if (leftType != rightType) {
            // 按装备类型排序
            leftType.compareTo(rightType)
        } else {
            // 装备类型相同，按 id 排序
            leftId.compareTo(rightId)
        }
    }
}