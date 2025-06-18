package com.mask.gameutils.module.energyBlast.config

/**
 * 词条 Comparator
 *
 * Create by lishilin on 2025-06-17
 */
class EnergyBlastAffixComparator : Comparator<IEnergyBlastAffix> {

    override fun compare(leftData: IEnergyBlastAffix?, rightData: IEnergyBlastAffix?): Int {
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

        return if (leftData.javaClass != rightData.javaClass) {
            // 按词条类型排序
            rightData.priority - leftData.priority
        } else {
            // 词条类型相同，按枚举顺序排序
            leftData.ordinal - rightData.ordinal
        }
    }
}