package com.mask.gameutils.module.energyBlast.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixTypeAdapter
import com.mask.gameutils.module.energyBlast.config.EnergyBlastConfig
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentCombinationComparator
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentComparator
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentCombinationVo
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo
import com.mask.gameutils.utils.ToastUtils

/**
 * ViewModel
 *
 * Create by lishilin on 2025-06-13
 */
class EnergyBlastViewModel(private val application: Application) : AndroidViewModel(application) {

    private val isPreview get() = application.baseContext == null

    private val spName = "energy_blast"
    private val keyEquipmentList = "equipment_list"
    private val keyAffixStatExtraNum = "affix_stat_extra_num"
    private val keyAffixSkillRequiredNum = "affix_skill_required_num"

    private val _equipmentList = mutableStateListOf<EnergyBlastEquipmentVo>()
    val equipmentList: List<EnergyBlastEquipmentVo> get() = _equipmentList

    private val _affixStatExtraNum = mutableIntStateOf(0)
    val affixStatExtraNum: Int get() = _affixStatExtraNum.intValue

    private val _affixSkillRequiredNum = mutableIntStateOf(0)
    val affixSkillRequiredNum: Int get() = _affixSkillRequiredNum.intValue

    private val _combinationList = mutableStateListOf<EnergyBlastEquipmentCombinationVo>()
    val combinationList: List<EnergyBlastEquipmentCombinationVo> get() = _combinationList

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(IEnergyBlastAffix::class.java, EnergyBlastAffixTypeAdapter())
            .create()
    }

    private val sharedPreferences by lazy {
        application.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    init {
        loadEquipmentList()
        loadAffixStatExtraNum()
        loadAffixSkillRequiredNum()
    }

    fun addEquipment(equipment: EnergyBlastEquipmentVo) {
        EnergyBlastConfig.lastAddType = equipment.type
        _equipmentList.add(equipment)
        transformEquipmentList()
        saveEquipmentList()
    }

    fun removeEquipment(equipment: EnergyBlastEquipmentVo) {
        _equipmentList.removeAll { it.id == equipment.id }
        convertEquipmentList()
        saveEquipmentList()
    }

    fun updateEquipment(equipment: EnergyBlastEquipmentVo) {
        val index = _equipmentList.indexOfFirst { it.id == equipment.id }
        if (index >= 0) {
            _equipmentList[index] = equipment
        }
        transformEquipmentList()
        saveEquipmentList()
    }

    fun addAffixStatExtraNum() {
        _affixStatExtraNum.intValue += 1
        saveAffixStatExtraNum()
    }

    fun minusAffixStatExtraNum() {
        if (_affixStatExtraNum.intValue > 0) {
            _affixStatExtraNum.intValue -= 1
            saveAffixStatExtraNum()
        }
    }

    fun addAffixSkillRequiredNum() {
        _affixSkillRequiredNum.intValue += 1
        saveAffixSkillRequiredNum()
    }

    fun minusAffixSkillRequiredNum() {
        if (_affixSkillRequiredNum.intValue > 0) {
            _affixSkillRequiredNum.intValue -= 1
            saveAffixSkillRequiredNum()
        }
    }

    /**
     * 计算最佳组合
     */
    fun calculateOptimalCombination() {
        _combinationList.clear()
        // 根据装备类型拆分到不同的集合
        val weaponList = mutableListOf<EnergyBlastEquipmentVo>()
        val armorList = mutableListOf<EnergyBlastEquipmentVo>()
        val ringList = mutableListOf<EnergyBlastEquipmentVo>()
        _equipmentList.forEach { equipment ->
            when (equipment.type) {
                EnergyBlastEquipmentType.WEAPON -> {
                    weaponList.add(equipment)
                }

                EnergyBlastEquipmentType.ARMOR -> {
                    armorList.add(equipment)
                }

                EnergyBlastEquipmentType.RING -> {
                    ringList.add(equipment)
                }
            }
        }
        if (weaponList.isEmpty() || armorList.isEmpty() || ringList.isEmpty()) {
            ToastUtils.show(R.string.calculate_optimal_combination_null)
            return
        }
        // 遍历所有组合
        val combinationList = mutableListOf<EnergyBlastEquipmentCombinationVo>()
        weaponList.forEach { weapon ->
            armorList.forEach { armor ->
                ringList.forEach { ring ->
                    combinationList.add(
                        EnergyBlastEquipmentCombinationVo.newInstance(
                            weapon,
                            armor,
                            ring
                        )
                    )
                }
            }
        }
        // 计算最佳组合
        combinationList.removeAll { combination ->
            !combination.isOptimal(_affixStatExtraNum.intValue, _affixSkillRequiredNum.intValue)
        }
        if (combinationList.isEmpty()) {
            ToastUtils.show(R.string.calculate_optimal_combination_null)
            return
        }
        // 保存最佳组合
        combinationList.sortWith(EnergyBlastEquipmentCombinationComparator())
        _combinationList.addAll(combinationList)
    }

    /************************************************************ S 数据转换 ************************************************************/

    private fun transformEquipmentList() {
        sortEquipmentList()
        convertEquipmentList()
    }

    private fun sortEquipmentList() {
        _equipmentList.sortWith(EnergyBlastEquipmentComparator())
    }

    private fun convertEquipmentList() {
        var indexOffset = 0 // 下标偏移
        _equipmentList.forEachIndexed { index, equipment ->
            // 行、列
//            val row = index / EnergyBlastConfig.GRID_COLUMN_NUM
//            val column = index % EnergyBlastConfig.GRID_COLUMN_NUM
//            equipment.position = "${row + 1}-${column + 1}"

            // 下标
            if (equipment.type != _equipmentList.getOrNull(index - 1)?.type) {
                indexOffset = index
            }
            equipment.positionText = "${index - indexOffset + 1}"
        }
    }

    /************************************************************ E 数据转换 ************************************************************/

    /************************************************************ S 数据存储 ************************************************************/

    private fun loadEquipmentList() {
        if (isPreview) {
            return
        }
        sharedPreferences.getString(keyEquipmentList, null)?.let { jsonStr ->
            val type = object : TypeToken<List<EnergyBlastEquipmentVo>>() {}.type
            val jsonList = gson.fromJson<List<EnergyBlastEquipmentVo>>(jsonStr, type) ?: emptyList()
            _equipmentList.addAll(jsonList)
        }
    }

    private fun saveEquipmentList() {
        if (isPreview) {
            return
        }
        sharedPreferences.edit {
            putString(keyEquipmentList, gson.toJson(_equipmentList))
        }
    }

    private fun loadAffixStatExtraNum() {
        if (isPreview) {
            return
        }
        _affixStatExtraNum.intValue = sharedPreferences.getInt(keyAffixStatExtraNum, 0)
    }

    private fun saveAffixStatExtraNum() {
        if (isPreview) {
            return
        }
        sharedPreferences.edit {
            putInt(keyAffixStatExtraNum, _affixStatExtraNum.intValue)
        }
    }

    private fun loadAffixSkillRequiredNum() {
        if (isPreview) {
            return
        }
        _affixSkillRequiredNum.intValue = sharedPreferences.getInt(keyAffixSkillRequiredNum, 0)
    }

    private fun saveAffixSkillRequiredNum() {
        if (isPreview) {
            return
        }
        sharedPreferences.edit {
            putInt(keyAffixSkillRequiredNum, _affixSkillRequiredNum.intValue)
        }
    }

    /************************************************************ E 数据存储 ************************************************************/

}