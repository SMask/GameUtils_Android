package com.mask.gameutils.module.energyBlast.config

import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * 装备 Comparator
 *
 * Create by lishilin on 2025-06-17
 */
class EnergyBlastEquipmentComparator : Comparator<EnergyBlastEquipmentVo> {

    override fun compare(leftData: EnergyBlastEquipmentVo?, rightData: EnergyBlastEquipmentVo?): Int {
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
            leftType.compareTo(rightType)
        } else {
            leftId.compareTo(rightId)
        }
    }
}