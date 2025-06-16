package com.mask.gameutils.module.energyBlast

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.EnergyBlastSkillAffix
import com.mask.gameutils.module.energyBlast.config.EnergyBlastStatAffix
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.viewmodel.EnergyBlastViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentVo
import com.mask.gameutils.ui.ButtonNormal
import com.mask.gameutils.ui.theme.Color_Bg_EnergyBlast_Affix
import com.mask.gameutils.ui.theme.Color_Bg_EnergyBlast_Affix_Main
import com.mask.gameutils.ui.theme.Color_Text_EnergyBlast_Affix_Main
import com.mask.gameutils.ui.theme.Color_Text_EnergyBlast_Affix_Unselected
import com.mask.gameutils.ui.theme.Dimen
import com.mask.gameutils.ui.theme.GameUtils_AndroidTheme
import com.mask.gameutils.ui.theme.Style
import com.mask.gameutils.utils.ActivityUtils
import com.mask.gameutils.utils.ToastUtils

class EnergyBlastActivity : ComponentActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[EnergyBlastViewModel::class.java]
    }

    companion object {
        fun startActivity(context: Context) {
            ActivityUtils.startActivity(context, EnergyBlastActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        updateEnergyBlastLayoutPreviewData(viewModel)

        setContent {
            GameUtils_AndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnergyBlastLayout(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(Dimen.padding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnergyBlastLayoutPreview() {
    val viewModel = viewModel<EnergyBlastViewModel>()

    updateEnergyBlastLayoutPreviewData(viewModel)

    GameUtils_AndroidTheme {
        EnergyBlastLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.padding),
            viewModel = viewModel
        )
    }
}

private fun updateEnergyBlastLayoutPreviewData(viewModel: EnergyBlastViewModel) {
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

@Composable
fun EnergyBlastLayout(viewModel: EnergyBlastViewModel, modifier: Modifier = Modifier) {
    // 状态管理
    var isShowAddDialog by remember { mutableStateOf(false) }
    var editEquipment by remember { mutableStateOf<EnergyBlastEquipmentVo?>(null) }
    var deleteEquipment by remember { mutableStateOf<EnergyBlastEquipmentVo?>(null) }

    // UI
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        EnergyBlastEquipmentGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            equipmentList = viewModel.equipmentList,
            onEditClick = { equipment -> editEquipment = equipment },
            onDeleteClick = { equipment -> deleteEquipment = equipment }
        )
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

    // 弹窗-添加
    if (isShowAddDialog) {
        EnergyBlastEquipmentEditDialog(
            onDismiss = { isShowAddDialog = false },
            onSave = { equipment ->
                viewModel.addEquipment(equipment)
                isShowAddDialog = false
            }
        )
    }
    // 弹窗-编辑
    val editEquipmentT = editEquipment
    if (editEquipmentT != null) {
        EnergyBlastEquipmentEditDialog(
            equipment = editEquipmentT,
            onDismiss = { editEquipment = null },
            onSave = { equipment ->
                viewModel.updateEquipment(equipment)
                editEquipment = null
            }
        )
    }
    // 弹窗-删除
    val deleteEquipmentT = deleteEquipment
    if (deleteEquipmentT != null) {
        EnergyBlastEquipmentDeleteDialog(
            equipment = deleteEquipmentT,
            onDismiss = { deleteEquipment = null },
            onDelete = {
                viewModel.removeEquipment(deleteEquipmentT)
                deleteEquipment = null
            }
        )
    }
}

@Composable
fun EnergyBlastEquipmentGrid(
    equipmentList: List<EnergyBlastEquipmentVo>,
    onEditClick: (EnergyBlastEquipmentVo) -> Unit,
    onDeleteClick: (EnergyBlastEquipmentVo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(Dimen.padding / 2),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        items(
            items = equipmentList,
            key = { equipment ->
                equipment.id
            }
        ) { equipment ->
            EnergyBlastEquipmentItem(
                modifier = Modifier
                    .fillMaxSize(),
                equipment = equipment,
                onEditClick = { onEditClick(equipment) },
                onDeleteClick = { onDeleteClick(equipment) }
            )
        }
    }
}

@Composable
fun EnergyBlastEquipmentItem(
    equipment: EnergyBlastEquipmentVo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(Dimen.strokeWidth, Color.Black, RoundedCornerShape(Dimen.radius / 2))
            .combinedClickable(
                onClick = { onEditClick() },
                onLongClick = { onDeleteClick() },
            )
            .padding(Dimen.padding / 2),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        Text(
            style = Style.TextStyle.CONTENT,
            color = equipment.type.textColor,
            text = equipment.type.title
        )
        if (equipment.mainAffix != null) {
            Text(
                style = Style.TextStyle.CONTENT,
                color = Color_Text_EnergyBlast_Affix_Main,
                fontSize = 12.sp,
                text = equipment.mainAffix.getAffixTitle()
            )
        }
        equipment.affixList.forEach { item ->
            Text(
                style = Style.TextStyle.CONTENT,
                color = item.getAffixTextColor(),
                fontSize = 12.sp,
                text = item.getAffixTitle()
            )
        }
    }
}

@Composable
fun EnergyBlastEquipmentEditDialog(
    equipment: EnergyBlastEquipmentVo? = null,
    onDismiss: () -> Unit,
    onSave: (EnergyBlastEquipmentVo) -> Unit
) {
    // 编辑状态管理
    var type by remember { mutableStateOf(equipment?.type ?: EnergyBlastEquipmentType.WEAPON) }
    val affixList: SnapshotStateList<IEnergyBlastAffix?> = remember {
        equipment?.affixList?.toMutableStateList()
            ?: MutableList<IEnergyBlastAffix?>(EnergyBlastEquipmentType.AFFIX_MAX_NUM) { null }.toMutableStateList()
    }
    var mainAffix by remember { mutableStateOf(equipment?.mainAffix) }

    // UI
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = equipment != null)
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
                    EnergyBlastEquipmentType.entries.forEach { item ->
                        FilterChip(
                            selected = type == item,
                            onClick = { type = item },
                            label = {
                                Text(
                                    style = Style.TextStyle.CONTENT,
                                    text = item.title
                                )
                            }
                        )
                    }
                }
                // 主词条
                if (type.hasMainAffix()) {
                    EnergyBlastEquipmentDropdownMenu(
                        affix = mainAffix,
                        onAffixChange = { affix -> mainAffix = affix as? EnergyBlastStatAffix },
                        isMainAffix = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.dropdownItemHeight)
                    )
                }
                // 词条列表
                affixList.forEachIndexed { index, item ->
                    EnergyBlastEquipmentDropdownMenu(
                        affix = item,
                        onAffixChange = { affix -> affixList[index] = affix },
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
                .background(if (isMainAffix) Color_Bg_EnergyBlast_Affix_Main else Color_Bg_EnergyBlast_Affix)
                .clickable { isExpandedDropdownMenu = !isExpandedDropdownMenu },
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                style = Style.TextStyle.CONTENT,
                color = if (affix != null) {
                    if (isMainAffix) {
                        Color_Text_EnergyBlast_Affix_Main
                    } else {
                        affix.getAffixTextColor()
                    }
                } else {
                    Color_Text_EnergyBlast_Affix_Unselected
                },
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
                EnergyBlastStatAffix.entries.forEach { item ->
                    EnergyBlastEquipmentDropdownMenuItem(
                        affix = item,
                        onClick = {
                            isExpandedDropdownMenu = false
                            onAffixChange(item)
                        }
                    )
                }
                // 技能词条
                if (!isMainAffix) {
                    EnergyBlastSkillAffix.entries.forEach { item ->
                        EnergyBlastEquipmentDropdownMenuItem(
                            affix = item,
                            onClick = {
                                isExpandedDropdownMenu = false
                                onAffixChange(item)
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

@Composable
fun EnergyBlastEquipmentDeleteDialog(
    equipment: EnergyBlastEquipmentVo,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.energy_blast_delete_title))
        },
        text = {
            Text(text = stringResource(R.string.energy_blast_delete_content))
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = {
                    onDelete()
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}