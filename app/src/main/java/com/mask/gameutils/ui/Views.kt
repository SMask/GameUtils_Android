package com.mask.gameutils.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mask.gameutils.ui.theme.Dimen

@Composable
fun ButtonNormal(textResId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier
            .height(Dimen.buttonHeight),
        onClick = onClick
    ) {
        Text(
            text = stringResource(textResId)
        )
    }
}