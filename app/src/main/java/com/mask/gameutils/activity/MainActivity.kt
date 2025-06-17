package com.mask.gameutils.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mask.gameutils.R
import com.mask.gameutils.module.energyBlast.EnergyBlastActivity
import com.mask.gameutils.ui.ButtonNormal
import com.mask.gameutils.ui.theme.Dimen
import com.mask.gameutils.ui.theme.GameUtils_AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameUtils_AndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainLayout(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainLayoutPreview() {
    GameUtils_AndroidTheme {
        MainLayout(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun MainLayout(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val buttonModifier = Modifier
        .fillMaxWidth()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimen.padding)
    ) {
        ButtonNormal(
            modifier = buttonModifier,
            textResId = R.string.title_activity_energy_blast,
            onClick = {
                EnergyBlastActivity.startActivity(context)
            }
        )
    }
}