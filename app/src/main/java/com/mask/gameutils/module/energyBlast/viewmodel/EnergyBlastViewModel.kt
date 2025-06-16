package com.mask.gameutils.module.energyBlast.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mask.gameutils.module.energyBlast.config.EnergyBlastConfig
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * ViewModel
 *
 * Create by lishilin on 2025-06-13
 */
class EnergyBlastViewModel : ViewModel() {

    private val _equipmentList = mutableStateListOf<EnergyBlastEquipmentVo>()
    val equipmentList: List<EnergyBlastEquipmentVo> get() = _equipmentList

    private val _extraAffixNum = mutableIntStateOf(0)
    val extraAffixNum: Int get() = _extraAffixNum.intValue

    fun addEquipment(equipment: EnergyBlastEquipmentVo) {
        EnergyBlastConfig.lastAddType = equipment.type
        _equipmentList.add(equipment)
        transformList()
    }

    fun removeEquipment(equipment: EnergyBlastEquipmentVo) {
        _equipmentList.removeAll { it.id == equipment.id }
    }

    fun updateEquipment(equipment: EnergyBlastEquipmentVo) {
        val index = _equipmentList.indexOfFirst { it.id == equipment.id }
        if (index >= 0) {
            _equipmentList[index] = equipment
        }
        transformList()
    }

    fun addExtraAffixNum() {
        _extraAffixNum.intValue += 1
    }

    fun minusExtraAffixNum() {
        if (_extraAffixNum.intValue > 0) {
            _extraAffixNum.intValue -= 1
        }
    }

    private fun transformList() {
        sortList()
        convertList()
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

    private fun convertList() {
        _equipmentList.forEachIndexed { index, equipment ->
            val row = index / EnergyBlastConfig.GRID_COLUMN_NUM
            val column = index % EnergyBlastConfig.GRID_COLUMN_NUM
            equipment.positionRowColumn = "${row + 1}-${column + 1}"
        }
    }
}