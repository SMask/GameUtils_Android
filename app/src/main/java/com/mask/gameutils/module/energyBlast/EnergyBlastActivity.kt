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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.config.EnergyBlastSkillAffix
import com.mask.gameutils.module.energyBlast.config.EnergyBlastStatAffix
import com.mask.gameutils.module.energyBlast.config.IEnergyBlastAffix
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
                            .padding(16.dp)
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
                .padding(16.dp)
        )
    }
}

@Composable
fun EnergyBlastLayout(modifier: Modifier = Modifier) {
    var isShowDialog by remember { mutableStateOf(true) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            columns = GridCells.Fixed(6)
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
                    isShowDialog = true
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

    if (isShowDialog) {
        EnergyBlastEquipmentDialog(
            onDismissRequest = { isShowDialog = false }
        )
    }
}

@Composable
fun EnergyBlastEquipmentDialog(onDismissRequest: () -> Unit) {
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
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = Style.TextStyle.TITLE,
                    text = stringResource(R.string.equipment_info)
                )
                EnergyBlastEquipmentDropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimen.dropdownItemHeight)
                )
                ButtonNormal(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textResId = R.string.equipment_add,
                    onClick = {
                        ToastUtils.show("计算最佳组合")
                    })
            }
        }
    }
}

@Composable
fun EnergyBlastEquipmentDropdownMenu(modifier: Modifier = Modifier) {
    var isExpandedDropdownMenu by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Dimen.radius))
                .background(Color.LightGray)
                .clickable { isExpandedDropdownMenu = !isExpandedDropdownMenu },
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                style = Style.TextStyle.CONTENT,
                text = "词条信息"
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
                EnergyBlastStatAffix.entries.forEach { statAffix ->
                    EnergyBlastEquipmentDropdownMenuItem(
                        affix = statAffix,
                        onClick = {
                            isExpandedDropdownMenu = false
                            ToastUtils.show(statAffix.title)
                        }
                    )
                }
                EnergyBlastSkillAffix.entries.forEach { statAffix ->
                    EnergyBlastEquipmentDropdownMenuItem(
                        affix = statAffix,
                        onClick = {
                            isExpandedDropdownMenu = false
                            ToastUtils.show(statAffix.title)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnergyBlastEquipmentDropdownMenuItem(modifier: Modifier = Modifier, affix: IEnergyBlastAffix, onClick: (affix: IEnergyBlastAffix) -> Unit) {
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