package com.mask.gameutils.module.energyBlast

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.EnergyBlastSkillAffix
import com.mask.gameutils.module.energyBlast.config.EnergyBlastStatAffix
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.viewmodel.EnergyBlastViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo
import com.mask.gameutils.ui.ButtonNormal
import com.mask.gameutils.ui.theme.Dimen
import com.mask.gameutils.ui.theme.GameUtils_AndroidTheme
import com.mask.gameutils.ui.theme.Style
import com.mask.gameutils.utils.ActivityUtils
import com.mask.gameutils.utils.ToastUtils

class EnergyBlastActivity : ComponentActivity() {

    companion object {
        fun startActivity(context: Context) {
            ActivityUtils.startActivity(context, EnergyBlastActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameUtils_AndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnergyBlastLayout(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        viewModel = viewModel()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnergyBlastLayoutPreview() {
    GameUtils_AndroidTheme {
        EnergyBlastLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            viewModel = viewModel()
        )
    }
}

@Composable
fun EnergyBlastLayout(viewModel: EnergyBlastViewModel, modifier: Modifier = Modifier) {
    var isShowAddDialog by remember { mutableStateOf(true) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            columns = GridCells.Fixed(5)
        ) {

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimen.padding)
        ) {
            ButtonNormal(
                modifier = Modifier
                    .weight(1f),
                textResId = R.string.equipment_add,
                onClick = {
                    isShowAddDialog = true
                })
            ButtonNormal(
                modifier = Modifier
                    .weight(1f),
                textResId = R.string.calculate_best_combination,
                onClick = {
                    ToastUtils.show("计算最佳组合")
                })
        }
    }

    if (isShowAddDialog) {
        EnergyBlastEquipmentEditDialog(
            onDismissRequest = { isShowAddDialog = false },
            onSave = { equipmentVo ->
                val msg = StringBuilder()
                msg.append(equipmentVo.type.title)
                for (affix in equipmentVo.affixList) {
                    msg.append(" ")
                    msg.append(affix.getAffixTitle())
                }
                equipmentVo.mainAffix?.let {
                    msg.append(" ")
                    msg.append(it.getAffixTitle())
                }
                ToastUtils.show(msg.toString())
            }
        )
    }
}

@Composable
fun EnergyBlastEquipmentEditDialog(
    equipment: EnergyBlastEquipmentVo? = null,
    onDismissRequest: () -> Unit,
    onSave: (EnergyBlastEquipmentVo) -> Unit
) {
    // 编辑状态管理
    var type by remember { mutableStateOf(equipment?.type ?: EnergyBlastEquipmentType.RING) }
    val affixList: SnapshotStateList<IEnergyBlastAffix?> = remember {
        equipment?.affixList?.toMutableStateList()
            ?: MutableList<IEnergyBlastAffix?>(EnergyBlastEquipmentType.AFFIX_MAX_NUM) { null }.toMutableStateList()
    }
    var mainAffix by remember { mutableStateOf(equipment?.mainAffix) }

    // UI
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(Dimen.radius),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.padding),
                verticalArrangement = Arrangement.spacedBy(Dimen.padding)
            ) {
                // 标题
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = Style.TextStyle.TITLE,
                    text = stringResource(if (equipment == null) R.string.equipment_add else R.string.equipment_edit),
                )
                // 装备类型
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EnergyBlastEquipmentType.entries.forEach { equipmentType ->
                        FilterChip(
                            selected = type == equipmentType,
                            onClick = { type = equipmentType },
                            label = {
                                Text(
                                    style = Style.TextStyle.CONTENT,
                                    text = equipmentType.title
                                )
                            }
                        )
                    }
                }
                // 主词条
                if (type == EnergyBlastEquipmentType.RING) {
                    EnergyBlastEquipmentDropdownMenu(
                        affix = mainAffix,
                        onAffixChange = { mainAffix = it as? EnergyBlastStatAffix },
                        isMainAffix = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.dropdownItemHeight)
                    )
                }
                // 词条列表
                affixList.forEachIndexed { index, affix ->
                    EnergyBlastEquipmentDropdownMenu(
                        affix = affix,
                        onAffixChange = { affixList[index] = it },
                        isMainAffix = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.dropdownItemHeight)
                    )
                }
                // 保存
                ButtonNormal(
                    modifier = Modifier.fillMaxWidth(),
                    textResId = R.string.save,
                    onClick = {
                        if (type.hasMainAffix() && mainAffix == null) {
                            ToastUtils.show(R.string.energy_blast_main_affix_null)
                            return@ButtonNormal
                        }
                        if (affixList.any { it == null }) {
                            ToastUtils.show(R.string.energy_blast_affix_null)
                            return@ButtonNormal
                        }
                        onSave(
                            EnergyBlastEquipmentVo(
                                equipment?.id ?: System.currentTimeMillis(),
                                type,
                                affixList.filterNotNull(),
                                if (type.hasMainAffix()) mainAffix else null,
                            )
                        )
                    })
            }
        }
    }
}

@Composable
fun EnergyBlastEquipmentDropdownMenu(
    affix: IEnergyBlastAffix?,
    onAffixChange: (IEnergyBlastAffix) -> Unit,
    isMainAffix: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpandedDropdownMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimen.radius))
                .background(if (isMainAffix) Color(0xFFCCCCFF) else Color.LightGray)
                .clickable { isExpandedDropdownMenu = !isExpandedDropdownMenu },
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                style = Style.TextStyle.CONTENT,
                color = affix?.getAffixTextColor() ?: Color.Gray,
                text = affix?.getAffixTitle() ?: stringResource(R.string.please_select)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.4f),
                expanded = isExpandedDropdownMenu,
                onDismissRequest = { isExpandedDropdownMenu = false }) {
                // 属性词条
                EnergyBlastStatAffix.entries.forEach { statAffix ->
                    EnergyBlastEquipmentDropdownMenuItem(
                        affix = statAffix,
                        onClick = {
                            isExpandedDropdownMenu = false
                            onAffixChange(statAffix)
                        }
                    )
                }
                // 技能词条
                if (!isMainAffix) {
                    EnergyBlastSkillAffix.entries.forEach { skillAffix ->
                        EnergyBlastEquipmentDropdownMenuItem(
                            affix = skillAffix,
                            onClick = {
                                isExpandedDropdownMenu = false
                                onAffixChange(skillAffix)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnergyBlastEquipmentDropdownMenuItem(affix: IEnergyBlastAffix, onClick: (affix: IEnergyBlastAffix) -> Unit, modifier: Modifier = Modifier) {
    DropdownMenuItem(
        onClick = { onClick(affix) },
        text = {
            Text(
                style = Style.TextStyle.CONTENT,
                color = affix.getAffixTextColor(),
                text = affix.getAffixTitle()
            )
        }
    )
}