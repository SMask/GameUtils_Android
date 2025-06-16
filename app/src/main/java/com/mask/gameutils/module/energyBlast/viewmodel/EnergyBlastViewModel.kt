package com.mask.gameutils.module.energyBlast.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * ViewModel
 *
 * Create by lishilin on 2025-06-13
 */
class EnergyBlastViewModel : ViewModel() {

    private val _equipmentList = mutableStateListOf<EnergyBlastEquipmentVo>()
    val equipmentList: List<EnergyBlastEquipmentVo> get() = _equipmentList

    fun addEquipment(equipment: EnergyBlastEquipmentVo) {
        _equipmentList.add(equipment)
        sortList()
    }

    fun removeEquipment(equipment: EnergyBlastEquipmentVo) {
        _equipmentList.removeAll { it.id == equipment.id }
    }

    fun updateEquipment(equipment: EnergyBlastEquipmentVo) {
        val index = _equipmentList.indexOfFirst { it.id == equipment.id }
        if (index >= 0) {
            _equipmentList[index] = equipment
        }
        sortList()
    }

    private fun sortList() {
        _equipmentList.sortWith { leftData, rightData ->
            val leftType = leftData.type
            val leftId = leftData.id

            val rightType = rightData.type
            val rightId = rightData.id

            if (leftType.priority > rightType.priority) {
                -1
            } else if (leftType.priority < rightType.priority) {
                1
            } else {
                leftId.compareTo(rightId)
            }
        }
    }
}