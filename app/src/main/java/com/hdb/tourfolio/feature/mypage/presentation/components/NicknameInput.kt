@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun NicknameInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle =
                LocalAppTypography
                    .current
                    .titleMedium
                    .bold
                    .copy(
                        color = Natural10,
                        textAlign = TextAlign.Center,
                    ),
            cursorBrush =
                SolidColor(
                    Primary,
                ),
            modifier =
                Modifier.fillMaxWidth(
                    0.7f,
                ),
        )

        Spacer(
            modifier =
                Modifier.height(
                    5.dp,
                ),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(
                        0.7f,
                    )
                    .height(
                        1.5.dp,
                    )
                    .background(
                        Primary,
                    ),
        )
    }
}
