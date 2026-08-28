
@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun CommonModal(
    message: String,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        containerColor = Natural100,
        shape =
            RoundedCornerShape(
                16.dp,
            ),
        title = {
            Text(
                text = message,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color = Natural10,
                        ),
            )
        },
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(
                modifier =
                    Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp,
                        ),
                ) {
                    DialogButton(
                        text = "취소",
                        onClick = onDismissClick,
                        modifier =
                            Modifier.weight(
                                1f,
                            ),
                        isPrimary = false,
                    )

                    DialogButton(
                        text = "확인",
                        onClick = onConfirmClick,
                        modifier =
                            Modifier.weight(
                                1f,
                            ),
                        isPrimary = true,
                    )
                }
            }
        },
    )
}

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(
        modifier =
            modifier
                .background(
                    color =
                        if (isPrimary) {
                            Primary
                        } else {
                            Natural90
                        },
                    shape =
                        RoundedCornerShape(
                            10.dp,
                        ),
                )
                .clickable(
                    onClick = onClick,
                )
                .padding(
                    vertical = 14.dp,
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .bold
                    .copy(
                        color =
                            if (isPrimary) {
                                Natural100
                            } else {
                                Natural60
                            },
                    ),
        )
    }
}