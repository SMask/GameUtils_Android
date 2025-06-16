package com.mask.gameutils.module.energyBlast.vo

import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.EnergyBlastStatAffix
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix

/**
 * 装备 实体类
 *
 * Create by lishilin on 2025-06-04
 */
data class EnergyBlastEquipmentVo(
    val id: Long = System.currentTimeMillis(), // 唯一标识（目前用时间戳）
    val type: EnergyBlastEquipmentType, // 装备类型
    val affixList: List<IEnergyBlastAffix>, // 词条列表
    val mainAffix: EnergyBlastStatAffix? = null, // 主词条（只有戒指有，数值是普通词条的两倍）

    // 以下数据需要特殊计算
    var positionRowColumn: String = "", // 位置（行、列）
)