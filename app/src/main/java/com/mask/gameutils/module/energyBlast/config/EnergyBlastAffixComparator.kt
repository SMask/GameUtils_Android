package com.mask.gameutils.module.energyBlast.config

/**
 * 词条 Comparator
 *
 * Create by lishilin on 2025-06-17
 */
class EnergyBlastAffixComparator : Comparator<IEnergyBlastAffix> {

    override fun compare(leftData: IEnergyBlastAffix?, rightData: IEnergyBlastAffix?): Int {
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
            rightData.priority - leftData.priority
        } else {
            leftData.ordinal - rightData.ordinal
        }
    }
}