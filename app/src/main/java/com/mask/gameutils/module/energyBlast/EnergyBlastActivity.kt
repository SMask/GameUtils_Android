package com.mask.gameutils.module.energyBlast

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mask.gameutils.App
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixSkill
import com.mask.gameutils.module.energyBlast.config.EnergyBlastAffixStat
import com.mask.gameutils.module.energyBlast.config.EnergyBlastConfig
import com.mask.gameutils.module.energyBlast.config.EnergyBlastEquipmentType
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
import com.mask.gameutils.module.energyBlast.utils.EnergyBlastUtils
import com.mask.gameutils.module.energyBlast.viewmodel.EnergyBlastViewModel
import com.mask.gameutils.module.energyBlast.vo.EnergyBlastEquipmentCombinationVo
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

    private val viewModel by viewModels<EnergyBlastViewModel>()

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
                            .fillMaxSize(),
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
    val viewModel = remember { EnergyBlastViewModel(App()) }

    EnergyBlastUtils.initPreviewData(viewModel)

    GameUtils_AndroidTheme {
        EnergyBlastLayout(
            modifier = Modifier
                .fillMaxSize(),
            viewModel = viewModel
        )
    }
}

@Composable
fun EnergyBlastLayout(viewModel: EnergyBlastViewModel, modifier: Modifier = Modifier) {
    // 状态管理
    var isShowAddDialog by remember { mutableStateOf(false) }
    var editEquipment by remember { mutableStateOf<EnergyBlastEquipmentVo?>(null) }
    var deleteEquipment by remember { mutableStateOf<EnergyBlastEquipmentVo?>(null) }
    var isShowCombinationDialog by remember { mutableStateOf(false) }

    // UI
    Column(
        modifier = modifier
            .padding(Dimen.padding),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        EnergyBlastEquipmentGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            equipmentList = viewModel.equipmentList,
            onEditClick = { equipment ->
                editEquipment = equipment
            },
            onDeleteClick = { equipment ->
                deleteEquipment = equipment
            }
        )
        EnergyBlastOptionItemNum(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.energy_blast_affix_stat_extra_num),
            content = viewModel.affixStatExtraNum.toString(),
            onMinusClick = {
                viewModel.minusAffixStatExtraNum()
            },
            onAddClick = {
                viewModel.addAffixStatExtraNum()
            }
        )
        EnergyBlastOptionItemSwitch(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.energy_blast_is_affix_stat_damage_reduction_required),
            isChecked = viewModel.isAffixStatDamageReductionRequired,
            onCheckedChange = { isChecked ->
                viewModel.setAffixStatDamageReductionRequired(isChecked)
            }
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
                textResId = R.string.calculate_optimal_combination,
                onClick = {
                    viewModel.calculateOptimalCombination()
                    isShowCombinationDialog = true
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
    // 弹窗-计算结果组合
    val combinationList = viewModel.combinationList
    if (isShowCombinationDialog && combinationList.isNotEmpty()) {
        EnergyBlastCombinationDialog(
            combinationList = combinationList,
            onDismiss = {
                isShowCombinationDialog = false
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
        columns = GridCells.Fixed(EnergyBlastConfig.GRID_COLUMN_NUM),
        horizontalArrangement = Arrangement.spacedBy(Dimen.padding / 2),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        itemsIndexed(
            items = equipmentList,
            key = { index, equipment ->
                "${index}_${equipment.id}"
            }
        ) { _, equipment ->
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
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .border(Dimen.strokeWidth, Color.Black, RoundedCornerShape(Dimen.radius / 2))
            .combinedClickable(
                onClick = { onEditClick?.invoke() },
                onLongClick = onDeleteClick,
            )
            .padding(Dimen.padding / 2),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        Text(
            style = Style.TextStyle.CONTENT,
            color = equipment.type.textColor,
            fontSize = 12.sp,
            text = equipment.type.title + " " + equipment.positionText
        )
        if (equipment.affixMain != null) {
            Text(
                style = Style.TextStyle.CONTENT,
                color = Color_Text_EnergyBlast_Affix_Main,
                fontSize = 12.sp,
                text = equipment.affixMain.title
            )
        }
        equipment.affixList.forEach { item ->
            Text(
                style = Style.TextStyle.CONTENT,
                color = item.textColor,
                fontSize = 12.sp,
                text = item.title
            )
        }
    }
}

@Composable
fun EnergyBlastOptionItemNum(
    title: String,
    content: String,
    onMinusClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = Style.TextStyle.CONTENT,
            text = title
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onMinusClick
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.minus)
            )
        }
        Text(
            modifier = Modifier.widthIn(min = 24.dp),
            style = Style.TextStyle.TITLE,
            textAlign = TextAlign.Center,
            text = content
        )
        IconButton(
            onClick = onAddClick
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.add)
            )
        }
    }
}

