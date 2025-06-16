package com.mask.gameutils.module.energyBlast.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mask.gameutils.App
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixTypeAdapter
import com.mask.gameutils.module.energyBlast.config.EnergyBlastConfig
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo

/**
 * ViewModel
 *
 * Create by lishilin on 2025-06-13
 */
class EnergyBlastViewModel : ViewModel() {

    private val keyEnergyBlast = "energy_blast"
    private val keyEquipmentList = "equipment_list"
    private val keyExtraAffixNum = "extra_affix_num"

    private val _equipmentList = mutableStateListOf<EnergyBlastEquipmentVo>()
    val equipmentList: List<EnergyBlastEquipmentVo> get() = _equipmentList

    private val _extraAffixNum = mutableIntStateOf(0)
    val extraAffixNum: Int get() = _extraAffixNum.intValue

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(IEnergyBlastAffix::class.java, EnergyBlastAffixTypeAdapter())
            .create()
    }

    private val sharedPreferences by lazy {
        App.context.getSharedPreferences(keyEnergyBlast, Context.MODE_PRIVATE)
    }

    init {
        loadList()
        loadExtraAffixNum()
    }

    fun addEquipment(equipment: EnergyBlastEquipmentVo) {
        EnergyBlastConfig.lastAddType = equipment.type
        _equipmentList.add(equipment)
        transformList()
        saveList()
    }

    fun removeEquipment(equipment: EnergyBlastEquipmentVo) {
        _equipmentList.removeAll { it.id == equipment.id }
        convertList()
        saveList()
    }

    fun updateEquipment(equipment: EnergyBlastEquipmentVo) {
        val index = _equipmentList.indexOfFirst { it.id == equipment.id }
        if (index >= 0) {
            _equipmentList[index] = equipment
        }
        transformList()
        saveList()
    }

    fun addExtraAffixNum() {
        _extraAffixNum.intValue += 1
        saveExtraAffixNum()
    }

    fun minusExtraAffixNum() {
        if (_extraAffixNum.intValue > 0) {
            _extraAffixNum.intValue -= 1
            saveExtraAffixNum()
        }
    }

    /************************************************************ S 数据转换 ************************************************************/

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

    /************************************************************ E 数据转换 ************************************************************/

    /************************************************************ S 数据存储 ************************************************************/

    private fun loadList() {
        sharedPreferences.getString(keyEquipmentList, null)?.let { jsonStr ->
            val type = object : TypeToken<List<EnergyBlastEquipmentVo>>() {}.type
            val jsonList = gson.fromJson<List<EnergyBlastEquipmentVo>>(jsonStr, type) ?: emptyList()
            _equipmentList.addAll(jsonList)
        }
    }

    private fun saveList() {
        sharedPreferences.edit {
            putString(keyEquipmentList, gson.toJson(_equipmentList))
        }
    }

    private fun loadExtraAffixNum() {
        _extraAffixNum.intValue = sharedPreferences.getInt(keyExtraAffixNum, 0)
    }

    private fun saveExtraAffixNum() {
        sharedPreferences.edit {
            putInt(keyExtraAffixNum, _extraAffixNum.intValue)
        }
    }

    /************************************************************ E 数据存储 ************************************************************/

}