package com.mask.gameutils.module.energyBlast.utils

import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.EnergyBlastSkillAffix
import com.mask.gameutils.module.energyBlast.config.EnergyBlastStatAffix
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.viewmodel.EnergyBlastViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * Create by lishilin on 2025-06-16
 */
object EnergyBlastUtils {

    /**
     * 初始化预览数据
     */
    fun initPreviewData(viewModel: EnergyBlastViewModel) {
        repeat(13) { index ->
            val type = EnergyBlastEquipmentType.entries[index % EnergyBlastEquipmentType.entries.size]
            val affixList = mutableListOf<IEnergyBlastAffix>()
            repeat(4) { affixIndex ->
                affixList.add(EnergyBlastStatAffix.entries[(index + affixIndex) % EnergyBlastStatAffix.entries.size])
            }
            repeat(2) { affixIndex ->
                affixList.add(EnergyBlastSkillAffix.entries[(index + affixIndex) % EnergyBlastSkillAffix.entries.size])
            }
            val mainAffix = if (type.hasMainAffix()) {
                EnergyBlastStatAffix.entries[index % EnergyBlastStatAffix.entries.size]
            } else {
                null
            }
            viewModel.addEquipment(EnergyBlastEquipmentVo(index.toLong(), type, affixList, mainAffix))
        }
    }
}