@Composable
fun EnergyBlastOptionItemSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = Style.TextStyle.CONTENT,
            text = title
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun EnergyBlastEquipmentEditDialog(
    equipment: EnergyBlastEquipmentVo? = null,
    onDismiss: () -> Unit,
    onSave: (EnergyBlastEquipmentVo) -> Unit
) {
    // 编辑状态管理
    var type by remember { mutableStateOf(equipment?.type ?: EnergyBlastConfig.lastAddType) }
    val affixList: SnapshotStateList<IEnergyBlastAffix?> = remember {
        equipment?.affixList?.toMutableStateList()
            ?: MutableList<IEnergyBlastAffix?>(EnergyBlastEquipmentType.AFFIX_MAX_NUM) { null }.toMutableStateList()
    }
    var affixMain by remember { mutableStateOf(equipment?.affixMain) }

    // UI
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = equipment != null)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimen.radius),
            color = Color.White
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
                    text = if (equipment == null) {
                        stringResource(R.string.equipment_add)
                    } else {
                        stringResource(R.string.equipment_edit) + " " + equipment.positionText
                    },
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
                if (type.hasAffixMain()) {
                    EnergyBlastEquipmentDropdownMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.dropdownItemHeight),
                        affix = affixMain,
                        onAffixChange = { affix ->
                            affixMain = affix as? EnergyBlastAffixStat
                        },
                        isAffixMain = true
                    )
                }
                // 词条列表
                affixList.forEachIndexed { index, item ->
                    EnergyBlastEquipmentDropdownMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.dropdownItemHeight),
                        affix = item,
                        onAffixChange = { affix ->
                            affixList[index] = affix
                        },
                        isAffixMain = false
                    )
                }
                // 保存
                ButtonNormal(
                    modifier = Modifier.fillMaxWidth(),
                    textResId = R.string.save,
                    onClick = {
                        if (type.hasAffixMain() && affixMain == null) {
                            ToastUtils.show(R.string.energy_blast_affix_main_null)
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
                                if (type.hasAffixMain()) affixMain else null,
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
    isAffixMain: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpandedDropdownMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimen.radius))
                .background(if (isAffixMain) Color_Bg_EnergyBlast_Affix_Main else Color_Bg_EnergyBlast_Affix)
                .clickable { isExpandedDropdownMenu = !isExpandedDropdownMenu },
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                style = Style.TextStyle.CONTENT,
                color = if (affix != null) {
                    if (isAffixMain) {
                        Color_Text_EnergyBlast_Affix_Main
                    } else {
                        affix.textColor
                    }
                } else {
                    Color_Text_EnergyBlast_Affix_Unselected
                },
                text = affix?.title ?: stringResource(R.string.please_select)
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
                EnergyBlastAffixStat.entries.forEach { item ->
                    EnergyBlastEquipmentDropdownMenuItem(
                        affix = item,
                        onClick = {
                            isExpandedDropdownMenu = false
                            onAffixChange(item)
                        }
                    )
                }
                // 技能词条
                if (!isAffixMain) {
                    EnergyBlastAffixSkill.entries.forEach { item ->
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
fun EnergyBlastEquipmentDropdownMenuItem(
    affix: IEnergyBlastAffix,
    onClick: (affix: IEnergyBlastAffix) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        modifier = modifier,
        onClick = { onClick(affix) },
        text = {
            Text(
                style = Style.TextStyle.CONTENT,
                color = affix.textColor,
                text = affix.title
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
            Text(
                text = stringResource(
                    R.string.energy_blast_delete_content,
                    "${equipment.type.title} ${equipment.positionText}"
                )
            )
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

@Composable
fun EnergyBlastCombinationDialog(
    combinationList: List<EnergyBlastEquipmentCombinationVo>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(Dimen.radius),
            color = Color.White
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
                    text = stringResource(R.string.optimal_combination),
                )
                // 内容
                EnergyBlastCombinationList(
                    modifier = Modifier.fillMaxWidth(),
                    combinationList = combinationList
                )
            }
        }
    }
}

@Composable
fun EnergyBlastCombinationList(
    combinationList: List<EnergyBlastEquipmentCombinationVo>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        itemsIndexed(
            items = combinationList
        ) { index, combination ->
            EnergyBlastCombinationItem(
                index = index,
                modifier = Modifier.fillMaxWidth(),
                combination = combination
            )
        }
    }
}

@Composable
fun EnergyBlastCombinationItem(
    index: Int,
    combination: EnergyBlastEquipmentCombinationVo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        Text(
            style = Style.TextStyle.CONTENT,
            text = stringResource(R.string.combination_title, (index + 1).toString())
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
        ) {
            // 装备信息
            combination.equipmentList.forEach { equipment ->
                EnergyBlastEquipmentItem(
                    modifier = Modifier.weight(1f),
                    equipment = equipment
                )
            }
            // 词条信息
            EnergyBlastCombinationAffixInfo(
                modifier = Modifier.weight(1.25f),
                combination = combination
            )
        }
    }
}

@Composable
fun EnergyBlastCombinationAffixInfo(
    combination: EnergyBlastEquipmentCombinationVo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(Dimen.strokeWidth, Color.Black, RoundedCornerShape(Dimen.radius / 2))
            .padding(Dimen.padding / 2),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
    ) {
        combination.affixMap.forEach { (affix, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = Style.TextStyle.CONTENT,
                    color = affix.textColor,
                    fontSize = 12.sp,
                    text = affix.title
                )
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(Dimen.padding / 2)
                ) {
                    // 词条当前数量
                    val isShowNum =
                        affix is EnergyBlastAffixStat && affix !in EnergyBlastAffixStat.NECESSARY_LIST
                    if (isShowNum) {
                        val num = combination.getAffixNum(affix)
                        Text(
                            style = Style.TextStyle.CONTENT,
                            color = Color(0xFF009900),
                            fontSize = 12.sp,
                            text = num.toString()
                        )
                    }

                    // 词条需要额外补充的数量
                    val requiredNum = combination.getAffixRequiredNumForMax(affix)
                    if (requiredNum > 0) {
                        Text(
                            style = Style.TextStyle.CONTENT,
                            color = Color.Red,
                            fontSize = 12.sp,
                            text = requiredNum.toString()
                        )
                    }

                    // 词条当前属性
                    val valueStr = when (affix) {
                        in EnergyBlastAffixStat.NECESSARY_LIST -> {
                            value.toString().padStart(2, '0')
                        }

                        is EnergyBlastAffixStat -> {
                            value.toString().padStart(3, '0')
                        }

                        else -> {
                            value.toString()
                        }
                    }
                    Text(
                        modifier = Modifier,
                        style = Style.TextStyle.CONTENT,
                        fontSize = 12.sp,
                        text = valueStr
                    )
                }
            }
        }
    }
}